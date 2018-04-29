package com.msccs.hku.familycaregiver.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.msccs.hku.familycaregiver.Model.CustomFirebaseUser;
import com.msccs.hku.familycaregiver.Model.LocalContacts;
import com.msccs.hku.familycaregiver.R;

import java.io.IOException;
import java.util.HashMap;


/**
 * Created by HoiKit on 04/12/2017.
 */

public class GroupMemberListFragment extends ListFragment {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 222;

    FirebaseListAdapter mAdapter;
    HashMap mLocalContactListHashMap;
    TextView mQuitGroupButton;
    String mGroupId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //After android 6.0 need to get the run time permission
        //Check does permission already granted
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            //Handle the case in which the users have replied not grant the read local contacts to the application, show the rationale and ask him to grant
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(getActivity(), "Please go to app settings to grant the READ CONTACTS PERMISSION", Toast.LENGTH_LONG).show();
            } else {
                //If user's have never answer grant the permission or not, display the dialog and ask for the requested permission
                String[] perReqArray = {Manifest.permission.READ_CONTACTS};
                requestPermissions(perReqArray, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_group_member_list, container, false);
        mQuitGroupButton = (TextView) v.findViewById(R.id.quitGroupBtn);
        mQuitGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setMessage(R.string.confirmQuitGroup);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Task<?>[] tasks = new Task[]{
                                removeSelfFromGroupMember(),
                                removeCurrGroupFromInGroup()
                        };

                        Tasks.whenAll(tasks)
                                .continueWithTask(new RollbackIfFailure())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getActivity(),R.string.quitGroupSuccessfully,Toast.LENGTH_LONG).show();
                                        getActivity().finish();
                                    }
                                });
                    }
                });

                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle b = getArguments();
        mGroupId = b.getString("groupId");
        DatabaseReference groupMemRef = FirebaseDatabase.getInstance().getReference("groupMem").child(mGroupId);

        //Check the permission one more time, if the get local contacts permission is already granted,fill in the localStoredList
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            mLocalContactListHashMap = getLocalContactsHashMap();
            mAdapter = new FirebaseListAdapter<CustomFirebaseUser>(getActivity(), CustomFirebaseUser.class, android.R.layout.simple_list_item_1, groupMemRef) {
                @Override
                protected void populateView(View v, CustomFirebaseUser model, int position) {
                    //I believe i should make groupMem pointing to-->users, as i need to retrieve the phone number for this display here
                    TextView textView1 = (TextView) v.findViewById(android.R.id.text1);
                    //if the phone number is the user himself, display "me" in the list item
                    if (model.getUID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        textView1.setText(getString(R.string.me));
                    } else if (mLocalContactListHashMap.get(model.getTelNum()) != null) {
                        //if have the user number in local contact, display local contact, this if is for the case local has the +852 saved also
                        String localContactName = ((LocalContacts) mLocalContactListHashMap.get(model.getTelNum())).getContactsLocalDisplayName();
                        textView1.setText(localContactName);
                    } else if (mLocalContactListHashMap.get(model.getTelNum().substring(4)) != null) {
                        //if have the user number in local contact, display local contact, this if is for the case local dont have the +852 saved
                        String localContactName = ((LocalContacts) mLocalContactListHashMap.get(model.getTelNum().substring(4))).getContactsLocalDisplayName();
                        textView1.setText(localContactName);
                    } else {
                        //if the user is not in my contact list, display their phone number
                        textView1.setText(model.getTelNum());
                    }
                }
            };
            this.setListAdapter(mAdapter);
        }
    }

    //Performance much better if user Hashmap instead of Arraylist
    public HashMap<String, LocalContacts> getLocalContactsHashMap() {
        HashMap<String, LocalContacts> s = new HashMap<>();
        Cursor cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\s+", "");
            s.put(phoneNumber, new LocalContacts(name, phoneNumber));
        }
        cursor.close();
        return s;
    }

    //About sync task handling: https://github.com/BoltsFramework/Bolts-Android/issues/13
    //Ref: https://stackoverflow.com/questions/38966056/android-wait-for-firebase-valueeventlistener/38966348#38966348
    //This 2 method as I want to ensure both query are completed before activity is killed, avoid causing data issue.
    public Task<String> removeSelfFromGroupMember() {
        final TaskCompletionSource<String> tcs = new TaskCompletionSource<>();
        FirebaseMessaging.getInstance().unsubscribeFromTopic(mGroupId);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference groupMemRef = FirebaseDatabase.getInstance().getReference("groupMem").child(mGroupId);
        Query groupMemQuery = groupMemRef.orderByChild("uid").equalTo(uid);
        groupMemQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().setValue(null);
                }
                tcs.setResult("1");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                tcs.setException(new IOException("TAG", databaseError.toException()));
            }
        });
        return tcs.getTask();
    }

    public Task<String> removeCurrGroupFromInGroup() {
        final TaskCompletionSource<String> tcs = new TaskCompletionSource<>();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference inGroupRef = FirebaseDatabase.getInstance().getReference("inGroup").child(uid);
        Query inGroupQuery = inGroupRef.orderByChild("groupId").equalTo(mGroupId);
        inGroupQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().setValue(null);
                    //philip: this line is just a work-around for letting the app know this task has been completed
                }
                tcs.setResult("1");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                tcs.setException(new IOException("TAG", databaseError.toException()));
            }
        });
        return tcs.getTask();
    }

    class RollbackIfFailure implements Continuation<Void, Task<Void>> {
        @Override
        public Task<Void> then(@NonNull Task<Void> task) throws Exception {
            final TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();
            if (task.isSuccessful()) {
                tcs.setResult(null);
            } else {
                // Rollback everything
            }
            return tcs.getTask();
        }
    }
}
