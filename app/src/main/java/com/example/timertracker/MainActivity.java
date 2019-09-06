package com.example.timertracker;

import android.os.Build;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ListIterator;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {


    private WorkdayViewModel mWorkdayViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
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

                Workday workday = new Workday(gregorianCalendar.getTimeInMillis(), 0, stringDate);
                mWorkdayViewModel.insert(workday);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final WorkdayListAdapter adapter = new WorkdayListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mWorkdayViewModel = ViewModelProviders.of(this).get(WorkdayViewModel.class);

        mWorkdayViewModel.getAllWorkdays().observe(this, new Observer<List<Workday>>() {
            @Override
            public void onChanged(@Nullable final List<Workday> workdays) {
                adapter.setWorkdays(workdays);
            }
        });
    }

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
