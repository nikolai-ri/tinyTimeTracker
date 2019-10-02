package org.niko.timertracker.workdayslistactivity.Persistence;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import org.niko.timertracker.workdayslistactivity.Model.Workday;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.transform.Result;

@IgnoreExtraProperties
public class FirebaseService {

    private final String LOG_TAG = "Firebase_Service:";

    private FirebaseUser firebaseUser;
    private DatabaseReference mDatabase;
    private MutableLiveData<LinkedHashMap<Long, Workday>> allWorkdays;
    private MutableLiveData<Workday> workdayLiveData;
    private LinkedHashMap<Long, Workday> cachedWorkdayList;
    private static boolean isWorkIntervalRunning;

    private DatabaseReference workdaysBasePath;

    public FirebaseService() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        workdaysBasePath = mDatabase.child(firebaseUser.getUid()).child("workdays");
    }

    /**
     * Gets the LiveData object of the workdays list, or sets it up if it has not been done (application start).
     * The setup mainly attaches an event handler to the database reference, to set the workdays list each time
     * a value changes in the database.
     *
     * @return
     */
    public LiveData<LinkedHashMap<Long, Workday>> getAllWorkdays(){
        if(allWorkdays == null) {
            allWorkdays = new MutableLiveData<>();
            workdaysBasePath.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        LinkedHashMap<Long, Workday> linkedHashMapWorkdays = toWorkdays(dataSnapshot);
                        cachedWorkdayList = linkedHashMapWorkdays;
                        reorderWorkdayListAfterChange();
                        allWorkdays.postValue(cachedWorkdayList);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        return allWorkdays;
    }

    public void reorderWorkdayListAfterChange() {
        if(allWorkdays != null && allWorkdays.getValue() != null && allWorkdays.getValue().size() > 0) {
            cachedWorkdayList = sortByValue(cachedWorkdayList);
        }
    }

    public LiveData<Workday> getWorkdayById(Long id) {
        workdayLiveData = new MutableLiveData<>();
        workdaysBasePath.child(String.valueOf(id)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    workdayLiveData.postValue(toWorkday((HashMap<String, Object>) dataSnapshot.getValue()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return workdayLiveData;
    }

    public void addWorkday() {

        Long idOfNewDay;

        if (allWorkdays != null && allWorkdays.getValue() != null) {
            final AtomicLong maxId = new AtomicLong();

            allWorkdays.getValue().forEach((aLong, workday) -> {

                if (aLong > maxId.get()) maxId.set(aLong);

            });

            idOfNewDay = maxId.get() + (24 * 3600 * 1000) + 1;
        } else {
            LocalDateTime localDateTime = ZonedDateTime.now(ZoneId.systemDefault()).toLocalDate().atStartOfDay();
            idOfNewDay = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }

        writeWorkday(createNewWorkday(idOfNewDay));
    }

    public void updateWorkdayDate(Workday workday, Long previousWorkdayId) {
        new FirebaseService.updateWorkdayDateAsyncTask().execute(workday, previousWorkdayId);
    }

    private class updateWorkdayDateAsyncTask extends AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... parameters) {
            Workday workday = (Workday) parameters[0];
            workdaysBasePath.child(String.valueOf(parameters[1])).removeValue();
            workdaysBasePath.child(String.valueOf(workday.getId())).setValue(workday);
            return null;
        }
    }

    /**
     * Writes a workday to the firebase database.
     * @param workday
     */
    private void writeWorkday(Workday workday) {
        new FirebaseService.writeWorkdayAsyncTask().execute(workday);
    }

    private class writeWorkdayAsyncTask extends AsyncTask<Workday, Void, Result> {

        @Override
        protected Result doInBackground(final Workday... workdays) {
            workdaysBasePath.child(String.valueOf(workdays[0].getId())).setValue(workdays[0]);
            return null;
        }
        @Override
        protected void onPostExecute(Result result) {
            reorderWorkdayListAfterChange();
        }


    }

    public void updateWorkIntervals(long workdayId, int workIntervalId, long workIntervalTime, boolean isStartTime) {
        try {

            ArrayList<Long> workInterval = allWorkdays.getValue().get(workdayId).getWorkIntervals().get(workIntervalId);

            if (isStartTime) {
                workInterval.set(0, workIntervalTime);
            }
            else {
                workInterval.set(1, workIntervalTime);
            }

            this._addWorkIntervals(workdayId, workIntervalId, workInterval);

        } catch (NullPointerException e) {
            Log.d(LOG_TAG, "Updating a workInterval threw NullPointerException. Probably the workInterval with the given ID does not exist.");
        }


    }

    public void addWorkInterval(long workdayId, boolean isStartTime){

        ArrayList<Long> newInterval;
        Integer workdayIntervalId;

        if (isStartTime) {

            LinkedHashMap<Long, Workday> workdayLinkedHashMap = this.allWorkdays.getValue();
            Workday workday = workdayLinkedHashMap.get(workdayId);
            if (workday.getWorkIntervals() != null) {
                ArrayList<ArrayList<Long>> arrayListArrayList = workday.getWorkIntervals();
                workdayIntervalId = arrayListArrayList.size();
            } else {
                workdayIntervalId = 0;
            }

            newInterval = new ArrayList<>();
            Long startTime = Instant.now().toEpochMilli();

            newInterval.add(startTime);
            newInterval.add(startTime); // stub


        } else {

            workdayIntervalId = this.allWorkdays.getValue().get(workdayId).getWorkIntervals().size() - 1;

            newInterval = this.allWorkdays.getValue().get(workdayId).getWorkIntervals().get(workdayIntervalId);

            newInterval.set(1, Instant.now().toEpochMilli());
        }

        this._addWorkIntervals(workdayId, workdayIntervalId, newInterval);
    }
    /**
     * Updates the work intervals of an existing workday in the database.
     * @param workdayId
     * @param workInterval
     * @param workIntervalId
     */
    private void _addWorkIntervals(Long workdayId, Integer workIntervalId, ArrayList<Long> workInterval) {
        new FirebaseService.addWorkIntervalsAsyncTask().execute(workdayId, workIntervalId, workInterval);
    }

    private class addWorkIntervalsAsyncTask extends AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(final Object... params) {
            workdaysBasePath
                    .child((String.valueOf(params[0])))
                    .child("workIntervals")
                    .child(String.valueOf(params[1]))
                    .setValue(params[2]);
            return null;
        }
    }

    /**
     * Deletes a workInterval with a given id
     */
    public void deleteWorkIntervalById(Workday workday, int workIntervalId) {
        workday.getWorkIntervals().remove(workIntervalId);
        workdaysBasePath.child(String.valueOf(workday.getId())).child("workIntervals").setValue(workday.getWorkIntervals());
    }

    private LinkedHashMap<Long, Workday> sortByValue(LinkedHashMap<Long, Workday> linkedHashMap) {
        List<Map.Entry<Long, Workday>> list = new ArrayList<>(linkedHashMap.entrySet());
        list.sort(Map.Entry.comparingByValue((Workday workday, Workday t1) -> Long.compare(workday.getId(), t1.getId())));

        LinkedHashMap<Long, Workday> result = new LinkedHashMap<>();
        for (Map.Entry<Long, Workday> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    /**
     * Upon retrieval of the database objects, they need to be converted into Java objects. This is done here.
     * @param dataSnapshot
     * @return
     */
    private LinkedHashMap<Long, Workday> toWorkdays(DataSnapshot dataSnapshot) {

        LinkedHashMap<Long, Workday> linkedHashMapWorkdays = new LinkedHashMap<>();
        Workday currentWorkday;

        Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
        while (iterator.hasNext()) {
            currentWorkday = toWorkday((HashMap<String, Object>) iterator.next().getValue());
            linkedHashMapWorkdays.put(currentWorkday.getId(), currentWorkday);
        }

        return linkedHashMapWorkdays;
    }

    /**
     * Upon retrieval of the database objects, they need to be converted into Java objects. This is done here.
     * @param uncastedFirebaseWorkdayObject
     * @return
     */
    private Workday toWorkday(HashMap<String, Object> uncastedFirebaseWorkdayObject) {

        Workday workday = new Workday((Long) uncastedFirebaseWorkdayObject.get("id"),
                (ArrayList<ArrayList<Long>>) uncastedFirebaseWorkdayObject.get("workIntervals"),
                (Long) uncastedFirebaseWorkdayObject.get("instantInTime"),
                (ArrayList<String>) (uncastedFirebaseWorkdayObject.get("workItems") != null ? uncastedFirebaseWorkdayObject.get("workItems") : new ArrayList<String>()));

        return workday;
    }

    private Workday createNewWorkday(Long newWorkdayId) {

        ArrayList<Long> workInterval = new ArrayList<>();
        workInterval.add(Long.valueOf(0));
        workInterval.add(Long.valueOf(0)); // stub for endtime so the array has the right length

        ArrayList<ArrayList<Long>> workIntervals = new ArrayList<>();
        workIntervals.add(workInterval);

        return new Workday(newWorkdayId, workIntervals, newWorkdayId, new ArrayList<>());
    }

    public static boolean isIsWorkIntervalRunning() {
        return isWorkIntervalRunning;
    }

    public static void setIsWorkIntervalRunning(boolean isWorkIntervalRunning) {
        FirebaseService.isWorkIntervalRunning = isWorkIntervalRunning;
    }
}
