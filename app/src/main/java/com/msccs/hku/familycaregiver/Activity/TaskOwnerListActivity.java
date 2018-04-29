package com.msccs.hku.familycaregiver.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.msccs.hku.familycaregiver.Manifest;
import com.msccs.hku.familycaregiver.Model.CustomFirebaseUser;
import com.msccs.hku.familycaregiver.Model.LocalContacts;
import com.msccs.hku.familycaregiver.R;

import java.util.HashMap;

public class TaskOwnerListActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "COM.MSCCS.HKU.FAMILYCAREGIVER.EXTRA_TASK_ID";
    public static final String EXTRA_GROUP_ID = "COM.MSCCS.HKU.FAMILYCAREGIVER.EXTRA_GROUP_ID";

    private FirebaseListAdapter<CustomFirebaseUser> mFirebaseListAdapter;
    private ListView mTaskOwnerListView;

    private HashMap<String, LocalContacts> mLocalContactHashmap;
    private String mTaskId;
    private String mGroupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_owner_list);

        //Get the information from the intent
        mTaskId = getIntent().getStringExtra(EXTRA_TASK_ID);
        mGroupId = getIntent().getStringExtra(EXTRA_GROUP_ID);

        //Link up the UI Components
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Task Owner List");

        mTaskOwnerListView = findViewById(R.id.taskOwnerListView);
        mLocalContactHashmap = getLocalContactsHashMap();

        DatabaseReference taskAssigneeRef = FirebaseDatabase.getInstance().getReference("taskAssignee").child(mTaskId);
        mFirebaseListAdapter = new FirebaseListAdapter<CustomFirebaseUser>(TaskOwnerListActivity.this, CustomFirebaseUser.class,R.layout.item_task_owner, taskAssigneeRef) {
            @Override
            protected void populateView(View v, final CustomFirebaseUser user, int position) {
                TextView nameLbl = v.findViewById(R.id.contactNameLbl);
                ImageView phoneLbl = v.findViewById(R.id.phone_button);
                final ImageView userPhotoImgView = v.findViewById(R.id.contact_photo);

                final String phoneNo = user.getTelNum();
                String uid = user.getUID();


                //If the whole tel code can be matched, use it, if not ignore the +XXX part
                LocalContacts contact = mLocalContactHashmap.get(phoneNo);
                if (contact == null) {
                    contact = mLocalContactHashmap.get(phoneNo.substring(4));
                }
                if (phoneNo.equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) {
                    nameLbl.setText(getString(R.string.me));
                } else if (contact != null) {
                    nameLbl.setText(contact.getContactsLocalDisplayName());
                } else {
                    nameLbl.setText(phoneNo);
                }


                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://family-care-giver.appspot.com");
                final StorageReference pathReference = storageReference.child("userPhoto/" + uid);
                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(TaskOwnerListActivity.this).using(new FirebaseImageLoader()).load(pathReference).asBitmap().centerCrop().into(new BitmapImageViewTarget(userPhotoImgView) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(TaskOwnerListActivity.this.getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                userPhotoImgView.setImageDrawable(circularBitmapDrawable);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // File not found
                        Glide.with(TaskOwnerListActivity.this).load(R.drawable.ic_account_circle_white_24dp).asBitmap().centerCrop().into(new BitmapImageViewTarget(userPhotoImgView) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(TaskOwnerListActivity.this.getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                userPhotoImgView.setImageDrawable(circularBitmapDrawable);
                                userPhotoImgView.setBackground(TaskOwnerListActivity.this.getResources().getDrawable(R.drawable.round_grey_oval));
                            }
                        });
                    }
                });

                //this is for trigger the dial action
                phoneLbl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + phoneNo));
                        startActivity(callIntent);
                    }
                });
            }
        };
        mTaskOwnerListView.setAdapter(mFirebaseListAdapter);

    }

    //Performance much better if user Hashmap instead of Arraylist
    public HashMap<String, LocalContacts> getLocalContactsHashMap() {
        HashMap<String, LocalContacts> s = new HashMap<>();
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\s+", "");
            s.put(phoneNumber, new LocalContacts(name, phoneNumber));
        }
        cursor.close();
        return s;
    }

}
