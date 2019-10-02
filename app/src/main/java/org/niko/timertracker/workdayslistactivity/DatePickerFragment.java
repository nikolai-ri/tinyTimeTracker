package org.niko.timertracker.workdayslistactivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import org.niko.timertracker.workdayslistactivity.ActivityViewLayer.WorkdayViewModel;
import org.niko.timertracker.workdayslistactivity.Model.Workday;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.TimeZone;

public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private Long previousWorkdayId;
        private Workday workday;
        private WorkdayViewModel workdayViewModel;
        private Context context;


        public DatePickerFragment(Long previousWorkdayId, Context context) {
            this.previousWorkdayId = previousWorkdayId;
            this.context = context;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            this.workdayViewModel = new ViewModelProvider(getActivity()).get(WorkdayViewModel.class);
            final DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, 2000, 0, 0); // year needs to be != 0, otherwise the datepickerdialog is set to a wrong date :(

            this.workdayViewModel.getWorkdayById(this.previousWorkdayId).observe(this, new Observer<Workday>() {
                @Override
                public void onChanged(@Nullable final Workday workdayObject) {

                    workday = workdayObject;
                    LocalDate localDate = Instant.ofEpochMilli(workday.getInstantInTime()).atZone(TimeZone.getDefault().toZoneId()).toLocalDate();
                    datePickerDialog.updateDate(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());

                }
            });

            // Create a new instance of TimePickerDialog and return it
            return datePickerDialog;
        }

        public void onDateSet(DatePicker var1, int year, int month, int day) {

            LocalDate newLocalDate = LocalDate.of(year, month + 1, day);
            Instant instantAtStartOfNewDay = newLocalDate.atStartOfDay(TimeZone.getDefault().toZoneId()).toInstant();

            LocalDate previousLocalDate = Instant.ofEpochMilli(workday.getInstantInTime()).atZone(TimeZone.getDefault().toZoneId()).toLocalDate();
            Instant instantAtStartOfPreviousDay = previousLocalDate.atStartOfDay(TimeZone.getDefault().toZoneId()).toInstant();

            Long millisDifferenceBetweenPrevAndNewDate = instantAtStartOfNewDay.toEpochMilli() - instantAtStartOfPreviousDay.toEpochMilli();

            workday.setId(instantAtStartOfNewDay.toEpochMilli());

            ListIterator workIntervalIterator = workday.getWorkIntervals().listIterator();

            while (workIntervalIterator.hasNext()) {

                ArrayList<Long> currentWorkInterval = (ArrayList<Long>) workIntervalIterator.next();

                currentWorkInterval.set(0, currentWorkInterval.get(0) + millisDifferenceBetweenPrevAndNewDate);
                currentWorkInterval.set(1, currentWorkInterval.get(1) + millisDifferenceBetweenPrevAndNewDate);

            }

            workday.setInstantInTime(workday.getInstantInTime() + millisDifferenceBetweenPrevAndNewDate);

            workdayViewModel.updateWorkdayDate(workday, previousWorkdayId);

        }

}