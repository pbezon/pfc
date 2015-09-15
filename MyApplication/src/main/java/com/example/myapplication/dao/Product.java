package com.example.myapplication.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Product {

    @JsonIgnore
    private String __v;
    @JsonProperty("id")
    private String _id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("price")
    private Integer unitprice;
    @JsonProperty("description")
    private String description;
    @JsonProperty("discontinued")
    private Boolean discontinued;
    @JsonProperty("History")
    private List<History> productHistory;
    @JsonProperty("Current Status")
    protected History currentStatus;


    @Override
    public String toString() {
        return "_id:" + _id + ", name:" + name + ", unitPrice:" + unitprice + ",discontinued:" + discontinued.toString();
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

    public Integer getUnitprice() {
        return unitprice;
    }

    public void setUnitprice(Integer unitprice) {
        this.unitprice = unitprice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getDiscontinued() {
        return discontinued;
    }

    public void setDiscontinued(Boolean discontinued) {
        this.discontinued = discontinued;
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
}
