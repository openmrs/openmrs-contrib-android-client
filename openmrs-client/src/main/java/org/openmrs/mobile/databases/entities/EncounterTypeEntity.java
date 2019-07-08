package org.openmrs.mobile.databases.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.openmrs.mobile.models.Link;
import org.openmrs.mobile.models.Resource;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "encounterTypes")
public class EncounterTypeEntity extends Resource {

    public static final String VITALS = "Vitals";
    public static final String VISIT_NOTE = "Visit Note";
    public static final String DISCHARGE = "Discharge";
    public static final String ADMISSION = "Admission";

    public EncounterTypeEntity() {}

    public EncounterTypeEntity(String display) {
        this.setDisplay(display);
    }

}
