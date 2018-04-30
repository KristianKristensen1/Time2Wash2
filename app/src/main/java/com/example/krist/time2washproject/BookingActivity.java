package com.example.krist.time2washproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;

public class BookingActivity extends AppCompatActivity {
    String[] machineList = {"Vask1", "Vask2", "Tør1", "Tør2"}, dateList = {"Mandag", "Tirsdag", "Onsdag", "Torsdag", "Fredag", "Lørdag", "Søndag"};
    ArrayAdapter<String> machineArrayAdapter, dateArrayAdapter;
    MaterialBetterSpinner machineBetterSpinner, dateBetterSpinner;
    com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner bookingActivity_chooseMachine_dropDown, bookingActivity_chooseDate_dropDown;

    private WashingTimeAdaptor washingTimeAdaptor;
    private ListView washingTimeListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        findViews();
        setDropDowns();

        final ArrayList<WashingTime> WashingTimeArrayList = new ArrayList<>();
        for(int i = 0; i < 7; i++){
            WashingTimeArrayList.add(new WashingTime("This is the time", "01.05.2018"));
        }
        WashingTimeArrayList.set(0, new WashingTime("kl. 8-10", "01.05.2018", "com.example.krist.vaskemaskinetider.BOOK", 0));
        WashingTimeArrayList.set(1, new WashingTime("kl. 10-12", "01.05.2018","com.example.krist.vaskemaskinetider.BOOK", 1));
        WashingTimeArrayList.set(2, new WashingTime("kl. 12-14", "01.05.2018","com.example.krist.vaskemaskinetider.BOOK", 2));
        WashingTimeArrayList.set(3, new WashingTime("kl. 14-16", "01.05.2018","com.example.krist.vaskemaskinetider.BOOK", 3));
        WashingTimeArrayList.set(4, new WashingTime("kl. 16-18", "01.05.2018","com.example.krist.vaskemaskinetider.BOOK", 4));
        WashingTimeArrayList.set(5, new WashingTime("kl. 18-20", "01.05.2018","com.example.krist.vaskemaskinetider.BOOK", 5));
        WashingTimeArrayList.set(6, new WashingTime("kl. 20-22", "01.05.2018","com.example.krist.vaskemaskinetider.BOOK", 6));
        washingTimeAdaptor = new WashingTimeAdaptor(this, WashingTimeArrayList);
        washingTimeListView = findViewById(R.id.bookingActivity_availableTimes_listView);
        washingTimeListView.setAdapter(washingTimeAdaptor);
    }

    /*https://www.youtube.com/watch?v=x6HtXktAoew*/
    private void setDropDowns() {
        machineArrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, machineList);
        dateArrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, dateList);
        machineBetterSpinner = bookingActivity_chooseMachine_dropDown;
        dateBetterSpinner = bookingActivity_chooseDate_dropDown;
        machineBetterSpinner.setAdapter(machineArrayAdapter);
        dateBetterSpinner.setAdapter(dateArrayAdapter);
    }

    private void findViews() {
        bookingActivity_chooseMachine_dropDown = findViewById(R.id.bookingActivity_chooseMachine_dropDown);
        bookingActivity_chooseDate_dropDown = findViewById(R.id.bookingActivity_chooseDate_dropDown);
    }

    public void LoadTimes(){

    }
}
