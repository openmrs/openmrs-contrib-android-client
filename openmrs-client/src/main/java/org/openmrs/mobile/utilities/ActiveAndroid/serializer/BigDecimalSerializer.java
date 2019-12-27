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

package org.openmrs.mobile.utilities.ActiveAndroid.serializer;

import java.math.BigDecimal;

public final class BigDecimalSerializer extends TypeSerializer {
    public Class<?> getDeserializedType() {
        return BigDecimal.class;
    }

    public Class<?> getSerializedType() {
        return String.class;
    }

    public String serialize(Object data) {
        if (data == null) {
            return null;
        }

        return data.toString();
    }

    public BigDecimal deserialize(Object data) {
        if (data == null) {
            return null;
        }

        return new BigDecimal((String) data);
    }
}