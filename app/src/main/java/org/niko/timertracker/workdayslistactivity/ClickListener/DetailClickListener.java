package org.niko.timertracker.workdayslistactivity.ClickListener;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class DetailClickListener implements View.OnClickListener {

    private Long workdayId;
    private int workIntervalId;
    private Context context;
    private boolean isStartTime;

    public DetailClickListener(Long workdayId, int workIntervalId, Context context, boolean isStartTime) {
        this.workdayId = workdayId;
        this.context = context;
        this.isStartTime = isStartTime;
        this.workIntervalId = workIntervalId;
    }

    @Override
    public void onClick(View v)
    {
        Intent intent = new Intent("open.timePicker.fragment_82346234");
        intent.putExtra("workdayId", this.workdayId);
        intent.putExtra("workIntervalId", this.workIntervalId);
        intent.putExtra("isStartTime", this.isStartTime);
        LocalBroadcastManager.getInstance(this.context).sendBroadcast(intent);
    }

}