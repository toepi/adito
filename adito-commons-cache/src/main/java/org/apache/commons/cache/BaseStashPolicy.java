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
 * An abstract base implementation of {@link StashPolicy},
 * declaring the non-serialized version of
 * {@link #shouldStore <tt>shouldStore</tt>} to invoke the
 * serialized one, and {@link #wantsSerializedForm <tt>wantsSerializedForm</tt>} to
 * return <tt>false</tt>.
 *
 * @version $Id: BaseStashPolicy.java 155435 2005-02-26 13:17:27Z dirkv $
 * @author Rodney Waldhoff
 */
public abstract class BaseStashPolicy implements StashPolicy {
  public abstract boolean shouldStore(Serializable key, Serializable val, Long expiresAt, Long cost, byte[] serialized);
  public abstract void setCache(Cache c);
  public abstract void unsetCache();

  /** Equivalent to {@link #shouldStore(java.io.Serializable,java.io.Serializable,java.lang.Long,java.lang.Long,byte[]) <tt>canStore(<i>key</i>,<i>val</i>,<i>expiresAt</i>,<i>cost</i>,null)</tt>)}. */
  public boolean shouldStore(Serializable key, Serializable val, Long expiresAt, Long cost) {
    return shouldStore(key,val,expiresAt,cost,null);
  }

  /**
   * Returns <tt>false</tt>.
   * @return <tt>false</tt>.
   */
  public boolean wantsSerializedForm() {
    return false;
  }

}