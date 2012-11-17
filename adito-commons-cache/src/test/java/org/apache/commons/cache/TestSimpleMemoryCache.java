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

import java.io.File;
import java.io.Serializable;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestSimpleMemoryCache extends TestCase {
   public TestSimpleMemoryCache(String testName) {
      super(testName);
   }

   public static void main(String args[]) {
      String[] testCaseName = { TestSimpleMemoryCache.class.getName() };
      junit.textui.TestRunner.main(testCaseName);
   }

   public static Test suite() {
      return new TestSuite(TestSimpleMemoryCache.class);
   }

   private Cache cache = null;

   public void setUp() {
      cache = new SimpleCache(new MemoryStash(10), (EvictionPolicy)null, (StashPolicy)null, (GroupMap)null, (File)null);
   }

   public void testStoreRetrieve() {
      Serializable key1 = "key 1";
      Serializable value1 = "value 1";
      Serializable key2 = "key 2";
      Serializable value2 = "value 2";
      Serializable key3 = "key 3";
      Serializable value3 = "value 3";

      assertTrue(!cache.contains(key1));
      assertTrue(cache.store(key1,value1,null,null));
      assertTrue(cache.contains(key1));
      assertEquals(value1,cache.retrieve(key1));

      assertTrue(!cache.contains(key2));
      assertTrue(cache.store(key2,value2,null,null));
      assertTrue(cache.contains(key2));
      assertEquals(value2,cache.retrieve(key2));

      assertTrue(!cache.contains(key3));
      assertTrue(cache.store(key3,value3,null,null));
      assertTrue(cache.contains(key3));
      assertEquals(value3,cache.retrieve(key3));

      assertTrue(cache.contains(key1));
      assertEquals(value1,cache.retrieve(key1));
   }

   public void testStoreRetrieveClear() {
      Serializable key1 = "key 1";
      Serializable value1 = "value 1";
      Serializable key2 = "key 2";
      Serializable value2 = "value 2";
      Serializable key3 = "key 3";
      Serializable value3 = "value 3";

      assertTrue(!cache.contains(key1));
      assertTrue(cache.store(key1,value1,null,null));
      assertTrue(cache.contains(key1));
      assertEquals(value1,cache.retrieve(key1));

      assertTrue(!cache.contains(key2));
      assertTrue(cache.store(key2,value2,null,null));
      assertTrue(cache.contains(key2));
      assertEquals(value2,cache.retrieve(key2));

      assertTrue(!cache.contains(key3));
      assertTrue(cache.store(key3,value3,null,null));
      assertTrue(cache.contains(key3));
      assertEquals(value3,cache.retrieve(key3));

      cache.clear(key1);
      assertTrue(!cache.contains(key1));
      assertTrue(cache.contains(key2));
      assertTrue(cache.contains(key3));

      cache.clear(key2);
      assertTrue(!cache.contains(key1));
      assertTrue(!cache.contains(key2));
      assertTrue(cache.contains(key3));

      cache.clear(key3);
      assertTrue(!cache.contains(key1));
      assertTrue(!cache.contains(key2));
      assertTrue(!cache.contains(key3));
   }
}
