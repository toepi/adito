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

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import java.io.*;
import org.apache.commons.cache.*;
import org.apache.commons.jocl.JOCLContentHandler;

/**
 * ...tk...
 * @version $Id: CacheTag.java 155435 2005-02-26 13:17:27Z dirkv $
 * @author Rodney Waldhoff
 */
public class CacheTag extends BodyTagSupport {

    protected Serializable _key = null;
    protected Serializable _group = null;
    protected Long _expiresAt = null;
    protected static Cache _cache = null;
    protected String _content = null;
    protected boolean _mustRevalidate = false;
    protected String _name = null;

    @Override
    public void release() {
        super.release();
        _key = null;
        _group = null;
        _name = null;
        _expiresAt = null;
        _content = null;
        _mustRevalidate = false;
    }

    public void setMustRevalidate(String obj) {
        _mustRevalidate = Boolean.valueOf(obj).booleanValue();
    }

    public void setMustRevalidate(boolean val) {
        _mustRevalidate = val;
    }

    public void setName(String name) {
        _name = name;
    }

    public void setGroup(String group) {
        _group = group;
    }

    public void setKey(String key) {
        _key = key;
    }

    public void setTtl(String ttl) {
        if (ttl != null) {
            if (ttl.endsWith("s") || ttl.endsWith("S")) {
                setTtl(1000L * Long.parseLong(ttl.substring(0, ttl.length() - 1)));
            } else if (ttl.endsWith("m") || ttl.endsWith("M")) {
                setTtl(60L * 1000L * Long.parseLong(ttl.substring(0, ttl.length() - 1)));
            } else if (ttl.endsWith("h") || ttl.endsWith("H")) {
                setTtl(60L * 60L * 1000L * Long.parseLong(ttl.substring(0, ttl.length() - 1)));
            } else if (ttl.endsWith("d") || ttl.endsWith("D")) {
                setTtl(24L * 60L * 60L * 1000L * Long.parseLong(ttl.substring(0, ttl.length() - 1)));
            } else {
                setTtl(1000L * Long.parseLong(ttl));
            }
        }
    }

    private void setTtl(long ttl) {
        _expiresAt = new Long(System.currentTimeMillis() + ttl);
    }

    private void setTtl(Long ttl) {
        if (null == ttl) {
            _expiresAt = null;
        } else {
            _expiresAt = new Long(System.currentTimeMillis() + ttl.longValue());
        }
    }

    public void setLastModified(String lastmodstr) {
        long lastmod = 0L;
        try {
            lastmod = Long.parseLong(lastmodstr);
            _expiresAt = new Long(System.currentTimeMillis() + ((System.currentTimeMillis() - lastmod) / 2));
        } catch (Exception e) {
            // do something?
            _expiresAt = null;
        }
    }

    public void setExpiresAt(String expires) {
        try {
            _expiresAt = new Long(Long.parseLong(expires));
        } catch (Exception e) {
            // do something?
            _expiresAt = null;
        }
    }

    @Override
    public int doStartTag() throws JspException {
        if (!_mustRevalidate) {
            try {
                Object obj = getCache(_name).retrieve(_key);
                if (null == obj) {
                    return EVAL_BODY_AGAIN;
                } else {
                    _content = obj.toString();
                    return SKIP_BODY;
                }
            } catch (Exception e) {
                throw new JspException(e.toString());
            }
        } else {
            getCache(_name).clear(_key);
            return EVAL_BODY_AGAIN;
        }
    }

    @Override
    public int doAfterBody() throws JspException {
        _content = getBodyContent().getString();
        getCache(_name).store(_key, _content, _expiresAt, null, _group);
        return SKIP_BODY;
    }

    @Override
    public int doEndTag() throws JspException {
        try {
            pageContext.getOut().write(_content);
        } catch (Exception e) {
            throw new JspException(e.toString());
        }
        return EVAL_PAGE;
    }
    private static final CacheTag _instance = new CacheTag(); // used in getCache

    public synchronized static Cache getCache(String name) throws JspException {
        if (CacheSingleton.hasCache(name)) {
            return CacheSingleton.getCache(name);
        } else {
            Cache cache = null;

            String serfile = System.getProperty("org.apache.commons.cache.tagext.CacheTag.cache." + name + ".serialized-cache-file");
            if (null != serfile) {
                try {
                    cache = SimpleCache.readFromFile(serfile);
                } catch (Exception e) {
                    cache = null;
                }
            }

            if (null != cache) {
                CacheSingleton.putCache(name, cache);
                return cache;
            } else {
                String config = System.getProperty("org.apache.commons.cache.tagext.CacheTag.cache." + name + ".configuration");
                if (null == config) {
                    throw new JspException("Cache \"" + name + "\" not found.");
                } else {
                    try {
                        JOCLContentHandler jocl = JOCLContentHandler.parse(_instance.getClass().getClassLoader().getResourceAsStream(config));
                        cache = (Cache) (jocl.getValue(0));
                        CacheSingleton.putCache(name, cache);
                        return cache;
                    } catch (Exception e) {
                        throw new JspException(e.toString());
                    }
                }
            }
        }
    }
}