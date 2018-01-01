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
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.provider.ContactsContract;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.msccs.hku.familycaregiver.Model.CustomFirebaseUser;
import com.msccs.hku.familycaregiver.Model.LocalContacts;
import com.msccs.hku.familycaregiver.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class InviteMembersFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 222;
    public static final String ARGS_MODE ="COM.MSCCS.HKU.FAMILYCAREGIVER.INVITE_MODE";
    public static final String ARGS_GROUPID ="COM.MSCCS.HKU.FAMILYCAREGIVER.EXTRA_GROUP_ID";

    private String mGroupId;

    ListView mContactsListView;
    Cursor cursor;
    String name, phoneNumber;
    ArrayList<CustomFirebaseUser> firebaseStoredUserList;
    ArrayAdapter arrayAdapter;
    String mInviteMode;         //For existing group or new group?

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
        mContactsListView = (ListView) v.findViewById(R.id.contacts_list);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mInviteMode = getArguments().getString(ARGS_MODE,"n");          //N = new group, e = Existing group
        mGroupId = getArguments().getString(ARGS_GROUPID,"z");

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

        //Check the permission one more time, if the get local contacts permission is already granted,fill in the localStoredList
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            initializeListView();
        }
    }

    //Performance much better if user Hashmap instead of Arraylist
    public void initializeListView() {
        //Get the Local Contacts into an array list
        Set<LocalContacts> s = new LinkedHashSet<LocalContacts>();
        cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            //20171111 - if need to use locally stored name in the add user list, should also retrieve the name here
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\s+", "");
            //This line is to put the phone number into the array-list,which is used for compare with the FireBase list
            s.add(new LocalContacts(name, phoneNumber));
        }

        final ArrayList<LocalContacts> localStoredContacts = new ArrayList<LocalContacts>();
        localStoredContacts.addAll(s);
        cursor.close();

        //Start getting the firebase user list
        final HashMap<String, String> phoneUIDHashmap = new HashMap<>();
        final ArrayList<LocalContacts> ToBeDisplayedList = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String phoneNum = userSnapshot.getValue(CustomFirebaseUser.class).getTelNum();
                    String uid = userSnapshot.getValue(CustomFirebaseUser.class).getUID();
                    if (!phoneNum.equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) {
                        phoneUIDHashmap.put(phoneNum, uid);
                    }
                }

                if (mInviteMode.equals("e")){
                    FirebaseDatabase.getInstance().getReference("groupMem").child(mGroupId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot groupMemSnapshot : dataSnapshot.getChildren()) {
                                String telNum = groupMemSnapshot.getValue(CustomFirebaseUser.class).getTelNum();
                                phoneUIDHashmap.remove(telNum);
                            }

                            //Intentionally out the comparison code here, to ensure the comparison is started after both local contact list and firebase user list has been completely loaded
                            for (LocalContacts localContacts : localStoredContacts) {
                                String localPhoneNum = localContacts.getContactsPhoneNumber();
                                String standardPhoneNum = "+852"+localContacts.getContactsPhoneNumber();

                                if (!localPhoneNum.equals("") && phoneUIDHashmap.get(localPhoneNum) != null) {
                                    localContacts.setFirebaseUID(phoneUIDHashmap.get(localPhoneNum));
                                    ToBeDisplayedList.add(localContacts);
                                }

                                if (phoneUIDHashmap.get(standardPhoneNum)!=null){
                                    localContacts.setFirebaseUID(phoneUIDHashmap.get(standardPhoneNum));
                                    ToBeDisplayedList.add(localContacts);
                                }
                            }

                            //Attach the array adapter only after the to be displayed list is ready
                            if (mContactsListView.getAdapter() == null) {
                                arrayAdapter = new ArrayAdapter<LocalContacts>(getActivity(), android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, ToBeDisplayedList);
                                mContactsListView.setAdapter(arrayAdapter);
                            } else {
                                arrayAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                if (mInviteMode.equals("n")) {
                    //Intentionally out the comparison code here, to ensure the comparison is started after both local contact list and firebase user list has been completely loaded
                    for (LocalContacts localContacts : localStoredContacts) {
                        String localPhoneNum = localContacts.getContactsPhoneNumber();
                        String standardPhoneNum = "+852" + localContacts.getContactsPhoneNumber();

                        if (!localPhoneNum.equals("") && phoneUIDHashmap.get(localPhoneNum) != null) {
                            localContacts.setFirebaseUID(phoneUIDHashmap.get(localPhoneNum));
                            ToBeDisplayedList.add(localContacts);
                        }

                        if (phoneUIDHashmap.get(standardPhoneNum) != null) {
                            localContacts.setFirebaseUID(phoneUIDHashmap.get(standardPhoneNum));
                            ToBeDisplayedList.add(localContacts);
                        }
                    }


                    //Attach the array adapter only after the to be displayed list is ready
                    if (mContactsListView.getAdapter() == null) {
                        arrayAdapter = new ArrayAdapter<LocalContacts>(getActivity(), android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, ToBeDisplayedList);
                        mContactsListView.setAdapter(arrayAdapter);
                    } else {
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {
        switch (RC) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();
                    initializeListView();
                } else {
                    Toast.makeText(getActivity(), "Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    //This is the method for the invite member activity to call in order to get the UID to be added to the group
    public ArrayList getToBeInvitedList() {
        ArrayList toBeInvitedUidList = new ArrayList();

        SparseBooleanArray selectePos = mContactsListView.getCheckedItemPositions();
        ListAdapter lAdapter = mContactsListView.getAdapter();

        for (int i = 0; i < lAdapter.getCount(); i++) {
            if (selectePos.get(i)) {
                toBeInvitedUidList.add(((LocalContacts) (lAdapter.getItem(i))).getFirebaseUID());
            }
        }
        return toBeInvitedUidList;
    }

}