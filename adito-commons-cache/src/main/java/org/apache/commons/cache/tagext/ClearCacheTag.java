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
package org.apache.commons.cache.tagext;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.Serializable;

/**
 * tk.
 * @version $Id: ClearCacheTag.java 155435 2005-02-26 13:17:27Z dirkv $
 * @author Rodney Waldhoff
 */
public class ClearCacheTag extends TagSupport {

    protected Serializable _key = null;
    protected Serializable _group = null;
    protected String _name = null;

    public void setName(String name) {
        _name = name;
    }

    public void setKey(String key) {
        _key = key;
    }

// weblogic reflection doesn't work very well
/*
    public void setKey(Serializable key) {
    _key = key;
    }
     */
    public void setGroup(String group) {
        _group = group;
    }

// weblogic reflection doesn't work very well
/*
    public void setGroup(Serializable group) {
    _group = group;
    }
     */
    public ClearCacheTag() {
        _group = null;
        _key = null;
    }

    @Override
    public int doStartTag() {
        return SKIP_BODY;
    }

    @Override
    public int doEndTag() throws JspException {
        if (null != _key) {
            CacheTag.getCache(_name).clear(_key);
        } else if (null != _group) {
            CacheTag.getCache(_name).clearGroup(_group);
        } else {
            CacheTag.getCache(_name).clear();
        }
        return EVAL_PAGE;
    }

    @Override
    public void release() {
        _group = null;
        _key = null;
        _name = null;
    }
}