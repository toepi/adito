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
 * .
 *
 * @ATVERSION@
 * @author Rodney Waldhoff
 */
public final class CacheStat implements Serializable {
  private static int _counter = 0;

  public final String strVal;
  public final int intVal = _counter++;

  private CacheStat() {
    strVal= null;
  }

  private CacheStat(String name) {
    strVal= name;
  }

  public String toString() {
    return strVal;
  }

  public int getIntValue() {
    return intVal;
  }

  public String getStrValue() {
    return strVal;
  }

  public boolean equals(CacheStat stat) {
    return (null != stat && stat.strVal.equals(strVal));
  }

  public int hashCode() {
    return intVal;
  }

  public static final CacheStat NUM_RETRIEVE_REQUESTED = new CacheStat("number of retrievals requested");
  public static final CacheStat NUM_RETRIEVE_FOUND = new CacheStat("number of retrievals found");
  public static final CacheStat NUM_RETRIEVE_NOT_FOUND = new CacheStat("number of retrievals not found");
  public static final CacheStat NUM_STORE_REQUESTED = new CacheStat("number of stores requested");
  public static final CacheStat NUM_STORE_STORED = new CacheStat("number of objects stored");
  public static final CacheStat NUM_STORE_NOT_STORED = new CacheStat("number of objects not stored");
  public static final CacheStat NUM_CLEARED = new CacheStat("number of objects cleared");
  public static final CacheStat CUR_CAPACITY = new CacheStat("current capacity");
}