package com.msccs.hku.familycaregiver.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.msccs.hku.familycaregiver.Activity.GroupDetailActivity;
import com.msccs.hku.familycaregiver.tempStructure.InGroup;
import com.msccs.hku.familycaregiver.R;

/**
 * Created by HoiKit on 20/08/2017.
 */

public class AllGroupListFragment extends ListFragment {


    FirebaseListAdapter<InGroup> mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference inGroupRef = database.getReference("inGroup").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        mAdapter = new FirebaseListAdapter<InGroup>(getActivity(), InGroup.class, android.R.layout.simple_list_item_1, inGroupRef) {
            @Override
            protected void populateView(View v, InGroup model, int position) {
                TextView groupNameLbl = (TextView) v.findViewById(android.R.id.text1);
                groupNameLbl.setText(model.getGroupName());
            }
        };
        this.setListAdapter(mAdapter);

        final ListView groupListView = this.getListView();
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), GroupDetailActivity.class);
                intent.putExtra(GroupDetailActivity.EXTRA_GROUP_NAME, ((InGroup) (getListAdapter().getItem(position))).getGroupName());
                intent.putExtra(GroupDetailActivity.EXTRA_GROUP_ID, ((InGroup) (getListAdapter().getItem(position))).getGroupId());
                startActivity(intent);
            }
        });
    }
}
