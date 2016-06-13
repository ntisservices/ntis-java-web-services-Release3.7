package com.thales.ntis.subscriber.model;

public enum FeedType {

    ANPR("ANPR Journey Time Data"),

    MIDAS("MIDAS Loop Traffic Data"),

    TMU("TMU Loop Traffic Data"),

    FUSED_SENSOR_ONLY("Fused Sensor-only PTD"),

    FUSED_FVD_AND_SENSOR_PTD("Fused FVD and Sensor PTD"),

    VMS("VMS and Matrix Signal Status Data"),

    NTIS_MODEL_UPDATE_NOTIFICATION("NTIS Model Update Notification"),

    EVENT_DATA("Event Data"),

    FULL_REFRESH("Event Data - Full Refresh");

    private String feedTypeText;

    FeedType(String feedTypeText) {
        this.feedTypeText = feedTypeText;
    }

    public String upperCase() {
        return value().toUpperCase();
    }

    public String lowerCase() {
        return value().toLowerCase();
    }

    public String value() {
        return feedTypeText;
    }
}
