package com.msccs.hku.familycaregiver.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.msccs.hku.familycaregiver.Model.CustomFirebaseUser;
import com.msccs.hku.familycaregiver.Model.CustomTasks;
import com.msccs.hku.familycaregiver.Model.LocalContacts;
import com.msccs.hku.familycaregiver.tempStructure.UserTask;
import com.msccs.hku.familycaregiver.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class TaskDetailActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 222;

    public static String EXTRA_TASK_ID = "COM.MSCCS.HKU.FAMILYCAREGIVER.EXTRA_TASK_ID";

    private TextView mTaskNameLbl;
    private TextView mTaskDescriptionLbl;
    private TextView mTaskTypeLbl;
    private TextView mStartDateTxtView;
    private TextView mStartTimeTxtView;
    private TextView mEndDateTxtView;
    private TextView mEndTimeTxtView;
    private TextView mIsAllDayEventLbl;
    private TextView mTaskStatusLbl;
    private LinearLayout mTaskOwnerLinearLayout;
    private LinearLayout mTaskOwnerBtnLinearLayout;

    private TextView mTakeUpBtn;
    private TextView mWithdrawBtn;
    private TextView mMarkAsCompleteBtn;

    private String mTaskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        //After android 6.0 need to get the run time permission
        //Check does permission already granted
        if (ContextCompat.checkSelfPermission(TaskDetailActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            //Handle the case in which the users have replied not grant the read local contacts to the application, show the rationale and ask him to grant
            if (ActivityCompat.shouldShowRequestPermissionRationale(TaskDetailActivity.this, Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(TaskDetailActivity.this, "Please go to app settings to grant the READ CONTACTS PERMISSION", Toast.LENGTH_LONG).show();
            } else {
                //If user's have never answer grant the permission or not, display the dialog and ask for the requested permission
                String[] perReqArray = {Manifest.permission.READ_CONTACTS};
                ActivityCompat.requestPermissions(TaskDetailActivity.this, perReqArray, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }

        //Get the task information from the intent
        mTaskId = getIntent().getStringExtra(EXTRA_TASK_ID);

        //setup and initialize the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.taskDetail);

        mTaskNameLbl = (TextView) findViewById(R.id.taskNameTbx);
        mTaskDescriptionLbl = (TextView) findViewById(R.id.taskDescriptionTbx);
        mTaskTypeLbl = (TextView) findViewById(R.id.taskTypeLbl);
        mStartDateTxtView = (TextView) findViewById(R.id.startDateTxtView);
        mStartTimeTxtView = (TextView) findViewById(R.id.startTimeTxtView);
        mEndDateTxtView = (TextView) findViewById(R.id.endDateTxtView);
        mEndTimeTxtView = (TextView) findViewById(R.id.endTimeTxtView);
        mTakeUpBtn = (TextView) findViewById(R.id.takeUpTaskBtn);
        mWithdrawBtn = (TextView) findViewById(R.id.withdrawTaskBtn);
        mMarkAsCompleteBtn = (TextView) findViewById(R.id.markCompleteBtn);

        mIsAllDayEventLbl = (TextView) findViewById(R.id.isAllDayEventLbl);
        mTaskStatusLbl = (TextView) findViewById(R.id.taskStatusLbl);
        mTaskOwnerLinearLayout = (LinearLayout) findViewById(R.id.ownerListLinearLayout);
        mTaskOwnerBtnLinearLayout = (LinearLayout) findViewById(R.id.taskOwnerBtnSet);

        FirebaseDatabase.getInstance().getReference("taskAssignee").child(mTaskId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                initTaskOwnerList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference("tasks").orderByKey().equalTo(mTaskId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                    CustomTasks task = taskSnapshot.getValue(CustomTasks.class);
                    String taskName = task.getTaskName();
                    String taskDescription = task.getTaskDescription();
                    String taskType = task.getTaskType();
                    Boolean isAllDayEvent = task.isAllDayEvent();
                    Date taskStartDate = task.getTaskStartDate();
                    Date taskEndDate = task.getTaskEndDate();
                    String taskStatus = task.getStatus();

                    //initialize the widget using the value of the task
                    mTaskNameLbl.setText(taskName);
                    mTaskDescriptionLbl.setText(taskDescription);
                    switch (taskType) {
                        case "c":
                            mTaskTypeLbl.setText(R.string.casualTask);
                            break;
                        case "i":
                            mTaskTypeLbl.setText(R.string.importantTask);
                            break;
                        case "r":
                            mTaskTypeLbl.setText(R.string.reminder);
                            break;
                        default:
                            break;
                    }

                    if (isAllDayEvent) {
                        mIsAllDayEventLbl.setText(R.string.yes);
                    } else {
                        mIsAllDayEventLbl.setText(R.string.no);
                    }

                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd-MM-yyyy");
                    mStartDateTxtView.setText(dateFormat.format(taskStartDate));
                    mEndDateTxtView.setText(dateFormat.format(taskEndDate));

                    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
                    mStartTimeTxtView.setText(timeFormat.format(taskStartDate));
                    mEndTimeTxtView.setText(timeFormat.format(taskEndDate));

                    String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    if (taskStatus.equals("C")) {
                        mTakeUpBtn.setVisibility(View.GONE);
                        mTaskOwnerBtnLinearLayout.setVisibility(View.GONE);
                    } else {
                        Query userTaskRef = FirebaseDatabase.getInstance().getReference("UserTask").child(currentUserId).child("Assigned").orderByChild("taskId").equalTo(mTaskId);
                        userTaskRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    mTakeUpBtn.setVisibility(View.VISIBLE);
                                    mTaskOwnerBtnLinearLayout.setVisibility(View.GONE);
                                } else {
                                    mTakeUpBtn.setVisibility(View.GONE);
                                    mTaskOwnerBtnLinearLayout.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    switch (taskStatus) {
                        case "A":
                            mTaskStatusLbl.setText(getString(R.string.assigned));
                            //initTaskOwnerList();
                            break;
                        case "N":
                            //Show there is no one owning this task at the moment
                            mTaskStatusLbl.setText(getString(R.string.pending));
                            break;
                        case "C":
                            mTaskStatusLbl.setText(getString(R.string.completed));
                            //initTaskOwnerList();
                            break;
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mTakeUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1) Set the task status as "assigned" in tasks json tree
                // 2) Add the record of current user into the taskAssignee node
                // 3) Add record to UserTask node
                Task<?>[] tasks = new Task[]{
                        setTaskStatus("A"),
                        addCurrentUserToTaskAssignee(),
                        addEntryToAssignedUserTask()
                };
                Tasks.whenAll(tasks).continueWithTask(new RollbackIfFailure())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                            }
                        });
            }
        });

        mMarkAsCompleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Task<?>[] tasks = new Task[]{
                        setTaskStatus("C"),
                        removeEntryFromAssignedUserTask(),
                        addEntryToCompletedUserTask()
                };
                Tasks.whenAll(tasks).continueWithTask(new RollbackIfFailure())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                            }
                        });
            }
        });


        mWithdrawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. remove the entry from UserTask
                // 2. remove the entry from taskAssignee
                // 3. if taskAssignee have no entry, revert back the status to pending

                Task<?>[] tasks = new Task[]{
                        removeEntryFromAssignedUserTask(),
                        removeTaskAssigneeMayChangeStatusToPending()
                };
                Tasks.whenAll(tasks).continueWithTask(new RollbackIfFailure())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                            }
                        });
            }
        });

    }

    private void initTaskOwnerList() {
        if (ContextCompat.checkSelfPermission(TaskDetailActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            final HashMap<String, LocalContacts> localContactsHashMap = getLocalContactsHashMap();
            //Remove the existing list of contact person displayed before load
            mTaskOwnerLinearLayout.removeAllViews();
            DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference("taskAssignee").child(mTaskId);
            taskRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        CustomFirebaseUser user = userSnapshot.getValue(CustomFirebaseUser.class);
                        final String phoneNo = user.getTelNum();
                        //If the whole tel code can be matched, use it, if not ignore the +XXX part
                        LocalContacts contact = localContactsHashMap.get(phoneNo);
                        if (contact == null) {
                            contact = localContactsHashMap.get(phoneNo.substring(4));
                        }

                        TextView contactTxtView = new TextView(TaskDetailActivity.this);
                        contactTxtView.setGravity(Gravity.END);
                        if (phoneNo.equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) {
                            contactTxtView.setText(getString(R.string.me));
                        } else if (contact != null) {
                            contactTxtView.setText(contact.getContactsLocalDisplayName());
                        } else {
                            contactTxtView.setText(phoneNo);
                        }

                        //Enable user to click the contact person to dial him, will copy the phone number to dial screen
                        contactTxtView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                callIntent.setData(Uri.parse("tel:" + phoneNo));
                                startActivity(callIntent);
                            }
                        });
                        mTaskOwnerLinearLayout.addView(contactTxtView);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    //Performance much better if user Hashmap instead of Arraylist
    public HashMap<String, LocalContacts> getLocalContactsHashMap() {
        HashMap<String, LocalContacts> s = new HashMap<>();
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\s+", "");
            s.put(phoneNumber, new LocalContacts(name, phoneNumber));
        }
        cursor.close();
        return s;
    }


    private Task<String> removeEntryFromAssignedUserTask() {
        final TaskCompletionSource<String> tcs = new TaskCompletionSource<>();
        final String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query assignedUserTaskAlreadyExistQuery = FirebaseDatabase.getInstance().getReference("UserTask").child(currentUserUid).child("Assigned").orderByChild("taskId").equalTo(mTaskId);
        assignedUserTaskAlreadyExistQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    dataSnapshot1.getRef().setValue(null);
                    tcs.setResult(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                tcs.setException(new IOException("IOError", databaseError.toException()));
            }
        });

        return tcs.getTask();
    }

    ;


    private Task<String> addEntryToAssignedUserTask() {
        final TaskCompletionSource<String> tcs = new TaskCompletionSource<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query checkUserTaskAssignedAlreadyExistQuery = database.getReference("UserTask").child(currentUserUid).child("Assigned").orderByChild("taskId").equalTo(mTaskId);
        checkUserTaskAssignedAlreadyExistQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    DatabaseReference userTaskAssignedRef = FirebaseDatabase.getInstance().getReference("UserTask").child(currentUserUid).child("Assigned");
                    userTaskAssignedRef.push().setValue(new UserTask(currentUserUid, mTaskId));
                    tcs.setResult(null);
                } else {
                    tcs.setException(new IllegalStateException());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                tcs.setException(new IOException("IOError", databaseError.toException()));
            }
        });

        return tcs.getTask();
    }

    private Task<String> addEntryToCompletedUserTask() {
        final TaskCompletionSource<String> tcs = new TaskCompletionSource<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query completedUserTaskAlreadyExistQuery = FirebaseDatabase.getInstance().getReference("UserTask").child(currentUserUid).child("Completed").orderByChild("taskId").equalTo(mTaskId);
        completedUserTaskAlreadyExistQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    DatabaseReference completedRef = FirebaseDatabase.getInstance().getReference("UserTask").child(currentUserUid).child("Completed");
                    completedRef.push().setValue(new UserTask(currentUserUid, mTaskId));
                    tcs.setResult(null);
                } else {
                    tcs.setException(new IllegalStateException());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                tcs.setException(new IOException("IOError", databaseError.toException()));
            }
        });

        return tcs.getTask();
    }

    private Task<String> addCurrentUserToTaskAssignee() {
        final TaskCompletionSource<String> tcs = new TaskCompletionSource<>();
        final String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query taskAssigneeAlreadyExistQuery = database.getReference("taskAssignee").child(mTaskId).orderByChild("uid").equalTo(currentUserUid);
        taskAssigneeAlreadyExistQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    DatabaseReference taskAssigneeRef = FirebaseDatabase.getInstance().getReference("taskAssignee").child(mTaskId);
                    String currentUserPhoneNum = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
                    taskAssigneeRef.push().setValue(new CustomFirebaseUser(currentUserUid, currentUserPhoneNum));
                    tcs.setResult(null);
                } else {
                    tcs.setException(new IllegalStateException());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                tcs.setException(new IOException("IOError", databaseError.toException()));
            }
        });

        return tcs.getTask();
    }

    ;

    private Task<String> setTaskStatus(final String status) {
        final TaskCompletionSource<String> tcs = new TaskCompletionSource<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference taskRef = database.getReference("tasks").child(mTaskId);
        taskRef.child("status").setValue(status);
        tcs.setResult(null);
        return tcs.getTask();
    }

    private Task<String> removeTaskAssigneeMayChangeStatusToPending() {

        final TaskCompletionSource<String> tcs = new TaskCompletionSource<>();

        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query taskAssigneeSelfQuery = FirebaseDatabase.getInstance().getReference("taskAssignee").child(mTaskId).orderByChild("uid").equalTo(currentUserUid);
        taskAssigneeSelfQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot taskAssigneeSnapshot) {
                if (taskAssigneeSnapshot.exists()) {
                    // if there is only 1 assignee for this task before delete, it means this task is no longer assigned after withdraw
                    Query checkAssigneeNoQuery = FirebaseDatabase.getInstance().getReference("taskAssignee").child(mTaskId);
                    checkAssigneeNoQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() == 1) {
                                DatabaseReference taskStatusRef = FirebaseDatabase.getInstance().getReference().child("tasks").child(mTaskId).child("status");
                                taskStatusRef.setValue("N");

                                for (DataSnapshot snapshot : taskAssigneeSnapshot.getChildren()) {
                                    snapshot.getRef().setValue(null);
                                }

                                tcs.setResult(null);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            tcs.setException(new IllegalArgumentException());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                tcs.setException(new IllegalArgumentException());
            }
        });

        return tcs.getTask();
    }

    class RollbackIfFailure implements Continuation<Void, Task<Void>> {
        @Override
        public Task<Void> then(@NonNull Task<Void> task) throws Exception {

            final TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

            if (task.isSuccessful()) {
                tcs.setResult(null);
            } else {
                // Rollback everything
            }

            return tcs.getTask();
        }
    }

}
