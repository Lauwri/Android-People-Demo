package com.example.lauri.androiddemo.Content;

import java.util.Date;

/**
 * Created by Lauri Mattila on 9.2.2018.
 * Person object class
 */

public class PersonModel {
    private int id;
    private Date dateTimeAdded;
    private String name;
    private String description;

    public PersonModel(int id, String name, String description, Date dateTime) {
        setId(id);
        setDateTimeAdded(dateTime);
        setDescription(description);
        setName(name);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateTimeAdded() {
        return dateTimeAdded;
    }

    public void setDateTimeAdded(Date dateTimeAdded) {
        this.dateTimeAdded = dateTimeAdded;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}