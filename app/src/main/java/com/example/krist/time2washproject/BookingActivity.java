package com.example.krist.time2washproject;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.Date;

import dialog.zoftino.com.dialog.MyDatePickerFragment;

public class BookingActivity extends AppCompatActivity {
    ArrayAdapter<String> machineArrayAdapter, dateArrayAdapter;
    MaterialBetterSpinner machineBetterSpinner, dateBetterSpinner;
    Button bookingActivity_chooseDate_button;
    com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner bookingActivity_chooseMachine_dropDown, bookingActivity_chooseDate_dropDown;
    private  WashingTimeAdaptor washingTimeAdaptor;
    private ListView washingTimeListView;
    Context activity;
    //Til DB
    private static final String TAG = "bookingActivity debug";
    ArrayList machineList;
    String selectedMachineName;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        machineList = new ArrayList();
        findViews();
        setDropDowns();

        final ArrayList<WashingTime> WashingTimeArrayList = new ArrayList<>();
        //Hardcoded list with My booked times for test
        for(int i = 0; i < 7; i++){
            WashingTimeArrayList.add(new WashingTime("This is the time", "01.05.2018", "Machine1"));
        }
        WashingTimeArrayList.set(0, new WashingTime("kl. 8-10", "01.05.2018", "Machine1", 0));
        WashingTimeArrayList.set(1, new WashingTime("kl. 10-12", "01.05.2018","Machine1", 1));
        WashingTimeArrayList.set(2, new WashingTime("kl. 12-14", "01.05.2018","Machine1", 2));
        WashingTimeArrayList.set(3, new WashingTime("kl. 14-16", "01.05.2018","Machine1", 3));
        WashingTimeArrayList.set(4, new WashingTime("kl. 16-18", "01.05.2018","Machine1", 4));
        WashingTimeArrayList.set(5, new WashingTime("kl. 18-20", "01.05.2018","Machine1", 5));
        WashingTimeArrayList.set(6, new WashingTime("kl. 20-22", "01.05.2018","Machine1", 6));
        washingTimeAdaptor = new WashingTimeAdaptor(this, WashingTimeArrayList);
        washingTimeListView = findViewById(R.id.bookingActivity_availableTimes_listView);
        washingTimeListView.setAdapter(washingTimeAdaptor);

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

        Query WashingMachineRef = db.collection("washing_machines").whereEqualTo("Name",selectedMachineName).whereEqualTo("Date", "13/05/2017"); // Kan bruges til at hente tider? og måske ens egne tider nested
        WashingMachineRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //Toast.makeText(getActivity(),"it happened" + document.getData().get("Name"),Toast.LENGTH_LONG).show();
                        Log.d(TAG,document.getId() + "=>" + document.getData());
                        WashingMachine washingMachineTest = document.toObject(WashingMachine.class);
                        Log.d(TAG,"" + washingMachineTest.getName());
                        //machineList.add(washingMachineTest.getName());

                    }
                }else{
                    Toast.makeText(getActivity(),"something went wrong",Toast.LENGTH_LONG);
                }

            }
        });
    }

    public Context getActivity() {
        return activity;
    }
}
