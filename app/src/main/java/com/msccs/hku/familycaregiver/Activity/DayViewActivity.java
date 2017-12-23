package com.msccs.hku.familycaregiver.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.msccs.hku.familycaregiver.databases.DatabaseQuery;
import com.msccs.hku.familycaregiver.databases.EventObjects;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import com.msccs.hku.familycaregiver.R;

import java.util.Calendar;

public class DayViewActivity extends AppCompatActivity {
    private static final String TAG = DayViewActivity.class.getSimpleName();
    private ImageView previousDay;
    private ImageView nextDay;
    private TextView currentDate;
    private Calendar cal = Calendar.getInstance();
    private DatabaseQuery mQuery;
    private RelativeLayout mLayout;
    private int eventIndex;
    private int eventCounter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_view);
        //mQuery = new DatabaseQuery(this);
        mLayout = (RelativeLayout)findViewById(R.id.left_event_column);
        eventIndex = mLayout.getChildCount();
        currentDate = (TextView)findViewById(R.id.display_current_date);
        currentDate.setText(displayDateInString(cal.getTime()));
        displayDailyEvents();
        previousDay = (ImageView)findViewById(R.id.previous_day);
        nextDay = (ImageView)findViewById(R.id.next_day);
        previousDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousCalendarDate();
            }
        });
        nextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextCalendarDate();
            }
        });
    }
    private void previousCalendarDate(){
        mLayout.removeViewAt(eventIndex - 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        currentDate.setText(displayDateInString(cal.getTime()));
        displayDailyEvents();
    }
    private void nextCalendarDate(){
        mLayout.removeViewAt(eventIndex - 1);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        currentDate.setText(displayDateInString(cal.getTime()));
        displayDailyEvents();
    }
    private String displayDateInString(Date mDate){
        SimpleDateFormat formatter = new SimpleDateFormat("d MMMM, yyyy", Locale.ENGLISH);
        return formatter.format(mDate);
    }
    private void displayDailyEvents(){
        Date calendarDate = cal.getTime();
        List<EventObjects> dailyEvent = new ArrayList<>(); //mQuery.getAllFutureEvents(calendarDate);
        dailyEvent.add(new EventObjects(1, "Hello world", convertStringToDate("21-12-2017 15:00"), convertStringToDate("21-12-2017 16:00")));
        dailyEvent.add(new EventObjects(2, "Byebye world", convertStringToDate("21-12-2017 16:00"), convertStringToDate("21-12-2017 17:00")));
        dailyEvent.add(new EventObjects(3, "Fun world", convertStringToDate("21-12-2017 17:00"), convertStringToDate("21-12-2017 18:00")));
        dailyEvent.add(new EventObjects(4, "Christmas world", convertStringToDate("22-12-2017 11:00"), convertStringToDate("22-12-2017 12:00")));
        for(EventObjects eObject : dailyEvent){
            Date eventDate = eObject.getDate();
            Date endDate = eObject.getEnd();
            String eventMessage = eObject.getMessage();
            int eventBlockHeight = getEventTimeFrame(eventDate, endDate);
            Log.d(TAG, "Height " + eventBlockHeight);
            displayEventSection(eventDate, eventBlockHeight, eventMessage);
        }
    }

    private Date convertStringToDate(String dateInString){
        DateFormat format = new SimpleDateFormat("d-MM-yyyy HH:mm", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private int getEventTimeFrame(Date start, Date end){
        long timeDifference = end.getTime() - start.getTime();
        Calendar mCal = Calendar.getInstance();
        mCal.setTimeInMillis(timeDifference);
        int hours = mCal.get(Calendar.HOUR);
        int minutes = mCal.get(Calendar.MINUTE);
        return (hours * 60) + ((minutes * 60) / 100);
    }
    private void displayEventSection(Date eventDate, int height, String message){
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        String displayValue = timeFormatter.format(eventDate);
        String[]hourMinutes = displayValue.split(":");
        int hours = Integer.parseInt(hourMinutes[0]);
        int minutes = Integer.parseInt(hourMinutes[1]);
        Log.d(TAG, "Hour value " + hours);
        Log.d(TAG, "Minutes value " + minutes);
        int topViewMargin = (hours * 60) + ((minutes * 60) / 100);
        Log.d(TAG, "Margin top " + topViewMargin);
        createEventView(topViewMargin, height, message);
    }
    private void createEventView(int topMargin, int height, String message){
        eventCounter++;
        String colorString = "BLUE";
        int colorIndex = eventCounter % 7;
        switch(colorIndex) {
            case 0:
                colorString = "BLUE";
                break;
            case 1:
                colorString = "CYAN";
                break;
            case 2:
                colorString = "GRAY";
                break;
            case 3:
                colorString = "GREEN";
                break;
            case 4:
                colorString = "MAGENTA";
                break;
            case 5:
                colorString = "RED";
                break;
            case 6:
                colorString = "YELLOW";
                break;
            default:
                colorString = "BLUE";
        }

        TextView mEventView = new TextView(DayViewActivity.this);
        RelativeLayout.LayoutParams lParam = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lParam.topMargin = topMargin * 2;
        lParam.leftMargin = 24;
        mEventView.setLayoutParams(lParam);
        mEventView.setPadding(24, 0, 24, 0);
        mEventView.setHeight(height * 2);
        mEventView.setGravity(0x11);
        mEventView.setTextColor(Color.parseColor("#ffffff"));
        mEventView.setText(message);
        //mEventView.setBackgroundColor(Color.parseColor("#3F51B5"));
        mEventView.setBackgroundColor(Color.parseColor(colorString));
        mLayout.addView(mEventView, eventIndex - 1);
    }
}
