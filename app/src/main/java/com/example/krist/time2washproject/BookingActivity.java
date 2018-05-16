package com.example.krist.time2washproject;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
    ListenerRegistration eventListener;

    static Context activity;
    //Til DB
    private static final String TAG = "bookingActivity debug";
    ArrayList machineList;
    ArrayList<WashingTime> bookedTimes;
    ArrayList<WashingTime> vacantTimes;
    String selectedMachineName;
    //String selectedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());


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
                LoadTimes();
            }
        });
        washingTimeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent startMyBookingMenuIntent = new Intent(BookingActivity.this, BookTimePopup.class);
                WashingTime wt = vacantTimes.get(position);
                startMyBookingMenuIntent.putExtra("chosenWashTime", wt);
                startActivity(startMyBookingMenuIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Test linjer til DB
        //lav lokale vaskemaskine objekter?

        CollectionReference WashingMachineRef = db.collection("washing_machines"); // Kan bruges til at hente tider? og måske ens egne tider nested
        WashingMachineRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //Toast.makeText(getActivity(),"it happened" + document.getData().get("Name"),Toast.LENGTH_LONG).show();
                        Log.d(TAG,document.getId() + "=>" + document.getData());
                        WashingMachine washingMachineTest = document.toObject(WashingMachine.class);
                        Log.d(TAG,"" + washingMachineTest.getName());
                        machineList.add(washingMachineTest.getName());

                    }
                    selectedMachineName = machineList.get(0).toString();
                }else{
                    Toast.makeText(getActivity(),"something went wrong",Toast.LENGTH_LONG);
                }
                setDropDowns();
                LoadTimes();

            }
        });
        //Query washQuery = WashingMachineRef.whereEqualTo("InUse",true);
        //String[] machineListTest = washQuery.get().getResult().getDocuments().toArray();

    }

    @Override
    protected void onStop(){
        super.onStop();
        eventListener.remove();
    }


        @Override
    protected void onResume() {
        super.onResume();
        if(selectedMachineName!=null){
            LoadTimes();
        }
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
        washingTimeListView = findViewById(R.id.bookingActivity_availableTimes_listView);
        //bookingActivity_chooseDate_dropDown = findViewById(R.id.bookingActivity_chooseDate_dropDown);
    }

    //http://www.zoftino.com/android-datepicker-example
    public void showDatePicker(View v) {
        //MyDatePickerFragment myFragment = new MyDatePickerFragment();
        MyDatePickerFragment myFragment = MyDatePickerFragment.newInstance(this);
        myFragment.show(getSupportFragmentManager(), "date picker");
    }
    public void LoadTimes(){
        Query bookedTimesQuery  = db.collection("washing_machines").document(selectedMachineName).collection("BookedTimes");
        eventListener = bookedTimesQuery
                .whereEqualTo("Date",date)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d(TAG, "Listen failed.", e);
                            return;
                        }
                        bookedTimes = new ArrayList<>();
                        for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                            Log.d(TAG,document.getId() + "=>" + document.getData());
                            WashingTime washingTime = document.toObject(WashingTime.class);
                            bookedTimes.add(washingTime);
                        }
                        showVacantTimes();
                        updateListview();
                    }
                });

    }


    public void showVacantTimes(){
        ArrayList<WashingTime> WashingTimeArrayList = new ArrayList<>();
        //Hardcoded list with My booked times for test
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
        //Hardcoded list with My booked times for test

        washingTimeAdaptor = new WashingTimeAdaptor(this, WashingTimeArrayList);
        //washingTimeListView = findViewById(R.id.bookingActivity_availableTimes_listView);
        washingTimeListView.setAdapter(washingTimeAdaptor);

        // washingTimeAdaptor = new WashingTimeAdaptor(this, WashingTimeArrayList);
        // washingTimeListView = findViewById(R.id.bookingActivity_availableTimes_listView);
        // washingTimeListView.setAdapter(washingTimeAdaptor);
    }

    public static Context getActivity() {
        return activity;
    }

    @Override
    public void onDateSet(String Date) {
        date = Date;
        LoadTimes();
        // This method will be called with the date from the `DatePicker`.
    }
}
