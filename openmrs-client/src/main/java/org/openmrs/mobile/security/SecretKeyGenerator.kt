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
package org.openmrs.mobile.security

import org.openmrs.mobile.application.OpenMRS
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import javax.crypto.KeyGenerator
import kotlin.experimental.and

object SecretKeyGenerator {
    fun generateKey(): String? {
        // Generate a 256-bit key
        val outputKeyLength = 256
        var keyGenerator: KeyGenerator? = null
        var secureRandom: SecureRandom? = null
        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG")
            // Do *not* seed secureRandom! Automatically seeded from system entropy.
            keyGenerator = KeyGenerator.getInstance("AES")
        } catch (e: NoSuchAlgorithmException) {
            OpenMRS.getInstance().openMRSLogger.d("Failed to generate DB secret key$e")
        }
        keyGenerator!!.init(outputKeyLength, secureRandom)
        val key = keyGenerator.generateKey()
        return bytesToHex(key.encoded)
    }

    private fun bytesToHex(data: ByteArray?): String? {
        if (data == null) {
            return null
        }
        val len = data.size
        val stringBuilder = StringBuilder()
        for (i in 0 until len) {
            if (data[i] and 0xFF.toByte() < 16) {
                stringBuilder.append('0')
                stringBuilder.append(Integer.toHexString((data[i] and 0xFF.toByte()).toInt()))
            } else {
                stringBuilder.append(Integer.toHexString((data[i] and 0xFF.toByte()).toInt()))
            }
        }
        return stringBuilder.toString()
    }
}