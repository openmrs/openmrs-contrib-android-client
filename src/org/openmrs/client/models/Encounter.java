package org.openmrs.client.models;

import java.io.Serializable;
import java.util.List;

public class Encounter implements Serializable {
    private Long id;
    private String uuid;
    private String display;
    private Long encounterDatetime;
    private EncounterType encounterType;
    private List<Observation> observations;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public Long getEncounterDatetime() {
        return encounterDatetime;
    }

    public void setEncounterDatetime(Long encounterDatetime) {
        this.encounterDatetime = encounterDatetime;
    }

    public EncounterType getEncounterType() {
        return encounterType;
    }

    public void setEncounterType(EncounterType encounterType) {
        this.encounterType = encounterType;
    }

    public List<Observation> getObservations() {
        return observations;
    }

    public void setObservations(List<Observation> observations) {
        this.observations = observations;
    }

    public enum EncounterType {
        VITALS("Vitals"), VISIT_NOTE("Visit Note");

        EncounterType(String type) {
            this.type = type;
        }

        private String type;

        public String getType() {
            return type;
        }

        public static EncounterType getType(String type) {
            if (type.equals(VISIT_NOTE.getType())) {
                return VISIT_NOTE;
            } else {
                return VITALS;
            }
        }
    }
}
