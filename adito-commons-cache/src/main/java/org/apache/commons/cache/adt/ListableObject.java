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
 * A simple {@link Listable} supporting singly-linked
 * lists of arbitrary {@link Object}s.
 *
 * @version $Id: ListableObject.java 155435 2005-02-26 13:17:27Z dirkv $
 * @author Rodney Waldhoff
 */
public class ListableObject extends ListableBase implements Listable, Cloneable {
  /**
   * Equivalent to {@link #ListableObject(java.lang.Object,Listable) <tt>ListableObject(val,null)</tt>}.
   */
  public ListableObject(Object val) {
    this(val,null);
  }

  /**
   * @param val my value
   * @param next the next Listable.
   */
  public ListableObject(Object val, Listable next) {
    super(next);
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

/*
  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
     out.defaultWriteObject();
     ListableObject cur = _next;
     while(cur != null) {
        out.writeObject(cur.getValue());
        cur = cur.getNext();
     }
     out.writeObject(null);
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
     in.defaultReadObject();
     Listable current = this;
     for(;;) {
        Object obj = in.readObject();
        if(null == obj) {
           break;
        } else if(obj instanceof Listable) {
           try {
              current.setNext((Listable)obj);
           } catch(UnsupportedOperationException e) {
              // ignored
           }
           try {
              ((Listable)obj).setPrev(current);
           } catch(UnsupportedOperationException e) {
              // ignored
           }
           current = (Listable)obj;
        } else {
           throw new java.io.IOException("Unexpected object while deserializing ListableBase: " + obj.getClass().getName());
        }
     }
  }
*/

}