package org.openmrs.mobile.models;


import android.graphics.Bitmap;
import android.util.Base64;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class PatientPhoto extends Resource implements Serializable {
    @SerializedName("person")
    @Expose
    private Person person;

    @SerializedName("base64EncodedImage")
    @Expose
    private String base64EncodedImage;

    public void setPhoto(Bitmap image) {
        byte[] byteArray = bitmapToByteArray(image);
        base64EncodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    private byte[] bitmapToByteArray(Bitmap image) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
