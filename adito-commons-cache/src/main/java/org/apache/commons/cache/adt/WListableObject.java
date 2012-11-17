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
package org.apache.commons.cache.adt;

/**
 * A simple {@link Listable} supporting doubly-linked
 * lists of arbitrary {@link Object}s.
 *
 * @version $Id: WListableObject.java 155435 2005-02-26 13:17:27Z dirkv $
 * @author Rodney Waldhoff
 */
public class WListableObject extends WListableBase implements Listable, Cloneable {
  /**
   * Equivalent to {@link #WListableObject(java.lang.Object,Listable,Listable) <tt>WListableObject(val,null,null)</tt>}.
   */
  public WListableObject(Object val) {
    this(val,null,null);
  }

  /**
   * @param val my value
   * @param next the next Listable.
   */
  public WListableObject(Object val, Listable prev, Listable next) {
    super(prev,next);
    _val = val;
  }

  /** My value. */
  protected Object _val = null;

  /**
   * Return the {@link Object} I'm wrapping.
   * @return the {@link Object} I'm wrapping.
   */
  public Object getValue() {
    return _val;
  }

  /**
   * Set the {@link Object} I'm wrapping.
   * @param the {@link Object} to wrap.
   */
  public void setValue(Object obj) {
    _val = obj;
  }
}