package org.openmrs.client.models;

public class SettingsListItemDTO {
    private String title;
    private String desc1;
    private String desc2;

    public SettingsListItemDTO(String title, String desc1, String desc2) {
        this.title = title;
        this.desc1 = desc1;
        this.desc2 = desc2;
    }

    public SettingsListItemDTO(String title) {
        this.title = title;
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
}
