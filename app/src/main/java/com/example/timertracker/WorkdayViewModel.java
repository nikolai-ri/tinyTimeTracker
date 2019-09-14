package com.example.timertracker;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class WorkdayViewModel extends AndroidViewModel {

    private WorkdayRepository mWorkdayRepository;
    private LiveData<List<Workday>> mAllWorkdays;

    public WorkdayViewModel (Application application) {
        super(application);
        mWorkdayRepository = new WorkdayRepository(application);
        mAllWorkdays = mWorkdayRepository.getAllWorkdays();
    }

    LiveData<List<Workday>> getAllWorkdays() {
        return mAllWorkdays;
    }

    LiveData<Workday> getWorkdayById(long id) {
        return this.mWorkdayRepository.getWorkdayById(id);
    }

    public void insert(Workday workday) {
        mWorkdayRepository.insert(workday);
    }
    public void update(Workday workday) { mWorkdayRepository.update(workday);}
}
