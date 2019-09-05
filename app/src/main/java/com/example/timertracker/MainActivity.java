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
import java.time.LocalDateTime;
import java.util.List;

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
                LocalDateTime now = LocalDateTime.now();
                int currentDayOfYear = now.getDayOfYear();

                while(mWorkdayViewModel.getAllWorkdays().getValue().listIterator().hasNext()) {
                    Workday workday = mWorkdayViewModel.getAllWorkdays().getValue().listIterator().next();
                    if(workday.getDayOfYear() == currentDayOfYear) {
                        workday.setEndTime(System.currentTimeMillis());
                        mWorkdayViewModel.update(workday);
                        return;
                    }
                }

                Workday workday = new Workday(System.currentTimeMillis(), 0, currentDayOfYear);
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
