package com.msccs.hku.familycaregiver.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.msccs.hku.familycaregiver.Model.Polling;
import com.msccs.hku.familycaregiver.Model.PollingAns;
import com.msccs.hku.familycaregiver.R;

/**
 * Created by HoiKit on 29/12/2017.
 */

public class PollingResultActivity extends AppCompatActivity {

    public static final String EXTRA_POLLING_RESULT_ACT_POLLINGID= "COM.MSCCS.HKU.FAMILYCAREGIVER.POLLING_DETAIL_POLL_ID";
    private FirebaseDatabase mDatabase;
    private View mOption1View,mOption2View,mOption3View,mOption4View,mOption5View;
    private TextView mQuestionLbl;
    private TextView mOption1Lbl;
    private TextView mOption2Lbl;
    private TextView mOption3Lbl;
    private TextView mOption4Lbl;
    private TextView mOption5Lbl;
    private TextView mOption1CountLbl;
    private TextView mOption2CountLbl;
    private TextView mOption3CountLbl;
    private TextView mOption4CountLbl;
    private TextView mOption5CountLbl;

    private String mPollingId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polling_result_activity);

        //setup and initialize the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.pollingResult);

        mPollingId = getIntent().getStringExtra(EXTRA_POLLING_RESULT_ACT_POLLINGID);

        mOption1View = findViewById(R.id.option1View);
        mOption2View = findViewById(R.id.option2View);
        mOption3View = findViewById(R.id.option3View);
        mOption4View = findViewById(R.id.option4View);
        mOption5View = findViewById(R.id.option5View);

        mQuestionLbl = (TextView) findViewById(R.id.pollingQuestionLbl);
        mOption1Lbl = (TextView) findViewById(R.id.option1Lbl);
        mOption2Lbl = (TextView) findViewById(R.id.option2Lbl);
        mOption3Lbl = (TextView) findViewById(R.id.option3Lbl);
        mOption4Lbl = (TextView) findViewById(R.id.option4Lbl);
        mOption5Lbl = (TextView) findViewById(R.id.option5Lbl);
        mOption1CountLbl = (TextView) findViewById(R.id.option1CountLbl);
        mOption2CountLbl = (TextView) findViewById(R.id.option2CountLbl);
        mOption3CountLbl = (TextView) findViewById(R.id.option3CountLbl);
        mOption4CountLbl = (TextView) findViewById(R.id.option4CountLbl);
        mOption5CountLbl = (TextView) findViewById(R.id.option5CountLbl);

        mDatabase = FirebaseDatabase.getInstance();
        Query pollingQuery = mDatabase.getReference("polling").orderByKey().equalTo(mPollingId);
        pollingQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot pollingSnapshot:dataSnapshot.getChildren()){
                    Polling polling = pollingSnapshot.getValue(Polling.class);
                    mQuestionLbl.setText(polling.getPollingQuestion());
                    mOption1Lbl.setText(polling.getOption1());
                    mOption2Lbl.setText(polling.getOption2());
                    mOption3Lbl.setText(polling.getOption3());
                    mOption4Lbl.setText(polling.getOption4());
                    mOption5Lbl.setText(polling.getOption5());

                    switch (polling.getNoOfOptions()){
                        case 2:
                            mOption1View.setVisibility(View.VISIBLE);
                            mOption2View.setVisibility(View.VISIBLE);
                            mOption3View.setVisibility(View.GONE);
                            mOption4View.setVisibility(View.GONE);
                            mOption5View.setVisibility(View.GONE);
                            break;
                        case 3:
                            mOption1View.setVisibility(View.VISIBLE);
                            mOption2View.setVisibility(View.VISIBLE);
                            mOption3View.setVisibility(View.VISIBLE);
                            mOption4View.setVisibility(View.GONE);
                            mOption5View.setVisibility(View.GONE);
                            break;
                        case 4:
                            mOption1View.setVisibility(View.VISIBLE);
                            mOption2View.setVisibility(View.VISIBLE);
                            mOption3View.setVisibility(View.VISIBLE);
                            mOption4View.setVisibility(View.VISIBLE);
                            mOption5View.setVisibility(View.GONE);
                            break;
                        case 5:
                            mOption1View.setVisibility(View.VISIBLE);
                            mOption2View.setVisibility(View.VISIBLE);
                            mOption3View.setVisibility(View.VISIBLE);
                            mOption4View.setVisibility(View.VISIBLE);
                            mOption5View.setVisibility(View.VISIBLE);
                            break;
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Log.d("pollingId",mPollingId);

        DatabaseReference pollingAnsRef = mDatabase.getReference("pollingAns").child(mPollingId);
        pollingAnsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int option1VoteCount=0;
                int option2VoteCount=0;
                int option3VoteCount=0;
                int option4VoteCount=0;
                int option5VoteCount=0;

                for (DataSnapshot voteSnapshot: dataSnapshot.getChildren()){
                    PollingAns pollAns = voteSnapshot.getValue(PollingAns.class);

                    if (pollAns.isOption1Chosen()){
                        option1VoteCount++;
                    }

                    if (pollAns.isOption2Chosen()){
                        option2VoteCount++;
                    }

                    if (pollAns.isOption3Chosen()){
                        option3VoteCount++;
                    }

                    if (pollAns.isOption4Chosen()){
                        option4VoteCount++;
                    }

                    if (pollAns.isOption5Chosen()){
                        option5VoteCount++;
                    }
                }

                mOption1CountLbl.setText(option1VoteCount+"");
                mOption2CountLbl.setText(option2VoteCount+"");
                mOption3CountLbl.setText(option3VoteCount+"");
                mOption4CountLbl.setText(option4VoteCount+"");
                mOption5CountLbl.setText(option5VoteCount+"");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mOption1View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PollingResultActivity.this,VoterListActivity.class);
                intent.putExtra(VoterListActivity.EXTRA_POLLING_LIST_OPTION_NUM,1);
                intent.putExtra(VoterListActivity.EXTRA_POLLING_LIST_POLLING_ID,mPollingId);
                intent.putExtra(VoterListActivity.EXTRA_POLLING_LIST_OPTION_STRING,mOption1Lbl.getText());
                startActivity(intent);
            }
        });

        mOption2View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PollingResultActivity.this,VoterListActivity.class);
                intent.putExtra(VoterListActivity.EXTRA_POLLING_LIST_OPTION_NUM,2);
                intent.putExtra(VoterListActivity.EXTRA_POLLING_LIST_POLLING_ID,mPollingId);
                intent.putExtra(VoterListActivity.EXTRA_POLLING_LIST_OPTION_STRING,mOption2Lbl.getText());
                startActivity(intent);
            }
        });

        mOption3View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PollingResultActivity.this,VoterListActivity.class);
                intent.putExtra(VoterListActivity.EXTRA_POLLING_LIST_OPTION_NUM,3);
                intent.putExtra(VoterListActivity.EXTRA_POLLING_LIST_POLLING_ID,mPollingId);
                intent.putExtra(VoterListActivity.EXTRA_POLLING_LIST_OPTION_STRING,mOption3Lbl.getText());
                startActivity(intent);
            }
        });

        mOption4View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PollingResultActivity.this,VoterListActivity.class);
                intent.putExtra(VoterListActivity.EXTRA_POLLING_LIST_OPTION_NUM,4);
                intent.putExtra(VoterListActivity.EXTRA_POLLING_LIST_POLLING_ID,mPollingId);
                intent.putExtra(VoterListActivity.EXTRA_POLLING_LIST_OPTION_STRING,mOption4Lbl.getText());
                startActivity(intent);
            }
        });

        mOption5View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PollingResultActivity.this,VoterListActivity.class);
                intent.putExtra(VoterListActivity.EXTRA_POLLING_LIST_OPTION_NUM,5);
                intent.putExtra(VoterListActivity.EXTRA_POLLING_LIST_POLLING_ID,mPollingId);
                intent.putExtra(VoterListActivity.EXTRA_POLLING_LIST_OPTION_STRING,mOption5Lbl.getText());
                startActivity(intent);
            }
        });

    }




}
