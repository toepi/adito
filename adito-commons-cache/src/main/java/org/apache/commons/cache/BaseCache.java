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

import java.util.ArrayList;
import java.io.Serializable;

/**
 * An abstract base {@link Cache} implementation,
 * managing the registration of listeners and the
 * broadcast of events.
 *
 * @version $Id: BaseCache.java 155435 2005-02-26 13:17:27Z dirkv $
 * @author Rodney Waldhoff
 */
public abstract class BaseCache implements Cache {
  public abstract boolean store(Serializable key, Serializable val, Long expiry, Long cost, Serializable group);
  public abstract Serializable retrieve(Serializable key);
  public abstract Serializable[] getKeysForGroup(Serializable group);
  public abstract boolean contains(Serializable key);
  public abstract void clear(Serializable key);
  public abstract void clear();

  public long getStat(CacheStat stat) throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }

  public synchronized void clearGroup(Serializable group) {
    Serializable[] keys = getKeysForGroup(group);
    if(null != keys) {
      for(int i=0,m=keys.length;i<m;i++) {
        try {
          clear(keys[i]);
        } catch(Exception e) {
          e.printStackTrace();
          /* ignored */
        }
      }
    }
  }

  public boolean store(Serializable key, Serializable val, Long expiry, Long cost) {
    return store(key,val,expiry,cost,null);
  }

  /** My list of {@link StorageListener}s. */
  protected ArrayList _storageListeners = new ArrayList();

  /** My list of {@link RetrievalListener}s. */
  protected ArrayList _retrievalListeners = new ArrayList();

  /**
   * Add the given {@link StorageListener} to my
   * set of {@link StorageListener}s.
   * @link obs the observer to add
   */
  public synchronized void registerStorageListener(StorageListener obs) {
    if(!_storageListeners.contains(obs)) {
      _storageListeners.add(obs);
    }
  }

  /**
   * Remove the given {@link StorageListener} from my
   * set of {@link StorageListener}s.
   * @link obs the observer to remove
   */
  public synchronized void unregisterStorageListener(StorageListener obs) {
    for(boolean found=true;found;found=_storageListeners.remove(obs));
  }

  /**
   * Clear my set of {@link StorageListener}s.
   */
  public synchronized void unregisterStorageListeners() {
    _storageListeners.clear();
  }

  /**
   * Add the given {@link RetrievalListener} to my
   * set of {@link RetrievalListener}s.
   * @link obs the observer to add
   */
  public synchronized void registerRetrievalListener(RetrievalListener obs) {
    if(!_retrievalListeners.contains(obs)) {
      _retrievalListeners.add(obs);
    }
  }

  /**
   * Remove the given {@link RetrievalListener} from my
   * set of {@link RetrievalListener}s.
   * @link obs the observer to remove
   */
  public synchronized void unregisterRetrievalListener(RetrievalListener obs) {
    for(boolean found=true;found;found=_retrievalListeners.remove(obs));
  }

  /**
   * Clear my set of {@link RetrievalListener}s.
   */
  public synchronized void unregisterRetrievalListeners() {
    _retrievalListeners.clear();
  }

  /**
   * Broadcast a {@link StorageListener#storeRequested(java.io.Serializable,java.io.Serializable,java.lang.Long,java.lang.Long,java.io.Serializable)}
   * event to my set of {@link StorageListener}s.
   *
   * @param key the cache key
   * @param val the cache value
   * @param expiresAt the expiration timestamp, or <tt>null</tt>
   * @param cost the cost of the object, or <tt>null</tt>
   */
  protected synchronized void broadcastStoreRequested(Serializable key, Serializable val, Long expiresAt, Long cost, Serializable group) {
    for(int i=0,m=_storageListeners.size();i<m;i++) {
      ((StorageListener)(_storageListeners.get(i))).storeRequested(key,val,expiresAt,cost,group);
    }
  }

  /**
   * Broadcast a {@link StorageListener#stored(java.io.Serializable,java.io.Serializable,java.lang.Long,java.lang.Long,java.io.Serializable)}
   * event to my set of {@link StorageListener}s.
   *
   * @param key the cache key
   * @param val the cache value
   * @param expiresAt the expiration timestamp, or <tt>null</tt>
   * @param cost the cost of the object, or <tt>null</tt>
   */
  protected synchronized void broadcastStored(Serializable key, Serializable val, Long expiresAt, Long cost, Serializable group) {
    for(int i=0,m=_storageListeners.size();i<m;i++) {
      ((StorageListener)(_storageListeners.get(i))).stored(key,val,expiresAt,cost,group);
    }
  }

  /**
   * Broadcast a {@link StorageListener#notStored(java.io.Serializable,java.io.Serializable,java.lang.Long,java.lang.Long,java.io.Serializable)}
   * event to my set of {@link StorageListener}s.
   *
   * @param key the cache key
   * @param val the cache value
   * @param expiresAt the expiration timestamp, or <tt>null</tt>
   * @param cost the cost of the object, or <tt>null</tt>
   */
  protected synchronized void broadcastNotStored(Serializable key, Serializable val, Long expiresAt, Long cost, Serializable group) {
    for(int i=0,m=_storageListeners.size();i<m;i++) {
      ((StorageListener)(_storageListeners.get(i))).notStored(key,val,expiresAt,cost, group);
    }
  }

  /**
   * Broadcast a {@link StorageListener#cleared(java.io.Serializable)}
   * event to my set of {@link StorageListener}s.
   *
   * @param key the cache key
   */
  protected synchronized void broadcastCleared(Serializable key) {
    for(int i=0,m=_storageListeners.size();i<m;i++) {
      ((StorageListener)(_storageListeners.get(i))).cleared(key);
    }
  }

  /**
   * Broadcast a {@link StorageListener#cleared()}
   * event to my set of {@link StorageListener}s.
   */
  protected synchronized void broadcastCleared() {
    for(int i=0,m=_storageListeners.size();i<m;i++) {
      ((StorageListener)(_storageListeners.get(i))).cleared();
    }
  }

  /**
   * Broadcast a {@link RetrievalListener#retrieveRequested(java.io.Serializable)}
   * event to my set of {@link RetrievalListener}s.
   *
   * @param key the cache key
   */
  protected synchronized void broadcastRetrieveRequested(Serializable key) {
    for(int i=0,m=_retrievalListeners.size();i<m;i++) {
      ((RetrievalListener)(_retrievalListeners.get(i))).retrieveRequested(key);
    }
  }

  /**
   * Broadcast a {@link RetrievalListener#retrieved(java.io.Serializable)}
   * event to my set of {@link RetrievalListener}s.
   *
   * @param key the cache key
   */
  protected synchronized void broadcastRetrieved(Serializable key) {
// System.out.println(System.currentTimeMillis() + ": BaseCache.broadcastRetrieved starting");
    for(int i=0,m=_retrievalListeners.size();i<m;i++) {
// System.out.println(System.currentTimeMillis() + ": BaseCache.broadcastRetrieved about to broadcast to " + _retrievalListeners.get(i));
      ((RetrievalListener)(_retrievalListeners.get(i))).retrieved(key);
// System.out.println(System.currentTimeMillis() + ": BaseCache.broadcastRetrieved just broadcasted to " + _retrievalListeners.get(i));
    }
// System.out.println(System.currentTimeMillis() + ": BaseCache.broadcastRetrieved ending");
  }

  /**
   * Broadcast a {@link RetrievalListener#notRetrieved(java.io.Serializable)}
   * event to my set of {@link RetrievalListener}s.
   *
   * @param key the cache key
   */
  protected synchronized void broadcastNotRetrieved(Serializable key) {
    for(int i=0,m=_retrievalListeners.size();i<m;i++) {
      ((RetrievalListener)(_retrievalListeners.get(i))).notRetrieved(key);
    }
  }
}