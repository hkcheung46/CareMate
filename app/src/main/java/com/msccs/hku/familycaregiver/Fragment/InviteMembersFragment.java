package com.msccs.hku.familycaregiver.Fragment;


import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.provider.ContactsContract;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.ui.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.msccs.hku.familycaregiver.Model.UserInfoMap;
import com.msccs.hku.familycaregiver.R;

import java.util.ArrayList;

public class InviteMembersFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    ListView mContactsList;
    Cursor cursor;
    String name, phoneNumber;
    ArrayList<String> localStoreContacts;
    ArrayList<UserInfoMap> firebaseStoredUserList;
    ArrayAdapter<String> arrayAdapter;

    /*************************************************************************************************************************************************************/

    public InviteMembersFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //After android 6.0 need to get the run time permission
        EnableRuntimePermission();

        //The piece of code following target to get the list of local contacts who are registered app user
        //plan to do:

        //This is the list of contacts from local
        localStoreContacts = new ArrayList<String>();
        GetContactsIntoArrayList();

        firebaseStoredUserList = new ArrayList<UserInfoMap>();
        //This section is to retrieve the firebase registered user info to local phone
        FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long value = dataSnapshot.getChildrenCount();
                    for (DataSnapshot userInfoMapSnapshot: dataSnapshot.getChildren()){
                        UserInfoMap userInfo = userInfoMapSnapshot.getValue(UserInfoMap.class);
                        //For debug only
                        Log.d("info",userInfo.getTelNum());
                        Log.d("info2",userInfo.getUID());
                        Log.d("info3",userInfo.getDisplayName());



                        firebaseStoredUserList.add(userInfo);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            }
        );
        Log.d("size1",firebaseStoredUserList.size()+"B");

        firebaseStoredUserList.retainAll(localStoreContacts);
        Log.d("size",firebaseStoredUserList.size()+"A");

        arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, localStoreContacts);
        mContactsList.setAdapter(arrayAdapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_invite_members, container, false);
        // Required empty public constructor
        mContactsList = (ListView) v.findViewById(R.id.contacts_list);
        return v;
    }

    public void GetContactsIntoArrayList() {

        cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            //This line is to put the content into Array List, which affect the display
            localStoreContacts.add(name);
        }
        cursor.close();
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {
        switch (RC) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


    public void EnableRuntimePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                getActivity(), Manifest.permission.READ_CONTACTS)) {
            Toast.makeText(getActivity(), "CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }


}
