package com.example.couchpoker.security;

import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Security {
    private static String encryptionKey = "AXkQDhrsPyrMqmvEMu3bZd4HISRCJ8KG";

    //code from: https://stackoverflow.com/questions/22034269/encryption-decryption-using-aes-ecb-nopadding

    public static String decrypt_data(String encData) {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(encryptionKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.DECRYPT_MODE, skeySpec);

            byte[] original = cipher
                    .doFinal(Base64.decode(encData.getBytes(), 0));
            return new String(original).trim();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

    public static String encrypt_data(String data) {
        try{
        SecretKeySpec skeySpec = new SecretKeySpec(encryptionKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");

        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

        byte[] original = Base64.encode(cipher.doFinal(data.getBytes()), 0);
        return new String(original);
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }
}
