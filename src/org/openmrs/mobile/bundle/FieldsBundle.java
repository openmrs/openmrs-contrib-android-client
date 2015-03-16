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

package org.openmrs.mobile.bundle;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class FieldsBundle implements Serializable {

    private Map<String, Serializable> fields;

    public FieldsBundle() {
        this.fields = new HashMap<String, Serializable>();
    }

    public FieldsBundle(int size) {
        this.fields = new HashMap<String, Serializable>(size);
    }

    public boolean hasField(String field) {
        return fields.containsKey(field);
    }

    public Serializable getField(String field) {
        return fields.get(field);
    }

    public String getStringField(String field) {
        return (String) fields.get(field);
    }

    public Long getLongField(String field) {
        return (Long) fields.get(field);
    }

    public void putField(String field, Serializable value) {
        fields.put(field, value);
    }

    public void putStringField(String field, String value) {
        fields.put(field, value);
    }

    public void putLongField(String field, long value) {
        fields.put(field, value);
    }

}
