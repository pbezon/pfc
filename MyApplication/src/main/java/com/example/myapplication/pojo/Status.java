package com.example.myapplication.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Status implements Serializable {

    @JsonProperty("calendarEventId")
    String calendarEventId;
    @JsonProperty("status")
    private String status;
    @JsonProperty("contactUri")
    private String contactUri;

    public String getCalendarEventId() {
        return calendarEventId;
    }

    public void setCalendarEventId(String calendarEventId) {
        this.calendarEventId = calendarEventId;
    }

    public String getContactUri() {
        return contactUri;
    }

    public void setContactUri(String contactUri) {
        this.contactUri = contactUri;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
