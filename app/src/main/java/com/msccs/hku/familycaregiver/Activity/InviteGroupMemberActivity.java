package com.msccs.hku.familycaregiver.Activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.msccs.hku.familycaregiver.Fragment.InviteMembersFragment;
import com.msccs.hku.familycaregiver.Fragment.ToDoListTabHostFragment;
import com.msccs.hku.familycaregiver.R;

public class InviteGroupMemberActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_group_member);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.invite);

        //Default entry page will be the to do list
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new InviteMembersFragment(), null);
        transaction.commit();
    }
}
