package com.msccs.hku.familycaregiver.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.msccs.hku.familycaregiver.tempStructure.GroupTask;
import com.msccs.hku.familycaregiver.tempStructure.InGroup;
import com.msccs.hku.familycaregiver.R;
import com.msccs.hku.familycaregiver.tempStructure.IdTask;

import java.util.ArrayList;

public class AllTaskFragment extends ListFragment {

    private ArrayList<IdTask> mIdTaskList = new ArrayList<IdTask>();
    private ToDoListAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SwipeRefreshLayout mSwipeRefreshLayoutEmpty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_all_task, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh1);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mIdTaskList = new ArrayList<IdTask>();
        mAdapter = new ToDoListAdapter(getContext(), mIdTaskList);
        getListView().setAdapter(mAdapter);
        reloadAllTask();

        // This part is for handle the swap to refresh
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        reloadAllTask();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
        );



        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String taskId = mAdapter.getCustomTaskId(position);
                final String groupId = mAdapter.getCustomTask(position).getBelongToGroupId();

                Query taskQuery = FirebaseDatabase.getInstance().getReference("tasks").orderByKey().equalTo(taskId);
                taskQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot taskSnapshot:dataSnapshot.getChildren()){
                            Intent intent = new Intent(getActivity(), TaskDetailActivity.class);
                            intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID,taskId);
                            intent.putExtra(TaskDetailActivity.EXTRA_GROUP_ID,groupId);

                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadAllTask();
    }

    private void reloadAllTask() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference inGroupRef = FirebaseDatabase.getInstance().getReference("inGroup").child(currentUserId);
        final ArrayList<IdTask> newIdTaskList = new ArrayList<IdTask>();
        inGroupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot inGroupSnapshot : dataSnapshot.getChildren()) {
                    InGroup inGroup = inGroupSnapshot.getValue(InGroup.class);
                    String groupId = inGroup.getGroupId();

                    DatabaseReference groupTaskRef = FirebaseDatabase.getInstance().getReference("groupTask").child(groupId);
                    groupTaskRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot1) {
                            for (DataSnapshot groupTaskSnapshot : dataSnapshot1.getChildren()) {
                                GroupTask groupTask = groupTaskSnapshot.getValue(GroupTask.class);

                                Query taskRef = FirebaseDatabase.getInstance().getReference("tasks").orderByKey().equalTo(groupTask.getTaskId());
                                taskRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot2) {
                                        for (DataSnapshot taskSnapshot : dataSnapshot2.getChildren()) {
                                            String taskId = taskSnapshot.getKey();
                                            CustomTasks task = taskSnapshot.getValue(CustomTasks.class);
                                            newIdTaskList.add(new IdTask(taskId,task));
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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}