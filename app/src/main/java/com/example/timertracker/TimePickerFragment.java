package com.example.timertracker;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private boolean isStartTime;
    private long workdayId;
    private Workday workday;
    private WorkdayViewModel workdayViewModel;
    private long currentlySetTime;
    private Context context;


    public TimePickerFragment(Context context, long workdayId, boolean isStartTime) {
        this.isStartTime = isStartTime;
        this.workdayId = workdayId;
        this.context = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        this.workdayViewModel = new ViewModelProvider(getActivity()).get(WorkdayViewModel.class);
        final TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), this, 0,0,
                DateFormat.is24HourFormat(getActivity()));

        workday = this.workdayViewModel.getWorkdayById(this.workdayId).getValue();

        this.workdayViewModel.getWorkdayById(this.workdayId).observe(this, new Observer<Workday>() {
            @Override
            public void onChanged(@Nullable final Workday workdayObject) {
                workday = workdayObject;
                if(isStartTime) currentlySetTime = workday.getStartTime();
                else            currentlySetTime = workday.getEndTime();

                // Use the current time as the default values for the picker
                final Calendar gregorianCalendar = GregorianCalendar.getInstance(TimeZone.getDefault());
                gregorianCalendar.setTimeInMillis(currentlySetTime);
                int hour = gregorianCalendar.get(Calendar.HOUR);
                int minute = gregorianCalendar.get(Calendar.MINUTE);

                timePickerDialog.updateTime(hour, minute);
            }
        });

        // Create a new instance of TimePickerDialog and return it
        return timePickerDialog;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        final Calendar gregorianCalendar = GregorianCalendar.getInstance(TimeZone.getDefault());
        gregorianCalendar.setTimeInMillis(workday.getStartTime());
        gregorianCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        gregorianCalendar.set(Calendar.MINUTE, minute);

        if(isStartTime) this.workday.setStartTime(gregorianCalendar.getTimeInMillis());
        else            this.workday.setEndTime(gregorianCalendar.getTimeInMillis());

        this.workdayViewModel.update(workday);
    }
}
