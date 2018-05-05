package dialog.zoftino.com.dialog;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.krist.time2washproject.AlertReceiver;
import com.example.krist.time2washproject.BookingActivity;
import com.example.krist.time2washproject.NotificationHelper;

import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.Date;

/*
http://www.zoftino.com/android-datepicker-example
*/
public class MyDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    Context myContext = BookingActivity.getActivity();

    public static int getIndexID() {
        return indexID;
    }

    public static void setIndexID(int indexID) {
        MyDatePickerFragment.indexID = indexID;
    }

    public static int indexID = 0;

    public static String[] getChannelID() {
        return channelID;
    }
    public static String[] channelID = {"24","1","Now","Done"};

public class MyDatePickerFragment extends DialogFragment{
    String date;
    //public Boolean dateIsSet = false;

    public static MyDatePickerFragment newInstance(DatePickerFragmentListener listener) {
        MyDatePickerFragment fragment = new MyDatePickerFragment();
        fragment.setDatePickerListener(listener);
        return fragment;
    }

    private DatePickerFragmentListener datePickerListener;

    public interface DatePickerFragmentListener {
        void onDateSet(Date date);
        public void onDateSet(String date);

    }

    public DatePickerFragmentListener getDatePickerListener() {
        return this.datePickerListener;
    }

    public void setDatePickerListener(DatePickerFragmentListener listener) {
        this.datePickerListener = listener;
    }

    protected void notifyDatePickerListener(Date date) {
        if (this.datePickerListener != null) {
    protected void notifyDatePickerListener(String date) {
        if(this.datePickerListener != null) {
            this.datePickerListener.onDateSet(date);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        setIndexID(0);

        /*https://stackoverflow.com/questions/16749361/how-set-maximum-date-in-datepicker-dialog-in-android*/
        /*https://android--code.blogspot.dk/2015/08/android-datepickerdialog-set-max-date.html*/
        DatePickerDialog dialog = new DatePickerDialog(this.getContext(), dateSetListener, year, month, day);

        dialog.getDatePicker().setMinDate(new Date().getTime());
        c.add(Calendar.DATE, 13);
        dialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        return dialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {

    }


    private DatePickerDialog.OnDateSetListener dateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    Calendar c = Calendar.getInstance();
                    c.set(year, month, day);

                    Date date = c.getTime();
                    // Here we call the listener and pass the date back to it.
                    notifyDatePickerListener(date);
                    startAlarm(c);
                    /*date = "" + view.getDayOfMonth() + "/" + (view.getMonth() + 1) + "/" + view.getYear();
                    Toast.makeText(getActivity(), "selected date is " + date, Toast.LENGTH_SHORT).show();*/
                }
            };

    private void startAlarm(Calendar c) {
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
                    date = "" + view.getDayOfMonth() + "/" + (view.getMonth()+1) + "/" + view.getYear();
                    Toast.makeText(getActivity(), "selected date is " + date, Toast.LENGTH_SHORT).show();
                    notifyDatePickerListener(date);
                }
            };
}