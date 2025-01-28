package com.example.streamingapp;

import java.util.List;

public class CastItems {
    String personName, personDesignation;
    int personImg;
    private List<String> filmographies;
    private List<String> biographyDetails;
    public CastItems(String personName, String personDesignation, int personImg) {
        this.personName = personName;
        this.personDesignation = personDesignation;
        this.personImg = personImg;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonDesignation() {
        return personDesignation;
    }

    public void setPersonDesignation(String personDesignation) {
        this.personDesignation = personDesignation;
    }

    public int getPersonImg() {
        return personImg;
    }

    public void setPersonImg(int personImg) {
        this.personImg = personImg;
    }
}
