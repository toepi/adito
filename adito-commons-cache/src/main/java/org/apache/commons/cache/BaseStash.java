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
 * An abstract base {@link Stash} implementation,
 * mapping the non-serialized versions of
 * {@link #canStore} and {@link #store} to the
 * serialized ones, and declaring {@link #wantsSerializedForm}
 * to return <tt>false</tt>.
 *
 * @version $Id: BaseStash.java 155435 2005-02-26 13:17:27Z dirkv $
 * @author Rodney Waldhoff
 */
public abstract class BaseStash implements Stash {
  /** Equivalent to {@link #canStore(java.io.Serializable,java.io.Serializable,java.lang.Long,java.lang.Long,java.io.Serializable,byte[]) <tt>canStore(<i>key</i>,<i>val</i>,<i>expiresAt</i>,<i>cost</i>,<i>group</i>,null)</tt>)}. */
  public int canStore(Serializable key, Serializable val, Long expiresAt, Long cost, Serializable group) {
    return canStore(key,val,expiresAt,cost,group,null);
  }

  /** Equivalent to {@link #store(java.io.Serializable,java.io.Serializable,java.lang.Long,java.lang.Long,java.io.Serializable,byte[]) <tt>canStore(<i>key</i>,<i>val</i>,<i>expiresAt</i>,<i>cost</i>,<i>group</i>,null)</tt>)}. */
  public boolean store(Serializable key, Serializable val, Long expiresAt, Long cost, Serializable group) {
    return store(key,val,expiresAt,cost,group,null);
  }

  /**
   * Returns <tt>false</tt>.
   * @return <tt>false</tt>.
   */
  public boolean wantsSerializedForm() {
    return false;
  }

  public abstract int canStore(Serializable key, Serializable val, Long expiresAt, Long cost, Serializable group, byte[] serialized);
  public abstract boolean store(Serializable key, Serializable val, Long expiresAt, Long cost, Serializable group, byte[] serialized);
  public abstract Serializable retrieve(Serializable key);
  public abstract boolean contains(Serializable key);
  public abstract float capacity();
  public abstract void clear(Serializable key);
  public abstract void setCache(Cache c);
  public abstract void unsetCache();

}