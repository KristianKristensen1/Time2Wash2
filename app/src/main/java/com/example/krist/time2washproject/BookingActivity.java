package com.example.krist.time2washproject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import dialog.zoftino.com.dialog.MyDatePickerFragment;

public class BookingActivity extends AppCompatActivity implements MyDatePickerFragment.DatePickerFragmentListener {
    ArrayAdapter<String> machineArrayAdapter, dateArrayAdapter;
    MaterialBetterSpinner machineBetterSpinner, dateBetterSpinner;
    Button bookingActivity_chooseDate_button;
    com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner bookingActivity_chooseMachine_dropDown, bookingActivity_chooseDate_dropDown;
    private  WashingTimeAdaptor washingTimeAdaptor;
    private ListView washingTimeListView;
    IntentFilter filter = new IntentFilter();

    static Context activity;
    //Til DB
    private static final String TAG = "bookingActivity debug";
    ArrayList machineList;
    ArrayList<WashingTime> bookedTimes;
    ArrayList<WashingTime> vacantTimes;
    String selectedMachineName;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    MyService myService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        machineList = new ArrayList();
        bookedTimes = new ArrayList<>();
        vacantTimes = new ArrayList<>();
        findViews();
        setDropDowns();
        updateListview();
        activity = BookingActivity.this;

        if (savedInstanceState != null) {
            date = savedInstanceState.getString("date");
            selectedMachineName = savedInstanceState.getString("machine");
        }

        bookingActivity_chooseDate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(v);

            }
        });

        machineBetterSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedMachineName = machineList.get(position).toString();
                myService.loadBookedTimes(selectedMachineName, date);
            }
        });
        washingTimeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent startMyBookingMenuIntent = new Intent(BookingActivity.this, BookTimePopup.class);
                WashingTime wt = vacantTimes.get(position);
                startMyBookingMenuIntent.putExtra("chosenWashTime", wt);
                startActivityForResult(startMyBookingMenuIntent, 1);
            }
        });
        filter.addAction(myService.serviceTaskLoadBookedTimes);
        filter.addAction(myService.serviceTaskLoadMachineNames);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if (resultCode == Activity.RESULT_OK){
                Toast.makeText(this, "Time was booked", Toast.LENGTH_LONG);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (myService == null){
            Intent binderIntent = new Intent(this, MyService.class);
            bindService(binderIntent, mConnection, Context.BIND_AUTO_CREATE);
            LocalBroadcastManager.getInstance(this).registerReceiver(onBackgroundServiceResult, filter);
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        myService.unRegisterEventlister();
    }

    /*https://www.youtube.com/watch?v=x6HtXktAoew*/
    private void setDropDowns() {
        machineArrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, machineList);
        machineBetterSpinner = bookingActivity_chooseMachine_dropDown;
        dateBetterSpinner = bookingActivity_chooseDate_dropDown;
        machineBetterSpinner.setAdapter(machineArrayAdapter);
    }

    private void findViews() {
        bookingActivity_chooseMachine_dropDown = findViewById(R.id.bookingActivity_chooseMachine_dropDown);
        bookingActivity_chooseDate_button = findViewById(R.id.bookingActivity_chooseDate_button);
        washingTimeListView = findViewById(R.id.bookingActivity_availableTimes_listView);
    }

    //http://www.zoftino.com/android-datepicker-example
    public void showDatePicker(View v) {
        MyDatePickerFragment myFragment = MyDatePickerFragment.newInstance(this);
        myFragment.show(getSupportFragmentManager(), "date picker");
    }

    //Creates a list with all times for the day and machine and removes the booked times.
    public void showVacantTimes(){
        ArrayList<WashingTime> WashingTimeArrayList = new ArrayList<>();
        for(int i = 0; i < 7; i++){
            WashingTimeArrayList.add(new WashingTime("This is the time", "01.05.2018", "Machine1"));
        }
        WashingTimeArrayList.set(0, new WashingTime("kl. 8-10", date, selectedMachineName, 0));
        WashingTimeArrayList.set(1, new WashingTime("kl. 10-12", date,selectedMachineName, 1));
        WashingTimeArrayList.set(2, new WashingTime("kl. 12-14", date,selectedMachineName, 2));
        WashingTimeArrayList.set(3, new WashingTime("kl. 14-16", date,selectedMachineName, 3));
        WashingTimeArrayList.set(4, new WashingTime("kl. 16-18", date,selectedMachineName, 4));
        WashingTimeArrayList.set(5, new WashingTime("kl. 18-20", date,selectedMachineName, 5));
        WashingTimeArrayList.set(6, new WashingTime("kl. 20-22", date,selectedMachineName, 6));
        for (int i = 0; i<bookedTimes.size(); i++){
            for (int j = 0; j<WashingTimeArrayList.size(); j++){
                if (bookedTimes.get(i).getTime().equals(WashingTimeArrayList.get(j).getTime()))
                    WashingTimeArrayList.remove(j);
            }
        }
        vacantTimes = WashingTimeArrayList;

    }

    public void updateListview(){
        ArrayList<WashingTime> WashingTimeArrayList = vacantTimes;
        washingTimeAdaptor = new WashingTimeAdaptor(this, WashingTimeArrayList);
        washingTimeListView.setAdapter(washingTimeAdaptor);
    }

    public static Context getActivity() {
        return activity;
    }

    // This method will be called with the date from the `DatePicker`.
    @Override
    public void onDateSet(String Date) {
        date = Date;
        myService.loadBookedTimes(selectedMachineName, date);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            myService = binder.getService();
            myService.loadMachines();
            if (date != null && selectedMachineName != null){
                myService.loadBookedTimes(selectedMachineName, date);
            }

        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    private BroadcastReceiver onBackgroundServiceResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String result = intent.getAction();
            if(result==null){
                //Handle error
            }
            if (myService.machineList != null){
                handleBackgroundResult(result);}
        }
    };
    private void handleBackgroundResult(String result){

        if (result == myService.serviceTaskLoadBookedTimes){
            bookedTimes = myService.bookedTimes4Realz;
            showVacantTimes();
            updateListview();
        }
        if (result == myService.serviceTaskLoadMachineNames){
            machineList = myService.machineList;
            if (selectedMachineName == null){
                selectedMachineName = machineList.get(0).toString();
            }
            setDropDowns();
            showVacantTimes();
            updateListview();
            myService.loadBookedTimes(selectedMachineName, date);
        }
        if (result == myService.serviceDatabaseFail){
            Toast.makeText(this,"Something went wrong, please try again", Toast.LENGTH_LONG);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("date", date);
        outState.putString("machine", selectedMachineName);
        super.onSaveInstanceState(outState);
    }
}
