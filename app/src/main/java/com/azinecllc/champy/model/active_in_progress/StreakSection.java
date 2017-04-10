package com.azinecllc.champy.model.active_in_progress;

/**
 * @autor SashaKhyzhun
 * Created on 4/10/17.
 */

public class StreakSection {

    private int dayNumber;
    private String dayStatus;


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
