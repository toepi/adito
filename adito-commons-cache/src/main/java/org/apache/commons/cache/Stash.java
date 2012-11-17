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
 * tk.
 * @version $Id: Stash.java 155435 2005-02-26 13:17:27Z dirkv $
 * @author Rodney Waldhoff
 */
public interface Stash extends Serializable {
  public static final int YES             = 1;
  public static final int NO_FULL         = 2;
  public static final int NO_NOT_STORABLE = 3;
  public static final int NO              = 4;

  public int canStore(Serializable key, Serializable val, Long expiresAt, Long cost, Serializable group);
  public int canStore(Serializable key, Serializable val, Long expiresAt, Long cost, Serializable group, byte[] serialized);
  public boolean store(Serializable key, Serializable val, Long expiresAt, Long cost, Serializable group);
  public boolean store(Serializable key, Serializable val, Long expiresAt, Long cost, Serializable group, byte[] serialized);
  public Serializable retrieve(Serializable key);
  public boolean contains(Serializable key);
  public void clear(Serializable key);
  public void clear();
  public float capacity();


  public void setCache(Cache c);
  public void unsetCache();
  public boolean wantsSerializedForm();
}