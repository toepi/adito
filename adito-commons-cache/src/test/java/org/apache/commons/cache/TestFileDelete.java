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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestFileDelete extends TestCase {
   public TestFileDelete(String testName) {
      super(testName);
   }

   public static void main(String args[]) {
      String[] testCaseName = { TestFileDelete.class.getName() };
      junit.textui.TestRunner.main(testCaseName);
   }

   public static Test suite() {
      return new TestSuite(TestFileDelete.class);
   }

   private Cache cache = null;
   private File basedir = new File("target/cachetest");

   public void setUp() {
      basedir.mkdir();
      cache = new SimpleCache(
                     new FileStash(1000,500,basedir.getAbsolutePath() + "/cachedir",10,false),
                     new LRUEvictionPolicy(10,60000),
                     null,
                     new GroupMapImpl(),
                     new File(basedir,"cache.ser")
                  );
   }

   public void tearDown() {
      // basedir.delete();
   }

   public void testLong() {
      StringBuffer buf = new StringBuffer();
      for(int i=0;i<3;i++) {
         buf.append("All your base are now belong to us. ");
      }
      for(int i=0;i<500;i++) {
         String key = "this is the key " + i;
         assertTrue("object " + i + " should be storeable",cache.store(key,buf,new Long(System.currentTimeMillis()+600000L),null));
         assertTrue("object " + i + " should be in the cache",cache.contains(key));
      }
   }
}
