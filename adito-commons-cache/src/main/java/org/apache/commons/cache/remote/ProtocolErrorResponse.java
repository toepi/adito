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
 * @version $Id: ProtocolErrorResponse.java 155435 2005-02-26 13:17:27Z dirkv $
 * @author Rodney Waldhoff
 */
public class ProtocolErrorResponse implements CacheResponse {
  protected String _message = null;

  public ProtocolErrorResponse(String message) {
    _message = message;
  }

  public String getMessage() {
    return _message;
  }

  public String toString() {
    return this.getClass().getName() + (null == _message ? "" : ": " + _message);
  }

  public boolean equals(ProtocolErrorResponse res) {
    return (null == res ? false : null == _message ? null == res.getMessage() : _message.equals(res.getMessage()));
  }

  public int hashCode() {
    return null == _message ? this.getClass().getName().hashCode() : _message.hashCode();
  }
}