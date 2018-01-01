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
import com.msccs.hku.familycaregiver.Activity.PollingDetailActivity;
import com.msccs.hku.familycaregiver.Activity.PollingResultActivity;
import com.msccs.hku.familycaregiver.Adapter.GroupPollingListAdapter;
import com.msccs.hku.familycaregiver.Model.Polling;
import com.msccs.hku.familycaregiver.R;
import com.msccs.hku.familycaregiver.tempStructure.IdPolling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by HoiKit on 31/12/2017.
 */

public class GroupPollingListFragment extends ListFragment {

    private String mGroupId;
    private ArrayList<IdPolling> idPollingList;
    private GroupPollingListAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_group_polling, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle b = getArguments();
        mGroupId = b.getString("groupId");


        idPollingList = new ArrayList<IdPolling>();
        mAdapter = new GroupPollingListAdapter(getContext(), idPollingList);
        getListView().setAdapter(mAdapter);

        // This part is for handle the swap to refresh
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        reloadGroupPollingList(mGroupId);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String pollingID = mAdapter.getPollingId(position);
                String pollingStatus = mAdapter.getPolling(position).getStatus();
                switch (pollingStatus){
                    case Polling.ACTIVE_POLLING_STATUS:
                        Intent intent = new Intent(getActivity(), PollingDetailActivity.class);
                        intent.putExtra(PollingDetailActivity.EXTRA_POLLING_DETAIL_POLL_ID,pollingID);
                        startActivity(intent);
                        break;
                    case Polling.CLOSED_POLLING_STATUS:
                        Intent intent1 = new Intent(getActivity(), PollingResultActivity.class);
                        intent1.putExtra(PollingResultActivity.EXTRA_POLLING_RESULT_ACT_POLLINGID,pollingID);
                        startActivity(intent1);
                        break;
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadGroupPollingList(mGroupId);
    }

    private void reloadGroupPollingList(String groupId) {
        mAdapter.removeData();
        Query pollingQuery = FirebaseDatabase.getInstance().getReference("polling").orderByChild("belongToGroupId").equalTo(groupId);
        ArrayList<IdPolling> newIdPollingList = new ArrayList<IdPolling>();

        pollingQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                idPollingList.clear();
                for (DataSnapshot pollingSnapshot : dataSnapshot.getChildren()) {
                    String pollingId = pollingSnapshot.getKey();
                    Polling pollingToAdd = pollingSnapshot.getValue(Polling.class);
                    IdPolling idPolling = new IdPolling(pollingId, pollingToAdd);
                    idPollingList.add(idPolling);
                }
                sortByPollingStatus();
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void sortByPollingStatus(){

        Comparator<IdPolling> comparator = new Comparator<IdPolling>() {
            @Override
            public int compare(IdPolling o1, IdPolling o2) {
                String pollingStatus1 = o1.getPolling().getStatus();
                String pollingStatus2 = o2.getPolling().getStatus();

                //Assign the priority of different task status here
                //A=Assigned, N=Pending, C=Completed
                Integer pollingStatus1Int;
                Integer pollingStatus2Int;
                switch (pollingStatus1) {
                    case Polling.ACTIVE_POLLING_STATUS:
                        pollingStatus1Int = 1;
                        break;
                    case Polling.CLOSED_POLLING_STATUS:
                        pollingStatus1Int = 2;
                        break;
                    default:
                        pollingStatus1Int = 1;
                        break;
                }

                switch (pollingStatus2) {
                    case Polling.ACTIVE_POLLING_STATUS:
                        pollingStatus2Int = 1;
                        break;
                    case Polling.CLOSED_POLLING_STATUS:
                        pollingStatus2Int = 2;
                        break;
                    default:
                        pollingStatus2Int = 1;
                        break;
                }

                return pollingStatus1Int.compareTo(pollingStatus2Int);

            }
        };

        Collections.sort(idPollingList,comparator);

    }
}
