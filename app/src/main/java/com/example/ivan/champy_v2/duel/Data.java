
package com.example.ivan.champy_v2.duel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Data {

    private String _id;
    private Sender sender;
    private Recipient recipient;
    private Challenge challenge;
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
    public String getId() {
        return _id;
    }

    /**
     * 
     * @param Id
     *     The _id
     */
    public void setId(String Id) {
        this._id = Id;
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
     *     The recipient
     */
    public Recipient getRecipient() {
        return recipient;
    }

    /**
     * 
     * @param recipient
     *     The recipient
     */
    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
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
