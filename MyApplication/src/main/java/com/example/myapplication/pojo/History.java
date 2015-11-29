package com.example.myapplication.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class History implements Serializable {

    @JsonProperty("calendarEvent")
    String calendarUri;
    @JsonProperty("status")
    private int status;
    @JsonProperty("contactId")
    private String contactId;

    private String currentStatus;

    public String getCalendarUri() {
        return calendarUri;
    }

    public void setCalendarUri(String calendarUri) {
        this.calendarUri = calendarUri;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }
}
