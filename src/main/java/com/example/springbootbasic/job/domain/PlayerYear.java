package com.example.springbootbasic.job.domain;

import lombok.Data;

import java.time.Year;

@Data
public class PlayerYear {

    private String ID;
    private String lastName;
    private String firstName;
    private String position;
    private int birthYear;
    private int debutYear;
    private int yearsExperience;

    public static PlayerYear of(Player item) {
        PlayerYear p = new PlayerYear();
        p.ID = item.getID();
        p.lastName = item.getLastName();
        p.firstName = item.getFirstName();
        p.position = item.getPosition();
        p.birthYear = item.getBirthYear();
        p.debutYear = item.getDebutYear();
        p.yearsExperience = Year.now().getValue() - p.debutYear;
        return p;
    }
}