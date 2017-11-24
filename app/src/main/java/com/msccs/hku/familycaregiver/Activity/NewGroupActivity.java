package com.msccs.hku.familycaregiver.Activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.msccs.hku.familycaregiver.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewGroupActivity extends AppCompatActivity {

    private Button mBirthdayBtn;
    private int mYear, mMonth, mDay;
    private IncludedLayout newGroupInfoLayout;

    //This is the include layout, bind it here
    @BindView(R.id.content_new_group) View newGroupInputView;

    @BindView(R.id.fab)
    public FloatingActionButton addNewGroupFab;

    @OnClick(R.id.fab)
    public void onAddNewGroupFabClicked(){
        String elderlyName = newGroupInfoLayout.elderlyNameTbx.getText().toString().trim();
        //Log.d("elderlyName",elderlyName);

        Intent intent = new Intent(NewGroupActivity.this,InviteGroupMemberActivity.class);
        intent.putExtra(InviteGroupMemberActivity.EXTRA_ELDERLY_NAME,elderlyName);
        intent.putExtra(InviteGroupMemberActivity.EXTRA_BIRTHDAY_DAY,mDay);
        intent.putExtra(InviteGroupMemberActivity.EXTRA_BIRTHDAY_MONTH,mMonth);
        intent.putExtra(InviteGroupMemberActivity.EXTRA_BIRTHDAY_YEAR,mYear);

        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        ButterKnife.bind(this);

        newGroupInfoLayout = new IncludedLayout();
        ButterKnife.bind(newGroupInfoLayout,newGroupInputView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.new_group);

        mBirthdayBtn = (Button) findViewById(R.id.birthdayBtn);
        mBirthdayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(NewGroupActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        c.set(Calendar.YEAR,(year));
                        c.set(Calendar.MONTH,month);
                        c.set(Calendar.DAY_OF_MONTH,day);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd-MM-yyyy");
                        mBirthdayBtn.setText(dateFormat.format(c.getTime()));
                    }

                }, mYear,mMonth, mDay).show();

            }
        });

    }


    //This is just for handle the include inside the xml, using butterknife
    //ref: https://stackoverflow.com/questions/40741828/butterknife-does-not-work-with-include-tag
    static class IncludedLayout{
        @BindView(R.id.elderlyNameTbx) EditText elderlyNameTbx;
    }
}
