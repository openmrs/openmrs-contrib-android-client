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

package org.openmrs.mobile.models;

public class SettingsListItemDTO {
    private String title;
    private String desc1;
    private String desc2;
    private boolean visibleSwitch;

    public SettingsListItemDTO(String title, String desc1, String desc2) {
        this.title = title;
        this.desc1 = desc1;
        this.desc2 = desc2;
    }

    public SettingsListItemDTO(String title) {
        this.title = title;
    }

    public SettingsListItemDTO(String title, boolean visibleSwitch) {
        this.title = title;
        this.visibleSwitch = visibleSwitch;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc1() {
        return desc1;
    }

    public String getDesc2() {
        return desc2;
    }

    public Boolean isVisibleSwitch() {
        return visibleSwitch;
    }
}
