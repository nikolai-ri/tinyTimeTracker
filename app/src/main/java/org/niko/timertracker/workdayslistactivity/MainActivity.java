package org.niko.timertracker.workdayslistactivity;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.niko.timertracker.EmailPasswordActivity.EmailPasswordActivity;
import org.niko.timertracker.workdayslistactivity.ActivityViewLayer.WorkdayListAdapter;
import org.niko.timertracker.workdayslistactivity.ActivityViewLayer.WorkdayViewModel;
import org.niko.timertracker.workdayslistactivity.Model.Workday;
import org.niko.timertracker.workdayslistactivity.Persistence.FirebaseService;
import org.niko.workdayslistactivity.timertracker.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Authentication
    private FirebaseAuth mAuth;

    private WorkdayViewModel mWorkdayViewModel;

    WorkdayListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listWorkdayTitles = new ArrayList<>();
    HashMap<String, ArrayList<ArrayList<Long>>> listWorkdayIntervals = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_main);

        FloatingActionButton addWorkdayButton = findViewById(R.id.addWorkdayButton);
        addWorkdayButton.setOnClickListener(this.mAddWorkdayButton_OnClickListener);

        FirebaseService.setIsWorkIntervalRunning(false);

        // get the listview
        expListView = findViewById(R.id.lvExp);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser); // All data filling logic now in updateUI
    }

    private void updateUI(FirebaseUser user) {

        if (user != null) {

            mWorkdayViewModel = new ViewModelProvider(this).get(WorkdayViewModel.class);

            // View model saves state across activity lifecycles. If a workinterval is running and thereby
            // if a play or stop icon shall be shown is decided by the static variable on firebaseservice.
            FirebaseService.setIsWorkIntervalRunning(mWorkdayViewModel.isWorkIntervalRunning());

            listAdapter = new WorkdayListAdapter(this, listWorkdayTitles, listWorkdayIntervals);

            // setting list adapter
            expListView.setAdapter(listAdapter);


            mWorkdayViewModel.getAllWorkdays().observe(this, new Observer<LinkedHashMap<Long, Workday>>() {
                @Override
                public void onChanged(@Nullable final LinkedHashMap<Long, Workday> workdays) {
                    listAdapter.setWorkdays(workdays);
                }
            });

            this._registerAllTheseBroadcastReceivers();

        } else {
            // Starts the login activity if no logged in user is found
            Intent intent = new Intent(this, EmailPasswordActivity.class);
            startActivity(intent);
        }
    }

    private void _registerAllTheseBroadcastReceivers() {

        this._registerBroadcastReceiversForFragments("open.timePicker.fragment_82346234");
        this._registerBroadcastReceiversForFragments("open.datePicker.fragment_14325af8943");
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mWorkdayViewModel.addWorkInterval(intent.getLongExtra("workdayId", 0), intent.getBooleanExtra("isStartTime", true));

                if (FirebaseService.isIsWorkIntervalRunning()) {
                    mWorkdayViewModel.setWorkIntervalRunning(false);
                }
                else {
                    mWorkdayViewModel.setWorkIntervalRunning(true);
                }


            }
        }, new IntentFilter("add.workInterval.listAdapter_243985sadnfoiu2"));
    }

    private void _registerBroadcastReceiversForFragments(String fragmentIdentifier) {

        switch(fragmentIdentifier) {
            case "open.timePicker.fragment_82346234":
                LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {

                                                                             @Override
                                                                             public void onReceive(Context context, Intent intent) {
                                                                                 DialogFragment newFragment = new TimePickerFragment(
                                                                                         intent.getLongExtra("workdayId", 0),
                                                                                         intent.getIntExtra("workIntervalId", 0),
                                                                                         intent.getBooleanExtra("isStartTime", true));
                                                                                 newFragment.show(getSupportFragmentManager(), "timePicker");
                                                                             }
                                                                         }
                        , new IntentFilter(fragmentIdentifier));
                break;
            case "open.datePicker.fragment_14325af8943":
                LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {

                                                                             @Override
                                                                             public void onReceive(Context context, Intent intent) {
                                                                                 DialogFragment newFragment = new DatePickerFragment(
                                                                                         intent.getLongExtra("workdayId", 0),
                                                                                         context);
                                                                                 newFragment.show(getSupportFragmentManager(), "datePicker");
                                                                             }
                                                                         }
                        , new IntentFilter(fragmentIdentifier));
                break;
            default:
                break;


        }
    }

    final View.OnClickListener mAddWorkdayButton_OnClickListener = new View.OnClickListener() {
        public void onClick(final View v) {
            mWorkdayViewModel.addWorkday();
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
