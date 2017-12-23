package com.msccs.hku.familycaregiver.Activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.msccs.hku.familycaregiver.Fragment.GroupDetailTabHostFragment;
import com.msccs.hku.familycaregiver.R;

public class GroupDetailActivity extends AppCompatActivity implements GroupDetailTabHostFragment.onGroupDetailTabSelectedListener {

    public final static String EXTRA_GROUP_NAME ="COM.MSCCS.HKU.FAMILYCAREGIVER.EXTRA_GROUP_NAME";
    public final static String EXTRA_GROUP_ID ="COM.MSCCS.HKU.FAMILYCAREGIVER.EXTRA_GROUP_ID";

    private String mGroupName;
    private String mGroupId;
    private FloatingActionButton mAddMemberFab;
    private FloatingActionButton mAddNewTaskFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        //Get the intent content
        mGroupId = getIntent().getStringExtra(EXTRA_GROUP_ID);
        mGroupName = getIntent().getStringExtra(EXTRA_GROUP_NAME);

        //setup and initialize the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("GROUP: "+ mGroupName);

        mAddMemberFab = (FloatingActionButton) findViewById(R.id.add_member_fab);
        mAddNewTaskFab = (FloatingActionButton) findViewById(R.id.add_new_task_fab);

        mAddMemberFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start Add New Member Activity Here
                Intent intent = new Intent(GroupDetailActivity.this,InviteGpMemberForExistingGpActivity.class);
                intent.putExtra(InviteGpMemberForExistingGpActivity.EXTRA_GROUP_ID,mGroupId);
                intent.putExtra(InviteGpMemberForExistingGpActivity.EXTRA_GROUP_NAME,mGroupName);
                startActivity(intent);
            }
        });


        mAddNewTaskFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start Add New Task Activity Here
                Intent intent = new Intent(GroupDetailActivity.this,CreateNewTaskActivity.class);
                intent.putExtra(CreateNewTaskActivity.EXTRA_GROUP_ID,mGroupId);
                intent.putExtra(CreateNewTaskActivity.EXTRA_CREATE_NEW_TASK_MODE,"s");
                startActivity(intent);
            }
        });


        GroupDetailTabHostFragment groupDetailTabHostFragment = new GroupDetailTabHostFragment();
        Bundle bundle = new Bundle();
        bundle.putString("groupId",mGroupId);
        groupDetailTabHostFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, groupDetailTabHostFragment, null);
        transaction.commit();
    }

    @Override
    public void onGroupDetailTabSelected(int position) {
        animateFab(position);
    }

    private void animateFab(int position) {
        switch (position){
            case 0:
                mAddMemberFab.hide();
                mAddNewTaskFab.show();
                break;
            case 1:
                mAddNewTaskFab.hide();
                mAddMemberFab.show();
                break;
        }
    }
}
