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
package com.adito.community.unix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.ContextHolder;
import com.adito.boot.SystemProperties;
import com.adito.boot.Util;
import com.adito.core.CoreServlet;
import com.adito.realms.Realm;
import com.adito.security.AccountLockedException;
import com.adito.security.DefaultUserDatabase;
import com.adito.security.InvalidLoginCredentialsException;
import com.adito.security.User;
import com.adito.security.UserDatabaseException;
import com.adito.security.UserNotFoundException;

public class UNIXUserDatabase extends DefaultUserDatabase {

    private static final Log LOG = LogFactory.getLog(UNIXUserDatabase.class);
    private static final File GROUP_FILE = new File("/etc/group");
    private static final File PASSWD_FILE = new File("/etc/passwd");
    private static final File SHADOW_FILE = new File("/etc/shadow");
    private static final File USER_EMAIL_MAP_FILE = new File(ContextHolder.getContext().getConfDirectory(), "userEmailMap.properties");

    private UNIXRole[] roles;
    private UNIXUser[] users;
    private Map<String, char[]> shadowPasswords;
    private Date lastGroupFileChange;
    private Date lastPasswdFileChange;
    private Date lastShadowFileChange;
    private Properties userEmailMap = new Properties();
    private long userEmailMapLastModified = -1;

    /**
     * Constant for the database type.
     */
    public static final String DATABASE_TYPE = "unixAuth";

    public UNIXUserDatabase() {
        super("Unix", false, false);
    }

    @Override
    public void open(CoreServlet controllingServlet, Realm realm) {
        String osName = SystemProperties.get("os.name", "").toLowerCase();
        if (!osName.startsWith("linux") && !osName.startsWith("solaris")) {
            LOG.warn("The UNIXAuth plugin will only be likely to work on Linux based systems, Solaris or other operating systems "
                    + "that use /etc/passwd, /etc/group and /etc/shadow. OpenBSD and FreeBSD will definately *not* work.");
        }
        open = true;
        if (SystemProperties.get("adito.unix.passwordChange", "false").equals("true")) {
            if (new File("/usr/sbin/chpasswd").exists()) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Found chpasswd, enabling experimental password change support.");
                }
                supportsPasswordChange = true;
            }
        }
        this.realm = realm;
    }

    public User logon(String username, String password) throws UserDatabaseException, InvalidLoginCredentialsException,
            AccountLockedException {
        if (!checkPassword(username, password)) {
            throw new InvalidLoginCredentialsException();
        }
        try {
            return getAccount(username);
        } catch (Exception e) {
            throw new UserDatabaseException("Failed to get user account.", e);
        }
    }

    public boolean checkPassword(String username, String password) throws UserDatabaseException, InvalidLoginCredentialsException {
        // Get the user account
        UNIXUser user = null;
        try {
            user = getAccount(username);
        } catch (Exception e) {
            throw new UserDatabaseException("Could not get user account", e);
        }

        // Make sure the user exists
        if (user == null) {
            throw new InvalidLoginCredentialsException();
        }

        // Determine the password type
        String pw = new String(user.getPassword());
        try {
            if (pw.startsWith("$1$")) {
                // MD5
                return pw.substring(12).equals(MD5Crypt.crypt(password, pw.substring(3, 11)).substring(12));
            } else if (pw.startsWith("$2a$")) {
                // Blowfish
                return BCrypt.checkpw(password, pw);
            } else {
                // DES
                return DESCrypt.crypt(pw.substring(0, 2), password).equals(pw.substring(2));
            }
        } catch (Exception e) {
            throw new UserDatabaseException("Invalid password format.", e);
        }
    }

    public void logout(User user) {
    }

    @SuppressWarnings("unchecked")
    public Iterable<UNIXUser> allUsers() throws UserDatabaseException {
        try {
            checkPasswdFile();
        } catch (IOException e) {
            throw new UserDatabaseException("failed to list all users", e);
        }
        return Arrays.asList(users);
    }

    public UNIXUser getAccount(String username) throws UserNotFoundException, Exception {
        try {
            checkPasswdFile();
            for (UNIXUser user : users) {
                if (user.getPrincipalName().equals(username)) {
                    return user;
                }
            }
            throw new UserNotFoundException("Could not find user " + username);
        } catch (IOException e) {
            throw e;
        }
    }

    public UNIXRole getRole(String rolename) throws Exception {
        checkGroupFile();
        for (UNIXRole role : roles) {
            if (role.getPrincipalName().equals(rolename)) {
                return role;
            }
        }
        return null;
    }

    public Iterable<UNIXRole> allRoles() throws UserDatabaseException {
        try {
            checkGroupFile();
        } catch (IOException e) {
            throw new UserDatabaseException("failed to list all roles", e);
        }
        return Arrays.asList(roles);
    }

    private void checkGroupFile() throws IOException {
        if (GROUP_FILE.exists()) {
            final Date current = new Date(GROUP_FILE.lastModified());
            if (lastGroupFileChange == null || !lastGroupFileChange.equals(current)) {
                lastGroupFileChange = current;
                FileInputStream fin = new FileInputStream(GROUP_FILE);
                List<UNIXRole> rolesList = new ArrayList<UNIXRole>();
                try {
                    String line;
                    BufferedReader r = new BufferedReader(new InputStreamReader(fin));
                    while ((line = r.readLine()) != null) {
                        try {
                            rolesList.add(new UNIXRole(getRealm(), line));
                        } catch (IllegalArgumentException iae) {
                        }
                    }
                } finally {
                    Util.closeStream(fin);
                }
                Collections.sort(rolesList);
                roles = new UNIXRole[rolesList.size()];
                rolesList.toArray(roles);
            }
        } else {
            throw new IOException("Could not locate " + GROUP_FILE.getAbsolutePath());
        }
    }

    private void checkPasswdFile() throws IOException {
        if (PASSWD_FILE.exists()) {
            if (checkShadowFile()) {
                lastPasswdFileChange = null;
            }
            if (checkUserEmailMapFile()) {
                lastPasswdFileChange = null;
            }
            final Date current = new Date(PASSWD_FILE.lastModified());
            if (lastPasswdFileChange == null || !lastPasswdFileChange.equals(current)) {
                lastPasswdFileChange = current;
                FileInputStream fin = new FileInputStream(PASSWD_FILE);
                List<User> userList = new ArrayList<User>();
                try {
                    BufferedReader r = new BufferedReader(new InputStreamReader(fin));
                    String line;
                    while ((line = r.readLine()) != null) {
                        String[] elements = line.split(":");
                        String username = elements[0];
                        if (elements.length > 5) {
                            String password = elements[1];
                            int uid = Integer.parseInt(elements[2]);
                            int gid = Integer.parseInt(elements[3]);
                            String fullname = elements[4];
                            String home = elements[5];
                            String shell = "";
                            if (elements.length > 6) {
                                shell = elements[6];
                            }
                            List<UNIXRole> userRolesList = new ArrayList<UNIXRole>();
                            UNIXRole primaryRole = getRoleByGID(gid);
                            if (primaryRole == null) {
                                LOG.warn("No primary group for user " + username);
                            } else {
                                userRolesList.add(primaryRole);
                            }
                            for (UNIXRole role : roles) {
                                if (role.containsMember(username)
                                        && !(primaryRole != null
                                        && role.getPrincipalName().equals(primaryRole.getPrincipalName()))) {
                                    userRolesList.add(role);
                                }
                            }
                            UNIXRole[] userRoles = new UNIXRole[userRolesList.size()];
                            userRolesList.toArray(userRoles);
                            final char[] pw;
                            if (password.equals("x")) {
                                pw = (char[]) shadowPasswords.get(username);
                                if (pw == null) {
                                    // No shadow password, continue to the next user
                                    LOG.warn("User " + username + " has 'x' as password indicating a shadow password. However, "
                                            + "either the shadow file does not exist or an entry for this user "
                                            + "does not exist. User has been omitted");
                                    continue;
                                }
                            } else {
                                pw = password.toCharArray();
                            }
                            UNIXUser user = new UNIXUser(username, userEmailMap == null ? "" : userEmailMap.getProperty(username,""),
                                    pw, uid, gid, fullname, home, shell, userRoles, this.getRealm());
                            userList.add(user);
                        }
                    }
                } finally {
                    Util.closeStream(fin);
                }
                Collections.sort(userList);
                users = new UNIXUser[userList.size()];
                userList.toArray(users);
            }
        } else {
            throw new IOException("Could not locate " + PASSWD_FILE.getAbsolutePath());
        }
    }

    private synchronized boolean checkShadowFile() throws IOException {
        shadowPasswords = new HashMap<String, char[]>();
        if (SHADOW_FILE.exists()) {
            final Date current = new Date(SHADOW_FILE.lastModified());
            if (lastShadowFileChange == null || !lastShadowFileChange.equals(current)) {
                lastShadowFileChange = current;
                FileInputStream fin = new FileInputStream(SHADOW_FILE);
                try {
                    BufferedReader r = new BufferedReader(new InputStreamReader(fin));
                    String line;
                    while ((line = r.readLine()) != null) {
                        String[] elements = line.split(":");
                        String username = elements[0];
                        if (elements.length > 1 && !username.equals("+")) {
                            char[] password = elements[1].toCharArray();
                            shadowPasswords.put(username, password);
                        }
                    }
                } finally {
                    Util.closeStream(fin);
                }
                return true;
            }
        } else {
            // The shadow did exist but does now not - unlikely to happen!
            if (lastShadowFileChange != null) {
                return true;
            }
        }
        return false;
    }

    private synchronized boolean checkUserEmailMapFile() {
        if (!USER_EMAIL_MAP_FILE.exists()) {
            if (userEmailMap != null) {
                userEmailMap = null;
                userEmailMapLastModified = -1;
                return true;
            }
        } else if (userEmailMap == null) {
            userEmailMap = new Properties();
        }
        if (userEmailMap != null
                && (userEmailMapLastModified == -1 || userEmailMapLastModified != USER_EMAIL_MAP_FILE.lastModified())) {
            FileInputStream fin = null;
            try {
                fin = new FileInputStream(USER_EMAIL_MAP_FILE);
                userEmailMap.load(fin);
            } catch (IOException ioe) {
                LOG.error("Failed to load user email map.");
            } finally {
                Util.closeStream(fin);
            }
            userEmailMapLastModified = USER_EMAIL_MAP_FILE.lastModified();
            return true;
        }
        return false;
    }

    private UNIXRole getRoleByGID(int gid) throws IOException {
        checkGroupFile();
        for (UNIXRole role : roles) {
            if (role.getGid() == gid) {
                return role;
            }
        }
        return null;
    }

    @Override
    public void cleanup() throws Exception {
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public void changePassword(String username, String oldPassword, String password, boolean forcePasswordChangeAtLogon)
            throws UserDatabaseException, InvalidLoginCredentialsException {
        if (!supportsPasswordChange()) {
            throw new InvalidLoginCredentialsException("Database doesn't support password change.");
        }
        if (forcePasswordChangeAtLogon) {
            LOG.warn("Password change function of UNIX user database does not support forcePassswordChangeAtLogon.");
        }
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(
                    "true".equals(SystemProperties.get("adito.useDevConfig", "false")) ? "sudo /usr/sbin/chpasswd"
                    : "/usr/sbin/chpasswd");
            OutputStream out = p.getOutputStream();
            PrintWriter pw = new PrintWriter(out);
            pw.println(username + ":" + password);
            pw.flush();
            out.close();
            try {
                p.waitFor();
            } catch (InterruptedException ie) {

            }
            int ret = p.exitValue();
            if (ret != 0) {
                throw new UserDatabaseException("Failed to change password. chpasswd returned exit code " + ret + ".");
            }

        } catch (IOException e) {
            throw new UserDatabaseException("Failed to change password.", e);
        } finally {
            if (p != null) {
                Util.closeStream(p.getOutputStream());
                Util.closeStream(p.getInputStream());
                Util.closeStream(p.getErrorStream());
            }
        }
    }
}
