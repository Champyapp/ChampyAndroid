
package com.example.ivan.champy_v2.model.Self;

import java.util.HashMap;
import java.util.Map;

public class Datum {

    private String _id;
    private String name;
    private Type type;
    private String description;
    private String createdBy;
    private String details;
    private Integer created;
    private Integer updated;
    private Boolean approved;
    private Boolean active;
    private Integer duration;
    private Integer points;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    public String get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getDetails() {
        return details;
    }

    public Integer getCreated() {
        return created;
    }

    public Integer getUpdated() {
        return updated;
    }

    public Boolean getApproved() {
        return approved;
    }

    public Boolean getActive() {
        return active;
    }

    public Integer getDuration() {
        return duration;
    }

    public Integer getPoints() {
        return points;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }



    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }



    public void set_id(String Id) {
        this._id = Id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setCreated(Integer created) {
        this.created = created;
    }

    public void setUpdated(Integer updated) {
        this.updated = updated;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
