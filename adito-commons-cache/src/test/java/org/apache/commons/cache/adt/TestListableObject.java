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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Rodney Waldhoff
 * @version $Id: TestListableObject.java 155435 2005-02-26 13:17:27Z dirkv $
 */
public class TestListableObject extends TestCase {
   public TestListableObject(String name) {
      super(name);
   }

   public static Test suite() {
      return new TestSuite(TestListableObject.class);
   }

   public void testNext() {
      ListableObject b = new ListableObject("beta");
      ListableObject a = new ListableObject("alpha",b);

      assertTrue(a.hasNext());
      assertTrue(!b.hasNext());
      assertSame(b,a.getNext());
      a.setNext(null);
      assertTrue(!a.hasNext());
      assertTrue(null == a.getNext());
      b.setNext(a);
      assertTrue(b.hasNext());
      assertSame(a,b.getNext());
   }

   public void testValue() {
      ListableObject a = new ListableObject("foo");
      assertEquals("foo",a.getValue());
      a.setValue("bar");
      assertEquals("bar",a.getValue());
   }
}
