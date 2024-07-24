package com.example.todaybuddy;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "mynotes")
public class notes {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String displayText;
    private String date; // Add this field

    // Constructor
    public notes(String title, String displayText, String date) {
        this.title = title;
        this.displayText = displayText;
        this.date = date;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
