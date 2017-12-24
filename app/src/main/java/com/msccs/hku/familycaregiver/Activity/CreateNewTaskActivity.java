package com.msccs.hku.familycaregiver.Activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.msccs.hku.familycaregiver.Model.CustomTasks;
import com.msccs.hku.familycaregiver.tempStructure.GroupTask;
import com.msccs.hku.familycaregiver.tempStructure.InGroup;
import com.msccs.hku.familycaregiver.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.R.layout.simple_spinner_dropdown_item;

public class CreateNewTaskActivity extends AppCompatActivity {

    public static String EXTRA_GROUP_ID = "COM.MSCCS.HKU.FAMILYCAREGIVER.EXTRA_GROUP_ID";
    public static String EXTRA_CREATE_NEW_TASK_MODE ="COM.MSCCS.HKU.FAMILYCAREGIVER.EXTRA_CREATENEWTASKMODE";


    private String mGroupId;            //this mGroup id is meaningful only if the Create mode is specific, if not, should read which group this task is for from the spinner

    private String mCreateMode;         // g is for generic (go in via task pages) , s is for specific group (go in via group page)
    private String mCurrentUserUid;

    private Calendar mEventStartCalendar;
    private Calendar mEventEndCalendar;

    private EditText mTaskNameTbx;
    private EditText  mTaskDescriptionTbx;
    private Spinner mTaskGroupSpinner;
    private RadioGroup mTaskTypeRgp;
    private Switch mAllDaySwitch;
    private TextView mStartDateTxtView;
    private TextView mEndDateTxtView;
    private TextView mStartTimeTxtView;
    private TextView mEndTimeTxtView;
    private View mTaskGroupUISection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_task);

        //Get the intent and retrieve content from other activity which kick start this activity
        mGroupId = getIntent().getStringExtra(EXTRA_GROUP_ID);
        mCreateMode = getIntent().getStringExtra(EXTRA_CREATE_NEW_TASK_MODE);

        //Get current user uid
        mCurrentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Setup the calendar for event start and event end
        mEventStartCalendar = Calendar.getInstance();
        mEventEndCalendar = Calendar.getInstance();

        //Toolbar setup
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.newTask);

        //Input widget linkage
        mTaskNameTbx = (EditText) findViewById(R.id.taskNameTbx);
        mTaskDescriptionTbx = (EditText) findViewById(R.id.taskDescriptionTbx);
        mTaskGroupSpinner = (Spinner) findViewById(R.id.taskGroupSpinner);
        mTaskTypeRgp = (RadioGroup) findViewById(R.id.taskTypeRgp);
        mAllDaySwitch = (Switch) findViewById(R.id.allDaySwitch);
        mStartDateTxtView = (TextView) findViewById(R.id.startDateTxtView);
        mEndDateTxtView = (TextView) findViewById(R.id.endDateTxtView);
        mStartTimeTxtView = (TextView) findViewById(R.id.startTimeTxtView);
        mEndTimeTxtView = (TextView) findViewById(R.id.endTimeTxtView);
        View mTaskGroupUISection = findViewById(R.id.taskGroupSection);

        switch (mCreateMode){
            case "s":
                mTaskGroupUISection.setVisibility(View.GONE);
                break;
            case "g":
                mTaskGroupUISection.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

        mAllDaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mStartTimeTxtView.setVisibility(View.GONE);
                    mEndTimeTxtView.setVisibility(View.GONE);
                }else{
                    mStartTimeTxtView.setVisibility(View.VISIBLE);
                    mEndTimeTxtView.setVisibility(View.VISIBLE);
                }
            }
        });

        //Initialize the start date,time & end date,time field
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd-MM-yyyy");
        mStartDateTxtView.setText(dateFormat.format(mEventStartCalendar.getTime()));
        mEndDateTxtView.setText(dateFormat.format(mEventEndCalendar.getTime()));

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
        mStartTimeTxtView.setText(timeFormat.format(mEventStartCalendar.getTime()));
        mEndTimeTxtView.setText(timeFormat.format(mEventEndCalendar.getTime()));

        mStartDateTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateNewTaskActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        mEventStartCalendar.set(Calendar.YEAR,(year));
                        mEventStartCalendar.set(Calendar.MONTH,month);
                        mEventStartCalendar.set(Calendar.DAY_OF_MONTH,day);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd-MM-yyyy");
                       mStartDateTxtView.setText(dateFormat.format(mEventStartCalendar.getTime()));
                    }

                }, mEventStartCalendar.get(Calendar.YEAR),mEventStartCalendar.get(Calendar.MONTH), mEventStartCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        mEndDateTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateNewTaskActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        mEventEndCalendar.set(Calendar.YEAR,(year));
                        mEventEndCalendar.set(Calendar.MONTH,month);
                        mEventEndCalendar.set(Calendar.DAY_OF_MONTH,day);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd-MM-yyyy");
                        mEndDateTxtView.setText(dateFormat.format( mEventEndCalendar.getTime()));
                    }

                },  mEventEndCalendar.get(Calendar.YEAR), mEventEndCalendar.get(Calendar.MONTH),  mEventEndCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        mStartTimeTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateNewTaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mEventStartCalendar.set(Calendar.HOUR,hourOfDay);
                        mEventStartCalendar.set(Calendar.MINUTE,minute);
                        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
                        mStartTimeTxtView.setText(timeFormat.format(mEventStartCalendar.getTime()));
                    }
                },mEventStartCalendar.get(Calendar.HOUR),mEventStartCalendar.get(Calendar.MINUTE),true);
                timePickerDialog.show();
            }
        });

        mEndTimeTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateNewTaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mEventEndCalendar.set(Calendar.HOUR,hourOfDay);
                        mEventEndCalendar.set(Calendar.MINUTE,minute);
                        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
                        mEndTimeTxtView.setText(timeFormat.format(mEventEndCalendar.getTime()));
                    }
                },mEventEndCalendar.get(Calendar.HOUR),mEventEndCalendar.get(Calendar.MINUTE),true);
                timePickerDialog.show();
            }
        });


        //Spinner setup
        //Query the inGroup node to find out what option should be in the task group spinner (Check current user is inside what group)
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference inGroupRef = database.getReference("inGroup").child(mCurrentUserUid);
        FirebaseListAdapter<InGroup> taskGroupSpinnerAdapter = new FirebaseListAdapter<InGroup>(this,InGroup.class,simple_spinner_dropdown_item,inGroupRef) {
            @Override
            protected void populateView(View v, InGroup model, int position) {
                TextView groupNameLbl = (TextView) v.findViewById(android.R.id.text1);
                groupNameLbl.setText(model.getGroupName());
            }
        };
        mTaskGroupSpinner.setAdapter(taskGroupSpinnerAdapter);


        //Floating action button setup
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInputValid()){
                    String taskName = (mTaskNameTbx.getText().toString().trim());
                    String taskDescription = (mTaskDescriptionTbx.getText().toString());
                    String status = "N";      //New Task should be in pending assignment state

                    String taskType;
                    switch (mTaskTypeRgp.getCheckedRadioButtonId()){
                        case R.id.important_task_rbtn:
                            taskType = "i";
                            break;
                        case R.id.casual_task_rbtn:
                            taskType ="c";
                            break;
                        case R.id.reminder_task_rbtn:
                            taskType="r";
                            break;
                        default:
                            //theoretically, this default should never be reached
                            taskType="i";
                            break;
                    }

                    Boolean isAllDayEvent = mAllDaySwitch.isChecked();

                    if (isAllDayEvent){
                        //if it is all day event, no need to save the start end time, keep all hour/ minutes/ second as 0
                        mEventStartCalendar.set(Calendar.HOUR,0);
                        mEventStartCalendar.set(Calendar.MINUTE,0);
                        mEventStartCalendar.set(Calendar.SECOND,0);
                        mEventStartCalendar.set(Calendar.MILLISECOND,0);
                        mEventEndCalendar.set(Calendar.HOUR,0);
                        mEventEndCalendar.set(Calendar.MINUTE,0);
                        mEventEndCalendar.set(Calendar.SECOND,0);
                        mEventEndCalendar.set(Calendar.MILLISECOND,0);
                    }

                    Date taskStartDateTime = mEventStartCalendar.getTime();
                    Date taskEndDateTime = mEventEndCalendar.getTime();

                    Date createDate = Calendar.getInstance().getTime();
                    String groupId;

                    if (mCreateMode.equals("s") && mGroupId!=null){
                        groupId = mGroupId;
                    }else{
                        groupId = ((InGroup) mTaskGroupSpinner.getSelectedItem()).getGroupId();
                    }

                    CustomTasks toBeInsertedTask = new CustomTasks(taskName,status,taskType,taskStartDateTime,taskEndDateTime,isAllDayEvent,createDate,taskDescription,groupId);
                    DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference("tasks");
                    String taskId = taskRef.push().getKey();
                    taskRef.child(taskId).setValue(toBeInsertedTask);

                    DatabaseReference groupTaskRef = FirebaseDatabase.getInstance().getReference("groupTask").child(groupId);
                    groupTaskRef.push().setValue(new GroupTask(groupId,taskId));

                    finish();
                }else{
                    Snackbar.make(view, getString(R.string.pleaseCheckInput), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean isInputValid(){
        Boolean isValid = true;

        if (mTaskNameTbx.getText().toString().trim().length()==0){
            mTaskNameTbx.setError(getString(R.string.taskNameNotEmptyValidation));
            isValid=false;
        }

        if (mTaskTypeRgp.getCheckedRadioButtonId()==-1){
            isValid=false;
        }

        //If the date being compared is after the date argument, a value greater than zero is returned
        if (mAllDaySwitch.isChecked()){
            mEventStartCalendar.set(Calendar.HOUR,0);
            mEventStartCalendar.set(Calendar.MINUTE,0);
            mEventStartCalendar.set(Calendar.SECOND,0);
            mEventStartCalendar.set(Calendar.MILLISECOND,0);
            mEventEndCalendar.set(Calendar.HOUR,0);
            mEventEndCalendar.set(Calendar.MINUTE,0);
            mEventEndCalendar.set(Calendar.SECOND,0);
            mEventEndCalendar.set(Calendar.MILLISECOND,0);
        }

        if (mEventStartCalendar.compareTo(mEventEndCalendar)>0){
            isValid=false;
        }

        return isValid;
    };

}
