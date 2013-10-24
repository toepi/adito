/*
 *  Adito
 *
 *  Copyright (C) 2003-2006 3SP LTD. All Rights Reserved
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package com.adito.server;

import com.adito.boot.ContextHolder;
import com.adito.boot.ContextKey;
import com.adito.boot.KeyStoreManager;
import com.adito.boot.PropertyClass;
import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.X509KeyManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of an {@link javax.net.ssl.X509KeyManager} that uses the Adito
 * keystore and the <b>Active Certifice Name</b>
 * configured in the property database to determine the alias to load as the SSL
 * Certificate.
 */
public class CustomKeyManager implements X509KeyManager {

    private static final Log LOG = LogFactory.getLog(CustomKeyManager.class);
    private final PropertyClass contextConfig;

    public CustomKeyManager() {
        contextConfig = ContextHolder.getContext().getConfig();
    }

    public String chooseClientAlias(String[] arg0, Principal[] arg1, Socket socket) {
        return null;
    }

    public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
        String alias = ContextHolder.getContext().getConfig().retrieveProperty(new ContextKey("webServer.alias"));
        return alias;
    }

    public X509Certificate[] getCertificateChain(String certname) {
        try {
            Certificate[] certChain = KeyStoreManager.getInstance(KeyStoreManager.DEFAULT_KEY_STORE)
                    .getCertificateChain(certname);
            List<X509Certificate> result = new ArrayList();
            for (Certificate curCert : certChain) {
                if (curCert instanceof X509Certificate) {
                    result.add((X509Certificate) curCert);
                }
            }
            return result.toArray(new X509Certificate[result.size()]);
        } catch (Exception e) {
            LOG.error(e);
        }
        return null;
    }

    public String[] getClientAliases(String keyType, Principal[] issuers) {
        String str[] = {""};
        return str;
    }

    public PrivateKey getPrivateKey(String alias) {
        try {
            return KeyStoreManager.getInstance(KeyStoreManager.DEFAULT_KEY_STORE)
                    .getPrivateKey(
                            retrieveProperty("webServer.alias"),
                            retrieveProperty("webServer.keystore.sslCertificate.password").toCharArray()
                    );
        } catch (IllegalArgumentException e) {
            LOG.error(e);
        }
        return null;
    }

    public String[] getServerAliases(String keyType, Principal[] issuers) {
        String str[] = {retrieveProperty("webServer.alias")};
        return str;
    }

    private String retrieveProperty(final String name) throws IllegalArgumentException {
        return contextConfig.retrieveProperty(new ContextKey(name));
    }
}
