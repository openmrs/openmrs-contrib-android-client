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

package org.openmrs.mobile.net.volley.wrappers;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyLog;

import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class GZIPByteEncoder {
    public static final String RESPONSE_HEADER_PARAM = "Content-Encoding";
    public static final String REQUEST_HEADER_PARAM = "Accept-Encoding";
    public static final String GZIP_HEADER_VALUE = "gzip";
    private static final OpenMRSLogger LOGGER = OpenMRS.getInstance().getOpenMRSLogger();

    private GZIPByteEncoder() {
    }
    /**
     * Use this method to encode bytes
     * into compressed, GZIP form.
     * If You send encoded data to server,
     * be sure that it can read it.
     *
     * @param rawBytes
     * Input bytes to be compressed. Assertion: rawBytes.length > 0
     *
     * @return
     * Compressed byte array.
     */
    public static byte[] encodeByteArray(byte[] rawBytes) {
        if (0 == rawBytes.length) {
            throw new IllegalArgumentException("Input cannot be empty!");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            GZIPOutputStream gzos = new GZIPOutputStream(baos);
            gzos.write(rawBytes);
            gzos.close();
        } catch (IOException e) {
            VolleyLog.e("IOException writing to GZIPOutputStream :" + e.getMessage());
        }

        LOGGER.i("Before GZIP compression: " + rawBytes.length + " bytes, after: " + baos.toByteArray().length + " bytes.");
        return baos.toByteArray();

    }

    /**
     * Use this method to read compressed data.
     *
     * @param compressedBytes
     * Compressed byte array. Assertion: compressedBytes.length > 0
     *
     * @return
     * Decompressed byte array.
     */
    public static byte[] decodeByteArray(byte[] compressedBytes) {
        if (0 == compressedBytes.length) {
            throw new IllegalArgumentException("Input cannot be empty!");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(compressedBytes);
            GZIPInputStream gzip = new GZIPInputStream(bais);
            final int buffSize = 8192;
            byte[] tempBuffer = new byte[buffSize];

            int size;
            while ((size = gzip.read(tempBuffer, 0, buffSize)) != -1) {
                baos.write(tempBuffer, 0, size);
            }

            baos.close();
        } catch (Exception e) {
            VolleyLog.e("IOException reading from GZIPOutputStream :" + e.getMessage());
        }

        LOGGER.i("Before GZIP decompression: " + compressedBytes.length + " bytes, after: " + baos.toByteArray().length + " bytes.");
        return baos.toByteArray();
    }

    /**
     * Check if NetworkResponse object
     * contains header with is responsible
     * for tagging Gzipped messages.
     *
     *
     * @return
     * If message is compressed, returns
     * uncompressed byte array, if not, then
     * it returns simply NetworkResponse.data
     */
    public static NetworkResponse decompressGZIPResponse(NetworkResponse response) {

        /**
         * Status code 304 means that request did not changed since
         * last time, and android is not downloading response from the web,
         * but from it's local storage (caching) so we don't need to
         * decompress it (it's already done)
         */
        if (response.headers.containsKey(RESPONSE_HEADER_PARAM) && response.statusCode != 304) {
            return new NetworkResponse(
                    response.statusCode,
                    decodeByteArray(response.data),
                    response.headers,
                    response.notModified
            );

        } else {
            return response;
        }
    }
}
