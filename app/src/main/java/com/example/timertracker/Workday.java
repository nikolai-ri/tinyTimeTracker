package com.example.timertracker;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

@Entity(tableName = "workday_table")
public class Workday {

    Workday(long startTime, long endTime, int dayOfYear) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.dayOfYear = dayOfYear;
    }

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private long startTime;

    @NonNull
    private long endTime;

    @NonNull
    private int dayOfYear;

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

    public int getDayOfYear() {
        return dayOfYear;
    }

    public void setDayOfYear(int dayOfYear) {
        this.dayOfYear = dayOfYear;
    }

    public String toString(){

        Date startDate = new Date(this.startTime);
        Date endDate = new Date(this.endTime);
        Date difference = new Date(this.endTime - startTime);
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        String startDateFormatted = formatter.format(startDate);
        String endDateFormatted = formatter.format(endDate);
        String differenceFormatted = formatter.format(difference);


        return startDateFormatted + " - " + endDateFormatted + " = " + differenceFormatted + " at " + this.dayOfYear;
    }
}
