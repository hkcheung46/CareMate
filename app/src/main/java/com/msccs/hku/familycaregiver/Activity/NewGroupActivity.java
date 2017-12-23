package com.msccs.hku.familycaregiver.Activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.msccs.hku.familycaregiver.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewGroupActivity extends AppCompatActivity {

    private static final int RC_PHOTO_PICKER = 2;
    private View mRootView;
    private Button mBirthdayBtn;
    private ImageView mElderPhotoImageView;
    private EditText mElderlyNameTbx;
    private int mYear, mMonth, mDay = 0;
    private Calendar mCalendar;
    private FloatingActionButton mAddNewGroupFAB;

    //This is the include layout, bind it here

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            mElderPhotoImageView.setImageURI(selectedImageUri);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.new_group);

        mRootView = (View) findViewById(R.id.root);

        mElderPhotoImageView = (ImageView) findViewById(R.id.elderlyPhotoImgView);
        mElderlyNameTbx = (EditText) findViewById(R.id.elderlyNameTbx);
        mBirthdayBtn = (Button) findViewById(R.id.birthdayBtn);
        mAddNewGroupFAB = (FloatingActionButton) findViewById(R.id.fab);

        mCalendar = Calendar.getInstance();
        mBirthdayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                DatePickerDialog datePickerDialog = new DatePickerDialog(NewGroupActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        mCalendar.set(Calendar.YEAR, (year));
                        mCalendar.set(Calendar.MONTH, month);
                        mCalendar.set(Calendar.DAY_OF_MONTH, day);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd-MM-yyyy");
                        mBirthdayBtn.setText(dateFormat.format(mCalendar.getTime()));
                    }

                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        mAddNewGroupFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String elderlyName = mElderlyNameTbx.getText().toString().trim();

                //Validations here
                if (elderlyName.trim().equals("")) {
                    showSnackbar(R.string.elderlyNameCannotBeEmpty);
                    mElderlyNameTbx.setError(getString(R.string.elderlyNameCannotBeEmpty));
                } else {
                    //if passed the validations, go to invite member page
                    Intent intent = new Intent(NewGroupActivity.this, InviteGroupMemberActivityForNewGp.class);

                    mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
                    //According to https://stackoverflow.com/questions/4694371/why-does-calendar-getcalendar-month-return-0
                    //The month returned started from 0 to 11, so always 1 less than actual,e.g. Jan =0, Dec=11
                    mMonth = mCalendar.get(Calendar.MONTH);
                    mYear = mCalendar.get(Calendar.YEAR);

                    intent.putExtra(InviteGroupMemberActivityForNewGp.EXTRA_ELDERLY_NAME, elderlyName);
                    intent.putExtra(InviteGroupMemberActivityForNewGp.EXTRA_BIRTHDAY_DAY, mDay);
                    intent.putExtra(InviteGroupMemberActivityForNewGp.EXTRA_BIRTHDAY_MONTH, mMonth);
                    intent.putExtra(InviteGroupMemberActivityForNewGp.EXTRA_BIRTHDAY_YEAR, mYear);
                    startActivity(intent);
                    finish();
                }
            }
        });

        mElderPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });


    }

    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }

}
