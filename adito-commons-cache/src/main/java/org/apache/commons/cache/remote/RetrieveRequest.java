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
 * @version $Id: RetrieveRequest.java 155435 2005-02-26 13:17:27Z dirkv $
 * @author Rodney Waldhoff
 */
public class RetrieveRequest implements CacheRequest {
  protected Serializable _key = null;

  public RetrieveRequest(Serializable key) {
    _key = key;
  }

  public Serializable getKey() {
    return _key;
  }

  public boolean equals(RetrieveRequest req) {
    return ( (null != req)  && (null == req.getKey() ? null == _key : _key.equals(req.getKey())) );
  }

  public int hashCode() {
    return (null == _key ? -1 : _key.hashCode());
  }
}