package com.example.krist.time2washproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmSwitch {
    Context myContext = BookingActivity.getActivity();


    /*https://stackoverflow.com/questions/2709253/converting-a-string-to-an-integer-on-android*/
    public int handleTimeInput(String time) {
        int startingHour = 0;
        String stringHour;
        try {
            stringHour = time.substring(time.indexOf(" ")+1, time.indexOf("-"));
            startingHour = Integer.parseInt(stringHour);
        } catch (NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }
        return startingHour;
    }

    public Date handleDateInput(String Date) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = simpleDateFormat.parse(Date);
        return date;
    }

    public void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) myContext.getSystemService(Context.ALARM_SERVICE);
        Calendar c24 = Calendar.getInstance();
        c24.setTimeInMillis(c.getTimeInMillis());
        c24.add(Calendar.HOUR, -24);

        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(c.getTimeInMillis());
        c1.add(Calendar.HOUR, -1);

        Calendar cNow = Calendar.getInstance();
        cNow.setTimeInMillis(c.getTimeInMillis());

        Calendar cDone = Calendar.getInstance();
        cDone.setTimeInMillis(c.getTimeInMillis());
        cDone.add(Calendar.HOUR, 2);

        if (alarmManager != null) {
            Intent alarmIntent24 = new Intent(myContext, AlertReceiver.class);
            PendingIntent pendingIntent24 = PendingIntent.getBroadcast(myContext, 1, alarmIntent24, 0);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, c24.getTimeInMillis(), pendingIntent24);

            Intent alarmIntent1 = new Intent(myContext, AlertReceiver.class);
            PendingIntent pendingIntent1 = PendingIntent.getBroadcast(myContext, 2, alarmIntent1, 0);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, c1.getTimeInMillis(), pendingIntent1);

            Intent alarmIntentNow = new Intent(myContext, AlertReceiver.class);
            PendingIntent pendingIntentNow = PendingIntent.getBroadcast(myContext, 3, alarmIntentNow, 0);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, cNow.getTimeInMillis(), pendingIntentNow);

            Intent alarmIntentDone = new Intent(myContext, AlertReceiver.class);
            PendingIntent pendingIntentDone = PendingIntent.getBroadcast(myContext, 4, alarmIntentDone, 0);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, cDone.getTimeInMillis(), pendingIntentDone);
        }
    }

    public void cancelAlarm(){
        AlarmManager alarmManager = (AlarmManager) myContext.getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent24 = new Intent(myContext, AlertReceiver.class);
        PendingIntent pendingIntent24 = PendingIntent.getBroadcast(myContext, 1, alarmIntent24, 0);
        alarmManager.cancel(pendingIntent24);

        Intent alarmIntent1 = new Intent(myContext, AlertReceiver.class);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(myContext, 2, alarmIntent1, 0);
        alarmManager.cancel(pendingIntent1);

        Intent alarmIntentNow = new Intent(myContext, AlertReceiver.class);
        PendingIntent pendingIntentNow = PendingIntent.getBroadcast(myContext, 3, alarmIntentNow, 0);
        alarmManager.cancel(pendingIntentNow);

        Intent alarmIntentDone = new Intent(myContext, AlertReceiver.class);
        PendingIntent pendingIntentDone = PendingIntent.getBroadcast(myContext, 4, alarmIntentDone, 0);
        alarmManager.cancel(pendingIntentDone);
    }
}
