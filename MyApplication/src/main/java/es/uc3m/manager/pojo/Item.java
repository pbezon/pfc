package es.uc3m.manager.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Item implements Serializable {

    @JsonProperty("currentStatus")
    private Status currentStatus;
    @JsonProperty("photo")
    private String photo;
    @JsonProperty("insertDate")
    private Date insertDate;
    @JsonProperty("type")
    private String type;
    @JsonIgnore
    private String __v;
    @JsonProperty("_id")
    private String _id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;

    @Override
    public String toString() {
        return "_id:" + _id + ", name:" + name + ", description:" + description;
    }

    public Item (Item copy) {
        this.currentStatus = copy.currentStatus;
        this.photo = copy.photo;
        this.insertDate = copy.insertDate;
        this.type = copy.type;
        this.__v = copy.__v;
        this._id = copy._id;
        this.name = copy.name;
        this.description = copy.description;
    }

    public Item() {

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

    public Status getCurrentStatus() {
        if (currentStatus == null)
            currentStatus = new Status();
        return currentStatus;
    }

    public void setCurrentStatus(Status currentStatus) {
        if (currentStatus == null)
            currentStatus = new Status();
        this.currentStatus = currentStatus;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Date getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(Date insertDate) {
        this.insertDate = insertDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
