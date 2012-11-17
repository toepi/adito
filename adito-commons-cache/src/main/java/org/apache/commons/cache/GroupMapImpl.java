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

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

/**
 * tk.
 * @version $Id: GroupMapImpl.java 155435 2005-02-26 13:17:27Z dirkv $
 * @author Rodney Waldhoff
 */
public class GroupMapImpl extends BaseStorageListener implements GroupMap, StorageListener {
  /** The {@link Cache} I am associated with. */
  protected Cache _cache = null;
  /** A map from groups to a set of associated keys. */
  protected HashMap _groupToKeys = new HashMap();
  /** A map from keys to their groups. */
  protected HashMap _keyToGroup = new HashMap();

  /** Returns an array of key values associated with the given group. */
  public synchronized Serializable[] getKeysForGroup(Serializable group) {
    try {
      HashSet set = (HashSet)(_groupToKeys.get(group));
      return (Serializable[])(set.toArray(new Serializable[0]));
    } catch(Exception e) {
      return new Serializable[0];
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
            _cache.registerStorageListener(this);
          }
        }
      }
    } else {
      synchronized(c) {
        synchronized(this) {
          _cache = c;
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
          _cache.unregisterStorageListener(this);
          clear();
          _cache = null;
        }
      }
    }
  }

  public synchronized void stored(Serializable key, Serializable val, Long expiresAt, Long cost, Serializable group) {
    if(null == group) {
      Object oldgroup = _keyToGroup.get(key);
      if(null != oldgroup) {
        HashSet oldset = (HashSet)(_groupToKeys.get(oldgroup));
        if(null != oldset) {
          oldset.remove(key);
        }
      }
    } else {
      HashSet set = (HashSet)(_groupToKeys.get(group));
      if(null != set) {
        set.add(key);
      } else {
        set = new HashSet();
        set.add(key);
        _groupToKeys.put(group,set);
      }
      Object oldgroup = _keyToGroup.put(key,group);
      if(null != oldgroup && !(oldgroup.equals(group))) {
        HashSet oldset = (HashSet)(_groupToKeys.get(oldgroup));
        if(null != oldset) {
          oldset.remove(key);
        }
      }
    }
  }

  public synchronized void cleared(Serializable key) {
    Serializable group = (Serializable)(_keyToGroup.remove(key));
    if(null != group) {
      HashSet set = (HashSet)(_groupToKeys.get(group));
      set.remove(key);
    }
  }

  public void cleared() {
    clear();
  }

  protected synchronized void clear() {
    _groupToKeys.clear();
    _keyToGroup.clear();
  }

  public static void main(String[] args) {
    GroupMapImpl tm = new GroupMapImpl();
    tm.stored("key1","val",null,null,"group1");
    tm.stored("key2","val",null,null,"group1");
    tm.stored("key3","val",null,null,"group1");
    tm.stored("key4","val",null,null,"group1");
    Serializable[] keys = tm.getKeysForGroup("group1");
    for(int i=0;i<keys.length;i++) {
      System.out.println(keys[i]);
    }
  }
}