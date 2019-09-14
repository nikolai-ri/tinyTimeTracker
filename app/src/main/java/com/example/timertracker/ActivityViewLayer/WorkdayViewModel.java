package com.example.timertracker.ActivityViewLayer;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.timertracker.Model.Workday;
import com.example.timertracker.Persistence.WorkdayRepository;

import java.util.List;

public class WorkdayViewModel extends AndroidViewModel {

    private WorkdayRepository mWorkdayRepository;
    private LiveData<List<Workday>> mAllWorkdays;

    public WorkdayViewModel (Application application) {
        super(application);
        mWorkdayRepository = new WorkdayRepository(application);
        mAllWorkdays = mWorkdayRepository.getAllWorkdays();
    }

    public LiveData<List<Workday>> getAllWorkdays() {
        return mAllWorkdays;
    }

    public LiveData<Workday> getWorkdayById(long id) {
        return this.mWorkdayRepository.getWorkdayById(id);
    }

    public void insert(Workday workday) {
        mWorkdayRepository.insert(workday);
    }
    public void update(Workday workday) { mWorkdayRepository.update(workday);}
}
