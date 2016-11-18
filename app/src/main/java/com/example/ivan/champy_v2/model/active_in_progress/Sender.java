
package com.example.ivan.champy_v2.model.active_in_progress;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Sender {

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
    private Level level;
    private ProfileOptions profileOptions;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * 
     * @param email
     *     The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The successChallenges
     */
    public Integer getSuccessChallenges() {
        return successChallenges;
    }

    /**
     * 
     * @param successChallenges
     *     The successChallenges
     */
    public void setSuccessChallenges(Integer successChallenges) {
        this.successChallenges = successChallenges;
    }

    /**
     * 
     * @return
     *     The failedChallenges
     */
    public Integer getFailedChallenges() {
        return failedChallenges;
    }

    /**
     * 
     * @param failedChallenges
     *     The failedChallenges
     */
    public void setFailedChallenges(Integer failedChallenges) {
        this.failedChallenges = failedChallenges;
    }

    /**
     * 
     * @return
     *     The allChallengesCount
     */
    public Integer getAllChallengesCount() {
        return allChallengesCount;
    }

    /**
     * 
     * @param allChallengesCount
     *     The allChallengesCount
     */
    public void setAllChallengesCount(Integer allChallengesCount) {
        this.allChallengesCount = allChallengesCount;
    }

    /**
     * 
     * @return
     *     The inProgressChallengesCount
     */
    public Integer getInProgressChallengesCount() {
        return inProgressChallengesCount;
    }

    /**
     * 
     * @param inProgressChallengesCount
     *     The inProgressChallengesCount
     */
    public void setInProgressChallengesCount(Integer inProgressChallengesCount) {
        this.inProgressChallengesCount = inProgressChallengesCount;
    }

    /**
     * 
     * @return
     *     The score
     */
    public Integer getScore() {
        return score;
    }

    /**
     * 
     * @param score
     *     The score
     */
    public void setScore(Integer score) {
        this.score = score;
    }

    /**
     * 
     * @return
     *     The Id
     */
    public String get_id() {
        return _id;
    }

    /**
     * 
     * @param Id
     *     The _id
     */
    public void set_Id(String _Id) {
        this._id = _id;
    }

    /**
     * 
     * @return
     *     The created
     */
    public Integer getCreated() {
        return created;
    }

    /**
     * 
     * @param created
     *     The created
     */
    public void setCreated(Integer created) {
        this.created = created;
    }

    /**
     * 
     * @return
     *     The updated
     */
    public Integer getUpdated() {
        return updated;
    }

    /**
     * 
     * @param updated
     *     The updated
     */
    public void setUpdated(Integer updated) {
        this.updated = updated;
    }

    /**
     * 
     * @return
     *     The level
     */
    public Level getLevel() {
        return level;
    }

    /**
     * 
     * @param level
     *     The level
     */
    public void setLevel(Level level) {
        this.level = level;
    }

    /**
     * 
     * @return
     *     The profileOptions
     */
    public ProfileOptions getProfileOptions() {
        return profileOptions;
    }

    /**
     * 
     * @param profileOptions
     *     The profileOptions
     */
    public void setProfileOptions(ProfileOptions profileOptions) {
        this.profileOptions = profileOptions;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
