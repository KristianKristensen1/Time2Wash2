package com.example.krist.time2washproject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;


public class myBookingPopup extends Activity {

    private static final String TAG = "BookTime debug";
    Button btnOK;
    Button btnCancel;
    TextView tvTimeOfBooking;
    TextView tvNameOfMachine;
    AlarmSwitch alarmSwitch;

    IntentFilter filter = new IntentFilter();
    MyService myService;
    WashingTime wt;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_booking_popup);
        btnCancel = findViewById(R.id.myBookingPopUp_Cancel_btn);
        btnOK = findViewById(R.id.myBookingPopUP_OK_btn);
        tvTimeOfBooking = findViewById(R.id.myBookingPopUp_timeOfBooking_tv);
        tvNameOfMachine = findViewById(R.id.myBookingPopUp_Machine_tv);
        alarmSwitch = new AlarmSwitch();

        Bundle extras = getIntent().getExtras();
        wt = (WashingTime) extras.getSerializable("testag");

        tvTimeOfBooking.setText(wt.getDate() + " " + wt.getTime());
        tvNameOfMachine.setText(wt.getMachine());

        //Scales popup window. Inspired from:
        //https://www.youtube.com/watch?v=fn5OlqQuOCk
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width*0.7), (int)(height*0.5));

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //Deletes booked time
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myService.deleteTimes(wt.getDate(), wt.getTime(), wt.getMachine());
            }
        });
    }

    //Binds to service
    @Override
    protected void onStart() {
        super.onStart();
        filter.addAction(myService.serviceTaskDeleteTime);
        if (myService == null){
            Intent binderIntent = new Intent(this, MyService.class);
            bindService(binderIntent, mConnection, Context.BIND_AUTO_CREATE);
            LocalBroadcastManager.getInstance(this).registerReceiver(onBackgroundServiceResult, filter);
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            myService = binder.getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    //Receives broadcasts from service and handles result depending on type.
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
        if (result == myService.serviceTaskDeleteTime){
            finish();
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
}
