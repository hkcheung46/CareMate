package com.msccs.hku.familycaregiver.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.msccs.hku.familycaregiver.R;
import com.msccs.hku.familycaregiver.tempStructure.HomeDataReading;

import java.text.SimpleDateFormat;
import java.util.Set;

public class HomePlusActivity extends AppCompatActivity {

    public final static String EXTRA_GROUP_NAME ="COM.MSCCS.HKU.FAMILYCAREGIVER.EXTRA_GROUP_NAME";
    public final static String EXTRA_GROUP_ID ="COM.MSCCS.HKU.FAMILYCAREGIVER.EXTRA_GROUP_ID";

    private String mGroupName;
    private String mGroupId;

    private TextView mTempReadingLbl;
    private TextView mHumidityReadingLbl;
    private TextView mLastLogTimeLbl;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_plus);

        //Get the intent content
        mGroupId = getIntent().getStringExtra(EXTRA_GROUP_ID);
        mGroupName = getIntent().getStringExtra(EXTRA_GROUP_NAME);

        //setup and initialize the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.homePlus);

        mTempReadingLbl = findViewById(R.id.temperature_reading_lbl);
        mHumidityReadingLbl = findViewById(R.id.humidity_reading_lbl);
        mLastLogTimeLbl = findViewById(R.id.lastDataLogTime);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabase.getReference("homeData").orderByKey().equalTo(mGroupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               if (dataSnapshot.exists()){
                   for (DataSnapshot readingSnapshot:dataSnapshot.getChildren()){
                       HomeDataReading homeDataReading = readingSnapshot.getValue(HomeDataReading.class);
                       mTempReadingLbl.setText(homeDataReading.getTemperature()+"°C");
                       mHumidityReadingLbl.setText(homeDataReading.getHumidity()+"%");
                       SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd-MM-yyyy hh:mm");
                       mLastLogTimeLbl.setText(dateFormat.format(homeDataReading.getLogDate()));
                   }
               }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.home_plus_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.menu_connectToDevice:
                Intent intent = new Intent(HomePlusActivity.this,SetupConnectionActivity.class);
                intent.putExtra(SetupConnectionActivity.EXTRA_GROUP_NAME,mGroupName);
                intent.putExtra(SetupConnectionActivity.EXTRA_GROUP_ID,mGroupId);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
