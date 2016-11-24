package com.example.ivan.champy_v2.model.active_in_progress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Datum {

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
    private List<Object> progress = new ArrayList<>();
    private String status;
    private String wakeUpTime;
    private String needsToCheckSender;
    private String needsToCheckRecipient;
    private Integer begin;
    private Integer end;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    public Recipient getRecipient() {
        return recipient;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public Sender getSender() {
        return sender;
    }

    public Boolean getSenderSuccess() {
        return senderSuccess;
    }

    public Boolean getRecipientSuccess() {
        return recipientSuccess;
    }

    public String get_id() {
        return _id;
    }

    public String getStatus() {
        return status;
    }

    public String getNeedsToCheckRecipient() {
        return needsToCheckRecipient;
    }

    public String getWakeUpTime() {
        return wakeUpTime;
    }

    public String getNeedsToCheckSender() {
        return needsToCheckSender;
    }

    public Integer getCreated() {
        return created;
    }

    public Integer getUpdated() {
        return updated;
    }

    public Integer getBegin() {
        return begin;
    }

    public Integer getEnd() {
        return end;
    }

    public List<Object> getRecipientProgress() {
        return recipientProgress;
    }

    public List<Object> getSenderProgress() {
        return senderProgress;
    }

    public List<Object> getProgress() {
        return progress;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }


    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public void setCreated(Integer created) {
        this.created = created;
    }

    public void setUpdated(Integer updated) {
        this.updated = updated;
    }

    public void setSenderSuccess(Boolean senderSuccess) {
        this.senderSuccess = senderSuccess;
    }

    public void setRecipientSuccess(Boolean recipientSuccess) {
        this.recipientSuccess = recipientSuccess;
    }

    public void setRecipientProgress(List<Object> recipientProgress) {
        this.recipientProgress = recipientProgress;
    }

    public void setSenderProgress(List<Object> senderProgress) {
        this.senderProgress = senderProgress;
    }

    public void setProgress(List<Object> progress) {
        this.progress = progress;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setBegin(Integer begin) {
        this.begin = begin;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public void setWakeUpTime(String wakeUpTime) {
        this.wakeUpTime = wakeUpTime;
    }

    public void setNeedsToCheckSender(String needsToCheckSender) {
        this.needsToCheckSender = needsToCheckSender;
    }

    public void setNeedsToCheckRecipient(String needsToCheckRecipient) {
        this.needsToCheckRecipient = needsToCheckRecipient;
    }
}
