
package com.example.ivan.champy_v2.model.User;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Data {

    private String email;
    private String name;
    private Integer successChallenges;
    private Integer failedChallenges;
    private Integer allChallengesCount;
    private Integer inProgressChallengesCount;
    private Integer score;
    private String _id;
    private Integer created;
    private Integer updated;
    private Photo photo;
    private Level level;
    private ProfileOptions profileOptions;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    public Integer getSuccessChallenges() {
        return successChallenges;
    }

    public Integer getFailedChallenges() {
        return failedChallenges;
    }

    public Integer getAllChallengesCount() {
        return this.inProgressChallengesCount + allChallengesCount;
    }

    public Integer getInProgressChallengesCount() {
        return inProgressChallengesCount;
    }

    public Integer getScore() {
        return inProgressChallengesCount;
    }

    public Integer getCreated() {
        return created;
    }

    public Integer getUpdated() {
        return updated;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String get_id() {
        return _id;
    }

    public Photo getPhoto() {
        return photo;
    }

    public Level getLevel() {
        return level;
    }


    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public ProfileOptions getProfileOptions() {
        return profileOptions;
    }


    public void setSuccessChallenges(Integer successChallenges) {
        this.successChallenges = successChallenges;
    }

    public void setFailedChallenges(Integer failedChallenges) {
        this.failedChallenges = failedChallenges;
    }

    public void setAllChallengesCount(Integer allChallengesCount) {
        this.allChallengesCount = allChallengesCount;
    }

    public void setInProgressChallengesCount(Integer inProgressChallengesCount) {
        this.inProgressChallengesCount = inProgressChallengesCount;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public void setCreated(Integer created) {
        this.created = created;
    }

    public void setUpdated(Integer updated) {
        this.updated = updated;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public void setProfileOptions(ProfileOptions profileOptions) {
        this.profileOptions = profileOptions;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
