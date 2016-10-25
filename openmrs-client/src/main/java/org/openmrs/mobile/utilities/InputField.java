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

import java.util.concurrent.atomic.AtomicInteger;

    public class InputField {
        int id;
        String concept;
        double value = -1.0;

        public void setId(int id)
        {
            this.id=id;
        }

        public int getId()
        {
            return id;
        }

        public void setConcept(String concept)
        {
            this.concept=concept;
        }

        public String getConcept()
        {
            return concept;
        }

        public void setValue(Double value)
        {
            this.value=value;
        }

        public Double getValue()
        {
            return value;
        }

        private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

        public static int generateViewId() {
            while (true) {
                final int result = sNextGeneratedId.get();
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF) newValue = 1;
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        }

    }
