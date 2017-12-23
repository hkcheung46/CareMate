package com.msccs.hku.familycaregiver.Activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.msccs.hku.familycaregiver.Fragment.InviteMembersFragment;
import com.msccs.hku.familycaregiver.Model.CustomFirebaseUser;
import com.msccs.hku.familycaregiver.Model.Group;
import com.msccs.hku.familycaregiver.Model.GroupInvitation;
import com.msccs.hku.familycaregiver.tempStructure.InGroup;
import com.msccs.hku.familycaregiver.R;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InviteGroupMemberActivityForNewGp extends AppCompatActivity {

    public final static String EXTRA_ELDERLY_NAME = "COM.MSCCS.HKU.FAMILYCAREGIVER.ELDERLYNAME";
    public final static String EXTRA_BIRTHDAY_DAY = "COM.MSCCS.HKU.FAMILYCAREGIVER.BIRTHDAYDAY";
    public final static String EXTRA_BIRTHDAY_MONTH = "COM.MSCCS.HKU.FAMILYCAREGIVER.BIRTHDAYMONTH";
    public final static String EXTRA_BIRTHDAY_YEAR = "COM.MSCCS.HKU.FAMILYCAREGIVER.BIRTHDAYYEAR";

    private String elderlyName;
    private int birthdayDay;
    //Just a reminder, the birthday month here is always 1 less than actual, Jan=0,Dec=11
    private int birthdayMonth;
    private int birthdayYear;

    String currentUserPhoneNumber;
    Fragment currentFragment;

    @BindView(R.id.inviteNewMemberFAB)
    public FloatingActionButton inviteNewMemberFAB;

    @OnClick(R.id.inviteNewMemberFAB)
    public void createGroupAndAddMember() {
        currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        //Get essential information for create group, adding user into group
        if (currentFragment instanceof InviteMembersFragment) {
            //Get the UID & telNum of the users sending invitation
            final String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference userReference = database.getReference("users");
            Query query = userReference.orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CustomFirebaseUser user = snapshot.getValue(CustomFirebaseUser.class);
                        currentUserPhoneNumber = user.getTelNum();
                    }

                    //Add a call back functions here to trigger the insertion of values
                    Group newGroup = new Group(elderlyName, new Date(birthdayYear, birthdayMonth, birthdayDay));
                    CustomFirebaseUser currentUser = new CustomFirebaseUser(currentUserUid, currentUserPhoneNumber);
                    ArrayList toBeInvitedList = ((InviteMembersFragment) currentFragment).getToBeInvitedList();
                    //Adding this line here is because it is a async call, ensure the necessary data is derived from network call before inserting new database entry
                    initNewGroup(newGroup, currentUser, toBeInvitedList);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_group_member);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        //GET THE TO-BE CREATED GROUP INFO FROM NEW GROUP ACTIVITY
        elderlyName = intent.getStringExtra(EXTRA_ELDERLY_NAME);
        birthdayDay = intent.getIntExtra(EXTRA_BIRTHDAY_DAY, 0);
        birthdayMonth = intent.getIntExtra(EXTRA_BIRTHDAY_MONTH, 0);
        birthdayYear = intent.getIntExtra(EXTRA_BIRTHDAY_YEAR, 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.invite);

        Bundle args = new Bundle();
        args.putString(InviteMembersFragment.ARGS_MODE,"n");
        InviteMembersFragment inviteMembersFragment = new InviteMembersFragment();
        inviteMembersFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, inviteMembersFragment, null);
        transaction.commit();
    }

    private void initNewGroup(Group group, CustomFirebaseUser currentUser, ArrayList toBeInvitedList) {
        //Create a new group and save their info
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("group");
        DatabaseReference groupXRef = groupRef.push();
        String groupId = groupXRef.getKey();
        groupXRef.setValue(group);

        //Insert the creator himself as group member
        DatabaseReference groupMemXRef = FirebaseDatabase.getInstance().getReference("groupMem").child(groupId);
        groupMemXRef.push().setValue(currentUser);

        //Send invitation to the users selected in the invitation page
        int noOfInvitation = toBeInvitedList.size();
        if (noOfInvitation != 0) {
            DatabaseReference groupInviteRef = FirebaseDatabase.getInstance().getReference("groupInvitation");
            for (int i = 0; i < noOfInvitation; i++) {
                String recipientUID = toBeInvitedList.get(i).toString();
                groupInviteRef.child(recipientUID).push().setValue(new GroupInvitation(recipientUID, groupId, elderlyName));
            }
        }

        //Also add in entry in IN GROUP (REPRESENT USER--IN GROUP)
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference inGroupXRef = FirebaseDatabase.getInstance().getReference("inGroup").child(uid);
        inGroupXRef.push().setValue(new InGroup(groupId, elderlyName));
    }
}
