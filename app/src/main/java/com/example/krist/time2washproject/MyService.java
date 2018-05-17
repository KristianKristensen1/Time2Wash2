package com.example.krist.time2washproject;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;

public class MyService extends Service{

    public static final String serviceTaskLoadBookedTimes = "loadingBookedTimes";
    public static final String serviceTaskLoadMachineNames = "loadingMachineNames";
    public static final String serviceTaskLoadMyTimes = "loadingMyTimes";
    public static final String serviceTaskBookTime = "bookTime";
    public static final String serviceTaskDeleteTime = "deleteTime";
    public static final String serviceDatabaseFail = "databaseFail";

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    AlarmSwitch alarmSwitch;


    private static final String TAG = "service debug";
    ArrayList machineList;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public class LocalBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }
    private final IBinder mBinder = new LocalBinder();
    ListenerRegistration eventListener;
    ArrayList<WashingTime> bookedTimes4Realz;
    ArrayList<WashingTime> myTimes;

    @Override
    public void onCreate() {
        alarmSwitch = new AlarmSwitch();
        bookedTimes4Realz = new ArrayList<>();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void loadMachines(){
        final ArrayList machineListTemp = new ArrayList();
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
                        machineListTemp.add(washingMachineTest.getName());

                    }
                }else{
                    BroadcastSender(serviceDatabaseFail);
                }
                machineList = machineListTemp;
                BroadcastSender(serviceTaskLoadMachineNames);
            }
        });
    }

    public void loadMyTimes(){
        final ArrayList<WashingTime> myTimesTemp = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference WashingMyTimeRef = db.collection("users").document(currentUser.getEmail()).collection("MyTimes"); // Kan bruges til at hente tider? og måske ens egne tider nested
        WashingMyTimeRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //Toast.makeText(getActivity(),"it happened" + document.getData().get("Name"),Toast.LENGTH_LONG).show();
                        //Log.d(TAG,document.getId() + "=>" + document.getData());
                        WashingTime washingTimeTest = document.toObject(WashingTime.class);
                        Log.d("Test","" + washingTimeTest.getMachine() + " " + washingTimeTest.getDate() + " " + washingTimeTest.getTime());
                        myTimesTemp.add(washingTimeTest);

                    }
                }else{
                    Log.d("Test", "something whent wrong getting myTimes");
                    BroadcastSender(serviceDatabaseFail);
                }
                myTimes = myTimesTemp;
                BroadcastSender(serviceTaskLoadMyTimes);
            }
        });
    }

    public void loadBookedTimes(String MachineName, String Date){
        Query bookedTimesQuery  = db.collection("washing_machines").document(MachineName).collection("BookedTimes");
        eventListener = bookedTimesQuery
                .whereEqualTo("Date",Date)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d(TAG, "Listen failed.", e);
                            BroadcastSender(serviceDatabaseFail);
                            return;
                        }
                        ArrayList<WashingTime> bookedTimes = new ArrayList<>();
                        for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                            Log.d(TAG,document.getId() + "=>" + document.getData());
                            WashingTime washingTime = document.toObject(WashingTime.class);
                            bookedTimes.add(washingTime);
                        }
                        bookedTimes4Realz = bookedTimes;
                        BroadcastSender(serviceTaskLoadBookedTimes);
                    }
                });
    }
    public void unRegisterEventlister(){
        eventListener.remove();
    }

    public void deleTimes(String chosenDate, String chosentime, final String chosenMachine){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String docNameMachine = chosenDate + chosentime;
        final String docNameUser = chosenMachine + chosenDate + chosentime;
        DocumentReference BookingTimesRef = db.collection("washing_machines").document(chosenMachine).collection("BookedTimes").document(docNameMachine);
        BookingTimesRef.delete().addOnCompleteListener(new OnCompleteListener<Void>(){
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d(TAG,"Time deleted");
                    db.collection("users").document(currentUser.getEmail()).collection("MyTimes").document(docNameUser).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Time deleted from MyTimes");
//                                alarmSwitch.cancelAlarm();
                                BroadcastSender(serviceTaskDeleteTime);
                            }
                        }
                    });
                }else {
                    Log.d(TAG,"Something went wrong deleting time");
                    BroadcastSender(serviceDatabaseFail);
                }
            }
        });
    }

    public void bookTime(String chosenDate, String chosentime, final String chosenMachine){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference BookingTimesRef = db.collection("washing_machines").document(chosenMachine).collection("BookedTimes");
        final Map<String, Object> BookedTime = new HashMap<>();
        BookedTime.put("Date", chosenDate);
        BookedTime.put("Time", chosentime);
        final String myTime = chosenMachine + chosenDate + chosentime;
        BookingTimesRef.document(chosenDate + chosentime).set(BookedTime).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Time has been booked");
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    BookedTime.put("Machine", chosenMachine);
                    db.collection("users").document(currentUser.getEmail()).collection("MyTimes").document(myTime).set(BookedTime).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Time is now in MyTimes aswell");
                                BroadcastSender(serviceTaskBookTime);
                            }
                        }
                    });
                } else {
                    Log.d(TAG, "Something went wrong booking time");
                    BroadcastSender(serviceDatabaseFail);
                }
            }
        });
    }

    public void BroadcastSender(String Result) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Result);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

}
