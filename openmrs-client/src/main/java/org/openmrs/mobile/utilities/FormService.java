package org.openmrs.mobile.utilities;

import android.content.Context;

import com.google.gson.Gson;

import org.openmrs.mobile.models.retrofit.Form;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FormService {

    public static Form getForm(String valuereference)
    {
        valuereference=StringUtils.unescapeJavaString(valuereference);
        Gson gson = new Gson();
        return gson.fromJson(valuereference,Form.class);
    }

}
