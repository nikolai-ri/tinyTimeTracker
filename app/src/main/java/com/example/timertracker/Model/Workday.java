package com.example.timertracker.Model;

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

    public Workday(ArrayList<ArrayList<Long>> workIntervalls, String stringDate, ArrayList<String> workItems) {
        this.workIntervalls = workIntervalls;
        this.stringDate = stringDate;
        this.workItems = workItems;
    }

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String stringDate;

    private ArrayList<String> workItems;

    private ArrayList<ArrayList<Long>> workIntervalls;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ArrayList<ArrayList<Long>> getWorkIntervalls() {
        return workIntervalls;
    }

    public void workIntervalls(ArrayList<ArrayList<Long>> workIntervalls) {
        this.workIntervalls = workIntervalls;
    }

    public ArrayList<Long> getWorkIntervallsById(int id) {
        return this.workIntervalls.get(id);
    }

    public void setWorkIntervalls(int id, ArrayList<Long> workIntervalls) {
        this.workIntervalls.set(id, workIntervalls);
    }

    public Long getStarttimeById(int id) {
        return this.workIntervalls.get(id).get(0);
    }

    public void setStarttimeById(int id, Long starttime) {
        this.workIntervalls.get(id).set(0, starttime);
    }

    public Long getEndtimeById(int id) {
        return this.workIntervalls.get(id).get(1);
    }

    public void setEndtimeById(int id, Long endtime) {
        this.workIntervalls.get(id).set(1, endtime);
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

        Date startTime = new Date(this.workIntervalls.get(0).get(0));

        // in case the most recent added workinterval is still running, the lastworkintervaltoday gives an index out of bound if one tries to get the endtime of that interval -> starttime ist taken as endtime of day
        ArrayList<Long> lastWorkIntervalToday = this.workIntervalls.get(this.workIntervalls.size() - 1);
        Date endTime = lastWorkIntervalToday.size() > 1 ? new Date(lastWorkIntervalToday.get(1)) : new Date(lastWorkIntervalToday.get(0));

        String workedHours = calculateWorkedHoursToString(startTime.getTime(), endTime.getTime());


        DateFormat formatter = new SimpleDateFormat("HH:mm");
        formatter.setTimeZone(TimeZone.getDefault());

        String startTimeFormatted = formatter.format(startTime);
        String endTimeFormatted = formatter.format(endTime);


        return startTimeFormatted + " - " + endTimeFormatted + " = " + workedHours + " at " + this.stringDate;
    }

    public static String toHourMinuteString(Long selectedTime){
        Date time = new Date(selectedTime);

        DateFormat formatter = new SimpleDateFormat("HH:mm");
        formatter.setTimeZone(TimeZone.getDefault());

        return formatter.format(time);
    }


    private static String calculateWorkedHoursToString(long startTime, long endTime) {
        return (int) (((endTime - startTime) / 1000) / 3600) + ":" +  (((endTime - startTime) / 60000) % 60);
    }


}
