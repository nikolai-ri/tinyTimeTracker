package org.niko.timertracker.workdayslistactivity.ClickListener;

import android.view.View;

import org.niko.timertracker.workdayslistactivity.Model.Workday;
import org.niko.timertracker.workdayslistactivity.Persistence.FirebaseService;

public class DeleteWorkIntervalClickListener implements View.OnLongClickListener {

    private int workIntervalId;
    private Workday workday;
    private FirebaseService firebaseService;

    public DeleteWorkIntervalClickListener (int workIntervalId, Workday workday) {
        this.workIntervalId = workIntervalId;
        this.workday = workday;
        firebaseService = new FirebaseService();
    }

    @Override
    public boolean onLongClick(View v)
    {
        firebaseService.deleteWorkIntervalById(workday, workIntervalId);
        return true;
    }


}
