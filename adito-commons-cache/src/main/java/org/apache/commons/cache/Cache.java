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

/**
 * A {@link Cache} defines an API for storing and later
 * retrieving {@link Object}s based upon key values.
 * <p>
 * A {@link Cache} supports an event subscription/publication
 * system.
 *
 * @version $Id: Cache.java 155435 2005-02-26 13:17:27Z dirkv $
 * @author Rodney Waldhoff
 */
public interface Cache extends Serializable {
  /**
   * Store the specified <i>val</i> under the specified
   * <i>key</i>.
   *
   * @param key the key used to later obtain the <i>val</i> from me,
   *            which MUST NOT be <tt>null</tt>.
   * @param val the val to store, which MUST NOT be <tt>null</tt>.
   * @param expiry the timestamp at which the given <i>val</i> becomes stale, or <tt>null</tt>.
   * @param cost the implemenation dependent cost of generating the <i>val</i>, or <tt>null</tt>.
   * @return <tt>true</tt> if the <i>val</i> was stored, <tt>false</tt> otherwise.
   */
  public boolean store(Serializable key, Serializable val, Long expiry, Long cost);

  /**
   * Store the specified <i>val</i> under the specified
   * <i>key</i> and the specified <i>group</i>.
   *
   * @param key the key used to later obtain the <i>val</i> from me,
   *            which MUST NOT be <tt>null</tt>.
   * @param group a meta-key which can be used to clear the object later
   * @param val the val to store, which MUST NOT be <tt>null</tt>.
   * @param expiry the timestamp at which the given <i>val</i> becomes stale, or <tt>null</tt>.
   * @param cost the implemenation dependent cost of generating the <i>val</i>, or <tt>null</tt>.
   * @return <tt>true</tt> if the <i>val</i> was stored, <tt>false</tt> otherwise.
   */
  public boolean store(Serializable key, Serializable val, Long expiry, Long cost, Serializable group);

  /**
   * Obtain the value previously {@link #store stored} under
   * the given <i>key</i>.
   *
   * @param key the key which MUST NOT be <tt>null</tt>.
   * @return the previously {@link #store stored} value, or <tt>null</tt>.
   */
  public Serializable retrieve(Serializable key);

  public Serializable[] getKeysForGroup(Serializable group);

  /**
   * Returns <tt>true</tt> if I have a value associated with
   * the given <i>key</i>, <tt>false</tt> otherwise.
   *
   * @param key the key which MUST NOT be <tt>null</tt>.
   * @return <tt>true</tt> if I have a value associated with
   *         the given <i>key</i>, <tt>false</tt> otherwise.
   */
  public boolean contains(Serializable key);

  /**
   * Remove any value previously {@link #store stored} under
   * the given <i>key</i>.
   *
   * @param key the key which MUST NOT be <tt>null</tt>.
   */
  public void clear(Serializable key);

  /**
   * Remove any value previously {@link #store stored} under
   * the given <i>group</i>.
   *
   * @param group the group which MUST NOT be <tt>null</tt>.
   */
  public void clearGroup(Serializable group);

  /**
   * Remove all values previously {@link #store stored}.
   */
  public void clear();

  /**
   * Add the given {@link StorageListener} to my
   * set of {@link StorageListener}s.
   * @link obs the observer to add
   */
  public abstract void registerStorageListener(StorageListener obs);

  /**
   * Remove the given {@link StorageListener} from my
   * set of {@link StorageListener}s.
   * @link obs the observer to remove
   */
  public abstract void unregisterStorageListener(StorageListener obs);

  /**
   * Clear my set of {@link StorageListener}s.
   */
  public abstract void unregisterStorageListeners();

  /**
   * Add the given {@link RetrievalListener} to my
   * set of {@link RetrievalListener}s.
   * @link obs the observer to add
   */
  public abstract void registerRetrievalListener(RetrievalListener obs);

  /**
   * Remove the given {@link RetrievalListener} from my
   * set of {@link RetrievalListener}s.
   * @link obs the observer to remove
   */
  public abstract void unregisterRetrievalListener(RetrievalListener obs);

  /**
   * Clear my set of {@link RetrievalListener}s.
   */
  public abstract void unregisterRetrievalListeners();

  public abstract long getStat(CacheStat stat) throws UnsupportedOperationException;
}