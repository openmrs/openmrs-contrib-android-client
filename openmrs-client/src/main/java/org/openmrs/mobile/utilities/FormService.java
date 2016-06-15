package org.openmrs.mobile.utilities;

import android.content.Context;

import com.google.gson.Gson;

import org.openmrs.mobile.models.retrofit.Form;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Avijit on 23/06/16.
 */
public class FormService {

    Context context;

    public FormService(Context context)
    {
        this.context=context;
    }

    public Form getFormByName(String formname)
    {
        String formjson=loadAssetTextAsString(context, "openmrs-forms/"+formname+".json");

        Gson gson = new Gson();
        return gson.fromJson(formjson,Form.class);
    }


    String loadAssetTextAsString(Context context, String name) {
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            InputStream is = context.getAssets().open(name);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ( (str = in.readLine()) != null ) {
                if (isFirst)
                    isFirst = false;
                else
                    buf.append('\n');
                buf.append(str);
            }
            return buf.toString();
        } catch (IOException e) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }

        return null;
    }
}
