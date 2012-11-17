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
 * @version $Id: ThrowableResponse.java 155435 2005-02-26 13:17:27Z dirkv $
 * @author Rodney Waldhoff
 */
public class ThrowableResponse implements CacheResponse {
  protected Throwable _t = null;

  public ThrowableResponse(Throwable t) {
    _t = t;
  }

  public Throwable getThrowable() {
    return _t;
  }

  public boolean equals(ThrowableResponse res) {
    return (null == res ? false : (null == res.getThrowable() ? null == _t : res.getThrowable().equals(_t) ));
  }

  public int hashCode() {
    return (null == _t ? this.getClass().getName().hashCode() : _t.hashCode());
  }
}