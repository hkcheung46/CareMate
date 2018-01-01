package com.msccs.hku.familycaregiver.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.msccs.hku.familycaregiver.Model.CustomFirebaseUser;
import com.msccs.hku.familycaregiver.Model.GroupInvitation;
import com.msccs.hku.familycaregiver.R;
import com.firebase.ui.database.FirebaseListAdapter;


public class InvitationListFragment extends ListFragment {

    FirebaseListAdapter<GroupInvitation> mAdapter;
    String currentUserPhoneNumber;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_invitation, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference groupInviteRef = database.getReference("groupInvitation").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mAdapter = new FirebaseListAdapter<GroupInvitation>(getActivity(), GroupInvitation.class, R.layout.invitation_list_item, groupInviteRef) {
            @Override
            protected void populateView(View v, final GroupInvitation model, final int position) {
                TextView groupNameLbl = (TextView) v.findViewById(R.id.groupNameLbl);
                ImageView acceptInviteImage = (ImageView) v.findViewById(R.id.acceptBtn);
                ImageView rejectInviteImage = (ImageView) v.findViewById(R.id.rejectBtn);

                //When user accept the invitation
                acceptInviteImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "Accept", Toast.LENGTH_SHORT).show();
                        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        final String groupId = getItem(position).getGroupId();
                        final String groupName =model.getGroupName();

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference userReference = database.getReference("users");
                        Query query = userReference.orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    CustomFirebaseUser user = snapshot.getValue(CustomFirebaseUser.class);
                                    insertDBentryForAcceptInvite(user,groupId,groupName);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        //Remove the invitation record
                        getRef(position).removeValue();
                    }
                });

                //When user reject the invitation
                rejectInviteImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "Reject", Toast.LENGTH_SHORT).show();
                        Log.e("Reject", getRef(position) + "");
                        //Remove the invitation record
                        getRef(position).removeValue();
                    }
                });
                groupNameLbl.setText(model.getGroupName());
            }
        };
        this.setListAdapter(mAdapter);
    }

    private void insertDBentryForAcceptInvite(CustomFirebaseUser user,String groupId,String groupName){
        //Insert a record into groupMem tree
        DatabaseReference groupMemRef = FirebaseDatabase.getInstance().getReference("groupMem").child(groupId);
        groupMemRef.push().setValue(user);
        Log.d("groupMemRef", groupMemRef.toString());

        //Insert a record into InGroup tree
        DatabaseReference inGroupRef = FirebaseDatabase.getInstance().getReference("inGroup").child(user.getUID());
        DatabaseReference ref = inGroupRef.push();
        ref.child("groupId").setValue(groupId);
        ref.child("groupName").setValue(groupName);

        //Subscribe Notification
        FirebaseMessaging.getInstance().subscribeToTopic(groupId);
    }


}
