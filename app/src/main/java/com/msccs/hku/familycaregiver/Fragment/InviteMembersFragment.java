package com.msccs.hku.familycaregiver.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.msccs.hku.familycaregiver.Model.UserInfoMap;
import com.msccs.hku.familycaregiver.R;

import java.util.ArrayList;

public class InviteMembersFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 222;

    ListView mContactsList;
    Cursor cursor;
    String name, phoneNumber;
    ArrayList<String> localStoredContacts;
    ArrayList<UserInfoMap> firebaseStoredUserList;
    ArrayAdapter arrayAdapter;

    public InviteMembersFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_invite_members, container, false);
        // Required empty public constructor
        mContactsList = (ListView) v.findViewById(R.id.contacts_list);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        localStoredContacts = new ArrayList<String>();
        firebaseStoredUserList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, firebaseStoredUserList);

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

        mContactsList.setAdapter(arrayAdapter);
    }


    public void getLocalContactsIntoArrayList() {
        localStoredContacts = new ArrayList<String>();
        cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            //name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            //This line is to put the phone number into the array-list,which is used for compare with the FireBase list
            localStoredContacts.add(phoneNumber);
        }
        cursor.close();
    }

    public void getFirebaseUserListIntoArrayList(){
        //This section is to retrieve the firebase registered user info to local phone
        FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot userInfoMapSnapshot : dataSnapshot.getChildren()) {
                            UserInfoMap userInfo = userInfoMapSnapshot.getValue(UserInfoMap.class);

                            //This line is for get rid of having user's own user account also put into the lists
                            if (!userInfo.getTelNum().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())){
                                firebaseStoredUserList.add(new UserInfoMap(userInfo.getUID(), userInfo.getTelNum(), userInfo.getDisplayName()));
                            }
                        }
                        firebaseStoredUserList.retainAll(localStoredContacts);
                        arrayAdapter.notifyDataSetChanged();
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