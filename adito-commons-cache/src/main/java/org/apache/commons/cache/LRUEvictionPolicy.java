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

import java.util.HashMap;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.NotActiveException;
import org.apache.commons.cache.adt.Listable;
import org.apache.commons.cache.adt.WListableObject;

/**
 * tk.
 * @version $Id: LRUEvictionPolicy.java 155435 2005-02-26 13:17:27Z dirkv $
 * @author Rodney Waldhoff
 */
public class LRUEvictionPolicy implements EvictionPolicy, StorageListener, RetrievalListener, CachedObjectIterator {
  transient protected HashMap _hash = new HashMap();
  transient protected WListableObject _leastRecent = null;
  transient protected WListableObject _mostRecent = null;
  transient protected WListableObject _current = null;

  protected boolean _canRemove = false;
  public Cache _cache = null;
  protected int _objsbetweennaps;
  protected long _sleeptimemillis;

  public LRUEvictionPolicy() {
    this(StaleObjectEvictor.DEFAULT_OBJECTS_BETWEEN_NAPS,StaleObjectEvictor.DEFAULT_SLEEP_TIME_MILLIS);
  }

  public LRUEvictionPolicy(int objsbetweennaps, long sleeptimemillis) {
    _objsbetweennaps = objsbetweennaps;
    _sleeptimemillis = sleeptimemillis;
    Thread t = new Thread(new StaleObjectEvictor(this,_objsbetweennaps,_sleeptimemillis));
    t.setDaemon(true);
    t.start();
  }

  private void writeObject(java.io.ObjectOutputStream out) throws IOException {
     out.defaultWriteObject();
     WListableObject cur = _leastRecent;
     while(null != cur) {
        out.writeObject(cur.getValue());
        cur =(WListableObject)(cur.getNext());
     }
     out.writeObject(null);
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    try {
      in.defaultReadObject();
      _hash = new HashMap();
      for(;;) {
         Object obj = in.readObject();
         if(null == obj) {
            break;
         } else {
            WListableObject temp = new WListableObject(obj,_mostRecent,null);
            if(null == _leastRecent) {
               _leastRecent = temp;
               _current = temp;
            }
            if(null != _mostRecent) {
               _mostRecent.setNext(temp);
            }
            _mostRecent = temp;
            _hash.put(((CachedObjectInfo)obj).getKey(),_mostRecent);
         }
      }

      Thread t = new Thread(new StaleObjectEvictor(this,_objsbetweennaps,_sleeptimemillis));
      t.setDaemon(true);
      t.start();
    } catch(NotActiveException e) {
      e.printStackTrace();
    }
  }

  protected synchronized void addToList(WListableObject cur) {
    if(null == _mostRecent) {
      _mostRecent = cur;
      _leastRecent = cur;
    } else {
      _mostRecent.setNext(cur);
      cur.setPrev(_mostRecent);
      _mostRecent = cur;
    }
  }

  protected synchronized void removeFromList(WListableObject cur) {
    // post condition: cur.next and cur.prev are both null
    if(_current == cur) {
      _current = (WListableObject)(_current.getPrev());
      _canRemove = false;
    }
    if(cur.hasPrev()) {
      if(cur.hasNext()) {
        cur.getPrev().setNext(cur.getNext());
        cur.getNext().setPrev(cur.getPrev());
        cur.setNext(null);
        cur.setPrev(null);
      } else {
        // else cur is the most recent and cur.next is null
        _mostRecent = (WListableObject)(cur.getPrev());
        cur.getPrev().setNext(null);
        cur.setPrev(null);
      }
    } else {
      // else cur is the least recent and cur.prev is null
      _leastRecent = (WListableObject)(cur.getNext());
      if(cur.hasNext()) {
        cur.getNext().setPrev(null);
        cur.setNext(null);
      } else {
        // else cur is both least and most recent
        // and both next and prev are null
        _leastRecent = null;
        _mostRecent = null;
      }
    }
  }


  public synchronized boolean hasNext() {
    return (null != _mostRecent);
  }

  public synchronized CachedObjectInfo getNext() throws NoSuchElementException {
    if(null != _current) {
      _current = (WListableObject)(_current.getNext());
    }
    if(null == _current) { _current = _leastRecent; }
    if(null == _current) {
      throw new NoSuchElementException();
    } else {
      _canRemove = true;
      return (CachedObjectInfo)(_current.getValue());
    }
  }

  public synchronized Object next() throws NoSuchElementException {
    return getNext();
  }

  public void remove() throws NoSuchElementException, IllegalStateException {
    // grab a lock on _cache first, since _cache may try to grab a lock on this
    synchronized(_cache) {
      synchronized(this) {
        if(null == _current) {
          throw new IllegalStateException();
        }
        if(_canRemove) {
          _cache.clear( ((CachedObjectInfo)(_current.getValue())).getKey() );
        } else {
          throw new IllegalStateException();
        }
      }
    }
  }


  /** Sets my cache. */
  public void setCache(Cache c) {
    if(null != _cache) {
      Object mutex = _cache;
      synchronized(mutex) {
        synchronized(c) {
          synchronized(this) {
            unsetCache();
            _cache = c;
            _cache.registerRetrievalListener(this);
            _cache.registerStorageListener(this);
          }
        }
      }
    } else {
      synchronized(c) {
        synchronized(this) {
          _cache = c;
          _cache.registerRetrievalListener(this);
          _cache.registerStorageListener(this);
        }
      }
    }
  }

  /** Unsets my cache. */
  public void unsetCache() {
    if(null != _cache) {
      Object mutex = _cache;
      synchronized(mutex) {
        synchronized(this) {
          _cache.unregisterRetrievalListener(this);
          _cache.unregisterStorageListener(this);
          _cache = null;
        }
      }
    }
  }

  public void storeRequested(Serializable key, Serializable val, Long expiresAt, Long cost, Serializable group) { }

  public void notStored(Serializable key, Serializable val, Long expiresAt, Long cost, Serializable group) { }

  public synchronized void stored(Serializable key, Serializable val, Long expiresAt, Long cost, Serializable group) {
    WListableObject cur = (WListableObject)(_hash.get(key));
    if(null == cur) {
      cur = new WListableObject(new CachedObjectInfoImpl(key,expiresAt,cost));
      addToList(cur);
      _hash.put(key,cur);
    } else {
      removeFromList(cur);
      addToList(cur);
    }
  }

  public synchronized void cleared(Serializable key) {
    WListableObject cur = (WListableObject)(_hash.get(key));
    if(cur != null) {
      removeFromList(cur);
      _hash.remove(key);
    }
  }

  public synchronized void cleared() {
    _hash.clear();
    _mostRecent = null;
    _leastRecent = null;
    _current = null;
  }

  public void retrieveRequested(Serializable key) {
  }

  public synchronized void retrieved(Serializable key) {
    WListableObject cur = (WListableObject)(_hash.get(key));
    if(null == cur) {
      cur = new WListableObject(new CachedObjectInfoImpl(key,null,null));
      addToList(cur);
      _hash.put(key,cur);
    } else {
      removeFromList(cur);
      addToList(cur);
    }
  }

  public void notRetrieved(Serializable key) {
  }

  public synchronized Serializable getEvictionCandidate() {
    try {
      return ((CachedObjectInfoImpl)(_leastRecent.getValue())).getKey();
    } catch(NullPointerException e) {
      return null;
    }
  }

  public synchronized Serializable[] getEvictionCandidates(int n) {
    ArrayList v = new ArrayList(n);
    WListableObject c = _leastRecent;
    while(n-- > 0) {
      if(null != c) {
        v.add(((CachedObjectInfoImpl)(c.getValue())).getKey());
        c = (WListableObject)(c.getNext());
      } else {
        break;
      }
    }
    return (Serializable[])(v.toArray(new Serializable[0]));
  }

  public synchronized String toString() {
    StringBuffer buf = new StringBuffer();
    WListableObject c = _leastRecent;
    while(c != null) {
      if(c.hasPrev()) {
        buf.append(", ");
      }
      buf.append(c.getValue());
      c = (WListableObject)(c.getNext());
    }
    return buf.toString();
  }
}