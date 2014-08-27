package org.openmrs.client.security;


import org.openmrs.client.application.OpenMRS;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public final class SecretKeyGenerator {

    private SecretKeyGenerator() {
    }

    public static String generateKey() {
        // Generate a 256-bit key
        final int outputKeyLength = 256;
        KeyGenerator keyGenerator = null;
        SecureRandom secureRandom = null;
        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
            // Do *not* seed secureRandom! Automatically seeded from system entropy.
            keyGenerator = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            OpenMRS.getInstance().getOpenMRSLogger().d("Failed to generate DB secret key" + e.toString());
        }
        keyGenerator.init(outputKeyLength, secureRandom);
        SecretKey key = keyGenerator.generateKey();
        return bytesToHex(key.getEncoded());
    }

    private static String bytesToHex(byte[] data) {
        if (data == null) {
            return null;
        }

        int len = data.length;
        String str = "";
        for (int i = 0; i < len; i++) {
            if ((data[i] & 0xFF) < 16) {
                str = str + "0" + java.lang.Integer.toHexString(data[i] & 0xFF);
            } else {
                str = str + java.lang.Integer.toHexString(data[i] & 0xFF);
            }
        }
        return str;
    }
}
