package com.azinecllc.champy.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bobyk on 3/23/17.
 */

public class SectionModel {

    private String label;
    private List<Integer> items = new ArrayList<>();

    public void setItems(List<Integer> items) {
        this.items = items;
    }

    public void setLabel(String label) {
        this.label = label;
    }


    public List<Integer> getItems() {
        return items;
    }

    public String getLabel() {
        return label;
    }

}
