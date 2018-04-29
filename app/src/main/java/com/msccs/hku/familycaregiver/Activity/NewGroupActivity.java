package com.msccs.hku.familycaregiver.Activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
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
    private Calendar mCalendar;
    Bitmap mElderlyImage;
    private FloatingActionButton mAddNewGroupFAB;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            try {
                Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                mElderlyImage = BitmapFactory.decodeStream(imageStream);
                mElderlyImage = getResizedBitmap(mElderlyImage, 200);
                Glide.with(NewGroupActivity.this).load(imageUri).asBitmap().centerCrop().into(new BitmapImageViewTarget(mElderPhotoImageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        mElderPhotoImageView.setImageDrawable(circularBitmapDrawable);
                        mElderPhotoImageView.setBackground(getResources().getDrawable(R.drawable.round_grey_oval));
                    }
                });

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
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
        mCalendar.set(Calendar.HOUR,0);
        mCalendar.set(Calendar.MINUTE,0);
        mCalendar.set(Calendar.SECOND,0);
        mCalendar.set(Calendar.MILLISECOND,0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd-MM-yyyy");
        mBirthdayBtn.setText(dateFormat.format(mCalendar.getTime()));

        Glide.with(NewGroupActivity.this).load(R.drawable.ic_account_circle_white_24dp).asBitmap().centerCrop().into(new BitmapImageViewTarget(mElderPhotoImageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                mElderPhotoImageView.setImageDrawable(circularBitmapDrawable);
                mElderPhotoImageView.setBackground(getResources().getDrawable(R.drawable.round_grey_oval));
            }
        });

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
                        mCalendar.set(Calendar.HOUR,0);
                        mCalendar.set(Calendar.MINUTE,0);
                        mCalendar.set(Calendar.SECOND,0);
                        mCalendar.set(Calendar.MILLISECOND,0);
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
                    long elderBirthday = mCalendar.getTimeInMillis();
                    //Toast.makeText(NewGroupActivity.this,elderBirthday+"",Toast.LENGTH_SHORT).show();
                    intent.putExtra(InviteGroupMemberActivityForNewGp.EXTRA_ELDERLY_NAME,elderlyName);
                    intent.putExtra(InviteGroupMemberActivityForNewGp.EXTRA_BIRTHDAY,elderBirthday);
                    intent.putExtra(InviteGroupMemberActivityForNewGp.EXTRA_ELDER_PHOTO,mElderlyImage);
                    startActivity(intent);
                    finish();
                }
            }
        });

        mElderPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, ""), RC_PHOTO_PICKER);
            }
        });
    }

    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }

    //--For resizing image
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }




}
