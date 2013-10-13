package com.adito.community.unix;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.digest.Crypt;

/**
 *
 * @author toepi <toepi@onlinehome.de>
 */
class DefaultPasswordChecker implements PasswordChecker {

    private final static Pattern SALT_PATTERN = Pattern.compile("^\\$[1256]\\$.*?\\$");

    public boolean checkpw(final String plaintext, final String hashed) {
        final Matcher matcher = SALT_PATTERN.matcher(hashed);
        final boolean result;
        if (matcher.find()) {
            result = hashed.equals(Crypt.crypt(plaintext, matcher.group()));
        } else {
            result = false;
        }
        return result;
    }
}
