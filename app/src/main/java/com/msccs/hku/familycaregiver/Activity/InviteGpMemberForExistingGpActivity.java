package com.msccs.hku.familycaregiver.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.msccs.hku.familycaregiver.Fragment.InviteMembersFragment;
import com.msccs.hku.familycaregiver.Model.CustomFirebaseUser;
import com.msccs.hku.familycaregiver.Model.Group;
import com.msccs.hku.familycaregiver.Model.GroupInvitation;
import com.msccs.hku.familycaregiver.R;

import java.util.ArrayList;

/**
 * Created by HoiKit on 09/12/2017.
 */

public class InviteGpMemberForExistingGpActivity extends AppCompatActivity{

    public final static String EXTRA_GROUP_ID="COM.MSCCS.HKU.FAMILYCAREGIVER.GROUPID";
    public final static String EXTRA_GROUP_NAME="COM.MSCCS.HKU.FAMILYCAREGIVER.GROUPNAME";

    private FloatingActionButton inviteNewMemberFAB;

    private String mGroupId;
    private String mGroupName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_group_member_existing_group);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.invite);

        Intent intent = getIntent();
        mGroupId = intent.getStringExtra(EXTRA_GROUP_ID);
        mGroupName = intent.getStringExtra(EXTRA_GROUP_NAME);

        FloatingActionButton inviteNewMemberFAB = (FloatingActionButton) findViewById(R.id.inviteNewMemberFAB);
        inviteNewMemberFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Send invite to users, who dont already have invitation for joining this group
                ArrayList toBeInvitedUIDList = new ArrayList<String>();
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

                //Get the selected to be invited users' uid into arraylist
                if (currentFragment instanceof InviteMembersFragment) {
                    toBeInvitedUIDList = ((InviteMembersFragment) currentFragment).getToBeInvitedList();
                    for (int i=0;i<toBeInvitedUIDList.size();i++){
                        String uid = toBeInvitedUIDList.get(i).toString();
                        final GroupInvitation toSendGroupInv = new GroupInvitation(uid,mGroupId,mGroupName);
                        final DatabaseReference invRef = FirebaseDatabase.getInstance().getReference("groupInvitation").child(uid);

                        //Avoid users have multiple invitation to same group
                        Query isInvAlreadyExistQuery = invRef.orderByChild("recipientUid").equalTo(uid);
                        isInvAlreadyExistQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()){
                                    invRef.push().setValue(toSendGroupInv);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    finish();
                }
            }


        });


        Bundle args = new Bundle();
        args.putString(InviteMembersFragment.ARGS_MODE,"e");
        args.putString(InviteMembersFragment.ARGS_GROUPID,mGroupId);
        InviteMembersFragment inviteMembersFragment = new InviteMembersFragment();
        inviteMembersFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, inviteMembersFragment, null);
        transaction.commit();
    }

}
