package org.niko.timertracker.workdayslistactivity.ActivityViewLayer;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.niko.timertracker.workdayslistactivity.Model.Workday;
import org.niko.timertracker.workdayslistactivity.Persistence.FirebaseService;

import java.util.LinkedHashMap;

public class WorkdayViewModel extends AndroidViewModel {

    private FirebaseService firebaseService;
    private LiveData<LinkedHashMap<Long, Workday>> mAllWorkdays;
    private boolean isWorkIntervalRunning;

    public WorkdayViewModel (Application application) {
        super(application);
        firebaseService = new FirebaseService();
        mAllWorkdays = firebaseService.getAllWorkdays();
    }

    public boolean isWorkIntervalRunning() {
        return isWorkIntervalRunning;
    }

    public void setWorkIntervalRunning(boolean workIntervalRunning) {
        isWorkIntervalRunning = workIntervalRunning;
    }

    public LiveData<LinkedHashMap<Long, Workday>> getAllWorkdays() {
        return mAllWorkdays;
    }

    public LiveData<Workday> getWorkdayById(Long id) {
        return this.firebaseService.getWorkdayById(id);
    }

    public void addWorkday() {
        firebaseService.addWorkday();
    }

    public void addWorkInterval(Long workdayId, boolean isStartTime) {
        firebaseService.addWorkInterval(workdayId, isStartTime);
    }

    public void updateWorkdayDate(Workday workday, Long previousWorkdayId) {
        firebaseService.updateWorkdayDate(workday, previousWorkdayId);
    }

    public void updateWorkIntervals(long workdayId, int workIntervalId, Long workIntervalTime, boolean isStartTime) {
        firebaseService.updateWorkIntervals(workdayId, workIntervalId, workIntervalTime, isStartTime);
    }
}
