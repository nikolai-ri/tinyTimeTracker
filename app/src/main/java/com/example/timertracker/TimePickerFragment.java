package com.example.timertracker;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.timertracker.ActivityViewLayer.WorkdayViewModel;
import com.example.timertracker.Model.Workday;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private boolean isStartTime;
    private long workdayId;
    private int workIntervalId;
    private Workday workday;
    private WorkdayViewModel workdayViewModel;
    private long currentlySetTime;


    public TimePickerFragment(long workdayId, int workIntervalId, boolean isStartTime) {
        this.isStartTime = isStartTime;
        this.workdayId = workdayId;
        this.workIntervalId = workIntervalId;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        this.workdayViewModel = new ViewModelProvider(getActivity()).get(WorkdayViewModel.class);
        final TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), this, 0,0,
                DateFormat.is24HourFormat(getActivity()));

        this.workdayViewModel.getWorkdayById(this.workdayId).observe(this, new Observer<Workday>() {
            @Override
            public void onChanged(@Nullable final Workday workdayObject) {

                workday = workdayObject;

                if(isStartTime) currentlySetTime = workday.getStarttimeById(workIntervalId);
                else            currentlySetTime = workday.getWorkIntervallsById(workIntervalId).size() > 1 ?
                        workday.getEndtimeById(workIntervalId) : workday.getStarttimeById(workIntervalId);

                // Use the current time as the default values for the picker
                final Calendar gregorianCalendar = GregorianCalendar.getInstance(TimeZone.getDefault());
                gregorianCalendar.setTimeInMillis(currentlySetTime);
                int hour = gregorianCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = gregorianCalendar.get(Calendar.MINUTE);

                timePickerDialog.updateTime(hour, minute);
            }
        });

        // Create a new instance of TimePickerDialog and return it
        return timePickerDialog;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        final Calendar gregorianCalendar = GregorianCalendar.getInstance(TimeZone.getDefault());
        gregorianCalendar.setTimeInMillis(workday.getStarttimeById(0));
        gregorianCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        gregorianCalendar.set(Calendar.MINUTE, minute);

        if(isStartTime) this.workday.getWorkIntervallsById(workIntervalId).set(0, gregorianCalendar.getTimeInMillis());
        else            this.workday.getWorkIntervallsById(workIntervalId).set(1, gregorianCalendar.getTimeInMillis());

        this.workdayViewModel.update(workday);
    }
}
