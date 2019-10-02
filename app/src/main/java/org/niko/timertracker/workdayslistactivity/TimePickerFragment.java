package org.niko.timertracker.workdayslistactivity;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import org.niko.timertracker.workdayslistactivity.ActivityViewLayer.WorkdayViewModel;
import org.niko.timertracker.workdayslistactivity.Model.Workday;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private boolean isStartTime;
    private Long workdayId;
    private int workIntervalId;
    private Workday workday;
    private WorkdayViewModel workdayViewModel;
    private long currentlySetTime;


    public TimePickerFragment(Long workdayId, int workIntervalId, boolean isStartTime) {
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

                if(isStartTime) currentlySetTime = workday.getWorkIntervals().get(workIntervalId).get(0);
                else            currentlySetTime = workday.getWorkIntervals().get(workIntervalId).get(1) ;

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
        gregorianCalendar.setTimeInMillis(workday.getWorkIntervals().get(0).get(0));
        gregorianCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        gregorianCalendar.set(Calendar.MINUTE, minute);

        this.workdayViewModel.updateWorkIntervals(workday.getId(), workIntervalId, gregorianCalendar.getTimeInMillis(), isStartTime);

    }
}
