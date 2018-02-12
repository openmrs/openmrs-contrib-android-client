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

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class InputField implements Serializable, Parcelable {

    private int id;
    private String concept;
    private double value = -1.0;
    private boolean isRed = false;


    public InputField(String concept) {
        this.concept = concept;
        this.id = Math.abs(concept.hashCode());
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public String getConcept() {
        return concept;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getValue() {
        return value;
    }

    public void setIsRed(boolean isRed) {
        this.isRed=isRed;
    }

    public boolean isRed() {
        return isRed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.concept);
        dest.writeDouble(this.value);
        dest.writeInt(this.isRed?1:0);
    }

    protected InputField(Parcel in) {
        this.id = in.readInt();
        this.concept = in.readString();
        this.value = in.readDouble();
        this.isRed = (in.readInt()==1);
    }

    public static final Parcelable.Creator<InputField> CREATOR = new Parcelable.Creator<InputField>() {
        @Override
        public InputField createFromParcel(Parcel source) {
            return new InputField(source);
        }

        @Override
        public InputField[] newArray(int size) {
            return new InputField[size];
        }
    };
}
