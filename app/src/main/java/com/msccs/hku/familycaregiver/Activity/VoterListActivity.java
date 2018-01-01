package com.msccs.hku.familycaregiver.Activity;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.msccs.hku.familycaregiver.Adapter.VoterAdapter;
import com.msccs.hku.familycaregiver.Model.CustomFirebaseUser;
import com.msccs.hku.familycaregiver.R;

import java.util.ArrayList;
import java.util.HashMap;

public class VoterListActivity extends ListActivity {

    public static final String EXTRA_POLLING_LIST_POLLING_ID = "COM.MSCCS.HKU.FAMILYCAREGIVER.POLLING_ID";
    public static final String EXTRA_POLLING_LIST_OPTION_NUM = "COM.MSCCS.HKU.FAMILYCAREGIVER.POLLING_OPTIONNUM";
    public static final String EXTRA_POLLING_LIST_OPTION_STRING = "COM.MSCCS.HKU.FAMILYCAREGIVER.POLLING.OPTIONSTRING";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private VoterAdapter mVoterAdapter;
    private ArrayList<String> mToBeDisplayedVoterList;

    private int mOptionNum;
    private String mPollingId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voter_list);

        mOptionNum = getIntent().getIntExtra(EXTRA_POLLING_LIST_OPTION_NUM, 0);
        mPollingId = getIntent().getStringExtra(EXTRA_POLLING_LIST_POLLING_ID);
        String optionString = getIntent().getStringExtra(EXTRA_POLLING_LIST_OPTION_STRING);

        //setup and initialize the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(optionString + "'s voter");

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        mToBeDisplayedVoterList = new ArrayList<String>();
        mVoterAdapter = new VoterAdapter(VoterListActivity.this, mToBeDisplayedVoterList);

        getListView().setAdapter(mVoterAdapter);
        reloadVoterList(mOptionNum);

        // This part is for handle the swap to refresh
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        reloadVoterList(mOptionNum);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
    }


    private void reloadVoterList(int optionNum) {

        final ArrayList<String> newToBeDisplayedVoterList = new ArrayList<String>();
        final HashMap<String, String> phoneNumNameHashmap = getLocalContactHashmap();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String childString = "option" + optionNum + "Chosen";
        Query pollingAnsRef = database.getReference().child("pollingAns").child(mPollingId).orderByChild(childString).equalTo(true);

        pollingAnsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot pollingAnsSnapshot : dataSnapshot.getChildren()) {
                    String uid = pollingAnsSnapshot.getKey();

                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
                    Query userQuery = userRef.orderByChild("uid").equalTo(uid);
                    userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot1) {
                            for (DataSnapshot userSnapshot : dataSnapshot1.getChildren()) {
                                String phoneNo = userSnapshot.getValue(CustomFirebaseUser.class).getTelNum();

                                if (phoneNo.equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())){
                                    newToBeDisplayedVoterList.add(getResources().getString(R.string.me));
                                } else if (phoneNumNameHashmap.get(phoneNo) != null) {
                                    newToBeDisplayedVoterList.add(phoneNumNameHashmap.get(phoneNo));
                                } else if (phoneNumNameHashmap.get(phoneNo.substring(4)) != null) {
                                    newToBeDisplayedVoterList.add(phoneNumNameHashmap.get(phoneNo.substring(4)));
                                }
                            }

                            mVoterAdapter.updateData(newToBeDisplayedVoterList);
                            mVoterAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private HashMap<String, String> getLocalContactHashmap() {
        HashMap<String, String> hm = new HashMap<String, String>();
        Cursor cursor = VoterListActivity.this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\s+", "");
            hm.put(phoneNumber, name);
        }
        cursor.close();
        return hm;
    }


}
