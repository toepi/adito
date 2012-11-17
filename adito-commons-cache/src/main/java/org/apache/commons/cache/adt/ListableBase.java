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
 * singly-linked lists.
 *
 * @version $Id: ListableBase.java 155435 2005-02-26 13:17:27Z dirkv $
 * @author Rodney Waldhoff
 */
public class ListableBase implements Listable, Cloneable {

  /** My following sibling. */
  protected transient Listable _next = null;

  /**
   * No arg constructor.
   * Equivalent to {@link #ListableBase(org.apache.commons.cache.adt.Listable) <tt>ListableBase(null)</tt>}.
   */
  public ListableBase() {
  }

  /**
   * Constructor.
   * @param next the next element in the list.
   */
  public ListableBase(Listable next) {
    _next = next;
  }

  public Listable getNext() {
    return _next;
  }

  public boolean hasNext() {
    return (null != _next);
  }

  public void setNext(Listable next) {
    _next = next;
  }

  /**
   * Throws {@link UnsupportedOperationException}.
   */
  public Listable getPrev() throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }

  /**
   * Throws {@link UnsupportedOperationException}.
   */
  public boolean hasPrev() throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }

  /**
   * Throws {@link UnsupportedOperationException}.
   */
  public void setPrev(Listable prev) throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }
}