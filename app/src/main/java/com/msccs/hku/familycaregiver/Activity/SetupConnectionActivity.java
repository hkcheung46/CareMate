package com.msccs.hku.familycaregiver.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.msccs.hku.familycaregiver.R;
import com.msccs.hku.familycaregiver.Util.RandomString;

import java.util.concurrent.ThreadLocalRandom;

public class SetupConnectionActivity extends AppCompatActivity {

    public final static String EXTRA_GROUP_NAME ="COM.MSCCS.HKU.FAMILYCAREGIVER.EXTRA_GROUP_NAME";
    public final static String EXTRA_GROUP_ID ="COM.MSCCS.HKU.FAMILYCAREGIVER.EXTRA_GROUP_ID";

    private String mGroupName;
    private String mGroupId;
    private String mConnectionPin;

    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    private CardView mConnectionCardView;
    private TextView mConnectionPinLbl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_connection);

        //Get the intent content
        mGroupId = getIntent().getStringExtra(EXTRA_GROUP_ID);
        mGroupName = getIntent().getStringExtra(EXTRA_GROUP_NAME);

        //setup and initialize the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.deviceConnection);

        mConnectionCardView = findViewById(R.id.connection_card_view);
        mConnectionPinLbl = findViewById(R.id.connection_lbl);

        mDatabase.getReference("HomeClientConnectionPin").orderByKey().equalTo(mGroupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot pinSnapshot:dataSnapshot.getChildren()){
                    String pin = pinSnapshot.getValue().toString();
                    mConnectionPinLbl.setText(pin);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mConnectionCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RandomString gen = new RandomString(8, ThreadLocalRandom.current());
                mConnectionPin = gen.nextString();
                mConnectionPinLbl.setText(mConnectionPin);
                mDatabase.getReference("HomeClientConnectionPin").child(mGroupId).setValue(mConnectionPin);

            }
        });
    }
}
