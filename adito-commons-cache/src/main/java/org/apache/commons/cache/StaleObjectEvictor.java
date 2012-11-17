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

import java.util.NoSuchElementException;
import java.io.Serializable;

/**
 * tk.
 * @version $Id: StaleObjectEvictor.java 155435 2005-02-26 13:17:27Z dirkv $
 * @author Rodney Waldhoff
 */
public class StaleObjectEvictor implements Runnable, Serializable {
  public transient static final int DEFAULT_OBJECTS_BETWEEN_NAPS;
  static {
    int objsTweenNaps = 10;
    try {
      objsTweenNaps = Integer.parseInt(System.getProperty("org.apache.commons.cache.StaleObjectEvictor.objs-between-naps","10"));
    } catch(Exception e) {
      objsTweenNaps = 10;
    }
    DEFAULT_OBJECTS_BETWEEN_NAPS = objsTweenNaps;
  }

  public transient static final long DEFAULT_SLEEP_TIME_MILLIS;
  static {
    long sleepTime = 10000;
    try {
      sleepTime = Long.parseLong(System.getProperty("org.apache.commons.cache.StaleObjectEvictor.sleep-time-millis","10000"));
    } catch(Exception e) {
      sleepTime = 10000;
    }
    DEFAULT_SLEEP_TIME_MILLIS = sleepTime;
  }

  protected int _objsBetweenNaps = DEFAULT_OBJECTS_BETWEEN_NAPS;
  protected long _sleepTimeMillis = DEFAULT_SLEEP_TIME_MILLIS;
  protected CachedObjectIterator _iterator;
  protected boolean _cancelled = false;

  public StaleObjectEvictor(CachedObjectIterator it) {
    this(it,DEFAULT_OBJECTS_BETWEEN_NAPS,DEFAULT_SLEEP_TIME_MILLIS);
  }

  public StaleObjectEvictor(CachedObjectIterator it, int objsBetweenNaps) {
    this(it,objsBetweenNaps,DEFAULT_SLEEP_TIME_MILLIS);
  }

  public StaleObjectEvictor(CachedObjectIterator it, long sleepTimeMillis) {
    this(it,DEFAULT_OBJECTS_BETWEEN_NAPS,sleepTimeMillis);
  }

  public StaleObjectEvictor(CachedObjectIterator it, int objsBetweenNaps, long sleepTimeMillis) {
    _iterator = it;
    _objsBetweenNaps = objsBetweenNaps;
    _sleepTimeMillis = sleepTimeMillis;
  }

  public void cancel() {
    _cancelled = true;
  }

  public void run() {
    while(!_cancelled) {
      for(int i=0;i<_objsBetweenNaps;i++) {
        CachedObjectInfo info = null;
        try {
          info = _iterator.getNext();
        } catch(NoSuchElementException e) {
          info = null;
        }
        if(null != info) {
          Long expiry = info.getExpirationTs();
          if(null != expiry) {
            if(System.currentTimeMillis() >= expiry.longValue()) {
            try {
                _iterator.remove();
              } catch(Exception e) {
                // ignored
              }
            }
          }
        } else {
          break;
        }
      }
      try {
        Thread.sleep(_sleepTimeMillis);
      } catch(InterruptedException e) {
        // ignored
      }
    }
  }

  /*
  public void passivate() {
    _cancelled = true;
  }

  public void activate() {
    _cancelled = false;
  }
  */


}