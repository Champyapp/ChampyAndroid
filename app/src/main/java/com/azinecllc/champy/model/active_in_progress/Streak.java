package com.azinecllc.champy.model.active_in_progress;

import java.util.ArrayList;
import java.util.List;

/**
 * @autor SashaKhyzhun
 * Created on 4/10/17.
 */

public class Streak {

    private String streakLabel;
    private String streakStatus;
    private List<StreakSection> streakSections = new ArrayList<>();


    public void setStreakSections(List<StreakSection> streakSections) {
        this.streakSections = streakSections;
    }

    public void setStreakLabel(String streakLabel) {
        this.streakLabel = streakLabel;
    }

    public void setStreakStatus(String streakStatus) {
        this.streakStatus = streakStatus;
    }


    public List<StreakSection> getStreakSections() {
        return streakSections;
    }

    public String getStreakLabel() {
        return streakLabel;
    }

    public String getStreakStatus() {
        return streakStatus;
    }

}
