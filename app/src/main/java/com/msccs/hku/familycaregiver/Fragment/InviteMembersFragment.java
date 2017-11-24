package com.msccs.hku.familycaregiver.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.provider.ContactsContract;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.msccs.hku.familycaregiver.Model.FirebaseUser;
import com.msccs.hku.familycaregiver.Model.LocalContacts;
import com.msccs.hku.familycaregiver.R;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class InviteMembersFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 222;

    ListView mContactsListView;
    Cursor cursor;
    String name, phoneNumber;
    ArrayList<LocalContacts> localStoredContacts;
    ArrayList<FirebaseUser> firebaseStoredUserList;
    ArrayAdapter arrayAdapter;

    public InviteMembersFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseStoredUserList = new ArrayList<FirebaseUser>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_invite_members, container, false);
        // Required empty public constructor
        mContactsListView = (ListView) v.findViewById(R.id.contacts_list);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //After android 6.0 need to get the run time permission
        //Check does permission already granted
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            //Handle the case in which the users have replied not grant the read local contacts to the application, show the rationale and ask him to grant
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(getActivity(), "Please go to app settings to grant the READ CONTACTS PERMISSION", Toast.LENGTH_LONG).show();
            } else {
                //If user's have never answer grant the permission or not, display the dialog and ask for the requested permission
                String[] perReqArray={Manifest.permission.READ_CONTACTS};
                requestPermissions(perReqArray,MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }

        //Check the permission one more time, if the get local contacts permission is already granted,fill in the localStoredList
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            //This is the list of contacts from local
            getLocalContactsIntoArrayList();
            getFirebaseUserListIntoArrayList();
        }
    }


    public void getLocalContactsIntoArrayList() {
        //The purpose of using hashset here is to avoid duplication, sometimes the local user contact is duplicate (one in google, one in sim card, one in local phone storage)
        Set<LocalContacts> s = new LinkedHashSet<LocalContacts>();
        localStoredContacts = new ArrayList<LocalContacts>();
        cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            //20171111 - if need to use locally stored name in the add user list, should also retrieve the name here
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            //This line is to put the phone number into the array-list,which is used for compare with the FireBase list
            s.add(new LocalContacts(name,phoneNumber));
        }
        localStoredContacts.addAll(s);
        cursor.close();
    }

    public void getFirebaseUserListIntoArrayList(){
        //This section is to retrieve the Firebase registered user info to local phone
        FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot userInfoMapSnapshot : dataSnapshot.getChildren()) {
                            FirebaseUser userInfo = userInfoMapSnapshot.getValue(FirebaseUser.class);

                            //This line is for get rid of having user's own user account also put into the lists
                            if (!userInfo.getTelNum().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())){
                                firebaseStoredUserList.add(new FirebaseUser(userInfo.getUID(), userInfo.getTelNum()));
                            }
                        }

                        //Compare the list of local contacts with firebase user list, retain only the common one inside the to-be displayed list
                        localStoredContacts.retainAll(firebaseStoredUserList);

                        //difficulties here: how to get the UID back to the local list, as we need it for adding user into group
                        //Temporary workaround, compare it one by one and add back the UID into the list to be displayed
                        //A manual version of inner join here
                        firebaseStoredUserList.retainAll(localStoredContacts);
                        for (LocalContacts localContacts:localStoredContacts){
                            if ((localContacts.getFirebaseUID()==null || localContacts.getFirebaseUID().equals(""))){
                                for (FirebaseUser firebaseUser:firebaseStoredUserList){
                                    if (localContacts.equals(firebaseUser)){
                                        localContacts.setFirebaseUID(firebaseUser.getUID());
                                        continue;
                                    }
                                }
                            }
                        }


                        //Attach the array adapter only after the to be displayed list is ready
                        if (mContactsListView.getAdapter()==null){
                            arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, localStoredContacts);
                            mContactsListView.setAdapter(arrayAdapter);
                        }else{
                            arrayAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {
        switch (RC) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();
                    getLocalContactsIntoArrayList();
                    getFirebaseUserListIntoArrayList();
                } else {
                    Toast.makeText(getActivity(), "Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

}