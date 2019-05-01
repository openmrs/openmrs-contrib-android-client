/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.mobile.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Table(name = "encountertype")
public class EncounterType extends Model implements Serializable {

    @Column(name = "uuid")
    @SerializedName("uuid")
    @Expose
    private String uuid;

    @Column(name = "display")
    @SerializedName("display")
    @Expose
    private String display;

    @Column(name = "links")
    @SerializedName("links")
    @Expose
    private List<Link> links = new ArrayList<Link>();

    public static final String VITALS = "Vitals";
    public static final String VISIT_NOTE = "Visit Note";
    public static final String DISCHARGE = "Discharge";
    public static final String ADMISSION = "Admission";

    public EncounterType() {}

    public EncounterType(String display) {
        this.setDisplay(display);
    }

    /**
     *
     * @return
     *     The uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     *
     * @param uuid
     *     The uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     *
     * @return
     *     The display
     */
    public String getDisplay() {
        return display;
    }

    /**
     *
     * @param display
     *     The display
     */
    public void setDisplay(String display) {
        this.display = display;
    }

    /**
     *
     * @return
     *     The links
     */
    public List<Link> getLinks() {
        return links;
    }

    /**
     *
     * @param links
     *     The links
     */
    public void setLinks(List<Link> links) {
        this.links = links;
    }


}
