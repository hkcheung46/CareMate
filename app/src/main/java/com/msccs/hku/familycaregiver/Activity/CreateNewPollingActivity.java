package com.msccs.hku.familycaregiver.Activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.msccs.hku.familycaregiver.Model.GroupPolling;
import com.msccs.hku.familycaregiver.Model.Polling;
import com.msccs.hku.familycaregiver.R;
import com.msccs.hku.familycaregiver.tempStructure.InGroup;

import static android.R.layout.simple_spinner_dropdown_item;

/**
 * Created by HoiKit on 26/12/2017.
 */

public class CreateNewPollingActivity extends AppCompatActivity {

    public static String EXTRA_CREATE_NEW_POLL_MODE ="COM.MSCCS.HKU.FAMILYCAREGIVER.EXTRA_CREATENEWPOLLMODE";
    public static String EXTRA_CREATE_NEW_POLL_GROUP_ID ="COM.MSCCS.HKU.FAMILYCAREGIVER.EXTRA_CREATENEWPOLLGROUPID";

    private String mCreateMode;         // g is for generic (go in via polling pages) , s is for specific group (go in via group page)

    private Spinner mPollingGroupSpinner;
    private TextInputLayout mPollingQuestionTextInputLayout;
    private TextInputEditText mPollingQuestionTextInputTbx;
    private RadioGroup mVotingModeRadioGroup;
    private RadioButton mSingleVoteRbtn;
    private RadioButton mMultipleVoteRbtn;
    private EditText mOption1Tbx;
    private EditText mOption2Tbx;
    private EditText mOption3Tbx;
    private EditText mOption4Tbx;
    private EditText mOption5Tbx;
    private Spinner mNumberOfOptionsSpinner;
    private LinearLayout mPollingOptionsLinearLayout;
    private View mPollingGroupSection;
    private FloatingActionButton mAddNewPollingFAB;

    private String mGroupId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_polling);

        //Get the intent and retrieve content from other activity which kick start this activity
        mGroupId = getIntent().getStringExtra(EXTRA_CREATE_NEW_POLL_GROUP_ID);
        mCreateMode = getIntent().getStringExtra(EXTRA_CREATE_NEW_POLL_MODE);
        
        //Toolbar setup
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.initiateNewPolling);

        //Link up the widget
        mPollingGroupSection = findViewById(R.id.pollingGroupSection);
        mPollingGroupSpinner = (Spinner) findViewById(R.id.pollingGroupSpinner);
        mPollingQuestionTextInputLayout = (TextInputLayout) findViewById(R.id.pollingQuesTextInputLayout);
        mPollingQuestionTextInputTbx = (TextInputEditText) findViewById(R.id.pollingQuestionTbx);
        mVotingModeRadioGroup = (RadioGroup) findViewById(R.id.singleOrMultiRgp);
        mSingleVoteRbtn = (RadioButton) findViewById(R.id.single_vote_rbtn);
        mMultipleVoteRbtn = (RadioButton) findViewById(R.id.multiple_votes_rbtn);
        mNumberOfOptionsSpinner = (Spinner) findViewById(R.id.noOfOptionsSpinner);
        mPollingOptionsLinearLayout = (LinearLayout) findViewById(R.id.pollingOptionsListLinearLayout);
        mOption1Tbx = (EditText) findViewById(R.id.option1Tbx);
        mOption2Tbx = (EditText) findViewById(R.id.options2Tbx);
        mOption3Tbx = (EditText) findViewById(R.id.options3Tbx);
        mOption4Tbx = (EditText) findViewById(R.id.options4Tbx);
        mOption5Tbx = (EditText) findViewById(R.id.options5Tbx);
        mAddNewPollingFAB = (FloatingActionButton) findViewById(R.id.fab);

        //Determine should show the polling group spinner
        switch (mCreateMode){
            case "g":
                mPollingGroupSection.setVisibility(View.VISIBLE);
                break;
            case "s":
                mPollingGroupSection.setVisibility(View.GONE);
                break;
        }

        //Get current user uid
        String mCurrentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //PollingGroup spinner setup
        //Query the inGroup node to find out what option should be in the task group spinner (Check current user is inside what group)
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference inGroupRef = database.getReference("inGroup").child(mCurrentUserUid);
        FirebaseListAdapter<InGroup> pollingGroupSpinnerAdapter = new FirebaseListAdapter<InGroup>(this,InGroup.class,simple_spinner_dropdown_item,inGroupRef) {
            @Override
            protected void populateView(View v, InGroup model, int position) {
                TextView groupNameLbl = (TextView) v.findViewById(android.R.id.text1);
                groupNameLbl.setText(model.getGroupName());
            }
        };
       mPollingGroupSpinner.setAdapter(pollingGroupSpinnerAdapter);

       mNumberOfOptionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               int noOfOptions = Integer.parseInt (parent.getItemAtPosition(position).toString());

               switch (noOfOptions){
                   case 2:
                       mOption3Tbx.setVisibility(View.GONE);
                       mOption4Tbx.setVisibility(View.GONE);
                       mOption5Tbx.setVisibility(View.GONE);
                       break;
                   case 3:
                       mOption3Tbx.setVisibility(View.VISIBLE);
                       mOption4Tbx.setVisibility(View.GONE);
                       mOption5Tbx.setVisibility(View.GONE);
                       break;
                   case 4:
                       mOption3Tbx.setVisibility(View.VISIBLE);
                       mOption4Tbx.setVisibility(View.VISIBLE);
                       mOption5Tbx.setVisibility(View.GONE);
                       break;
                   case 5:
                       mOption3Tbx.setVisibility(View.VISIBLE);
                       mOption4Tbx.setVisibility(View.VISIBLE);
                       mOption5Tbx.setVisibility(View.VISIBLE);
                       break;
               }

           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });

        mAddNewPollingFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInputValid()){
                    //create new poll
                    String pollingQuestion = mPollingQuestionTextInputTbx.getText().toString().trim();
                    String pollingMode;
                    switch (mVotingModeRadioGroup.getCheckedRadioButtonId()){
                        case R.id.single_vote_rbtn:
                            pollingMode=Polling.SINGLE_CHOICE_POLLING_MODE;
                            break;
                        case R.id.multiple_votes_rbtn:
                            pollingMode=Polling.MULTIPLE_CHOICE_POLLING_MODE;
                            break;
                        default:
                            pollingMode=Polling.SINGLE_CHOICE_POLLING_MODE;
                            break;
                    }

                    String belongToGroupId;
                    if (mCreateMode.equals("s") && mGroupId!=null){
                        belongToGroupId = mGroupId;
                    }else{
                        belongToGroupId = ((InGroup) mPollingGroupSpinner.getSelectedItem()).getGroupId();
                    }

                    //New polling should be in active status
                    String status = Polling.ACTIVE_POLLING_STATUS;

                    String option1 ="";
                    String option2 ="";
                    String option3 ="";
                    String option4 ="";
                    String option5 ="";

                    int noOfOptions = Integer.parseInt(mNumberOfOptionsSpinner.getSelectedItem().toString());
                    switch (noOfOptions){
                        case 2:
                            option1 = mOption1Tbx.getText().toString().trim();
                            option2 = mOption2Tbx.getText().toString().trim();
                            option3 = "";
                            option4 = "";
                            option5 = "";
                            break;
                        case 3:
                            option1 = mOption1Tbx.getText().toString().trim();
                            option2 = mOption2Tbx.getText().toString().trim();
                            option3 = mOption3Tbx.getText().toString().trim();
                            option4 = "";
                            option5 = "";
                            break;
                        case 4:
                            option1 = mOption1Tbx.getText().toString().trim();
                            option2 = mOption2Tbx.getText().toString().trim();
                            option3 = mOption3Tbx.getText().toString().trim();;
                            option4 = mOption4Tbx.getText().toString().trim();
                            option5 = "";
                            break;
                        case 5:
                            option1 = mOption1Tbx.getText().toString().trim();
                            option2 = mOption2Tbx.getText().toString().trim();
                            option3 = mOption3Tbx.getText().toString().trim();
                            option4 = mOption4Tbx.getText().toString().trim();
                            option5 = mOption5Tbx.getText().toString().trim();
                            break;
                    }

                    String creatorUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    Polling newPolling = new Polling(pollingQuestion,pollingMode,belongToGroupId,status,noOfOptions,option1,option2,option3,option4,option5,creatorUid);


                    String pollingId = addEntryToPolling(newPolling).getResult();
                    addEntryToGroupPolling(pollingId,belongToGroupId).addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            finish();
                        }
                    });
                }else{
                    Toast.makeText(CreateNewPollingActivity.this,R.string.pleaseCheckInput,Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean isInputValid(){
        Boolean isValid = true;

        if (mPollingQuestionTextInputTbx.getText().toString().trim().length()==0){
            mPollingQuestionTextInputTbx.setError(getString(R.string.pollingQNotEmptyValidation));
            isValid=false;
        }

        if (mCreateMode.equals("g")&&mPollingGroupSpinner.getSelectedItem()==null){
            isValid=false;
        }

        if (mVotingModeRadioGroup.getCheckedRadioButtonId()==-1){
            isValid=false;
        }

        //Check options is not empty
        switch (mNumberOfOptionsSpinner.getSelectedItem().toString()){
            case "2":
                if (mOption1Tbx.getText().toString().trim().length()==0){
                    isValid=false;
                }
                if (mOption2Tbx.getText().toString().trim().length()==0){
                    isValid=false;
                }
                break;
            case "3":
                if (mOption1Tbx.getText().toString().trim().length()==0){
                    isValid=false;
                }
                if (mOption2Tbx.getText().toString().trim().length()==0){
                    isValid=false;
                }
                if (mOption3Tbx.getText().toString().trim().length()==0){
                    isValid=false;
                }
                break;
            case "4":
                if (mOption1Tbx.getText().toString().trim().length()==0){
                    isValid=false;
                }
                if (mOption2Tbx.getText().toString().trim().length()==0){
                    isValid=false;
                }
                if (mOption3Tbx.getText().toString().trim().length()==0){
                    isValid=false;
                }
                if (mOption4Tbx.getText().toString().trim().length()==0){
                    isValid=false;
                }
                break;
            case "5":
                if (mOption1Tbx.getText().toString().trim().length()==0){
                    isValid=false;
                }
                if (mOption2Tbx.getText().toString().trim().length()==0){
                    isValid=false;
                }
                if (mOption3Tbx.getText().toString().trim().length()==0){
                    isValid=false;
                }
                if (mOption4Tbx.getText().toString().trim().length()==0){
                    isValid=false;
                }
                if (mOption5Tbx.getText().toString().trim().length()==0){
                    isValid=false;
                }
                break;
        }

        return isValid;
    }


    private Task<String> addEntryToPolling(Polling polling) {
        final TaskCompletionSource<String> tcs = new TaskCompletionSource<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String pollingId = database.getReference("polling").push().getKey();
        database.getReference("polling").child(pollingId).setValue(polling);

        tcs.setResult(pollingId);
        return tcs.getTask();
    }

    private Task<String> addEntryToGroupPolling(String pollingId,String groupId){
        final TaskCompletionSource tcs = new TaskCompletionSource();
        GroupPolling groupPolling = new GroupPolling(groupId,pollingId);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference groupPollRef = database.getReference("groupPolling").child(groupId);
        groupPollRef.push().setValue(groupPolling);
        tcs.setResult(null);
        return tcs.getTask();
    };
}
