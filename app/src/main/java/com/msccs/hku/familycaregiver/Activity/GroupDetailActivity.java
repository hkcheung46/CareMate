package com.msccs.hku.familycaregiver.Activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.msccs.hku.familycaregiver.Fragment.GroupDetailTabHostFragment;
import com.msccs.hku.familycaregiver.Model.CustomTasks;
import com.msccs.hku.familycaregiver.R;

public class GroupDetailActivity extends AppCompatActivity implements GroupDetailTabHostFragment.onGroupDetailTabSelectedListener {

    public final static String EXTRA_GROUP_NAME ="COM.MSCCS.HKU.FAMILYCAREGIVER.EXTRA_GROUP_NAME";
    public final static String EXTRA_GROUP_ID ="COM.MSCCS.HKU.FAMILYCAREGIVER.EXTRA_GROUP_ID";

    private String mGroupName;
    private String mGroupId;
    private FloatingActionButton mAddMemberFab;
    private FloatingActionButton mAddNewPollingFab;
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
        getSupportActionBar().setTitle(mGroupName+"'s caring group");

        mAddMemberFab = (FloatingActionButton) findViewById(R.id.add_member_fab);
        mAddNewPollingFab = (FloatingActionButton) findViewById(R.id.add_new_polling_fab);
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

        mAddNewPollingFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupDetailActivity.this,CreateNewPollingActivity.class);
                intent.putExtra(CreateNewPollingActivity.EXTRA_CREATE_NEW_POLL_GROUP_ID,mGroupId);
                intent.putExtra(CreateNewPollingActivity.EXTRA_CREATE_NEW_POLL_MODE,"s");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.group_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.menu_householdMonitoring:
                Intent intent = new Intent(GroupDetailActivity.this,HomePlusActivity.class);
                intent.putExtra(HomePlusActivity.EXTRA_GROUP_NAME,mGroupName);
                intent.putExtra(HomePlusActivity.EXTRA_GROUP_ID,mGroupId);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGroupDetailTabSelected(int position) {
        animateFab(position);
    }

    private void animateFab(int position) {
        switch (position){
            case 0:
                mAddMemberFab.hide();
                mAddNewPollingFab.hide();
                mAddNewTaskFab.show();
                break;
            case 1:
                mAddMemberFab.hide();
                mAddNewTaskFab.hide();
                mAddNewPollingFab.show();
                break;
            case 2:
                mAddNewTaskFab.hide();
                mAddNewPollingFab.hide();
                mAddMemberFab.show();
                break;
        }
    }
}
