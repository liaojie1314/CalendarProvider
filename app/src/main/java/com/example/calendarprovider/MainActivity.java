package com.example.calendarprovider;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.TimeZone;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkCalendarPermission();
        }
        //queryCalendars();
    }

    public void addAlertEvent(View view) {
        long calID = 1;
        Calendar beginTime = Calendar.getInstance();
        //月从0开始
        beginTime.set(2021, 1, 14, 5, 20);
        long beginTimeMills = beginTime.getTimeInMillis();
        //结束时间
        Calendar endTime = Calendar.getInstance();
        endTime.set(2021, 2, 14, 5, 20);
        long endTimeMillis = endTime.getTimeInMillis();
        //事件内容

        String timeZone = TimeZone.getDefault().getID();
        Log.d(TAG, "timeZone-->" + timeZone);
        ContentValues eventValues = new ContentValues();
        eventValues.put(CalendarContract.Events.DTSTART, beginTimeMills);
        eventValues.put(CalendarContract.Events.DTEND, endTimeMillis);
        eventValues.put(CalendarContract.Events.CALENDAR_ID, calID);
        eventValues.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone);
        eventValues.put(CalendarContract.Events.TITLE, "双十一购物狂欢开抢");
        eventValues.put(CalendarContract.Events.EVENT_LOCATION, "重庆");
        eventValues.put(CalendarContract.Events.DESCRIPTION, "买东西");
        Uri eventUri = CalendarContract.Events.CONTENT_URI;
        //Uri uri=Uri.parse("content://"+..);
        ContentResolver contentResolver = getContentResolver();
        Uri resultUri = contentResolver.insert(eventUri, eventValues);
        String eventID = resultUri.getLastPathSegment();
        Log.d(TAG, "eventID-->" + eventID);
        ContentValues reminderValues = new ContentValues();
        Uri reminderUri = CalendarContract.Reminders.CONTENT_URI;
        contentResolver.insert(reminderUri, reminderValues);
        reminderValues.put(CalendarContract.Reminders.EVENT_ID, eventID);
        reminderValues.put(CalendarContract.Reminders.MINUTES, 15);
        reminderValues.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALARM);

        Log.d(TAG, "resultUri-->" + resultUri);
    }

    private void checkCalendarPermission() {
        int readPermission = checkSelfPermission(Manifest.permission.READ_CALENDAR);
        int writePermission = checkSelfPermission(Manifest.permission.WRITE_CALENDAR);
        if (readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED) {

        } else {
            Log.d(TAG, "requestPermissions...");
            requestPermissions(new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "has permission...");
            } else {
                Log.d(TAG, "no permission...");
                finish();
            }
        }
    }

    private void queryCalendars() {
        ContentResolver contentResolver = getContentResolver();
        //Uri uri=Uri.parse("content://"+"com.android.calender"+"calenders");
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        Cursor query = contentResolver.query(uri, null, null, null, null);
        String[] columnNames = query.getColumnNames();
        while (query.moveToNext()) {
            Log.d(TAG, "==================================");
            for (String columnName : columnNames) {
                Log.d(TAG, columnName + "===" + query.getString(query.getColumnIndex(columnName)));
            }
            Log.d(TAG, "===================================");
        }
    }
}