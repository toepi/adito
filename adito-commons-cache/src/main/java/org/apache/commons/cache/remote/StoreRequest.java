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
 * @version $Id: StoreRequest.java 155435 2005-02-26 13:17:27Z dirkv $
 * @author Rodney Waldhoff
 */
public class StoreRequest implements CacheRequest {
  protected Serializable _key = null;
  protected Serializable _val = null;
  protected Serializable _group = null;
  protected Long _expiry = null;
  protected Long _cost = null;

  public StoreRequest(Serializable key, Serializable val, Long expiry) {
    this(key,val,expiry,(Long)null,(Serializable)null);
  }

  public StoreRequest(Serializable key, Serializable val, Long expiry, Long cost) {
    this(key,val,expiry,cost,(Serializable)null);
  }

  public StoreRequest(Serializable key, Serializable val, Long expiry, Long cost, Serializable group) {
    _key = key;
    _val = val;
    _expiry = expiry;
    _cost = cost;
    _group = group;
  }

  public Serializable getKey() {
    return _key;
  }

  public Serializable getValue() {
    return _val;
  }

  public Serializable getGroup() {
    return _group;
  }

  public Long getExpiresAt() {
    return _expiry;
  }

  public Long getCost() {
    return _cost;
  }

  public boolean equals(StoreRequest req) {
    return ( (null != req)
             && (null == req.getKey()       ? null == _key    : _key.equals(req.getKey()))
             && (null == req.getGroup()     ? null == _group  : _group.equals(req.getGroup()))
             && (null == req.getExpiresAt() ? null == _expiry : _expiry.equals(req.getExpiresAt()))
             && (null == req.getCost()      ? null == _cost   : _cost.equals(req.getCost()))
             && (null == req.getValue()     ? null == _val    : _val.equals(req.getValue()))
           );
  }

  public int hashCode() {
    return (null == _key ? -1 : _key.hashCode());
  }
}