package org.niko.timertracker.workdayslistactivity.ClickListener;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.niko.timertracker.workdayslistactivity.Persistence.FirebaseService;
import org.niko.workdayslistactivity.timertracker.R;

public class AddWorkIntervalClickListener implements View.OnClickListener {

    private Long workdayId;
    private Context context;
    private boolean isStartTime;

    public AddWorkIntervalClickListener(Long workdayId, boolean isStartTime, Context context) {
        this.workdayId = workdayId;
        this.context = context;
        this.isStartTime = isStartTime;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent("add.workInterval.listAdapter_243985sadnfoiu2");
        intent.putExtra("workdayId", this.workdayId);
        intent.putExtra("isStartTime", this.isStartTime);
        FloatingActionButton floatingActionButton = v.findViewById(R.id.addWorkIntervalButton);

        if (FirebaseService.isIsWorkIntervalRunning()) {
            floatingActionButton.setImageResource(R.drawable.ic_playicon);
            FirebaseService.setIsWorkIntervalRunning(false);
        }
        else {
            floatingActionButton.setImageResource(R.drawable.ic_stopicon);
            FirebaseService.setIsWorkIntervalRunning(true);
        }
        LocalBroadcastManager.getInstance(this.context).sendBroadcast(intent);
    }
}
