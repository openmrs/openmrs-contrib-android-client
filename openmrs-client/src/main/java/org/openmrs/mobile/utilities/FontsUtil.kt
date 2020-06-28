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
package org.openmrs.mobile.utilities

import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.openmrs.mobile.application.OpenMRS

object FontsUtil {

    private const val OPEN_FONTS_PATH = "fonts/OpenSans/"
    private var typefacesForFonts: HashMap<OpenFonts, Typeface?>? = null

    @JvmStatic
    fun setFont(view: View, openFont: OpenFonts) {
        val openTypeFace = getOpenTypeface(openFont)
        setFont(view, openTypeFace)
    }

    @JvmStatic
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

    @JvmStatic
    private fun getOpenTypeface(openFont: OpenFonts): Typeface? {
        var openTypeFace = typefacesForFonts!![openFont]
        if (openTypeFace == null) {
            openTypeFace = Typeface.createFromAsset(OpenMRS.getInstance().assets,
                    "$OPEN_FONTS_PATH ${openFont.fontName}")
            typefacesForFonts!![openFont] = openTypeFace
        }
        return openTypeFace
    }

    @JvmStatic
    private fun setFont(view: View, openTypeFace: Typeface?) {
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                setFont(view.getChildAt(i), openTypeFace)
            }
        } else if (view is TextView) {
            view.typeface = openTypeFace
        }
    }

    @JvmStatic
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