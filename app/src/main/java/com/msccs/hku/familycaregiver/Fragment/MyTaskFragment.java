package com.msccs.hku.familycaregiver.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.msccs.hku.familycaregiver.Activity.TaskDetailActivity;
import com.msccs.hku.familycaregiver.Adapter.ToDoListAdapter;
import com.msccs.hku.familycaregiver.Model.CustomTasks;
import com.msccs.hku.familycaregiver.tempStructure.UserTask;
import com.msccs.hku.familycaregiver.R;
import com.msccs.hku.familycaregiver.tempStructure.IdTask;
import java.util.ArrayList;

/**
 * Created by HoiKit on 20/08/2017.
 */

public class MyTaskFragment extends ListFragment {

    private ArrayList<IdTask> mIdTaskList;
    private ToDoListAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_task, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh1);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mIdTaskList = new ArrayList<IdTask>();
        mAdapter = new ToDoListAdapter(getContext(), mIdTaskList);
        getListView().setAdapter(mAdapter);

        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String taskId = mAdapter.getCustomTaskId(position);

                Query taskQuery = FirebaseDatabase.getInstance().getReference("tasks").orderByKey().equalTo(taskId);
                taskQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot taskSnapshot:dataSnapshot.getChildren()){
                            Intent intent = new Intent(getActivity(), TaskDetailActivity.class);
                            intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID,taskId);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


        // This part is for handle the swap to refresh
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        reloadMyTask();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        //This on resume fire when return frm other activity, so it works
        reloadMyTask();
    }

    public void reloadMyTask(){
        mAdapter.removeData();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference myTaskQuery = FirebaseDatabase.getInstance().getReference("UserTask").child(currentUserId).child("Assigned");
        final ArrayList<IdTask> newIdTaskList = new ArrayList<IdTask>();
        myTaskQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userTaskSnapshot:dataSnapshot.getChildren()){
                    UserTask userTask = userTaskSnapshot.getValue(UserTask.class);
                    Query taskRef = FirebaseDatabase.getInstance().getReference().child("tasks").orderByKey().equalTo(userTask.getTaskId());
                    taskRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot1) {
                            for (DataSnapshot taskSnapshot:dataSnapshot1.getChildren()){
                                CustomTasks task = taskSnapshot.getValue(CustomTasks.class);
                                String taskId = taskSnapshot.getKey();
                                IdTask idTask = new IdTask(taskId,task);
                                newIdTaskList.add(idTask);
                            }
                            mAdapter.updateData(newIdTaskList);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
