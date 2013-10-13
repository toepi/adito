package com.adito.community.unix;

/**
 *
 * @author toepi <toepi@onlinehome.de>
 */
public interface PasswordChecker {

    /**
     * Check that a plaintext password matches a previously hashed one
     *
     * @param plaintext	the plaintext password to verify
     * @param hashed	the previously-hashed password
     * @return	true if the passwords match, false otherwise
     */
    boolean checkpw(String plaintext, String hashed);
}
