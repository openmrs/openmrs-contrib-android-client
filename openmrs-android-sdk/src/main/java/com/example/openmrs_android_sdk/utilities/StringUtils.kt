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
package com.example.openmrs_android_sdk.utilities

object StringUtils {
    private const val NULL_AS_STRING = "null"
    private const val SPACE_CHAR = " "

    @JvmStatic
    fun notNull(string: String?): Boolean {
        return null != string && NULL_AS_STRING != string.trim { it <= ' ' }
    }

    @JvmStatic
    fun isBlank(string: String?): Boolean {
        return null == string || SPACE_CHAR == string
    }

    @JvmStatic
    fun notEmpty(string: String?): Boolean {
        return string != null && !string.isEmpty()
    }

    @JvmStatic
    fun unescapeJavaString(st: String): String {
        val sb = StringBuilder(st.length)
        var i = 0
        loop@ while (i < st.length) {
            var ch = st[i]
            if (ch == '\\') {
                val nextChar = if (i == st.length - 1) '\\' else st[i + 1]
                // Octal escape?
                if (nextChar >= '0' && nextChar <= '7') {
                    var code = "" + nextChar
                    i++
                    if (i < st.length - 1 && st[i + 1] >= '0' && st[i + 1] <= '7') {
                        code += st[i + 1]
                        i++
                        if (i < st.length - 1 && st[i + 1] >= '0' && st[i + 1] <= '7') {
                            code += st[i + 1]
                            i++
                        }
                    }
                    sb.append(code.toInt(8).toChar())
                    i++
                    continue
                }
                when (nextChar) {
                    '\\' -> ch = '\\'
                    'b' -> ch = '\b'
                    'f' -> ch = '\u000C'
                    'n' -> ch = '\n'
                    'r' -> ch = '\r'
                    't' -> ch = '\t'
                    '\"' -> ch = '\"'
                    '\'' -> ch = '\''
                    'u' -> {
                        if (i >= st.length - 5) {
                            break@loop
                        }
                        val code =
                                ("" + st[i + 2] + st[i + 3]
                                        + st[i + 4] + st[i + 5]).toInt(16)
                        sb.append(Character.toChars(code))
                        i += 5
                        i++
                        continue@loop
                    }
                    else -> {
                    }
                }
                i++
            }
            sb.append(ch)
            i++
        }
        return sb.toString()
    }
}