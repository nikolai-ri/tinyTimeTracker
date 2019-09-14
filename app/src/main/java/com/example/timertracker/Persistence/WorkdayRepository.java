package com.example.timertracker.Persistence;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.timertracker.Model.Workday;

import java.util.List;

public class WorkdayRepository {

    private WorkdayDao mWorkdayDao;
    private LiveData<List<Workday>> mAllWorkdays;

    public WorkdayRepository(Application application) {
        WorkdayRoomDatabase db = WorkdayRoomDatabase.getDatabase(application);
        mWorkdayDao = db.workdayDao();
        mAllWorkdays = mWorkdayDao.getAllWorkdays();
    }

    public LiveData<List<Workday>> getAllWorkdays() {
        return mAllWorkdays;
    }
    public LiveData<Workday> getWorkdayById(long id) {return this.mWorkdayDao.getWorkdayById(id);}

    public void insert (Workday workday) {
        new insertAsyncTask(mWorkdayDao).execute(workday);
    }
    public void update (Workday workday) { new updateAsyncTask(mWorkdayDao).execute(workday); }

    private static class insertAsyncTask extends AsyncTask<Workday, Void, Void> {

        private WorkdayDao mAsyncTaskDao;

        insertAsyncTask(WorkdayDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Workday... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
    private static class updateAsyncTask extends AsyncTask<Workday, Void, Void> {

        private WorkdayDao mAsyncTaskDao;

        updateAsyncTask(WorkdayDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Workday... params) {
            mAsyncTaskDao.update(params[0]);
            return null;
        }
    }

}
