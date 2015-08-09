package com.example.myapplication.dao;

import com.fasterxml.jackson.annotation.JsonProperty;

public class History {

    @JsonProperty("Out Date")
    private String outDate;
    @JsonProperty("In Date")
    private String inDate;
    @JsonProperty("Status")
    private int status;
    @JsonProperty("Who")
    private String who;
    @JsonProperty("Name")
    private String name;

    public String getOutDate() {
        return outDate;
    }

    public void setOutDate(String outDate) {
        this.outDate = outDate;
    }

    public String getInDate() {
        return inDate;
    }

    public void setInDate(String inDate) {
        this.inDate = inDate;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
