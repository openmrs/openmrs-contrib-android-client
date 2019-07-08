package org.openmrs.mobile.databases.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import org.openmrs.mobile.models.Resource;

import java.io.Serializable;

@Entity(tableName = "link")
public class LinkEntity extends Resource implements Serializable{

    @ColumnInfo(name = "rel")
    private String rel;

    @ColumnInfo(name = "uri")
    private String uri;

    /**
     *
     * @return
     *     The rel
     */
    public String getRel() {
        return rel;
    }

    /**
     *
     * @param rel
     *     The rel
     */
    public void setRel(String rel) {
        this.rel = rel;
    }

    /**
     *
     * @return
     *     The uri
     */
    public String getUri() {
        return uri;
    }

    /**
     *
     * @param uri
     *     The uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

}

