package com.example.myapplication.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Product implements Serializable {

    @JsonIgnore
    private String __v;
    @JsonProperty("id")
    private String _id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("history")
    private List<History> productHistory;
    @JsonProperty("currentStatus")
    protected History currentStatus;
    @JsonProperty("bitmap")
    protected byte[] photo;
    protected String contactUri;
    protected String calendarEventId;


    @Override
    public String toString() {
        return "_id:" + _id + ", name:" + name + ", description:" + description;
    }

    public String get__v() {
        return __v;
    }

    public void set__v(String __v) {
        this.__v = __v;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<History> getProductHistory() {
        if (productHistory == null)
            productHistory = new ArrayList<History>();
        return productHistory;
    }

    public void setProductHistory(List<History> productHistory) {
        this.productHistory = productHistory;
    }

    public History getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(History currentStatus) {
        this.currentStatus = currentStatus;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getContactUri() {
        return contactUri;
    }

    public void setContactUri(String contactUri) {
        this.contactUri = contactUri;
    }

    public String getCalendarEventId() {
        return calendarEventId;
    }

    public void setCalendarEventId(String calendarEventId) {
        this.calendarEventId = calendarEventId;
    }
}
