package com.azinecllc.champy.model;

/**
 * @autor SashaKhyzhun
 * Created on 4/10/17.
 */

public class StreakSection {

    private int dayNumber;
    private int dayStatus;
    private int dayCurrent;


    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public void setCurrentDay(int dayStatus) {
        this.dayCurrent = dayStatus;
    }

    public void setDayStatus(int dayStatus) {
        this.dayStatus = dayStatus;
    }


    public int getDayStatus() {
        return dayStatus;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public int getDayCurrent() {
        return dayCurrent;
    }


}
