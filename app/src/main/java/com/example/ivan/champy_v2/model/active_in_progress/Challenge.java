
package com.example.ivan.champy_v2.model.active_in_progress;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Challenge {

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    private String _id;
    private String name;
    private String type;
    private String description;
    private String details;
    private String versus;
    private Integer created;
    private Integer updated;
    private Boolean approved;
    private Boolean active;
    private Integer duration;
    private Integer points;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    public String getVersus() {
        return versus;
    }

    public void setVersus(String versus) {
        this.versus = versus;
    }

    public String getName() {
        return name;
    }

    public String getType() {
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

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
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
