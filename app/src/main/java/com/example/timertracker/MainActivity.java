package com.example.timertracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.timertracker.ActivityViewLayer.WorkdayListAdapter;
import com.example.timertracker.ActivityViewLayer.WorkdayViewModel;
import com.example.timertracker.Model.Workday;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private WorkdayViewModel mWorkdayViewModel;

    WorkdayListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader = new ArrayList<>();
    HashMap<String, List<Long>> listDataChild = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this.mAddWorkdayButton_OnClickListener);

        // get the listview
        expListView = findViewById(R.id.lvExp);

        mWorkdayViewModel = new ViewModelProvider(this).get(WorkdayViewModel.class);

        listAdapter = new WorkdayListAdapter(this, listDataHeader, listDataChild);

        mWorkdayViewModel.getAllWorkdays().observe(this, new Observer<List<Workday>>() {
            @Override
            public void onChanged(@Nullable final List<Workday> workdays) {
                listAdapter.setWorkdays(workdays);
            }
        });

        // setting list adapter
        expListView.setAdapter(listAdapter);

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                DialogFragment newFragment = new TimePickerFragment(
                        intent.getLongExtra("workdayId", 0),
                        intent.getBooleanExtra("isStartTime", true));
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        }
        , new IntentFilter("open.timePicker.fragment_82346234"));
    }

    final View.OnClickListener mAddWorkdayButton_OnClickListener = new View.OnClickListener() {
        public void onClick(final View v) {
            ListIterator<Workday> iterator = mWorkdayViewModel.getAllWorkdays().getValue().listIterator();
            Calendar gregorianCalendar = GregorianCalendar.getInstance(TimeZone.getDefault());
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            String stringDate = sdf.format(gregorianCalendar.getTime());

            while(iterator.hasNext()) {
                Workday workday = iterator.next();
                if(workday.getStringDate().equals(stringDate)) {
                    workday.setEndTime(gregorianCalendar.getTimeInMillis());
                    mWorkdayViewModel.update(workday);
                    return;
                }
            }

            Workday workday = new Workday(gregorianCalendar.getTimeInMillis(), 0, stringDate, new ArrayList<String>());
            mWorkdayViewModel.insert(workday);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
