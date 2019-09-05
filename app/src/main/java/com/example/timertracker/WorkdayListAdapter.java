package com.example.timertracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WorkdayListAdapter extends RecyclerView.Adapter<WorkdayListAdapter.WorkdayViewHolder> {

    class WorkdayViewHolder extends RecyclerView.ViewHolder {
        private final TextView workdayItemView;

        private WorkdayViewHolder(View itemView) {
            super(itemView);
            workdayItemView = itemView.findViewById(R.id.textView);
        }
    }

    private final LayoutInflater mInflater;
    private List<Workday> mWorkdays;

    WorkdayListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public WorkdayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new WorkdayViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WorkdayViewHolder holder, int position) {
        if (mWorkdays != null) {
            Workday current = mWorkdays.get(position);
            holder.workdayItemView.setText(current.toString());
        } else {
            // Covers the case of data not being ready yet.
            holder.workdayItemView.setText("No start time");
        }
    }

    void setWorkdays(List<Workday> workdays){
        mWorkdays = workdays;
        notifyDataSetChanged();
    }


    // getItemCount() is called many times, and when it is first called,
    // mWorkdays has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mWorkdays != null)
            return mWorkdays.size();
        else return 0;
    }

}
