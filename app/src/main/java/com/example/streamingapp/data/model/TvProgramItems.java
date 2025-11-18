package com.example.streamingapp.data.model;

public class TvProgramItems {
    int programImg;
    String programTiming, programTitle;

    public TvProgramItems(int programImg, String programTiming, String programTitle) {
        this.programImg = programImg;
        this.programTiming = programTiming;
        this.programTitle = programTitle;
    }

    public int getProgramImg() {
        return programImg;
    }

    public void setProgramImg(int programImg) {
        this.programImg = programImg;
    }

    public String getProgramTiming() {
        return programTiming;
    }

    public void setProgramTiming(String programTiming) {
        this.programTiming = programTiming;
    }

    public String getProgramTitle() {
        return programTitle;
    }

    public void setProgramTitle(String programTitle) {
        this.programTitle = programTitle;
    }
}
