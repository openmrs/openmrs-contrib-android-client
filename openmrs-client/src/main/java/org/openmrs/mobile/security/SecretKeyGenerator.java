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

package org.openmrs.mobile.security;


import android.util.Log;

import org.openmrs.mobile.application.OpenMRS;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class SecretKeyGenerator {

    private SecretKeyGenerator() {
    }

    public static String generateKey() {
        String usernamePasswordStr = OpenMRS.getInstance().getUsername() + OpenMRS.getInstance().getPassword();
        String timeStampStr = String.valueOf(System.currentTimeMillis());
        return sha256(timeStampStr + usernamePasswordStr);
    }

    public static String digest(String s, String algorithm) {
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return s;
        }

        try {
            m.update(s.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            m.update(s.getBytes());
        }
        return bytesToHex(m.digest());
    }

    public static String sha256(String s) {
        Log.e("HASH: ", digest(s, "SHA-256"));
        return digest(s, "SHA-256");
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
