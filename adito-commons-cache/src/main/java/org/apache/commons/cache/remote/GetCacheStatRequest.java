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
import org.apache.commons.cache.CacheStat;

/**
 * tk.
 *
 * @version $Id: GetCacheStatRequest.java 155435 2005-02-26 13:17:27Z dirkv $
 * @author Rodney Waldhoff
 */
public class GetCacheStatRequest implements CacheRequest {
  protected CacheStat[] _stats = null;

  public GetCacheStatRequest(CacheStat stat) {
    this(new CacheStat[] { stat } );
  }

  public GetCacheStatRequest(CacheStat[] stats) {
    _stats = stats;
  }

  public CacheStat[] getCacheStats() {
    return _stats;
  }

  public boolean equals(GetCacheStatRequest req) {
    if(null == req) {
      return false;
    } else if(null == req.getCacheStats()) {
      return (null == _stats);
    } else if(null == _stats) {
      return false;
    } else if(req.getCacheStats().length != _stats.length) {
      return false;
    } else {
      for(int i=0;i<_stats.length;i++) {
        if(! (_stats[i] == null ? ( null == (req.getCacheStats())[i] ) : _stats[i].equals(req.getCacheStats()[i]) )) {
          return false;
        }
      }
      return true;
    }
  }

  public int hashCode() {
    if(null == _stats) {
      return this.getClass().getName().hashCode();
    } else {
      int hc = 0;
      for(int i=0;i<_stats.length;i++) {
        try {
          hc ^= _stats[i].hashCode();
        } catch(NullPointerException e) {
          // ignored
        }
      }
      return hc;
    }
  }
}