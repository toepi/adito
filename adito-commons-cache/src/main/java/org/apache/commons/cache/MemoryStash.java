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
import java.io.Serializable;

/**
 * tk.
 * @version $Id: MemoryStash.java 155435 2005-02-26 13:17:27Z dirkv $
 * @author Rodney Waldhoff
 */
public class MemoryStash extends BaseStash implements Stash {
  protected HashMap _hash = null;
  protected int _maxObjs = 1000;
  protected Cache _cache = null;

  public MemoryStash(int maxobjs) {
    _maxObjs = maxobjs;
    _hash = new HashMap();
  }

  public synchronized int canStore(Serializable key, Serializable val, Long expiresAt, Long cost, Serializable group, byte[] serialized) {
    if(_hash.size() < _maxObjs) {
      return Stash.YES;
    } else {
      return Stash.NO_FULL;
    }
  }

  public synchronized  boolean store(Serializable key, Serializable val, Long expiresAt, Long cost, Serializable group, byte[] serialized) {
    try {
      _hash.put(key,new CachedObjectInfoImpl(val,expiresAt,cost));
    } catch(Exception e) {
      return false;
    }
    return true;
  }

  public Serializable retrieve(Serializable key) {
    // grab a lock on the cache first, since it may try to grab a lock on me
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
              return info.getKey();
            }
          } else {
            return info.getKey();
          }
        } else {
          return null;
        }
      }
    }
  }

  public boolean contains(Serializable key) {
    return _hash.containsKey(key);
  }

  public synchronized float capacity() {
    return (((float)_hash.size())/((float)_maxObjs));
  }

  public void clear(Serializable key) {
    _hash.remove(key);
  }

  public synchronized void clear() {
    _hash.clear();
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
}