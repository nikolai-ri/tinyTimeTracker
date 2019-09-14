package com.example.timertracker;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class DetailClickListener extends FragmentActivity implements View.OnClickListener {

    private long workdayId;
    private int workIntervalId;
    private Context context;
    private boolean isStartTime;

    public DetailClickListener(long workdayId, int workIntervalId, Context context, boolean isStartTime) {
        this.workdayId = workdayId;
        this.context = context;
        this.isStartTime = isStartTime;
        this.workIntervalId = workIntervalId;
    }

    @Override
    public void onClick(View v)
    {
        //Here i want to call my fragment
        //You need to pass a reference of the context to your adapter...
        Intent intent = new Intent("open.timePicker.fragment_82346234");
        intent.putExtra("workdayId", this.workdayId);
        intent.putExtra("workIntervalId", this.workIntervalId);
        intent.putExtra("isStartTime", this.isStartTime);
        LocalBroadcastManager.getInstance(this.context).sendBroadcast(intent);
    }

}