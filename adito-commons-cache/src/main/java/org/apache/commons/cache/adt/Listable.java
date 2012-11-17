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

import java.util.LinkedList; // only used in JavaDoc comments, but this makes the warnings go away
/**
 * A simple linked-listed inteface, supporting
 * both singly and doubly linked lists.
 * <p>
 * Unlike {@link java.util.LinkedList}, this interface
 * provides access to the list elements themselves, which
 * may necessary for some uses.
 *
 * @see java.util.LinkedList
 *
 * @version $Id: Listable.java 155435 2005-02-26 13:17:27Z dirkv $
 * @author Rodney Waldhoff
 */
public interface Listable {
  /**
   * Return the <tt>Listable</tt> element following me,
   * or <tt>null</tt> if I am the last element in the
   * list.
   *
   * @return the <tt>Listable</tt> element following me,
   *         or <tt>null</tt>.
   */
  public abstract Listable getNext();

  /**
   * Return <tt>true</tt> if there is a <tt>Listable</tt>
   * element following me, <tt>false</tt> otherwise.
   *
   * @return <tt>true</tt> if there is a <tt>Listable</tt>
   *         element following me, <tt>false</tt> otherwise.
   */
  public abstract boolean hasNext();

  /**
   * Change my next element to <i>next</i>.
   * Note that this does not change the {@link #getPrev}
   * value of <i>next</i>.
   *
   * @param next my new following sibling.
   * @throws UnsupportedOperationException if I am a read-only list.
   */
  public abstract void setNext(Listable next) throws UnsupportedOperationException;

  /**
   * Return the <tt>Listable</tt> element preceeding me,
   * or <tt>null</tt> if I am the last element in the
   * list.
   *
   * @return the <tt>Listable</tt> element preceeding me,
   *         or <tt>null</tt>.
   * @throws UnsupportedOperationException if I am a singly-linked list.
   */
  public abstract Listable getPrev() throws UnsupportedOperationException;

  /**
   * Return <tt>true</tt> if there is a <tt>Listable</tt>
   * element preceeding me, <tt>false</tt> otherwise.
   *
   * @return <tt>true</tt> if there is a <tt>Listable</tt>
   *         element preceeding me, <tt>false</tt> otherwise.
   * @throws UnsupportedOperationException if I am a singly-linked list.
   */
  public abstract boolean hasPrev() throws UnsupportedOperationException;

  /**
   * Change my preceeding element to <i>prev</i>.
   * Note that this does not change the {@link #getNext}
   * value of <i>prev</i>.
   *
   * @param prev my new preceeding sibling.
   * @throws UnsupportedOperationException if I am a singly-linked or read-only list.
   */
  public abstract void setPrev(Listable prev) throws UnsupportedOperationException;
}