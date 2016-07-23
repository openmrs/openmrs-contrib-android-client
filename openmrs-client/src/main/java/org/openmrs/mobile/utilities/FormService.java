package org.openmrs.mobile.utilities;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.openmrs.mobile.models.retrofit.Form;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;

public class FormService {

    public static Form getForm(String valuereference)
    {
        valuereference=StringUtils.unescapeJavaString(valuereference);

        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
        builder.excludeFieldsWithoutExposeAnnotation();
        Gson gson = builder.create();

        return gson.fromJson(valuereference,Form.class);
    }

}
