package com.azinecllc.champy.model;

/**
 * @autor SashaKhyzhun
 * Created on 4/10/17.
 */

public class StreakSection {

    private int dayNumber;
    private String dayStatus;


    public StreakSection(int dayNumber, String dayStatus) {
        this.dayNumber = dayNumber;
        this.dayStatus = dayStatus;
    }


    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public void setDayStatus(String dayStatus) {
        this.dayStatus = dayStatus;
    }


    public int getDayNumber() {
        return dayNumber;
    }

    public String getDayStatus() {
        return dayStatus;
    }


}
