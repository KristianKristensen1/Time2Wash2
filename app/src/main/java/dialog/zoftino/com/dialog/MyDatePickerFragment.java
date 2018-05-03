package dialog.zoftino.com.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
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
public class MyDatePickerFragment extends DialogFragment {
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
                    Toast.makeText(getActivity(), "selected date is " + view.getYear() +
                            " / " + (view.getMonth()+1) +
                            " / " + view.getDayOfMonth(), Toast.LENGTH_SHORT).show();
                }
            };
}