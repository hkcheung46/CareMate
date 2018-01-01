package com.msccs.hku.familycaregiver.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.msccs.hku.familycaregiver.Model.CustomTasks;
import com.msccs.hku.familycaregiver.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by HoiKit on 17/12/2017.
 */

public class TaskAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<CustomTasks> customTasksList;


    public TaskAdapter(Context context, ArrayList<CustomTasks> customTasksList) {
        this.context = context;
        this.customTasksList = customTasksList;
    }

    @Override
    public int getCount() {
        return customTasksList.size();
    }

    @Override
    public Object getItem(int position) {
        return customTasksList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.to_be_assigned_task_list_item, parent, false);
        }

        // get current item to be displayed
        CustomTasks task = (CustomTasks) getItem(position);
        TextView taskNameLbl = (TextView) convertView.findViewById(R.id.taskNameLbl);
        TextView startDateLbl = (TextView) convertView.findViewById(R.id.taskStartDateLbl);
        TextView endDateLbl = (TextView) convertView.findViewById(R.id.taskEndDateLbl);

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd-MM-yyyy");
        taskNameLbl.setText(task.getTaskName());
        startDateLbl.setText(dateFormat.format(task.getTaskStartDate()));
        endDateLbl.setText(dateFormat.format(task.getTaskEndDate()));

        // returns the view for the current row
        return convertView;
    }
}
