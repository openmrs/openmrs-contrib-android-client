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

package org.openmrs.mobile.utilities;

import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.openmrs.mobile.application.OpenMRS;

import java.util.HashMap;

public final class FontsUtil {

    private static final String OPEN_FONTS_PATH = "fonts/OpenSans/";
    private static HashMap<OpenFonts, Typeface> typefacesForFonts;

    public enum OpenFonts {
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

        private final String font;

        private OpenFonts(String font) {
            this.font = font;
        }

        public String getFontName() {
            return font;
        }
    }

    private FontsUtil() {
    }

    public static void setFont(View view, OpenFonts openFont) {
        Typeface openTypeFace = getOpenTypeface(openFont);
        setFont(view, openTypeFace);
    }

    public static void setFont(ViewGroup group) {
        int count = group.getChildCount();
        View v;
        for (int i = 0; i < count; i++) {
            v = group.getChildAt(i);
            if (v instanceof TextView || v instanceof EditText || v instanceof Button) {
                setTypeface((TextView) v);
            } else if (v instanceof ViewGroup) {
                setFont((ViewGroup) v);
            }
        }
    }

    private static Typeface getOpenTypeface(OpenFonts openFont) {
        Typeface openTypeFace = typefacesForFonts.get(openFont);

        if (openTypeFace == null) {
            openTypeFace = Typeface.createFromAsset(OpenMRS.getInstance().getAssets(),
                    OPEN_FONTS_PATH + openFont.getFontName());
            typefacesForFonts.put(openFont, openTypeFace);
        }
        return openTypeFace;
    }

    private static void setFont(View view, Typeface openTypeFace) {
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                setFont(((ViewGroup) view).getChildAt(i), openTypeFace);
            }
        } else if (view instanceof TextView) {
            ((TextView) view).setTypeface(openTypeFace);
        }
    }

    private static void setTypeface(TextView textView) {
        if (textView != null) {
            if (textView.getTypeface() != null && textView.getTypeface().isBold()
                                              && textView.getTypeface().isItalic()) {
                textView.setTypeface(getOpenTypeface(OpenFonts.OPEN_SANS_BOLD_ITALIC));
            } else if (textView.getTypeface() != null && textView.getTypeface().isItalic()) {
                textView.setTypeface(getOpenTypeface(OpenFonts.OPEN_SANS_ITALIC));
            } else if (textView.getTypeface() != null && textView.getTypeface().isBold()) {
                textView.setTypeface(getOpenTypeface(OpenFonts.OPEN_SANS_BOLD));
            } else {
                textView.setTypeface(getOpenTypeface(OpenFonts.OPEN_SANS_REGULAR));
            }
        }
    }

    static {
        typefacesForFonts = new HashMap<OpenFonts, Typeface>(OpenFonts.values().length);
    }
}
