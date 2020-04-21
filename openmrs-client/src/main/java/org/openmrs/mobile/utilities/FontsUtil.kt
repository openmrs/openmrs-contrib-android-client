package org.openmrs.mobile.utilities

import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.openmrs.mobile.application.OpenMRS
import java.util.*


object FontsUtil {

    private const val OPEN_FONTS_PATH = "fonts/OpenSans/"
    private var typefacesForFonts: HashMap<OpenFonts, Typeface?>? = null

    fun setFont(view: View, openFont: OpenFonts) {
        val openTypeFace = getOpenTypeface(openFont)
        setFont(view, openTypeFace)
    }

    fun setFont(group: ViewGroup) {
        val count = group.childCount
        var v: View?
        for (i in 0 until count) {
            v = group.getChildAt(i)
            if (v is TextView || v is EditText || v is Button) {
                setTypeface(v as TextView?)
            } else if (v is ViewGroup) {
                setFont(v)
            }
        }
    }

    private fun getOpenTypeface(openFont: OpenFonts): Typeface? {
        var openTypeFace = typefacesForFonts!![openFont]
        if (openTypeFace == null) {
            openTypeFace = Typeface.createFromAsset(OpenMRS.getInstance().assets,
                    OPEN_FONTS_PATH + openFont.fontName)
            typefacesForFonts!![openFont] = openTypeFace
        }
        return openTypeFace
    }

    private fun setFont(view: View, openTypeFace: Typeface?) {
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                setFont(view.getChildAt(i), openTypeFace)
            }
        } else if (view is TextView) {
            view.typeface = openTypeFace
        }
    }

    private fun setTypeface(textView: TextView?) {
        if (textView != null) {
            if (textView.typeface != null && textView.typeface.isBold
                    && textView.typeface.isItalic) {
                textView.typeface = getOpenTypeface(OpenFonts.OPEN_SANS_BOLD_ITALIC)
            } else if (textView.typeface != null && textView.typeface.isItalic) {
                textView.typeface = getOpenTypeface(OpenFonts.OPEN_SANS_ITALIC)
            } else if (textView.typeface != null && textView.typeface.isBold) {
                textView.typeface = getOpenTypeface(OpenFonts.OPEN_SANS_BOLD)
            } else {
                textView.typeface = getOpenTypeface(OpenFonts.OPEN_SANS_REGULAR)
            }
        }
    }

    enum class OpenFonts(val fontName: String) {
        OPEN_SANS_BOLD("OpenSans-Bold.ttf"),
        OPEN_SANS_BOLD_ITALIC("OpenSans-BoldItalic.ttf"),
        OPEN_SANS_EXTRA_BOLD("OpenSans-ExtraBold.ttf"),
        OPEN_SANS_EXTRA_BOLD_ITALIC("OpenSans-ExtraBoldItalic.ttf"),
        OPEN_SANS_ITALIC("OpenSans-Italic.ttf"),
        OPEN_SANS_LIGHT("OpenSans-Light.ttf"),
        OPEN_SANS_LIGHT_ITALIC("OpenSans-LightItalic.ttf"),
        OPEN_SANS_REGULAR("OpenSans-Regular.ttf"),
        OPEN_SANS_SEMIBOLD("OpenSans-Semibold.ttf"),
        OPEN_SANS_SEMIBOLD_ITALIC("OpenSans-SemiboldItalic.ttf");

    }

    init {
        typefacesForFonts = HashMap(OpenFonts.values().size)
    }
}
