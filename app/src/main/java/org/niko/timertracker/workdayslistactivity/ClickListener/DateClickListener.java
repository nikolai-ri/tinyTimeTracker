package org.niko.timertracker.workdayslistactivity.ClickListener;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class DateClickListener implements View.OnClickListener {

    private Long workdayId;
    private Context context;

    public DateClickListener (Long workdayId, Context context) {
        this.workdayId = workdayId;
        this.context = context;
    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent("open.datePicker.fragment_14325af8943");
        intent.putExtra("workdayId", this.workdayId);
        LocalBroadcastManager.getInstance(this.context).sendBroadcast(intent);
    }
}
