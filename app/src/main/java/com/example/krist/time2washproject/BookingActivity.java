package com.example.krist.time2washproject;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;

import dialog.zoftino.com.dialog.MyDatePickerFragment;

public class BookingActivity extends AppCompatActivity {
    String[] machineList = {"Vask1", "Vask2", "Tør1", "Tør2"};//, dateList = {"Mandag", "Tirsdag", "Onsdag", "Torsdag", "Fredag", "Lørdag", "Søndag"};
    ArrayAdapter<String> machineArrayAdapter, dateArrayAdapter;
    MaterialBetterSpinner machineBetterSpinner, dateBetterSpinner;
    Button bookingActivity_chooseDate_button;
    com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner bookingActivity_chooseMachine_dropDown, bookingActivity_chooseDate_dropDown;
    private WashingTimeAdaptor washingTimeAdaptor;
    private ListView washingTimeListView;
    Context activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        findViews();
        setDropDowns();

        final ArrayList<WashingTime> WashingTimeArrayList = new ArrayList<>();
        for(int i = 0; i < 7; i++){
            WashingTimeArrayList.add(new WashingTime("This is the time", false));
        }
        WashingTimeArrayList.set(0, new WashingTime("kl. 8-10", false, "com.example.krist.vaskemaskinetider.BOOK", 0));
        WashingTimeArrayList.set(1, new WashingTime("kl. 10-12", true,"com.example.krist.vaskemaskinetider.BOOK", 1));
        WashingTimeArrayList.set(2, new WashingTime("kl. 12-14", false,"com.example.krist.vaskemaskinetider.BOOK", 2));
        WashingTimeArrayList.set(3, new WashingTime("kl. 14-16", false,"com.example.krist.vaskemaskinetider.BOOK", 3));
        WashingTimeArrayList.set(4, new WashingTime("kl. 16-18", true,"com.example.krist.vaskemaskinetider.BOOK", 4));
        WashingTimeArrayList.set(5, new WashingTime("kl. 18-20", false,"com.example.krist.vaskemaskinetider.BOOK", 5));
        WashingTimeArrayList.set(6, new WashingTime("kl. 20-22", false,"com.example.krist.vaskemaskinetider.BOOK", 6));
        washingTimeAdaptor = new WashingTimeAdaptor(this, WashingTimeArrayList);
        washingTimeListView = findViewById(R.id.bookingActivity_availableTimes_listView);
        washingTimeListView.setAdapter(washingTimeAdaptor);

        bookingActivity_chooseDate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(v);
            }
        });
    }

    /*https://www.youtube.com/watch?v=x6HtXktAoew*/
    private void setDropDowns() {
        machineArrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, machineList);
        //dateArrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, dateList);
        machineBetterSpinner = bookingActivity_chooseMachine_dropDown;
        dateBetterSpinner = bookingActivity_chooseDate_dropDown;
        machineBetterSpinner.setAdapter(machineArrayAdapter);
//        dateBetterSpinner.setAdapter(dateArrayAdapter);
    }

    private void findViews() {
        bookingActivity_chooseMachine_dropDown = findViewById(R.id.bookingActivity_chooseMachine_dropDown);
        bookingActivity_chooseDate_button = findViewById(R.id.bookingActivity_chooseDate_button);
        //bookingActivity_chooseDate_dropDown = findViewById(R.id.bookingActivity_chooseDate_dropDown);
    }

    //http://www.zoftino.com/android-datepicker-example
    public void showDatePicker(View v) {
        MyDatePickerFragment myFragment = new MyDatePickerFragment();
        myFragment.show(getSupportFragmentManager(), "date picker");

        new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int month, int day) {
                Toast.makeText(getActivity(), "selected date is " + view.getYear() +
                        " / " + (view.getMonth() + 1) +
                        " / " + view.getDayOfMonth(), Toast.LENGTH_SHORT).show();
            }
        };
    }
    public void LoadTimes(){

    }

    public Context getActivity() {
        return activity;
    }
}
