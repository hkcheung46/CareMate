package com.msccs.hku.familycaregiver.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.msccs.hku.familycaregiver.Model.CustomTasks;
import com.msccs.hku.familycaregiver.R;
import com.msccs.hku.familycaregiver.tempStructure.IdTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by HoiKit on 20/12/2017.
 */

public class ToDoListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<IdTask> idTaskList;

    public ToDoListAdapter(Context context, ArrayList<IdTask> idTaskList) {
        this.context = context;
        this.idTaskList = idTaskList;
    }

    public ToDoListAdapter() {

    }

    @Override
    public int getCount() {
        return idTaskList.size();
    }

    @Override
    public Object getItem(int position) {
        return idTaskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.my_task_list_item, parent, false);
        }

        final TextView taskNameLbl = (TextView) convertView.findViewById(R.id.taskNameLbl);
        final TextView taskStartDateLbl = (TextView) convertView.findViewById(R.id.taskStartDateLbl);
        final TextView taskEndDateLbl = (TextView) convertView.findViewById(R.id.taskEndDateLbl);

        CustomTasks currentTask = getCustomTask(position);

        taskNameLbl.setText(currentTask.getTaskName());
        taskStartDateLbl.setText(new SimpleDateFormat("MM-dd-yyyy").format(currentTask.getTaskStartDate()));
        taskEndDateLbl.setText(new SimpleDateFormat("MM-dd-yyyy").format(currentTask.getTaskEndDate()));

        return convertView;
    }

    //insert two method here for getting the CustomTask and Task Id directly
    public String getCustomTaskId(int position) {
        return idTaskList.get(position).getTaskId();
    }

    ;

    public CustomTasks getCustomTask(int position) {
        return idTaskList.get(position).getCustomTask();
    }


}
