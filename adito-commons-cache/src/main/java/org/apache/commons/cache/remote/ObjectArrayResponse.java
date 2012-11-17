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
package org.apache.commons.cache.remote;

import java.io.Serializable;

/**
 * tk.
 *
 * @version $Id: ObjectArrayResponse.java 155435 2005-02-26 13:17:27Z dirkv $
 * @author Rodney Waldhoff
 */
public class ObjectArrayResponse implements CacheResponse {
  protected Serializable[] _sers = null;

  public ObjectArrayResponse(Serializable[] sers) {
    _sers = sers;
  }

  public Serializable[] getArray() {
    return _sers;
  }

  public Serializable getObject(int i) {
    return _sers[i];
  }

  public int getLength() {
    return _sers.length;
  }

  public boolean equals(ObjectArrayResponse res) {
    if(null == res) {
      return false;
    } else if(null == res.getArray()) {
      return (null == _sers);
    } else if(null == _sers) {
      return false;
    } else if(_sers.length != res.getLength()) {
      return false;
    } else {
      for(int i=0;i<_sers.length;i++) {
        if(! (_sers[i] == null ? ( null == res.getObject(i) ) : _sers[i].equals(res.getObject(i)) ) ) {
          return false;
        }
      }
      return true;
    }
  }

  public int hashCode() {
    if(null == _sers) {
      return this.getClass().getName().hashCode();
    } else {
      int hc = 0;
      for(int i=0;i<_sers.length;i++) {
        try {
          hc ^= _sers[i].hashCode();
        } catch(NullPointerException e) {
          // ignored
        }
      }
      return hc;
    }
  }
}