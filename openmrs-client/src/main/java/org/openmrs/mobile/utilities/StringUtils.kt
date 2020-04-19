package org.openmrs.mobile.utilities

object StringUtils {
    private const val NULL_AS_STRING = "null"
    private const val SPACE_CHAR = " "
    fun notNull(string: String?): Boolean {
        return null != string && NULL_AS_STRING != string.trim { it <= ' ' }
    }

    fun isBlank(string: String?): Boolean {
        return null == string || SPACE_CHAR == string
    }

    fun notEmpty(string: String?): Boolean {
        return string != null && !string.isEmpty()
    }

    fun unescapeJavaString(st: String): String {
        val sb = StringBuilder(st.length)
        var i = 0
        while (i < st.length) {
            var conti =false;
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
                    'f' -> ch = '\u000C'  /* \f is not supported in kotlin so unicode escape is used*/
                    'n' -> ch = '\n'
                    'r' -> ch = '\r'
                    't' -> ch = '\t'
                    '\"' -> ch = '\"'
                    '\'' -> ch = '\''
                    'u' -> {
                        if (i >= st.length - 5) {
                            ch = 'u'
                        }
                        else{
                            val code =
                                    ("" + st[i + 2] + st[i + 3]
                                            + st[i + 4] + st[i + 5]).toInt(16)
                            sb.append(Character.toChars(code))
                            i += 5
                            i++
                            conti = true
                        }
                    }
                    else -> {
                        //do nothing
                    }
                }
                if (conti) continue
                i++
            }
            sb.append(ch)
            i++
        }
        return sb.toString()
    }
}
