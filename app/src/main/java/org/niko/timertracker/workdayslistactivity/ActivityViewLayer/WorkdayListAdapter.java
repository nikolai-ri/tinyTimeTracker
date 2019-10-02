package org.niko.timertracker.workdayslistactivity.ActivityViewLayer;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.niko.timertracker.workdayslistactivity.ClickListener.AddWorkIntervalClickListener;
import org.niko.timertracker.workdayslistactivity.ClickListener.DateClickListener;
import org.niko.timertracker.workdayslistactivity.ClickListener.DeleteWorkIntervalClickListener;
import org.niko.timertracker.workdayslistactivity.ClickListener.DetailClickListener;
import org.niko.timertracker.workdayslistactivity.Model.Workday;
import org.niko.timertracker.workdayslistactivity.Persistence.FirebaseService;
import org.niko.workdayslistactivity.timertracker.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class WorkdayListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, ArrayList<ArrayList<Long>>> _listDataChild;
    private ArrayList<Workday> workdayList = new ArrayList<>(); // needs to be an arraylist here, because workdays are accessed by index by the adapter

    public WorkdayListAdapter(Context context, List<String> listDataHeader,
                              HashMap<String, ArrayList<ArrayList<Long>>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this._listDataChild.get(String.valueOf(this.workdayList.get(groupPosition).getId())).get(childPosition);
    }


    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        Workday parentWorkday = this.workdayList.get(groupPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }

        this.configureButtonsInChildView(convertView, parentWorkday.getWorkIntervals().get(childPosition), parentWorkday, childPosition);

        return convertView;
    }

    private void configureButtonsInChildView(View convertView, ArrayList<Long> currentWorkInterval, Workday parentWorkday, int childPosition) {

        if(currentWorkInterval != null) {
            TextView startTimeButton = convertView.findViewById(R.id.startTime);
            startTimeButton.setOnClickListener(new DetailClickListener(parentWorkday.getId(), childPosition, this._context, true));
            startTimeButton.setText(Workday.toHourMinuteString(currentWorkInterval.get(0)));

            TextView endTimeButton = convertView.findViewById(R.id.endTime);
            endTimeButton.setOnClickListener(new DetailClickListener(parentWorkday.getId(), childPosition, this._context, false));
            endTimeButton.setText(Workday.toHourMinuteString(currentWorkInterval.get(1)));

            startTimeButton.setOnLongClickListener(new DeleteWorkIntervalClickListener(childPosition, parentWorkday));
            endTimeButton.setOnLongClickListener(new DeleteWorkIntervalClickListener(childPosition , parentWorkday));
        }
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        int childrenCount = 0;

        if(this._listDataChild != null &&
                this._listDataHeader != null &&
                this._listDataChild.get(String.valueOf(this.workdayList.get(groupPosition).getId())) != null) {
            childrenCount = this._listDataChild.get(String.valueOf(this.workdayList.get(groupPosition).getId())).size();
        }

        return childrenCount;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        Workday workday = this.workdayList.get(groupPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }

        resetGroupView(convertView, workday);

        FloatingActionButton floatingActionButtonChangeDate = convertView.findViewById(R.id.changeDate);
        floatingActionButtonChangeDate.setOnClickListener(new DateClickListener(this.workdayList.get(groupPosition).getId(), this._context));
        floatingActionButtonChangeDate.setFocusable(false);

        FloatingActionButton addWorkIntervalButton = convertView.findViewById(R.id.addWorkIntervalButton);
        addWorkIntervalButton.setOnClickListener(new AddWorkIntervalClickListener(this.workdayList.get(groupPosition).getId(), !FirebaseService.isIsWorkIntervalRunning(), this._context));
        addWorkIntervalButton.setFocusable(false);

        return convertView;
    }

    private void resetGroupView(View convertView, Workday workday) {
        TextView headerTime = convertView.findViewById(R.id.lblListHeaderTime);
        TextView headerDate = convertView.findViewById(R.id.lblListHeaderDate);

        headerTime.setTypeface(null, Typeface.BOLD);
        headerDate.setTypeface(null, Typeface.BOLD);

        headerDate.setText(workday.toStartEndTimeString());
        headerTime.setText(workday.toDateString());
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setWorkdays(LinkedHashMap<Long, Workday> workdays) {
        this._listDataHeader.clear();
        this._listDataChild.clear();
        this.workdayList.clear();

        workdays.forEach((Long aLong, Workday workday) -> {

            _listDataHeader.add(workday.toString());

            if (workday.getWorkIntervals() != null) {
                workday.getWorkIntervals().forEach((ArrayList<Long> arrayList) -> {
                    if (_listDataChild.containsKey(String.valueOf(workday.getId()))) {
                        _listDataChild.get(String.valueOf(workday.getId())).add(arrayList);
                    } else {
                        ArrayList<ArrayList<Long>> arrayListWorkInterval = new ArrayList<>();
                        arrayListWorkInterval.add(arrayList);
                        _listDataChild.put(String.valueOf(workday.getId()), arrayListWorkInterval);
                    }
                });
            }

            workdayList.add(workday);

        });

        notifyDataSetChanged();
    }

}
