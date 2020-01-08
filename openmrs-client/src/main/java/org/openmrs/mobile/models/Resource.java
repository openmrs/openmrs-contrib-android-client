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


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

public class Resource implements Serializable {

    @SerializedName("uuid")
    @NonNull
    @ColumnInfo(name = "uuid")
    @Expose
    protected String uuid;

    @ColumnInfo(name = "display")
    @SerializedName("display")
    @Expose
    protected String display;

    @Ignore
    @SerializedName("links")
    @Expose
    protected List<Link> links = new ArrayList<>();

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "_id")
    private Long id;

    public Resource() { }

    Resource(@NotNull String uuid, String display, List<Link> links, @NotNull Long id) {
        this.uuid = uuid;
        this.display = display;
        this.links = links;
        this.id = id;
    }

    /**
     * @return id
     */
    @NotNull
    public Long getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(@NotNull Long id) {
        this.id = id;
    }

    /**
     * @return The uuid
     */
    @NotNull
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid The uuid
     */
    public void setUuid(@NotNull String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return The display
     */
    public String getDisplay() {
        return display;
    }

    /**
     * @param display The display
     */
    public void setDisplay(String display) {
        this.display = display;
    }

    /**
     * @return The links
     */
    public List<Link> getLinks() {
        return links;
    }

    /**
     * @param links The links
     */
    public void setLinks(List<Link> links) {
        this.links = links;
    }


}
