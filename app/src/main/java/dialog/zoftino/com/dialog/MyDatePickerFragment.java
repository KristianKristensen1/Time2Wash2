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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/*
http://www.zoftino.com/android-datepicker-example
*/

//https://stackoverflow.com/questions/24558835/how-can-i-pass-the-date-chosen-in-a-date-picker-to-the-activity-which-contains-t

public class MyDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

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

    public static String[] channelID = {"24", "1", "Now", "Done"};


    public static MyDatePickerFragment newInstance(DatePickerFragmentListener listener) {
        MyDatePickerFragment fragment = new MyDatePickerFragment();
        fragment.setDatePickerListener(listener);
        return fragment;
    }

    private DatePickerFragmentListener datePickerListener;

    public interface DatePickerFragmentListener {
        public void onDateSet(String date);

    }

    public DatePickerFragmentListener getDatePickerListener() {
        return this.datePickerListener;
    }

    public void setDatePickerListener(DatePickerFragmentListener listener) {
        this.datePickerListener = listener;
    }

    protected void notifyDatePickerListener(String date) {
        if (this.datePickerListener != null) {
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
                    String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(c.getTime());
                    notifyDatePickerListener(date);
                }
            };
}