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

import java.util.StringTokenizer;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Serializable;
import java.io.IOException;
import java.io.File;

/**
 * tk.
 * @version $Id: ShareableFileStash.java 155435 2005-02-26 13:17:27Z dirkv $
 * @author Rodney Waldhoff
 */
public class ShareableFileStash extends BaseStash implements Stash {
    protected int _numDirectories = 10;
    protected File _rootdir = null;
    protected int _maxFilenameLength = 128;

    /**
     * @param root the root directory to store objects in
     * @param numdirs the number of directories to create under root (must be >0)
     */
    public ShareableFileStash(String root, int numdirs) {
        _rootdir = new File(root);
        _numDirectories = numdirs;
    }

    /**
     * @param root the root directory to store objects in
     * @param numdirs the number of directories to create under root (must be >0)
     */
    public ShareableFileStash(File root, int numdirs) {
        _rootdir = root;
        _numDirectories = numdirs;
    }

    /**
     * @param root the root directory to store objects in
     * @param numdirs the number of directories to create under root (must be >0)
     * @param maxfilenamelength the maximum length filename to create
     */
    public ShareableFileStash(File root, int numdirs, int maxfilenamelength) {
        _rootdir = root;
        _numDirectories = numdirs;
        _maxFilenameLength = maxfilenamelength;
    }

    /**
     * @param root the root directory to store objects in
     * @param numdirs the number of directories to create under root (must be >0)
     * @param maxfilenamelength the maximum length filename to create
     */
    public ShareableFileStash(String root, int numdirs, int maxfilenamelength) {
        _rootdir = new File(root);
        _numDirectories = numdirs;
        _maxFilenameLength = maxfilenamelength;
    }

    protected byte[] getSerializedForm(Serializable val) {
        byte[] serform = null;
        if(null == val) {
            return null;
        }
        ByteArrayOutputStream byout = null;
        ObjectOutputStream out = null;
        try {
            byout = new ByteArrayOutputStream();
            out = new ObjectOutputStream(byout);
            out.writeObject(val);
            out.flush();
            serform = byout.toByteArray();
        } catch(IOException e) {
            serform = null;
        } finally {
            try {
                byout.close();
            } catch(Exception e) {
            }
            try {
                out.close();
            } catch(Exception e) {
            }
        }
        return serform;
    }

    protected synchronized Serializable readFromFile(File file) {
        ObjectInputStream oin = null;
        try {
            oin = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
            return(Serializable)(oin.readObject());
        } catch(Exception e) {
            return null;
        } finally {
            try {
                oin.close();
            } catch(Exception e) {
            }
        }
    }

    protected File getFile(Serializable key, boolean mkdirs) {
        String keystr = key.toString();
        char[] chars = keystr.toCharArray();
        StringBuffer buf = new StringBuffer();
        File parent = new File(_rootdir,String.valueOf(Math.abs(keystr.hashCode()%_numDirectories)));
        for(int i=0;i<chars.length;i++) {
            if((chars[i] >= 'a' && chars[i] <= 'z') ||
               (chars[i] >= 'A' && chars[i] <= 'Z') ||
               (chars[i] >= '0' && chars[i] <= '9')) {
                if(buf.length() >= _maxFilenameLength) {
                    buf.append("_");
                    parent = new File(parent,buf.toString());
                    buf.setLength(0);
                }
                buf.append(chars[i]);
            } else {
                if(buf.length() + 5 >= _maxFilenameLength) {
                    buf.append("_");
                    parent = new File(parent,buf.toString());
                    buf.setLength(0);
                }
                buf.append("_");
                buf.append(Integer.toHexString((int)chars[i]));
            }
        }
        if(buf.length() == 0) {
            buf.append("_");
        }
        if(mkdirs) {
            parent.mkdirs();
        }
        return new File(parent,buf.toString());
    }

    public int canStore(Serializable key, Serializable val, Long expiresAt, Long cost, Serializable group, byte[] serialized) {
        if(null == serialized) {
            serialized = getSerializedForm(val);
        }
        if(null == serialized) {
            return Stash.NO;
        } else {
            return Stash.YES;
        }
    }

    public boolean store(Serializable key, Serializable val, Long expiresAt, Long cost, Serializable group, byte[] serialized) {
        if(null == serialized) {
            serialized = getSerializedForm(val);
        }
        if(null == serialized) {
            return false;
        }

        File cachefile = getFile(key,true);
        if(null == cachefile) {
            return false;
        }

        BufferedOutputStream fout = null;
        try {
            fout = new BufferedOutputStream(new FileOutputStream(cachefile));
            fout.write(serialized);
            fout.flush();
        } catch(Exception e) {
            try {
                fout.close();
            } catch(Exception ex) {
            }
            fout = null;
            try {
                cachefile.delete();
            } catch(Exception ex) {
            }
            return false;
        } finally {
            try {
                fout.close();
            } catch(Exception e) {
            }
        }
        return true;
    }

    public Serializable retrieve(Serializable key) {
        File cachefile = getFile(key,false);
        if(cachefile.exists()) {
            return readFromFile(cachefile);
        } else {
            return null;
        }
    }

    public boolean contains(Serializable key) {
        File cachefile = getFile(key,false);
        return cachefile.exists();
    }

    public float capacity() {
        return 0;
    }

    public void clear(Serializable key) {
        File cachefile = getFile(key,false);
        cachefile.delete();
    }

    public synchronized void clear() {
        File f = getMoveToLoc();
        try {
            f.getParentFile().mkdirs();
        } catch(NullPointerException e) {
            // ignored
        }
        _rootdir.renameTo(f);
        _rootdir.mkdirs();
        Thread t = new Thread(new RecursiveFileDeleter(f));
        t.start();
    }

    public boolean wantsSerializedForm() {
        return true;
    }

    public void unsetCache() {
    }

    public void setCache(Cache c) {
    }

    protected File getMoveToLoc() {
        File parent = _rootdir.getAbsoluteFile().getParentFile();
        for(int i=0;i<100;i++) {
            String fname = String.valueOf(System.currentTimeMillis() % 100000000L);
            File f = new File(parent,fname);
            if(!f.exists()) {
                return f;
            }
            try {
                Thread.currentThread().sleep(17);
            } catch(Exception e) {
                // ignored
            }
        }
        throw new RuntimeException("Couldn't create a temp file to move the shareable file stash to.");
    }
}

class RecursiveFileDeleter implements Runnable {
    private File _root = null;

    public RecursiveFileDeleter(File root) {
        _root = root;
    }

    public void run() {
        boolean success = recursiveDelete(_root);
        if(!success) {
            System.err.println("Unable to fully delete the file at \"" + _root + "\". Please delete it manually.");
        }
    }

    boolean recursiveDelete(File f) {
        if(f.isDirectory()) {
            File[] files = f.listFiles();
            for(int i=0;i<files.length;i++) {
                if(files[i].isFile()) {
                    if(!files[i].delete()) {
                        return false;
                    }
                } else {
                    if(!recursiveDelete(files[i])) {
                        return false;
                    }
                }
            }
        }
        return f.delete();
    }
}



