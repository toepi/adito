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
 * A base {@link RetrievalListener} implementation,
 * defining all methods as no-ops.
 *
 * @version $Id: BaseRetrievalListener.java 155435 2005-02-26 13:17:27Z dirkv $
 * @author Rodney Waldhoff
 */
public class BaseRetrievalListener implements RetrievalListener {
  public void retrieveRequested(Serializable key) { }
  public void retrieved(Serializable key) { }
  public void notRetrieved(Serializable key) { }
}