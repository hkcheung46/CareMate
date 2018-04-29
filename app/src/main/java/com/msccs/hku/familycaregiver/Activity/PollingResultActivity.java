package com.msccs.hku.familycaregiver.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.msccs.hku.familycaregiver.Model.Polling;
import com.msccs.hku.familycaregiver.Model.PollingAns;
import com.msccs.hku.familycaregiver.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HoiKit on 29/12/2017.
 */

public class PollingResultActivity extends AppCompatActivity {

    public static final String EXTRA_POLLING_RESULT_ACT_POLLINGID = "COM.MSCCS.HKU.FAMILYCAREGIVER.POLLING_DETAIL_POLL_ID";
    private FirebaseDatabase mDatabase;
    private View mOption1View, mOption2View, mOption3View, mOption4View, mOption5View;
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

    int option1VoteCount = 0;
    int option2VoteCount = 0;
    int option3VoteCount = 0;
    int option4VoteCount = 0;
    int option5VoteCount = 0;

    private int[] voteData;
    private String[] optionData;

    private String mPollingId;

    PieChart pieChart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polling_result_activity);

        //setup and initialize the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.pollingResult);

        //Get the polling ID from intent
        mPollingId = getIntent().getStringExtra(EXTRA_POLLING_RESULT_ACT_POLLINGID);

        //Link up the UI
        mOption1View = findViewById(R.id.option1View);
        mOption2View = findViewById(R.id.option2View);
        mOption3View = findViewById(R.id.option3View);
        mOption4View = findViewById(R.id.option4View);
        mOption5View = findViewById(R.id.option5View);
        mQuestionLbl = findViewById(R.id.pollingQuestionLbl);
        mOption1Lbl = findViewById(R.id.option1Lbl);
        mOption2Lbl = findViewById(R.id.option2Lbl);
        mOption3Lbl = findViewById(R.id.option3Lbl);
        mOption4Lbl = findViewById(R.id.option4Lbl);
        mOption5Lbl = findViewById(R.id.option5Lbl);
        mOption1CountLbl = findViewById(R.id.option1CountLbl);
        mOption2CountLbl = findViewById(R.id.option2CountLbl);
        mOption3CountLbl = findViewById(R.id.option3CountLbl);
        mOption4CountLbl = findViewById(R.id.option4CountLbl);
        mOption5CountLbl = findViewById(R.id.option5CountLbl);

        //Query to setup the questions and answer display
        mDatabase = FirebaseDatabase.getInstance();
        Query pollingQuery = mDatabase.getReference("polling").orderByKey().equalTo(mPollingId);
        pollingQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot pollingSnapshot : dataSnapshot.getChildren()) {
                    Polling polling = pollingSnapshot.getValue(Polling.class);
                    mQuestionLbl.setText(polling.getPollingQuestion());
                    mOption1Lbl.setText(polling.getOption1());
                    mOption2Lbl.setText(polling.getOption2());
                    mOption3Lbl.setText(polling.getOption3());
                    mOption4Lbl.setText(polling.getOption4());
                    mOption5Lbl.setText(polling.getOption5());

                    int numOfOptions = polling.getNoOfOptions();
                    switch (numOfOptions) {
                        case 2:
                            mOption1View.setVisibility(View.VISIBLE);
                            mOption2View.setVisibility(View.VISIBLE);
                            mOption3View.setVisibility(View.GONE);
                            mOption4View.setVisibility(View.GONE);
                            mOption5View.setVisibility(View.GONE);
                            optionData = new String[]{polling.getOption1(), polling.getOption2()};
                            recalculateResult(2);
                            break;
                        case 3:
                            mOption1View.setVisibility(View.VISIBLE);
                            mOption2View.setVisibility(View.VISIBLE);
                            mOption3View.setVisibility(View.VISIBLE);
                            mOption4View.setVisibility(View.GONE);
                            mOption5View.setVisibility(View.GONE);
                            optionData = new String[]{polling.getOption1(), polling.getOption2(),polling.getOption3()};
                            recalculateResult(3);
                            break;
                        case 4:
                            mOption1View.setVisibility(View.VISIBLE);
                            mOption2View.setVisibility(View.VISIBLE);
                            mOption3View.setVisibility(View.VISIBLE);
                            mOption4View.setVisibility(View.VISIBLE);
                            mOption5View.setVisibility(View.GONE);
                            optionData = new String[]{polling.getOption1(), polling.getOption2(),polling.getOption3(),polling.getOption4()};
                            recalculateResult(4);
                            break;
                        case 5:
                            mOption1View.setVisibility(View.VISIBLE);
                            mOption2View.setVisibility(View.VISIBLE);
                            mOption3View.setVisibility(View.VISIBLE);
                            mOption4View.setVisibility(View.VISIBLE);
                            mOption5View.setVisibility(View.VISIBLE);
                            optionData = new String[]{polling.getOption1(), polling.getOption2(),polling.getOption3(),polling.getOption4(),polling.getOption5()};
                            recalculateResult(5);
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //initialize pie chart
        pieChart = (PieChart) findViewById(R.id.pieChart);
        Description description = new Description();
        description.setText("Votes(s) received by each option");
        pieChart.setDescription(description);
        pieChart.setHoleRadius(20);
        pieChart.setTransparentCircleRadius(35);
        pieChart.setTransparentCircleAlpha(50);
        pieChart.setDrawEntryLabels(true);


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

    private void addDataSet() {
        ArrayList<PieEntry> votesEntry = new ArrayList<>();
        ArrayList<String> optionEntry = new ArrayList<>();

        for (int i = 0; i < optionData.length; i++) {
            optionEntry.add(optionData[i]);
        }

        for (int i = 0; i < voteData.length; i++) {
            votesEntry.add(new PieEntry(voteData[i], optionData[i]));
        }

        //create the dataset
        PieDataSet pieDataSet = new PieDataSet(votesEntry,"");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(15);

        //Color for pie
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.GRAY);
        colors.add(Color.BLUE);
        colors.add(Color.RED);
        colors.add(Color.MAGENTA);
        colors.add(Color.rgb(26,148,49));
        pieDataSet.setColors(colors);

        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }


    private void recalculateResult(final int optionCount) {
        DatabaseReference pollingAnsRef = mDatabase.getReference("pollingAns").child(mPollingId);
        pollingAnsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot voteSnapshot : dataSnapshot.getChildren()) {
                    PollingAns pollAns = voteSnapshot.getValue(PollingAns.class);
                    if (pollAns.isOption1Chosen()) {
                        option1VoteCount++;
                    }

                    if (pollAns.isOption2Chosen()) {
                        option2VoteCount++;
                    }

                    if (pollAns.isOption3Chosen()) {
                        option3VoteCount++;
                    }

                    if (pollAns.isOption4Chosen()) {
                        option4VoteCount++;
                    }

                    if (pollAns.isOption5Chosen()) {
                        option5VoteCount++;
                    }
                }
                mOption1CountLbl.setText(option1VoteCount + "");
                mOption2CountLbl.setText(option2VoteCount + "");
                mOption3CountLbl.setText(option3VoteCount + "");
                mOption4CountLbl.setText(option4VoteCount + "");
                mOption5CountLbl.setText(option5VoteCount + "");

                if (optionCount==2){
                    voteData= new int[]{option1VoteCount, option2VoteCount};
                }else if(optionCount==3){
                    voteData= new int[]{option1VoteCount, option2VoteCount,option3VoteCount};
                }else if(optionCount==4){
                    voteData= new int[]{option1VoteCount, option2VoteCount, option3VoteCount, option4VoteCount};
                }else{
                    voteData= new int[]{option1VoteCount, option2VoteCount, option3VoteCount, option4VoteCount, option5VoteCount};
                }
                addDataSet();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
