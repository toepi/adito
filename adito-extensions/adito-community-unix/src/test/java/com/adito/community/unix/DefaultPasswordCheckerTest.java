package com.adito.community.unix;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author toepi <toepi@onlinehome.de>
 */
public class DefaultPasswordCheckerTest {

    private DefaultPasswordChecker instance;

    @Before
    public void initPasswordChecker() {
        instance = new DefaultPasswordChecker();
    }

    @Test
    public void checkSHA256Password() {
        String plaintext = "test123";
        String hashed = "$5$Ng8BUiQh$pAf54v.vOosw/befiT0ax/7bMFwOJ7I3GbsQsJg6Uc9";
        assertTrue(instance.checkpw(plaintext, hashed));
    }

    @Test
    public void checkSHA512Password() {
        String plaintext = "test123";
        String hashed = "$6$TJwuT344$NRaRtD2SoAbqwZq.h0At1MpvGM.JyINT5SxcgqxEIqhjnyLZQ0Au0ifpGMwHuDmxBBJHBh5HmvpYdFpASylAv0";
        assertTrue(instance.checkpw(plaintext, hashed));
    }

    @Test
    public void checkInvalidHash() {
        assertFalse(instance.checkpw("test", "asafefsdasd"));
    }
}
