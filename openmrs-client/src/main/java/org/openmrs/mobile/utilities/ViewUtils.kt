package org.openmrs.mobile.utilities

import android.widget.EditText
import com.hbb20.CountryCodePicker
import org.openmrs.mobile.utilities.StringUtils.isBlank
import java.util.regex.Pattern


object ViewUtils {
    const val ILLEGAL_CHARACTERS = "[$&+:;=\\\\?@#|/'<>^*()%!]"
    const val ILLEGAL_ADDRESS_CHARACTERS = "[$&+:;=\\\\?@|<>^%!]"
    fun getInput(e: EditText): String? {
        return if (e.text == null) {
            null
        } else if (isEmpty(e)) {
            null
        } else {
            e.text.toString()
        }
    }

    fun isEmpty(etText: EditText): Boolean {
        return etText.text.toString().trim { it <= ' ' }.length == 0
    }

    fun isCountryCodePickerEmpty(countryCodePicker: CountryCodePicker): Boolean {
        return countryCodePicker.selectedCountryName == null
    }

    /**
     * Validate a String for invalid characters
     * @param toValidate    the String to check
     * @return true if String is appropriate
     */
    fun validateText(toValidate: String, invalidCharacters: String): Boolean { //TODO: Add more checks to the String
        return !containsCharacters(toValidate, invalidCharacters)
    }

    /**
     * Check if a name contains a character from a string param
     * @param toExamine     the String to check
     * @param characters    characters checked against toExamine
     * @return true if the String contains a character from a sequence of characters
     */
    private fun containsCharacters(toExamine: String, characters: String): Boolean {
        if (isBlank(toExamine)) {
            return false
        }
        val charPattern = Pattern.compile(characters)
        return charPattern.matcher(toExamine).find()
    }
}