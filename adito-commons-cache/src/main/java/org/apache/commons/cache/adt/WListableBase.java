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
 * An base implementation of {@link Listable}, supporting
 * doubly-linked lists.
 *
 * @version $Id: WListableBase.java 155435 2005-02-26 13:17:27Z dirkv $
 * @author Rodney Waldhoff
 */
public class WListableBase extends ListableBase implements Listable, Cloneable {

  /** My preceeding sibling. */
  protected transient Listable _prev = null;

  /**
   * No arg constructor.
   * Equivalent to {@link #WListableBase(Listable,Listable) <tt>WListableBase(null,null)</tt>}.
   */
  public WListableBase() {
  }

  /**
   * Constructor.
   * @param prev the prev element in the list.
   * @param next the next element in the list.
   */
  public WListableBase(Listable prev, Listable next) {
    super(next);
    _prev = prev;
  }

  public Listable getPrev() {
    return _prev;
  }

  public boolean hasPrev() {
    return (null != _prev);
  }

  public void setPrev(Listable prev) {
    _prev = prev;
  }
}