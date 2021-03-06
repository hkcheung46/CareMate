package com.msccs.hku.familycaregiver.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.msccs.hku.familycaregiver.Activity.TaskDetailActivity;
import com.msccs.hku.familycaregiver.Adapter.GroupTaskListAdapter;
import com.msccs.hku.familycaregiver.Model.CustomTasks;
import com.msccs.hku.familycaregiver.R;
import com.msccs.hku.familycaregiver.tempStructure.IdTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class GroupTaskListFragment extends ListFragment {

    private String mGroupId;
    private ArrayList<IdTask> idTaskList;
    private GroupTaskListAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_group_task, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle b = getArguments();
        mGroupId = b.getString("groupId");

        //Initialize the Id TaskList
        idTaskList = new ArrayList<IdTask>();
        mAdapter = new GroupTaskListAdapter(getContext(), idTaskList);
        getListView().setAdapter(mAdapter);

        // This part is for handle the swap to refresh
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        reloadGroupTaskList(mGroupId);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
        );


        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String taskId = mAdapter.getCustomTaskId(position);
                Intent intent = new Intent(getActivity(), TaskDetailActivity.class);
                intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, taskId);
                intent.putExtra(TaskDetailActivity.EXTRA_GROUP_ID,mAdapter.getCustomTask(position).getBelongToGroupId());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadGroupTaskList(mGroupId);
    }

    private void reloadGroupTaskList(String groupId){
        mAdapter.removeData();
        Query taskRef = FirebaseDatabase.getInstance().getReference().child("tasks").orderByChild("belongToGroupId").equalTo(groupId);
        ArrayList<IdTask> newIdPollingList = new ArrayList<IdTask>();

        taskRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //When there is value changes clear the idTaskList, reload the data and then notify the list view to refresh
                idTaskList.clear();
                for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                    String taskId = taskSnapshot.getKey();
                    CustomTasks taskToAdd = taskSnapshot.getValue(CustomTasks.class);
                    IdTask idTask = new IdTask(taskId, taskToAdd);
                    idTaskList.add(idTask);
                }
                //Replace the existing list when finished loading and tell the list to refresh
                sortByTaskStatusStartDate();
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    //This function for sorting purpose (pending>assigned>completed, if same status, the earlier start date one is higher position
    public void sortByTaskStatusStartDate() {
        Comparator<IdTask> comparator = new Comparator<IdTask>() {

            @Override
            public int compare(IdTask object1, IdTask object2) {
                String taskStatus1 = object1.getCustomTask().getStatus();
                String taskStatus2 = object2.getCustomTask().getStatus();

                //Assign the priority of different task status here
                //A=Assigned, N=Pending, C=Completed
                Integer taskStatus1Int;
                Integer taskStatus2Int;
                switch (taskStatus1) {
                    case "A":
                        taskStatus1Int = 2;
                        break;
                    case "N":
                        taskStatus1Int = 1;
                        break;
                    case "C":
                        taskStatus1Int = 3;
                        break;
                    default:
                        taskStatus1Int = 0;
                        break;
                }


                switch (taskStatus2) {
                    case "A":
                        taskStatus2Int = 2;
                        break;
                    case "N":
                        taskStatus2Int = 1;
                        break;
                    case "C":
                        taskStatus2Int = 3;
                        break;
                    default:
                        taskStatus2Int = 0;
                        break;
                }

                if (!taskStatus1.equals(taskStatus2)) {
                    return taskStatus1Int.compareTo(taskStatus2Int);
                } else {
                    double task1StartDate = object1.getCustomTask().getTaskStartDate();
                    double task2StartDate = object2.getCustomTask().getTaskStartDate();
                    return (task1StartDate>task2StartDate?1:0);
                }
            }
        };
        Collections.sort(idTaskList, comparator);
    }
}



