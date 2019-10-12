package org.niko.timertracker.workdayslistactivity.Model;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ListIterator;
import java.util.TimeZone;

public class Workday {

    public Workday(Long id, ArrayList<ArrayList<Long>> workIntervals, Long instantInTime, ArrayList<String> workItems) {
        this.id = id;
        this.workIntervals = workIntervals;
        this.instantInTime = instantInTime;
        this.workItems = workItems;
    }

    public Workday(){

    }

    private Long id;

    @NonNull
    private Long instantInTime;

    private ArrayList<String> workItems;

    @NonNull
    private ArrayList<ArrayList<Long>> workIntervals;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ArrayList<ArrayList<Long>> getWorkIntervals() {
        return workIntervals;
    }

    public void setWorkIntervals(ArrayList<ArrayList<Long>> workIntervals) {
        this.workIntervals = workIntervals;
    }

    public ArrayList<String> getWorkItems() {
        return workItems;
    }

    public void setWorkItems(ArrayList<String> workItems) {
        this.workItems = workItems;
    }

    @NonNull
    public Long getInstantInTime() {
        return instantInTime;
    }

    public void setInstantInTime(@NonNull Long instantInTime) {
        this.instantInTime = instantInTime;
    }

    public String toString() {
        return this.toStartEndTimeString() + " at " + this.toDateString();
    }

    public String toDateString(){

        DateFormat formatter = new SimpleDateFormat("dd.MM.YY");

        String stringDate = formatter.format(this.instantInTime);

        return stringDate;
    }

    public String toStartEndTimeString() {

        if (this.workIntervals != null && this.workIntervals.size() > 0) {

            Long effectiveEndTimeWithoutPauses = this.workIntervals.get(0).get(0);
            ListIterator<ArrayList<Long>> workIntervalIterator = this.workIntervals.listIterator();

            // Calculates the numbers of hours worked in total for a day, respecting the pauses.
            while(workIntervalIterator.hasNext()) {
                ArrayList<Long> currentInterval = workIntervalIterator.next();
                effectiveEndTimeWithoutPauses += currentInterval.get(1) - currentInterval.get(0);
            }

            String workedHours = calculateWorkedHoursToString(this.workIntervals.get(0).get(0), effectiveEndTimeWithoutPauses);

            DateFormat formatter = new SimpleDateFormat("HH:mm");
            formatter.setTimeZone(TimeZone.getDefault());

            String startTimeFormatted = formatter.format(this.workIntervals.get(0).get(0));
            String endTimeFormatted = formatter.format(workIntervals.get(workIntervals.size() - 1).get(1));

            return startTimeFormatted + "-" + endTimeFormatted + " @ " + workedHours;
        } else {
            return "No work interval yet!";
        }

    }

    public static String toHourMinuteString(Long selectedTime){
        Date time = new Date(selectedTime);

        DateFormat formatter = new SimpleDateFormat("HH:mm");
        formatter.setTimeZone(TimeZone.getDefault());

        return formatter.format(time);
    }

    private static String calculateWorkedHoursToString(long startTime, long endTime) {
        return (((endTime - startTime) / 1000 / 3600) + ":" +  (((endTime - startTime) / 60000) % 60));
    }

}
