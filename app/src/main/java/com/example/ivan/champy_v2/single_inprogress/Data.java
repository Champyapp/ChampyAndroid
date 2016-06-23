
package com.example.ivan.champy_v2.single_inprogress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Data {

    private String _id;
    private Sender sender;
    private Challenge challenge;
    private Integer begin;
    private Integer end;
    private Integer created;
    private Integer updated;
    private Boolean senderSuccess;
    private Boolean recipientSuccess;
    private List<Object> recipientProgress = new ArrayList<Object>();
    private List<Object> senderProgress = new ArrayList<Object>();
    private String status;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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
     *     The _id
     */
    public void set_id(String _id) {
        this._id = _id;
    }

    /**
     * 
     * @return
     *     The sender
     */
    public Sender getSender() {
        return sender;
    }

    /**
     * 
     * @param sender
     *     The sender
     */
    public void setSender(Sender sender) {
        this.sender = sender;
    }

    /**
     * 
     * @return
     *     The challenge
     */
    public Challenge getChallenge() {
        return challenge;
    }

    /**
     * 
     * @param challenge
     *     The challenge
     */
    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    /**
     * 
     * @return
     *     The begin
     */
    public Integer getBegin() {
        return begin;
    }

    /**
     * 
     * @param begin
     *     The begin
     */
    public void setBegin(Integer begin) {
        this.begin = begin;
    }

    /**
     * 
     * @return
     *     The end
     */
    public Integer getEnd() {
        return end;
    }

    /**
     * 
     * @param end
     *     The end
     */
    public void setEnd(Integer end) {
        this.end = end;
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
     *     The senderSuccess
     */
    public Boolean getSenderSuccess() {
        return senderSuccess;
    }

    /**
     * 
     * @param senderSuccess
     *     The senderSuccess
     */
    public void setSenderSuccess(Boolean senderSuccess) {
        this.senderSuccess = senderSuccess;
    }

    /**
     * 
     * @return
     *     The recipientSuccess
     */
    public Boolean getRecipientSuccess() {
        return recipientSuccess;
    }

    /**
     * 
     * @param recipientSuccess
     *     The recipientSuccess
     */
    public void setRecipientSuccess(Boolean recipientSuccess) {
        this.recipientSuccess = recipientSuccess;
    }

    /**
     * 
     * @return
     *     The recipientProgress
     */
    public List<Object> getRecipientProgress() {
        return recipientProgress;
    }

    /**
     * 
     * @param recipientProgress
     *     The recipientProgress
     */
    public void setRecipientProgress(List<Object> recipientProgress) {
        this.recipientProgress = recipientProgress;
    }

    /**
     * 
     * @return
     *     The senderProgress
     */
    public List<Object> getSenderProgress() {
        return senderProgress;
    }

    /**
     * 
     * @param senderProgress
     *     The senderProgress
     */
    public void setSenderProgress(List<Object> senderProgress) {
        this.senderProgress = senderProgress;
    }

    /**
     * 
     * @return
     *     The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * 
     * @param status
     *     The status
     */

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
