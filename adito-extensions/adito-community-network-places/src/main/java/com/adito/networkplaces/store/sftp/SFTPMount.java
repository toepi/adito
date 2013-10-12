package com.adito.networkplaces.store.sftp;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

import com.adito.networkplaces.AbstractNetworkPlaceMount;
import com.adito.policyframework.LaunchSession;
import com.adito.security.PasswordCredentials;
import com.adito.vfs.VFSStore;
import com.adito.vfs.utils.URI;
import com.adito.vfs.webdav.DAVAuthenticationRequiredException;
import com.adito.vfs.webdav.DAVUtilities;

public class SFTPMount extends AbstractNetworkPlaceMount {

    final static Log log = LogFactory.getLog(SFTPMount.class);

    public SFTPMount(LaunchSession launchSession, VFSStore store) {
        super(launchSession, store);
    }

    public FileSystemOptions getOptions(URI uri) throws FileSystemException {
        FileSystemOptions options = new FileSystemOptions();
        SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(options, "no");
        return options;
    }

    public FileObject createVFSFileObject(String path, PasswordCredentials credentials) throws IOException {
        try {
            URI uri = getRootVFSURI();
            if (credentials != null) {
                uri.setUserinfo(DAVUtilities.encodeURIUserInfo(credentials.getUsername() + (credentials.getPassword() != null ? ":" + new String(credentials.getPassword()) : "")));
            }
            log.info("Sftp Path here" + uri.toString());
            uri.setPath(uri.getPath() + (uri.getPath().endsWith("/") ? "" : "/") + DAVUtilities.encodePath(path));
            FileObject fileObject = this.getStore().getRepository().getFileSystemManager().resolveFile(uri.toString(), getOptions(uri));
            return fileObject;
        } catch (FileSystemException fse) {
            if (fse.getCode().equals("vfs.provider.ftp/connect.error")) {
                throw new DAVAuthenticationRequiredException(getMountString());
            }
            throw fse;
        }
    }
}
