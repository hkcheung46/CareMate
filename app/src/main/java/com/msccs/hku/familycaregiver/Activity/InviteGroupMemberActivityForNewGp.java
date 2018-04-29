package com.msccs.hku.familycaregiver.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.msccs.hku.familycaregiver.Fragment.InviteMembersFragment;
import com.msccs.hku.familycaregiver.Model.CustomFirebaseUser;
import com.msccs.hku.familycaregiver.Model.Group;
import com.msccs.hku.familycaregiver.Model.GroupInvitation;
import com.msccs.hku.familycaregiver.tempStructure.InGroup;
import com.msccs.hku.familycaregiver.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InviteGroupMemberActivityForNewGp extends AppCompatActivity {

    public final static String EXTRA_ELDERLY_NAME = "COM.MSCCS.HKU.FAMILYCAREGIVER.ELDERLYNAME";
    public final static String EXTRA_BIRTHDAY = "COM.MSCCS.HKU.FAMILYCAREGIVER.BIRTHDAY";
    public final static String EXTRA_ELDER_PHOTO="COM.MSCCS.HKU.FAMILYCAREGIVER.ELDERPHOTO";

    private String mElderlyName;
    private long mBirthday;
    private Bitmap elderlyImage;
    //Just a reminder, the birthday month here is always 1 less than actual, Jan=0,Dec=11

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
                    Group newGroup = new Group(mElderlyName, mBirthday);
                    CustomFirebaseUser currentUser = new CustomFirebaseUser(currentUserUid, currentUserPhoneNumber);
                    ArrayList toBeInvitedList = ((InviteMembersFragment) currentFragment).getToBeInvitedList();
                    //Adding this line here is because it is a async call, ensure the necessary data is derived from network call before inserting new database entry
                    String groupId = createNewGroupAndSubcribeNotifications(newGroup, currentUser, toBeInvitedList,mElderlyName).getResult();

                    uploadGroupPhoto(elderlyImage,groupId).addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            finish();
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_group_member);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        //GET THE TO-BE CREATED GROUP INFO FROM NEW GROUP ACTIVITY
        mElderlyName = intent.getStringExtra(EXTRA_ELDERLY_NAME);
        mBirthday = intent.getLongExtra(EXTRA_BIRTHDAY,0);
        elderlyImage = (Bitmap) intent.getParcelableExtra(EXTRA_ELDER_PHOTO);

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


    public Uri bitmapToUriConverter(Bitmap mBitmap) {
        Uri uri = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            //Bitmap newBitmap = Bitmap.createScaledBitmap(mBitmap,200, 200, true);
            File file = new File(InviteGroupMemberActivityForNewGp.this.getFilesDir(), "Image" + new Random().nextInt() + ".jpeg");
            FileOutputStream out = InviteGroupMemberActivityForNewGp.this.openFileOutput(file.getName(), Context.MODE_PRIVATE);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            //get absolute path
            String realPath = file.getAbsolutePath();
            File f = new File(realPath);
            uri = Uri.fromFile(f);

        } catch (Exception e) {
            Log.e("Error message", e.getMessage());
        }
        return uri;
    }

    private Task<String> createNewGroupAndSubcribeNotifications(Group group, CustomFirebaseUser currentUser, ArrayList toBeInvitedList, String elderlyName) {
        final TaskCompletionSource<String> tcs = new TaskCompletionSource<>();
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
        inGroupXRef.push().setValue(new InGroup(groupId,  elderlyName));

        //Subscribe to topic (the topic id is the groupId)
        FirebaseMessaging.getInstance().subscribeToTopic(groupId);

        tcs.setResult(groupId);

        return tcs.getTask();
    }

    private Task<String> uploadGroupPhoto(Bitmap elderlyImage,String groupId){
        final TaskCompletionSource<String> tcs = new TaskCompletionSource<>();

        //Save elderly photo thumbnail
        if (elderlyImage!=null){
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference thumbnailRef = firebaseStorage.getReference().child("groupThumbNail").child(groupId);
            thumbnailRef.putFile(bitmapToUriConverter(elderlyImage)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    tcs.setResult(null);
                }
            });
        }else{
            tcs.setResult(null);
        }
        return tcs.getTask();
    }

}
