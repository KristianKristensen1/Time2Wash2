package dialog.zoftino.com.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.krist.time2washproject.BookingActivity;

import java.util.Calendar;
import java.util.Date;

/*
http://www.zoftino.com/android-datepicker-example
*/

//https://stackoverflow.com/questions/24558835/how-can-i-pass-the-date-chosen-in-a-date-picker-to-the-activity-which-contains-t
public class MyDatePickerFragment extends DialogFragment{
    String date;

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

        /*https://stackoverflow.com/questions/16749361/how-set-maximum-date-in-datepicker-dialog-in-android*/
        /*https://android--code.blogspot.dk/2015/08/android-datepickerdialog-set-max-date.html*/
        DatePickerDialog dialog = new DatePickerDialog(this.getContext(), dateSetListener, year, month, day);

        dialog.getDatePicker().setMinDate(new Date().getTime());
        c.add(Calendar.DATE, 13);
        dialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        return dialog;
    }


    private DatePickerDialog.OnDateSetListener dateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    date = "" + view.getDayOfMonth() + "/" + (view.getMonth()+1) + "/" + view.getYear();
                    Toast.makeText(getActivity(), "selected date is " + date, Toast.LENGTH_SHORT).show();
                    notifyDatePickerListener(date);
                }
            };
}