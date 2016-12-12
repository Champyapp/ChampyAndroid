package com.azinecllc.champy.model.friend;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Level {

    private Integer progress;
    private Integer number;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The progress
     */
    public Integer getProgress() {
        return progress;
    }

    /**
     * 
     * @param progress
     *     The progress
     */
    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    /**
     * 
     * @return
     *     The number
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * 
     * @param number
     *     The number
     */
    public void setNumber(Integer number) {
        this.number = number;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
