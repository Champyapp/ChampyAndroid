package com.azinecllc.champy.model.active_in_progress;

/**
 * @autor SashaKhyzhun
 * Created on 4/10/17.
 */

public class StreakSection {

    private int dayNumber;
    private int dayStatus;
    private int isCurrentDay;



    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public void setDayStatus(int dayStatus) {
        this.dayStatus = dayStatus;
    }

    public void setCurrentDay(int currentDay) {
        isCurrentDay = currentDay;
    }


    public int getDayNumber() {
        return dayNumber;
    }

    public int getDayStatus() {
        return dayStatus;
    }

    public int isCurrentDay() {
        return isCurrentDay;
    }


}
