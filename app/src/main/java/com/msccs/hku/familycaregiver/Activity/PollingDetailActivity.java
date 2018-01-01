package com.msccs.hku.familycaregiver.Activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.msccs.hku.familycaregiver.Model.Polling;
import com.msccs.hku.familycaregiver.Model.PollingAns;
import com.msccs.hku.familycaregiver.R;

import org.w3c.dom.Text;

/**
 * Created by HoiKit on 26/12/2017.
 */

public class PollingDetailActivity extends AppCompatActivity {

    public static final String EXTRA_POLLING_DETAIL_POLL_ID = "COM.MSCCS.HKU.FAMILYCAREGIVER.POLLING_DETAIL_POLL_ID";

    private TextView mPollingQuestionLbl;
    private View mSubmitCloseBtnArea;
    private TextView mSubmitBtn;
    private TextView mClosePollingBtn;

    private RadioGroup mOptionsRgp;
    private RadioButton mOption1Rbtn;
    private RadioButton mOption2Rbtn;
    private RadioButton mOption3Rbtn;
    private RadioButton mOption4Rbtn;
    private RadioButton mOption5Rbtn;
    private View mCheckboxArea;
    private CheckBox mOption1Cbx;
    private CheckBox mOption2Cbx;
    private CheckBox mOption3Cbx;
    private CheckBox mOption4Cbx;
    private CheckBox mOption5Cbx;
    private String mPollingId;

    private Polling mPolling;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polling_detail);

        //setup and initialize the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.pollingDetail);

        mPollingId = getIntent().getStringExtra(EXTRA_POLLING_DETAIL_POLL_ID);
        mPollingQuestionLbl = (TextView) findViewById(R.id.pollingQuestionLbl);
        mSubmitCloseBtnArea = findViewById(R.id.btnView);
        mSubmitBtn = (TextView) findViewById(R.id.submit_btn);
        mClosePollingBtn = (TextView) findViewById(R.id.close_polling_btn);
        mOptionsRgp = (RadioGroup) findViewById(R.id.optionsRadioGroup);
        mOption1Rbtn = (RadioButton) findViewById(R.id.option1Rbtn);
        mOption2Rbtn = (RadioButton) findViewById(R.id.option2Rbtn);
        mOption3Rbtn = (RadioButton) findViewById(R.id.option3Rbtn);
        mOption4Rbtn = (RadioButton) findViewById(R.id.option4Rbtn);
        mOption5Rbtn = (RadioButton) findViewById(R.id.option5Rbtn);
        mCheckboxArea = findViewById(R.id.checkboxHolder);
        mOption1Cbx = (CheckBox) findViewById(R.id.option1cbx);
        mOption2Cbx = (CheckBox) findViewById(R.id.option2cbx);
        mOption3Cbx = (CheckBox) findViewById(R.id.option3cbx);
        mOption4Cbx = (CheckBox) findViewById(R.id.option4cbx);
        mOption5Cbx = (CheckBox) findViewById(R.id.option5cbx);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query pollingRef = database.getReference("polling").orderByKey().equalTo(mPollingId);
        pollingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot pollingSnapshot : dataSnapshot.getChildren()) {
                    mPolling = pollingSnapshot.getValue(Polling.class);
                    String pollingQuestion = mPolling.getPollingQuestion();
                    String pollingMode = mPolling.getPollingMode();
                    String pollingStatus = mPolling.getStatus();
                    int noOfOptions = mPolling.getNoOfOptions();
                    String option1 = mPolling.getOption1();
                    String option2 = mPolling.getOption2();
                    String option3 = mPolling.getOption3();
                    String option4 = mPolling.getOption4();
                    String option5 = mPolling.getOption5();

                    mPollingQuestionLbl.setText(pollingQuestion);

                    switch (pollingMode) {
                        case Polling.SINGLE_CHOICE_POLLING_MODE:
                            mCheckboxArea.setVisibility(View.GONE);
                            mOptionsRgp.setVisibility(View.VISIBLE);
                            mOption1Rbtn.setText(option1);
                            mOption2Rbtn.setText(option2);
                            mOption3Rbtn.setText(option3);
                            mOption4Rbtn.setText(option4);
                            mOption5Rbtn.setText(option5);

                            switch (noOfOptions) {
                                case 2:
                                    mOption1Rbtn.setVisibility(View.VISIBLE);
                                    mOption2Rbtn.setVisibility(View.VISIBLE);
                                    break;
                                case 3:
                                    mOption1Rbtn.setVisibility(View.VISIBLE);
                                    mOption2Rbtn.setVisibility(View.VISIBLE);
                                    mOption3Rbtn.setVisibility(View.VISIBLE);
                                    break;
                                case 4:
                                    mOption1Rbtn.setVisibility(View.VISIBLE);
                                    mOption2Rbtn.setVisibility(View.VISIBLE);
                                    mOption3Rbtn.setVisibility(View.VISIBLE);
                                    mOption4Rbtn.setVisibility(View.VISIBLE);
                                    break;
                                case 5:
                                    mOption1Rbtn.setVisibility(View.VISIBLE);
                                    mOption2Rbtn.setVisibility(View.VISIBLE);
                                    mOption3Rbtn.setVisibility(View.VISIBLE);
                                    mOption4Rbtn.setVisibility(View.VISIBLE);
                                    mOption5Rbtn.setVisibility(View.VISIBLE);
                                    break;
                            }

                            break;
                        case Polling.MULTIPLE_CHOICE_POLLING_MODE:
                            mOptionsRgp.setVisibility(View.GONE);
                            mCheckboxArea.setVisibility(View.VISIBLE);
                            mOption1Cbx.setText(option1);
                            mOption2Cbx.setText(option2);
                            mOption3Cbx.setText(option3);
                            mOption4Cbx.setText(option4);
                            mOption5Cbx.setText(option5);

                            switch (noOfOptions) {
                                case 2:
                                    mOption1Cbx.setVisibility(View.VISIBLE);
                                    mOption2Cbx.setVisibility(View.VISIBLE);
                                    break;
                                case 3:
                                    mOption1Cbx.setVisibility(View.VISIBLE);
                                    mOption2Cbx.setVisibility(View.VISIBLE);
                                    mOption3Cbx.setVisibility(View.VISIBLE);
                                    break;
                                case 4:
                                    mOption1Cbx.setVisibility(View.VISIBLE);
                                    mOption2Cbx.setVisibility(View.VISIBLE);
                                    mOption3Cbx.setVisibility(View.VISIBLE);
                                    mOption4Cbx.setVisibility(View.VISIBLE);
                                    break;
                                case 5:
                                    mOption1Cbx.setVisibility(View.VISIBLE);
                                    mOption2Cbx.setVisibility(View.VISIBLE);
                                    mOption3Cbx.setVisibility(View.VISIBLE);
                                    mOption4Cbx.setVisibility(View.VISIBLE);
                                    mOption5Cbx.setVisibility(View.VISIBLE);
                                    break;
                            }
                            break;
                    }

                    switch (pollingStatus) {
                        case Polling.ACTIVE_POLLING_STATUS:
                            mSubmitCloseBtnArea.setVisibility(View.VISIBLE);
                            mOption1Rbtn.setEnabled(true);
                            mOption2Rbtn.setEnabled(true);
                            mOption3Rbtn.setEnabled(true);
                            mOption4Rbtn.setEnabled(true);
                            mOption4Rbtn.setEnabled(true);
                            mOption1Cbx.setEnabled(true);
                            mOption2Cbx.setEnabled(true);
                            mOption3Cbx.setEnabled(true);
                            mOption4Cbx.setEnabled(true);
                            mOption4Cbx.setEnabled(true);
                            break;
                        case Polling.CLOSED_POLLING_STATUS:
                            mSubmitCloseBtnArea.setVisibility(View.GONE);
                            mOption1Rbtn.setEnabled(false);
                            mOption2Rbtn.setEnabled(false);
                            mOption3Rbtn.setEnabled(false);
                            mOption4Rbtn.setEnabled(false);
                            mOption4Rbtn.setEnabled(false);
                            mOption1Cbx.setEnabled(false);
                            mOption2Cbx.setEnabled(false);
                            mOption3Cbx.setEnabled(false);
                            mOption4Cbx.setEnabled(false);
                            mOption4Cbx.setEnabled(false);
                            break;
                    }
                }

                //Initialize users'choice if any
                String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Query ownResultQuery = database.getReference("pollingAns").child(mPollingId).orderByKey().equalTo(currentUserUid);
                ownResultQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Log.d("debug", "data exist");
                            for (DataSnapshot pollingAnsSnapshot : dataSnapshot.getChildren()) {
                                PollingAns pollingAns = pollingAnsSnapshot.getValue(PollingAns.class);
                                boolean isOption1Selected = pollingAns.isOption1Chosen();
                                boolean isOption2Selected = pollingAns.isOption2Chosen();
                                boolean isOption3Selected = pollingAns.isOption3Chosen();
                                boolean isOption4Selected = pollingAns.isOption4Chosen();
                                boolean isOption5Selected = pollingAns.isOption5Chosen();

                                mOption1Cbx.setChecked(isOption1Selected);
                                mOption2Cbx.setChecked(isOption2Selected);
                                mOption3Cbx.setChecked(isOption3Selected);
                                mOption4Cbx.setChecked(isOption4Selected);
                                mOption5Cbx.setChecked(isOption5Selected);

                                mOption1Rbtn.setChecked(isOption1Selected);
                                mOption2Rbtn.setChecked(isOption2Selected);
                                mOption3Rbtn.setChecked(isOption3Selected);
                                mOption4Rbtn.setChecked(isOption4Selected);
                                mOption5Rbtn.setChecked(isOption5Selected);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference pollingAnsRef = database.getReference("pollingAns").child(mPollingId);
                String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                boolean isOption1Selected;
                boolean isOption2Selected;
                boolean isOption3Selected;
                boolean isOption4Selected;
                boolean isOption5Selected;
                switch (mPolling.getPollingMode()) {
                    case Polling.SINGLE_CHOICE_POLLING_MODE:
                        isOption1Selected = mOption1Rbtn.isChecked();
                        isOption2Selected = mOption2Rbtn.isChecked();
                        isOption3Selected = mOption3Rbtn.isChecked();
                        isOption4Selected = mOption4Rbtn.isChecked();
                        isOption5Selected = mOption5Rbtn.isChecked();
                        pollingAnsRef.child(currentUserUid).setValue(new PollingAns(isOption1Selected, isOption2Selected, isOption3Selected, isOption4Selected, isOption5Selected)).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                            }
                        });
                        break;
                    case Polling.MULTIPLE_CHOICE_POLLING_MODE:
                        isOption1Selected = mOption1Cbx.isChecked();
                        isOption2Selected = mOption2Cbx.isChecked();
                        isOption3Selected = mOption3Cbx.isChecked();
                        isOption4Selected = mOption4Cbx.isChecked();
                        isOption5Selected = mOption5Cbx.isChecked();
                        pollingAnsRef.child(currentUserUid).setValue(new PollingAns(isOption1Selected, isOption2Selected, isOption3Selected, isOption4Selected, isOption5Selected)).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                            }
                        });
                        break;
                }


            }
        });

        mClosePollingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PollingDetailActivity.this).setMessage(R.string.confirmClosePolling);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        database.getReference("polling").child(mPollingId).child("status").setValue("c").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                            }
                        });
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


    }
}
