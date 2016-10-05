package com.example.ivan.champy_v2.model;

import com.example.ivan.champy_v2.Friend;

import java.util.List;

public class Other {

    private List<Friend> other;

    public Other (List<Friend> list) {
        this.other = list;
    }

    public List<Friend> getOther() {
        return other;
    }

    public void add(Friend friend) {
        other.add(friend);
    }
}
