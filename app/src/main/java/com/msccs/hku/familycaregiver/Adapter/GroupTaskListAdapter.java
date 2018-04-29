package com.msccs.hku.familycaregiver.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.msccs.hku.familycaregiver.Model.CustomTasks;
import com.msccs.hku.familycaregiver.R;
import com.msccs.hku.familycaregiver.tempStructure.IdTask;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static android.view.View.GONE;

/**
 * Created by HoiKit on 19/12/2017.
 */

/**
 This class is for adapting the idTask structure into the list view insid ethe groupTaskListFragment
 */

public class GroupTaskListAdapter  extends BaseAdapter {

    private Context context;
    private ArrayList<IdTask> idTaskList;

    public GroupTaskListAdapter(Context context, ArrayList<IdTask> idTaskList) {
        this.context = context;
        this.idTaskList = idTaskList;
    }

    public GroupTaskListAdapter() {

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
            convertView = LayoutInflater.from(context).inflate(R.layout.group_task_list_item, parent, false);
        }

        TextView taskNameLbl = (TextView) convertView.findViewById(R.id.taskNameLbl);
        TextView taskStartDateLbl = (TextView) convertView.findViewById(R.id.taskStartDateLbl);
        TextView taskEndDateLbl = (TextView) convertView.findViewById(R.id.taskEndDateLbl);
        TextView dateTimeHyphenLbl = (TextView) convertView.findViewById(R.id.dateTimeHyphenLbl);
        TextView statusLbl = (TextView) convertView.findViewById(R.id.taskStatusLbl);

        CustomTasks currentTask = getCustomTask(position);

        taskNameLbl.setText(currentTask.getTaskName());
        if (currentTask.getTaskEventType().equals("E")){
            taskStartDateLbl.setText(new SimpleDateFormat("MM-dd-yyyy").format(currentTask.getTaskStartDate()));
            taskEndDateLbl.setText(new SimpleDateFormat("MM-dd-yyyy").format(currentTask.getTaskEndDate()));
        }else{

            taskStartDateLbl.setText(new SimpleDateFormat("MM-dd-yyyy hh:mm").format(currentTask.getTaskStartDate()));
            dateTimeHyphenLbl.setVisibility(GONE);
            taskEndDateLbl.setVisibility(GONE);
        }


        switch (currentTask.getStatus()) {
            case "A":
                statusLbl.setText(R.string.assigned);
                statusLbl.setBackground(context.getResources().getDrawable(R.drawable.round_rect_green_shape));
                break;
            case "N":
                statusLbl.setText(R.string.pending);
                statusLbl.setBackground(context.getResources().getDrawable(R.drawable.round_rect_brown_shape));
                break;
            case "C":
                statusLbl.setText(R.string.completed);
                statusLbl.setBackground(context.getResources().getDrawable(R.drawable.round_rect_grey_shape));
                break;
            default:
                break;
        }

        return convertView;
    }


    //insert two method here for getting the CustomTask and Task Id directly
    public String getCustomTaskId(int position){
        return idTaskList.get(position).getTaskId();
    };

    public CustomTasks getCustomTask(int position){
        return idTaskList.get(position).getCustomTask();
    }

    public void removeData() {
        this.idTaskList.clear();
        notifyDataSetChanged();
    }

}
