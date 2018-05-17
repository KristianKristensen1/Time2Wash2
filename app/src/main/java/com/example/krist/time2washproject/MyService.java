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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.crypto.Mac;

public class MyService extends Service{

    public static final String serviceTaskLoadBookedTimes = "loadingBookedTimes";
    public static final String serviceTaskLoadMachineNames = "loadingMachineNames";
    public static final String serviceTaskLoadMyTimes = "loadingMyTimes";


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
                    //BroadCast
                }
                machineList = machineListTemp;
                BroadcastSender(serviceTaskLoadMachineNames);
            }
        });
    }

    public void loadMyTimes(String Email){
        final ArrayList<WashingTime> myTimesTemp = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference WashingMyTimeRef = db.collection("users").document(Email).collection("MyTimes"); // Kan bruges til at hente tider? og måske ens egne tider nested
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
                        //Broadcast list
                    }
                });
    }

    public void deleTimes(){

    }

    public void BroadcastSender(String Result) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Result);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

}
