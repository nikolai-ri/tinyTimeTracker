package com.example.timertracker;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

@Entity(tableName = "workday_table")
public class Workday {

    Workday(long startTime, long endTime, String stringDate, ArrayList<String> workItems) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.stringDate = stringDate;
        this.workItems = workItems;
    }

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private long startTime;

    @NonNull
    private long endTime;

    @NonNull
    private String stringDate;

    private ArrayList<String> workItems;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public ArrayList<String> getWorkItems() {
        return workItems;
    }

    public void setWorkItems(ArrayList<String> workItems) {
        this.workItems = workItems;
    }

    @NonNull
    public String getStringDate() {
        return stringDate;
    }

    public void setStringDate(@NonNull String stringDate) {
        this.stringDate = stringDate;
    }

    public String toString(){

        Date startDate = new Date(this.startTime);
        Date endDate = new Date(this.endTime);

        String workedHours = formatMilliIntoHours(this.startTime, this.endTime);


        DateFormat formatter = new SimpleDateFormat("HH:mm");
        formatter.setTimeZone(TimeZone.getDefault());

        String startDateFormatted = formatter.format(startDate);
        String endDateFormatted = formatter.format(endDate);


        return startDateFormatted + " - " + endDateFormatted + " = " + workedHours + " at " + this.stringDate;
    }


    private String formatMilliIntoHours(long startTime, long endTime) {
        return (int) (((endTime - startTime) / 1000) / 3600) + ":" +  (((endTime - startTime) / 60000) % 60);
    }


}
