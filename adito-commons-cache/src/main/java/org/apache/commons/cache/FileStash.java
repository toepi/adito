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
import java.util.Iterator;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Serializable;
import java.io.IOException;
import java.io.File;

/**
 * tk.
 * @version $Id: FileStash.java 155435 2005-02-26 13:17:27Z dirkv $
 * @author Rodney Waldhoff
 */
public class FileStash extends BaseStash implements Stash {
  public transient static final long DEFAULT_MAX_OBJS;
  static {
    int defaultMaxObjs = -1;
    try {
      defaultMaxObjs = Integer.parseInt(System.getProperty("org.apache.commons.cache.FileStash.max-objs","-1"));
    } catch(Exception e) {
      defaultMaxObjs = -1;
    }
    DEFAULT_MAX_OBJS = defaultMaxObjs;
  }

  public transient static final long DEFAULT_MAX_BYTES;
  static {
    int defaultMaxBytes = -1;
    try {
      defaultMaxBytes = Integer.parseInt(System.getProperty("org.apache.commons.cache.FileStash.max-bytes","-1"));
    } catch(Exception e) {
      defaultMaxBytes = -1;
    }
    DEFAULT_MAX_BYTES = defaultMaxBytes;
  }

  public transient static final File[] DEFAULT_TEMP_DIRS;
  static {
    ArrayList v = new ArrayList();
    String rootdirstr = System.getProperty("org.apache.commons.cache.FileStash.root-cache-dir");
    File rootdir = null;
    if(null == rootdir) {
      DEFAULT_TEMP_DIRS = null;
    } else {
      rootdir = new File(rootdirstr);
      int branchingFactor = 10;
      try {
        branchingFactor = Integer.parseInt(System.getProperty("org.apache.commons.cache.FileStash.num-cache-dirs","10"));
      } catch(Exception e) {
        branchingFactor = 10;
      }
      if(branchingFactor < 0) {
        branchingFactor = 10;
      } else if(branchingFactor==0) {
        v.add(rootdir);
      } else {
        for(int i=0;i<branchingFactor;i++) {
          v.add(new File(rootdir,String.valueOf(i)));
        }
      }
      DEFAULT_TEMP_DIRS = (File[])(v.toArray(new File[0]));
    }
  }

  public transient static final String DEFAULT_FILE_PREFIX;
  static {
    DEFAULT_FILE_PREFIX = System.getProperty("org.apache.commons.cached.FileStash.default-file-prefix","cache");
  }


  public transient static final String DEFAULT_FILE_SUFFIX;
  static {
    DEFAULT_FILE_SUFFIX = System.getProperty("org.apache.commons.cache.FileStash.default-file-suffix",".ser");
  }

  protected HashMap _hash = null;
  protected long _maxObjs = DEFAULT_MAX_OBJS;
  protected long _maxBytes = DEFAULT_MAX_BYTES;
  protected Cache _cache = null;
  protected long _curBytes = 0;
  protected File[] _tempFileDirs = null;
  protected String _tempFilePrefix = DEFAULT_FILE_PREFIX;
  protected String _tempFileSuffix = DEFAULT_FILE_SUFFIX;
  protected boolean _cleanupfiles = false;

  public FileStash() {
    this(DEFAULT_MAX_BYTES,DEFAULT_MAX_OBJS,DEFAULT_TEMP_DIRS,false);
  }

  public FileStash(long maxbytes) {
    this(maxbytes,DEFAULT_MAX_OBJS,DEFAULT_TEMP_DIRS,false);
  }

  public FileStash(long maxbytes, long maxobjs) {
    this(maxbytes,maxobjs,DEFAULT_TEMP_DIRS,false);
  }

  public FileStash(long maxbytes, long maxobjs, File[] tempdirs, boolean cleanup) {
    _maxObjs = maxobjs;
    _maxBytes = maxbytes;
    _tempFileDirs = tempdirs;
    _hash = new HashMap();
    _cleanupfiles = cleanup;
  }

  public FileStash(long maxbytes, long maxobjs, String rootdir, int numdirs) {
    this(maxbytes,maxobjs,(null == rootdir ? ((File)null) : new File(rootdir)),numdirs,false);
  }

  public FileStash(long maxbytes, long maxobjs, String rootdir, int numdirs, boolean cleanup) {
    this(maxbytes,maxobjs,(null == rootdir ? ((File)null) : new File(rootdir)),numdirs,cleanup);
  }

  public FileStash(long maxbytes, long maxobjs, File rootdir, int numdirs) {
    this(maxbytes,maxobjs,rootdir,numdirs,false);
  }

  public FileStash(long maxbytes, long maxobjs, File rootdir, int numdirs, boolean cleanup) {
    _maxObjs = maxobjs;
    _maxBytes = maxbytes;
    ArrayList v = new ArrayList();
    if(null == rootdir) {
      _tempFileDirs = null;
    } else {
      if(numdirs < 0) {
        numdirs = 10;
      } else if(numdirs==0) {
        v.add(rootdir);
      } else {
        for(int i=0;i<numdirs;i++) {
          v.add(new File(rootdir,String.valueOf(i)));
        }
      }
      _tempFileDirs = (File[])(v.toArray(new File[0]));
    }
    _cleanupfiles = cleanup;
    _hash = new HashMap();
  }

  protected byte[] getSerializedForm(Serializable val) {
    byte[] serform = null;
    if(null == val) { return null; }
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
    return serform;
  }

  protected synchronized Serializable readFromFile(File file) {
    ObjectInputStream oin = null;
    try {
      oin = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
      return (Serializable)(oin.readObject());
    } catch(Exception e) {
      return null;
    } finally {
      try { oin.close(); } catch(Exception e) { }
    }
  }

  public synchronized int canStore(Serializable key, Serializable val, Long expiresAt, Long cost, Serializable group, byte[] serialized) {
    if(null == serialized) {
      serialized = getSerializedForm(val);
    }
    if(null == serialized) {
      return Stash.NO;
    } else if(_maxBytes != -1 && serialized.length > _maxBytes) {
      return Stash.NO_NOT_STORABLE;
    } else if(_maxBytes != -1 && _curBytes + serialized.length > _maxBytes) {
      return Stash.NO_FULL;
    } else if(_maxObjs != -1 && _hash.size() > _maxObjs) {
      return Stash.NO_FULL;
    } else {
      return Stash.YES;
    }
  }

  protected int _tempfileCounter = 0;

  protected File getTempfile() {
    File cachefile = null;
    try {
      File tempdir = null;
      if(_tempFileDirs != null && _tempFileDirs.length > 0) {
        tempdir = _tempFileDirs[_tempfileCounter++%_tempFileDirs.length];
        if(!tempdir.exists()) {
          if(_cleanupfiles) { tempdir.deleteOnExit(); }
          tempdir.mkdirs();
          tempdir.mkdir();
        }
      }
      cachefile = File.createTempFile(_tempFilePrefix,_tempFileSuffix,tempdir);
      if(_cleanupfiles) { cachefile.deleteOnExit(); }
    } catch(Exception e) {
      return null;
    }
    return cachefile;
  }

  public synchronized boolean store(Serializable key, Serializable val, Long expiresAt, Long cost, Serializable group, byte[] serialized) {
    if(null == serialized) {
      serialized = getSerializedForm(val);
    }
    if(null == serialized) {
      return false;
    }

    File cachefile = getTempfile();
    if(null == cachefile) {
      return false;
    }

    BufferedOutputStream fout = null;
    try {
      fout = new BufferedOutputStream(new FileOutputStream(cachefile));
      fout.write(serialized);
      fout.flush();
    } catch(Exception e) {
      try { fout.close(); } catch(Exception ex) { }
      fout = null;
      try { cachefile.delete(); } catch(Exception ex) { }
      return false;
    } finally {
      try { fout.close(); } catch(Exception e) { }
    }

    Object oldobj = null;
    try {
      oldobj = _hash.put(key,new CachedObjectInfoImpl(cachefile,expiresAt,new Long(serialized.length)));
    } catch(Exception e) {
      try { cachefile.delete(); } catch(Exception ex)  { }
      return false;
    } finally {
      if(null != oldobj && oldobj instanceof CachedObjectInfo) {
        CachedObjectInfo oldcachedobj = (CachedObjectInfo)oldobj;
        try {
          _curBytes -= oldcachedobj.getCost().longValue();
        } catch(NullPointerException ex) {
          // ignored
        }
        try {
          File f = (File)(oldcachedobj.getKey());
          f.delete();
        } catch(Exception ex) {
            ex.printStackTrace();
          // ignored
        }
      }
    }
    _curBytes += serialized.length;
    return true;
  }

  public Serializable retrieve(Serializable key) {
    // grab a lock on the cache first, since it may attempt to grab a lock on this
    synchronized(_cache) {
      synchronized(this) {
        CachedObjectInfo info = (CachedObjectInfo)(_hash.get(key));
        if(null != info) {
          Long expiry = info.getExpirationTs();
          if(null != expiry) {
            if(expiry.longValue() < System.currentTimeMillis()) {
              _cache.clear(key);
              return null;
            } else {
              return readFromFile((File)(info.getKey()));
            }
          } else {
            return readFromFile((File)(info.getKey()));
          }
        } else {
          return null;
        }
      }
    }
  }

  public synchronized boolean contains(Serializable key) {
    return _hash.containsKey(key);
  }

  public synchronized float capacity() {
    float objcount = 0;
    if(_maxObjs > 0) {
       objcount = (((float)_hash.size())/((float)_maxObjs));
    }
    float bytecount = 0;
    if(_maxBytes > 0) {
      bytecount = (((float)_curBytes)/((float)_maxBytes));
    }
    return (objcount > bytecount) ? objcount : bytecount;
  }

  public synchronized void clear(Serializable key) {
    CachedObjectInfo obj = (CachedObjectInfo)(_hash.remove(key));
    if(null != obj) {
      _curBytes -= obj.getCost().longValue();
      ((File)(obj.getKey())).delete();
    }
  }

  public synchronized void clear() {
    Iterator it = _hash.keySet().iterator();
    while(it.hasNext()) {
      try {
        CachedObjectInfo obj = (CachedObjectInfo)(it.next());
        ((File)(obj.getKey())).delete();
      } catch(Exception e) {
          e.printStackTrace();
        // ignored
      }
    }
    _hash.clear();
    _curBytes = 0;
  }

  public void setCache(Cache c) {
    if(null != _cache) {
      Object mutex = _cache;
      synchronized(mutex) {
        synchronized(this) {
          unsetCache();
          _cache = c;
        }
      }
    } else {
      _cache = c;
    }
  }

  public void unsetCache() {
    if(null != _cache) {
      Object mutex = _cache;
      synchronized(mutex) {
        clear();
        _cache = null;
      }
    }
  }

  public boolean wantsSerializedForm() {
    return true;
  }
}