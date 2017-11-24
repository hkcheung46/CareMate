package com.msccs.hku.familycaregiver.Activity;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.msccs.hku.familycaregiver.Fragment.InviteMembersFragment;
import com.msccs.hku.familycaregiver.Fragment.ToDoListTabHostFragment;
import com.msccs.hku.familycaregiver.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InviteGroupMemberActivity extends BaseActivity {

    public final static String EXTRA_ELDERLY_NAME="COM.MSCCS.HKU.FAMILYCAREGIVER.ELDERLYNAME";
    public final static String EXTRA_BIRTHDAY_DAY="COM.MSCCS.HKU.FAMILYCAREGIVER.BIRTHDAYDAY";
    public final static String EXTRA_BIRTHDAY_MONTH="COM.MSCCS.HKU.FAMILYCAREGIVER.BIRTHDAYMONTH";
    public final static String EXTRA_BIRTHDAY_YEAR="COM.MSCCS.HKU.FAMILYCAREGIVER.BIRTHDAYYEAR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_group_member);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.invite);

        //Default entry page will be the to do list
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new InviteMembersFragment(), null);
        transaction.commit();
    }
}
