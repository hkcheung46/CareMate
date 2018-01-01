package com.msccs.hku.familycaregiver.Fragment;

import android.content.Intent;
import android.os.Bundle;
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
import com.msccs.hku.familycaregiver.Activity.PollingDetailActivity;
import com.msccs.hku.familycaregiver.Activity.PollingResultActivity;
import com.msccs.hku.familycaregiver.Adapter.PollingListAdapter;
import com.msccs.hku.familycaregiver.Model.GroupPolling;
import com.msccs.hku.familycaregiver.Model.Polling;
import com.msccs.hku.familycaregiver.R;
import com.msccs.hku.familycaregiver.tempStructure.IdPolling;
import com.msccs.hku.familycaregiver.tempStructure.InGroup;

import java.util.ArrayList;

/**
 * Created by HoiKit on 26/12/2017.
 */

public class ClosedPollingListFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_closed_polling, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh1);
        return v;
    }

    private ArrayList<IdPolling> mIdPollingList = new ArrayList<IdPolling>();
    private PollingListAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    public void onResume() {
        super.onResume();
        reloadClosedPollingList();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mIdPollingList = new ArrayList<IdPolling>();
        mAdapter = new PollingListAdapter(getContext(), mIdPollingList);
        getListView().setAdapter(mAdapter);
        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), PollingDetailActivity.class);
                intent.putExtra(PollingDetailActivity.EXTRA_POLLING_DETAIL_POLL_ID,mAdapter.getPollingId(position));
                startActivity(intent);
            }
        });


        reloadClosedPollingList();
        // This part is for handle the swap to refresh
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        reloadClosedPollingList();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), PollingResultActivity.class);
                String pollingId = mAdapter.getPollingId(position);
                intent.putExtra(PollingResultActivity.EXTRA_POLLING_RESULT_ACT_POLLINGID,pollingId);
                startActivity(intent);
            }
        });

    }


    private void reloadClosedPollingList() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference inGroupRef = FirebaseDatabase.getInstance().getReference("inGroup").child(currentUserId);
        final ArrayList<IdPolling> newIdPollingList = new ArrayList<IdPolling>();
        inGroupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot inGroupSnapshot : dataSnapshot.getChildren()) {
                    InGroup inGroup = inGroupSnapshot.getValue(InGroup.class);
                    String groupId = inGroup.getGroupId();

                    DatabaseReference groupPollingRef = FirebaseDatabase.getInstance().getReference("groupPolling").child(groupId);
                    groupPollingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot1) {
                            for (DataSnapshot groupTaskSnapshot : dataSnapshot1.getChildren()) {
                                GroupPolling groupPolling = groupTaskSnapshot.getValue(GroupPolling.class);
                                Query pollRef = FirebaseDatabase.getInstance().getReference("polling").orderByKey().equalTo(groupPolling.getPollingId());
                                pollRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot2) {
                                        for (DataSnapshot pollingSnapshot : dataSnapshot2.getChildren()) {
                                            String pollingId = pollingSnapshot.getKey();
                                            Polling polling = pollingSnapshot.getValue(Polling.class);
                                            if (polling.getStatus() .equals(Polling.CLOSED_POLLING_STATUS)) {
                                                newIdPollingList.add(new IdPolling(pollingId, polling));
                                            }
                                        }
                                        mAdapter.updateData(newIdPollingList);
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
