/*
 * Copyright 2001-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.cache;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.Serializable;
import java.io.IOException;
import java.io.File;

/**
 * tk.
 * @version $Id: SimpleCache.java 155435 2005-02-26 13:17:27Z dirkv $
 * @author Rodney Waldhoff
 */
public class SimpleCache extends BaseCache implements Cache {
  protected GroupMap _gm = null;
  protected EvictionPolicy _ep = null;
  protected StashPolicy _sp = null;
  protected Stash _stash = null;
  protected boolean _wantsSerialized = false;
  protected File _persistFile = null;

  protected long _numRetrieveRequested = 0;
  protected long _numRetrieveFound = 0;
  protected long _numStoreRequested = 0;
  protected long _numStoreStored = 0;
  protected long _numCleared = 0;

  public SimpleCache(Stash stash) {
    this(stash,null,null,null);
  }

  public SimpleCache(Stash stash, EvictionPolicy ep) {
    this(stash,ep,null,null);
  }

  public SimpleCache(Stash stash, EvictionPolicy ep, StashPolicy sp) {
    this(stash,ep,sp,null,null);
  }

  public SimpleCache(Stash stash, EvictionPolicy ep, StashPolicy sp, GroupMap tm) {
    this(stash,ep,sp,tm,null);
  }

  public SimpleCache(Stash stash, EvictionPolicy ep, StashPolicy sp, GroupMap tm, File persistFile) {
    _gm = tm;
    if(null != _gm) { tm.setCache(this); }
    _stash = stash;
    _stash.setCache(this); // stash cannot be null
    _ep = ep;
    if(null != _ep) { _ep.setCache(this); }
    _sp = sp;
    if(null != _sp) { _sp.setCache(this); }
    _wantsSerialized = _stash.wantsSerializedForm() || ((null == _sp) ? false : _sp.wantsSerializedForm());
    _persistFile = persistFile;
  }

  public synchronized long getStat(CacheStat stat) throws UnsupportedOperationException {
    if(stat.equals(CacheStat.CUR_CAPACITY)) {
      try {
        return (long)(1000F * _stash.capacity());
      } catch(Exception e) {
        throw new UnsupportedOperationException();
      }
    } else if(stat.equals(CacheStat.NUM_CLEARED)) {
      throw new UnsupportedOperationException();
    } else if(stat.equals(CacheStat.NUM_RETRIEVE_FOUND)) {
      return _numRetrieveFound;
    } else if(stat.equals(CacheStat.NUM_RETRIEVE_NOT_FOUND)) {
      return (_numRetrieveRequested - _numRetrieveFound);
    } else if(stat.equals(CacheStat.NUM_RETRIEVE_REQUESTED)) {
      return _numRetrieveRequested;
    } else if(stat.equals(CacheStat.NUM_STORE_NOT_STORED)) {
      return (_numStoreRequested - _numStoreStored);
    } else if(stat.equals(CacheStat.NUM_STORE_REQUESTED)) {
      return _numStoreRequested;
    } else if(stat.equals(CacheStat.NUM_STORE_STORED)) {
      return _numStoreStored;
    } else {
      throw new UnsupportedOperationException("CacheStat \"" + stat.toString() + "\" not recoginzed.");
    }
  }

  public static SimpleCache readFromFile(String f) throws IOException, ClassNotFoundException {
    return SimpleCache.readFromFile(new File(f));
  }

  public static SimpleCache readFromFile(File f) throws IOException, ClassNotFoundException {
    ObjectInputStream in = null;
    try {
      in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)));
      return (SimpleCache)(in.readObject());
    } catch(ClassNotFoundException e) {
      e.fillInStackTrace();
      throw e;
    } catch(IOException e) {
      e.fillInStackTrace();
      throw e;
    } finally {
      try { in.close(); } catch(Exception e) { }
    }
  }

  protected synchronized void writeToFile() {
    if(null == _persistFile) {
      return;
    } else {
      ObjectOutputStream out = null;
      try {
        out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(_persistFile)));
        out.writeObject(this);
        out.flush();
      } catch(Exception e) {
        e.printStackTrace();
      } finally {
        try { out.close(); } catch(Exception e) { }
      }
    }
  }

  public Serializable[] getKeysForGroup(Serializable group) {
    if(null == _gm) {
      throw new IllegalStateException("no group map right now");
    } else {
      return _gm.getKeysForGroup(group);
    }
  }

  public synchronized boolean store(Serializable key, Serializable val, Long expiry, Long cost, Serializable group) {
    if(contains(key)) {
      clear(key);
    }
    _numStoreRequested++;
    if(null != expiry) {
      if(expiry.longValue() <= System.currentTimeMillis()) {
        return false;
      }
    }

    broadcastStoreRequested(key,val,expiry,cost,group);
    byte[] serform = null;
    if(_wantsSerialized) {
      ByteArrayOutputStream byout = null;
      ObjectOutputStream out = null;
      try {
        byout = new ByteArrayOutputStream();
        out = new ObjectOutputStream(byout);
        out.writeObject(val);
        out.flush();
        serform = byout.toByteArray();
      } catch(IOException e) {
        serform = null;
      } finally {
        try { byout.close(); } catch(Exception e) { }
        try { out.close(); } catch(Exception e) { }
      }
    }
    if(_sp == null || _sp.shouldStore(key,val,expiry,cost,serform)) {
      switch(_stash.canStore(key,val,expiry,cost,group,serform)) {
        case Stash.YES:
          _stash.store(key,val,expiry,cost,group,serform);
          broadcastStored(key,val,expiry,cost,group);
          _numStoreStored++;
          writeToFile();
          return true;
        case Stash.NO_FULL:
          if(tryToEvict()) {
            return store(key,val,expiry,cost,group);
          } else {
            broadcastNotStored(key,val,expiry,cost,group);
            return false;
          }
        case Stash.NO_NOT_STORABLE:
        case Stash.NO:
        default:
          broadcastNotStored(key,val,expiry,cost,group);
          return false;
      }
    } else {
      broadcastNotStored(key,val,expiry,cost,group);
      return false;
    }
  }

  public synchronized Serializable retrieve(Serializable key) {
    _numRetrieveRequested++;
    broadcastRetrieveRequested(key);
    Serializable obj = _stash.retrieve(key);
    if(null == obj) {
      broadcastNotRetrieved(key);
      return null;
    } else {
      broadcastRetrieved(key);
      _numRetrieveFound++;
      return obj;
    }
  }

  public synchronized boolean contains(Serializable key) {
    return _stash.contains(key);
  }

  public synchronized void clear(Serializable key) {
    _numCleared++;
    _stash.clear(key);
    broadcastCleared(key);
    writeToFile();
  }

  public synchronized void clear() {
    _stash.clear();
    broadcastCleared();
    writeToFile();
  }

  protected synchronized boolean tryToEvict() {
    if(null == _ep) {
      return false;
    } else {
      Serializable key = _ep.getEvictionCandidate();
      if(null == key) {
        return false;
      } else {
        clear(key);
        return true;
      }
    }
  }

  public static void main(String[] args) throws Exception {
    File cfile = null;
    SimpleCache cache = null;
    if(args.length > 0) {
      ObjectInputStream in = null;
      try {
        in = new ObjectInputStream(new FileInputStream(new File(args[0])));
        cache = (SimpleCache)(in.readObject());
      } catch(Exception e) {
        cache = null;
        e.printStackTrace();
      } finally {
        try { in.close(); } catch(Exception e) { }
      }
    }

    if(null == cache) {
      LRUEvictionPolicy ep = new LRUEvictionPolicy();
      Stash s = new FileStash(10000L);
      cache = new SimpleCache(s,ep,null,null,new File("persitent.ser"));

      System.out.println(cache.store("Key1","Value1",null,null));
      System.out.println(cache.store("Key2","Value2",new Long(System.currentTimeMillis() + 10000),null));
      System.out.println(cache.store("Key3","Value3",new Long(System.currentTimeMillis() + 9000),null));
      System.out.println(cache.store("Key4","Value4",new Long(System.currentTimeMillis() + 8000),null));
      System.out.println(cache.store("Key5","Value5",new Long(System.currentTimeMillis() + 7000),null));
      System.out.println(cache.store("Key6","Value6",new Long(System.currentTimeMillis() + 6000),null));
      System.out.println(cache.store("Key7","Value7",new Long(System.currentTimeMillis() + 5000),null));
      System.out.println(cache.store("Key8","Value8",new Long(System.currentTimeMillis() + 4000),null));
      System.out.println(cache.store("Key9","Value9",new Long(System.currentTimeMillis() + 3000),null));
      System.out.println(cache.store("Key10","Value10",new Long(System.currentTimeMillis() + 40000),null));
      System.out.println(cache.store("Key11","Value11",new Long(System.currentTimeMillis() + 30000),null));
      System.out.println(cache.store("Key12","Value12",new Long(System.currentTimeMillis() + 20000),null));

      if(args.length > 0) {
        ObjectOutputStream out = null;
        try {
          out = new ObjectOutputStream(new FileOutputStream(new File(args[0])));
          out.writeObject(cache);
        } catch(Exception e) {
          e.printStackTrace();
        } finally {
          try { out.close(); } catch(Exception e) { }
        }
      }
    }

    for(int i=0;i<12;i++) {
      System.out.println("Key1\t" + cache.retrieve("Key1"));
      System.out.println("Key2\t" + cache.retrieve("Key2"));
      System.out.println("Key3\t" + cache.retrieve("Key3"));
      System.out.println("Key4\t" + cache.retrieve("Key4"));
      System.out.println("Key5\t" + cache.retrieve("Key5"));
      System.out.println("Key6\t" + cache.retrieve("Key6"));
      System.out.println("Key7\t" + cache.retrieve("Key7"));
      System.out.println("Key8\t" + cache.retrieve("Key8"));
      System.out.println("Key9\t" + cache.retrieve("Key9"));
      System.out.println("Key10\t" + cache.retrieve("Key10"));
      System.out.println("Key11\t" + cache.retrieve("Key11"));
      System.out.println("Key12\t" + cache.retrieve("Key12"));

      System.out.println();
      Thread.sleep(1000L);
    }
  }
}