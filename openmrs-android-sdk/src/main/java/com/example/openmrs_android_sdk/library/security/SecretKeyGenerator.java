/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package com.example.openmrs_android_sdk.library.security;


import com.example.openmrs_android_sdk.library.OpenmrsAndroid;

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
            OpenmrsAndroid.getOpenMRSLogger().d("Failed to generate DB secret key" + e.toString());
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
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            if ((data[i] & 0xFF) < 16) {
                stringBuilder.append('0');
                stringBuilder.append(java.lang.Integer.toHexString(data[i] & 0xFF));
            } else {
                stringBuilder.append(java.lang.Integer.toHexString(data[i] & 0xFF));
            }
        }
        return stringBuilder.toString();
    }
}
