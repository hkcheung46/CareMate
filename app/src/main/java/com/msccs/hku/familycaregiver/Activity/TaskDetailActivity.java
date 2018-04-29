package com.msccs.hku.familycaregiver.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.msccs.hku.familycaregiver.Model.CustomFirebaseUser;
import com.msccs.hku.familycaregiver.Model.CustomTasks;
import com.msccs.hku.familycaregiver.Model.Group;
import com.msccs.hku.familycaregiver.tempStructure.UserTask;
import com.msccs.hku.familycaregiver.R;

import java.io.IOException;
import java.text.SimpleDateFormat;

import static android.view.View.GONE;

public class TaskDetailActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 222;
    public static final String EXTRA_TASK_ID = "COM.MSCCS.HKU.FAMILYCAREGIVER.EXTRA_TASK_ID";
    public static final String EXTRA_GROUP_ID = "COM.MSCCS.HKU.FAMILYCAREGIVER.EXTRA_GROUP_ID";

    private TextView mElderNameLbl;
    private TextView mTaskNameLbl;
    private TextView mTaskDescriptionLbl;
    private TextView mImportanceLbl;
    private TextView mStartDateTxtView;
    private TextView mStartTimeTxtView;
    private TextView mEndDateTxtView;
    private TextView mEndTimeTxtView;
    private ImageView mGroupImageView;
    private TextView mTaskStatusLbl;
    private TextView mViewTaskOwnerLbl;
    private LinearLayout mTaskOwnerBtnLinearLayout;
    private LinearLayout mEndDateDisappearSection;
    private TextView mTakeUpBtn;
    private TextView mWithdrawEventBtn;
    private TextView mMarkAsCompleteBtn;
    private TextView mEventTaskTypeLbl;
    private LinearLayout mEventJoinerButtonLinearLayout;
    private TextView mJoinBtn;
    private TextView mEventCompleteBtn;
    private TextView mWithdrawTaskBtn;


    private String mTaskId;
    private String mGroupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail2);

        //setup and initialize the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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

        //Get the information from the intent
        mTaskId = getIntent().getStringExtra(EXTRA_TASK_ID);
        mGroupId = getIntent().getStringExtra(EXTRA_GROUP_ID);
        mEventJoinerButtonLinearLayout = findViewById(R.id.eventJoinerBtnSet);
        mJoinBtn = findViewById(R.id.joinBtn);
        mGroupImageView = findViewById(R.id.groupImageView);
        mViewTaskOwnerLbl = findViewById(R.id.viewTaskOwnerListLbl);
        mElderNameLbl = findViewById(R.id.elderNameLbl);
        mTaskNameLbl = findViewById(R.id.taskNameLbl);
        mTaskDescriptionLbl = findViewById(R.id.taskDescriptionTbx);
        mImportanceLbl = findViewById(R.id.taskTypeLbl);
        mStartDateTxtView = findViewById(R.id.startDateTxtView);
        mStartTimeTxtView = findViewById(R.id.startTimeTxtView);
        mEndDateTxtView = findViewById(R.id.endDateTxtView);
        mEndTimeTxtView = findViewById(R.id.endTimeTxtView);
        mTakeUpBtn = findViewById(R.id.takeUpTaskBtn);
        mWithdrawTaskBtn = findViewById(R.id.withdrawTaskBtn);
        mMarkAsCompleteBtn = findViewById(R.id.markCompleteBtn);
        mTaskStatusLbl = findViewById(R.id.taskStatusLbl);
        mTaskOwnerBtnLinearLayout = findViewById(R.id.taskOwnerBtnSet);
        mEventTaskTypeLbl = findViewById(R.id.eventTaskTypeLbl);
        mEndDateDisappearSection = findViewById(R.id.endDateDisappearSection);
        mEventCompleteBtn = findViewById(R.id.markEventCompleteBtn);
        mWithdrawEventBtn = findViewById(R.id.withdrawEventBtn);

        /**
         //This line is for getting the task owner list
         FirebaseDatabase.getInstance().getReference("taskAssignee").child(mTaskId).addValueEventListener(new ValueEventListener() {
        @Override public void onDataChange(DataSnapshot dataSnapshot) {
        initTaskOwnerList();
        }

        @Override public void onCancelled(DatabaseError databaseError) {

        }
        });
         **/

        FirebaseDatabase.getInstance().getReference("group").orderByKey().equalTo(mGroupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                    Group group = groupSnapshot.getValue(Group.class);
                    mElderNameLbl.setText(group.getElderName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //StorageReference storageReference = storage.getReference();
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://family-care-giver.appspot.com");
        final StorageReference pathReference = storageReference.child("groupThumbNail/" + mGroupId);

        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(TaskDetailActivity.this).using(new FirebaseImageLoader()).load(pathReference).asBitmap().centerCrop().into(new BitmapImageViewTarget(mGroupImageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(TaskDetailActivity.this.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        mGroupImageView.setImageDrawable(circularBitmapDrawable);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // File not found
                Glide.with(TaskDetailActivity.this).load(R.drawable.ic_account_circle_white_24dp).asBitmap().centerCrop().into(new BitmapImageViewTarget(mGroupImageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(TaskDetailActivity.this.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        mGroupImageView.setImageDrawable(circularBitmapDrawable);
                        mGroupImageView.setBackground(TaskDetailActivity.this.getResources().getDrawable(R.drawable.round_grey_oval));
                    }
                });
            }
        });

        FirebaseDatabase.getInstance().getReference("tasks").orderByKey().equalTo(mTaskId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                    CustomTasks task = taskSnapshot.getValue(CustomTasks.class);
                    String taskName = task.getTaskName();
                    String taskDescription = task.getTaskDescription();
                    String importance = task.getImportance();
                    //Boolean isAllDayEvent = task.isAllDayEvent();
                    double taskStartDate = task.getTaskStartDate();
                    double taskEndDate = task.getTaskEndDate();
                    String taskStatus = task.getStatus();
                    String eventTaskType = task.getTaskEventType();

                    mTaskNameLbl.setText(taskName);
                    //initialize the widget using the value of the task
                    if (!taskDescription.trim().equals("")) {
                        mTaskDescriptionLbl.setText(taskDescription);
                    } else {
                        mTaskDescriptionLbl.setText(R.string.noDescription);
                    }

                    switch (eventTaskType) {
                        case "E":
                            mEventTaskTypeLbl.setText(R.string.event);
                            mViewTaskOwnerLbl.setText("View event joiner list");
                            mEndDateDisappearSection.setVisibility(View.VISIBLE);
                            break;
                        case "T":
                            mEventTaskTypeLbl.setText(R.string.task);
                            mEndDateDisappearSection.setVisibility(GONE);
                            break;
                    }
                    ;

                    switch (importance) {

                        case "c":
                            mImportanceLbl.setText(R.string.casualTask);
                            break;
                        case "i":
                            mImportanceLbl.setText(R.string.importantTask);
                            break;
                        case "r":
                            mImportanceLbl.setText(R.string.reminder);
                            break;
                        default:
                            break;
                    }

                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd-MM-yyyy");
                    mStartDateTxtView.setText(dateFormat.format(taskStartDate));
                    mEndDateTxtView.setText(dateFormat.format(taskEndDate));

                    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
                    mStartTimeTxtView.setText(timeFormat.format(taskStartDate));
                    mEndTimeTxtView.setText(timeFormat.format(taskEndDate));

                    String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();


                    switch (eventTaskType) {
                        case "E":
                            Log.d("debug", "event");
                            if (taskStatus.equals("C")) {
                                //Completed event -->N button should be visible, but allow to view the joiner list
                                mTakeUpBtn.setVisibility(GONE);
                                mJoinBtn.setVisibility(GONE);
                                mTaskOwnerBtnLinearLayout.setVisibility(GONE);
                                mEventJoinerButtonLinearLayout.setVisibility(GONE);
                                mViewTaskOwnerLbl.setVisibility(View.VISIBLE);
                            } else if (taskStatus.equals("N")) {
                                //New event, then have to check did the user already join the event
                                Query userTaskRef = FirebaseDatabase.getInstance().getReference("UserTask").child(currentUserId).child("Pending").orderByChild("taskId").equalTo(mTaskId);
                                userTaskRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (!dataSnapshot.exists()) {
                                            //if they have not joined the event
                                            //show up join button, hide the button set for already join person (withdraw and complete btn hidden)
                                            mJoinBtn.setVisibility(View.VISIBLE);
                                            mViewTaskOwnerLbl.setVisibility(View.VISIBLE);
                                            mEventJoinerButtonLinearLayout.setVisibility(GONE);
                                        } else {
                                            // if they have already joined the event
                                            //hide join button, show the button set for already join person (withdraw and complete btn hidden)
                                            mJoinBtn.setVisibility(GONE);
                                            mViewTaskOwnerLbl.setVisibility(View.VISIBLE);
                                            mEventJoinerButtonLinearLayout.setVisibility(View.VISIBLE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                            break;
                        case "T":
                            Log.d("debug", "task");
                            if (taskStatus.equals("C")) {
                                //Completed task
                                //show up ta=ke up button, hide join button, show view task owner button, hide button set the task owner & event joiner
                                mTakeUpBtn.setVisibility(GONE);
                                mJoinBtn.setVisibility(GONE);
                                mViewTaskOwnerLbl.setVisibility(View.VISIBLE);
                                mTaskOwnerBtnLinearLayout.setVisibility(GONE);
                                mEventJoinerButtonLinearLayout.setVisibility(GONE);
                            } else if (taskStatus.equals("N")) {
                                //New Task (PENDING)
                                mTakeUpBtn.setVisibility(View.VISIBLE);
                                mJoinBtn.setVisibility(GONE);
                                mViewTaskOwnerLbl.setVisibility(GONE);
                                mTaskOwnerBtnLinearLayout.setVisibility(View.GONE);
                                mEventJoinerButtonLinearLayout.setVisibility(GONE);
                            } else if (taskStatus.equals("A")) {
                                //Assigned Task (Assigned)
                                mTakeUpBtn.setVisibility(View.GONE);
                                mJoinBtn.setVisibility(GONE);
                                mViewTaskOwnerLbl.setVisibility(View.VISIBLE);
                                mTaskOwnerBtnLinearLayout.setVisibility(View.VISIBLE);
                                mEventJoinerButtonLinearLayout.setVisibility(GONE);
                            }
                            break;
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

        mJoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task<?>[] tasks = new Task[]{
                        addCurrentUserToTaskAssignee(),
                        addEntryToPendingUserTask()
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


        mEventCompleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task<?>[] tasks = new Task[]{
                        setTaskStatus("C"),
                        removeEntryFromPendingUserTask(),
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


        mWithdrawTaskBtn.setOnClickListener(new View.OnClickListener() {
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

        mWithdrawEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Task<?>[] tasks = new Task[]{
                        removeEntryFromPendingUserTask(),
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

        mViewTaskOwnerLbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start the activity for view the task owner list
                Intent intent = new Intent(TaskDetailActivity.this, TaskOwnerListActivity.class);
                intent.putExtra(TaskOwnerListActivity.EXTRA_GROUP_ID, mGroupId);
                intent.putExtra(TaskOwnerListActivity.EXTRA_TASK_ID, mTaskId);
                startActivity(intent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.task_detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_chat_room:
                FirebaseDatabase.getInstance().getReference().child("tasks").orderByKey().equalTo(mTaskId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                            Intent intent = new Intent(TaskDetailActivity.this, ChatRoomActivity.class);
                            intent.putExtra(ChatRoomActivity.EXTRA_TASK_ID, mTaskId);
                            intent.putExtra(ChatRoomActivity.EXTRA_GROUP_ID, mGroupId);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                break;
            case R.id.menu_attachment:
                Intent intent = new Intent(TaskDetailActivity.this, AttachmentListActivity.class);
                intent.putExtra(AttachmentListActivity.EXTRA_TASK_ID, mTaskId);
                startActivity(intent);
                break;
        }
        return true;
    }

    /**
     * private void initTaskOwnerList() {
     * if (ContextCompat.checkSelfPermission(TaskDetailActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
     * final HashMap<String, LocalContacts> localContactsHashMap = getLocalContactsHashMap();
     * //Remove the existing list of contact person displayed before load
     * mTaskOwnerLinearLayout.removeAllViews();
     * DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference("taskAssignee").child(mTaskId);
     * taskRef.addListenerForSingleValueEvent(new ValueEventListener() {
     *
     * @Override public void onDataChange(DataSnapshot dataSnapshot) {
     * for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
     * CustomFirebaseUser user = userSnapshot.getValue(CustomFirebaseUser.class);
     * final String phoneNo = user.getTelNum();
     * //If the whole tel code can be matched, use it, if not ignore the +XXX part
     * LocalContacts contact = localContactsHashMap.get(phoneNo);
     * if (contact == null) {
     * contact = localContactsHashMap.get(phoneNo.substring(4));
     * }
     * <p>
     * TextView contactTxtView = new TextView(TaskDetailActivity.this);
     * contactTxtView.setTextSize(20);
     * contactTxtView.setGravity(Gravity.END);
     * if (phoneNo.equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) {
     * contactTxtView.setText(getString(R.string.me));
     * } else if (contact != null) {
     * contactTxtView.setText(contact.getContactsLocalDisplayName());
     * } else {
     * contactTxtView.setText(phoneNo);
     * }
     * <p>
     * //Enable user to click the contact person to dial him, will copy the phone number to dial screen
     * contactTxtView.setOnClickListener(new View.OnClickListener() {
     * @Override public void onClick(View v) {
     * Intent callIntent = new Intent(Intent.ACTION_DIAL);
     * callIntent.setData(Uri.parse("tel:" + phoneNo));
     * startActivity(callIntent);
     * }
     * });
     * mTaskOwnerLinearLayout.addView(contactTxtView);
     * }
     * }
     * @Override public void onCancelled(DatabaseError databaseError) {
     * <p>
     * }
     * });
     * }
     * }
     **/


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

    private Task<String> removeEntryFromPendingUserTask() {
        final TaskCompletionSource<String> tcs = new TaskCompletionSource<>();
        final String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query assignedUserTaskAlreadyExistQuery = FirebaseDatabase.getInstance().getReference("UserTask").child(currentUserUid).child("Pending").orderByChild("taskId").equalTo(mTaskId);
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


    private Task<String> addEntryToPendingUserTask() {
        final TaskCompletionSource<String> tcs = new TaskCompletionSource<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query checkUserTaskAssignedAlreadyExistQuery = database.getReference("UserTask").child(currentUserUid).child("Pending").orderByChild("taskId").equalTo(mTaskId);
        checkUserTaskAssignedAlreadyExistQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    DatabaseReference userTaskAssignedRef = FirebaseDatabase.getInstance().getReference("UserTask").child(currentUserUid).child("Pending");
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
