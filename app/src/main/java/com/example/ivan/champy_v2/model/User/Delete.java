package com.example.ivan.champy_v2.model.User;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

public class Delete {

    private Integer code;
    private String description;
    private Boolean data;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The code
     */
    public Integer getCode() {
        return code;
    }

    /**
     *
     * @param code
     * The code
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     *
     * @return
     * The description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     * The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     * The data
     */
    public Boolean getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(Boolean data) {
        this.data = data;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}