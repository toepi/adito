package com.adito.boot;

/**
 *
 * @author toepi <toepi@users.noreply.github.com>
 */
public interface AditoServerFactory {

    void createServer(ClassLoader bootLoader, String[] args);
}
