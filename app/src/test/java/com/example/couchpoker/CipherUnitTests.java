package com.example.couchpoker;

import com.example.couchpoker.security.Security;

import org.junit.Test;

import java.util.Base64;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CipherUnitTests {
    @Test
    public void addition_isCorrect() {
        String textToPass = "sampleText123";

        String encryptedData = Security.encrypt_data(textToPass);
        String decryptedData = Security.decrypt_data(encryptedData);


        assertEquals(textToPass, decryptedData);
    }
}