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
 * A {@link Cache} that doesn't.
 * @version $Id: NoOpCache.java 155435 2005-02-26 13:17:27Z dirkv $
 * @author Rodney Waldhoff
 */
public class NoOpCache extends BaseCache implements Cache {
  /** Returns <tt>false</tt>. */
  public boolean store(Serializable key, Serializable val, Long expiry, Long cost, Serializable group) {
    broadcastStoreRequested(key,val,expiry,cost,group);
    broadcastNotStored(key,val,expiry,cost,group);
    return false;
  }

  /** Returns <tt>null</tt>. */
  public Serializable retrieve(Serializable key) {
    broadcastRetrieveRequested(key);
    broadcastNotRetrieved(key);
    return null;
  }

  /** Returns an empty array. */
  public Serializable[] getKeysForGroup(Serializable group) {
    return new Serializable[0];
  }

  /** Returns <tt>false</tt> */
  public boolean contains(Serializable key) {
    return false;
  }

  /** No-op. */
  public void clear(Serializable key) {
  }

  /** No-op. */
  public void clearGroup(Serializable group) {
  }

  /** No-op. */
  public void clear() {
  }

  /** Throws {@link UnsupportedOperationException} */
  public long getStat(CacheStat stat) throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }
}