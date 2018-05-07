package com.example.krist.time2washproject;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class BookTimePopup extends Activity {

    Button btnOK;
    Button btnCancel;
    TextView tvTimeOfBooking;
    TextView tvNameOfMachine;
    private static final String TAG = "BookTime debug";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_booking_popup);
        btnCancel = findViewById(R.id.myBookingPopUp_Cancel_btn);
        btnOK = findViewById(R.id.myBookingPopUP_OK_btn);
        tvTimeOfBooking = findViewById(R.id.myBookingPopUp_timeOfBooking_tv);
        tvNameOfMachine = findViewById(R.id.myBookingPopUp_Machine_tv);

        Bundle extras = getIntent().getExtras();
        final WashingTime wt = (WashingTime) extras.getSerializable("chosenWashTime");

        tvTimeOfBooking.setText(wt.getDate() + " " + wt.getTime());
        tvNameOfMachine.setText(wt.getMachine());

        //https://www.youtube.com/watch?v=fn5OlqQuOCk
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width*0.7), (int)(height*0.5));

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookTime(wt.getDate(),wt.getTime(),wt.getMachine());
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public void BookTime(String chosenDate, String chosentime, final String chosenMachine)//Sende machine og date og tid med?
    {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference BookingTimesRef = db.collection("washing_machines").document(chosenMachine).collection("BookedTimes");
        final Map<String, Object> BookedTime = new HashMap<>();
        BookedTime.put("Date",chosenDate);
        BookedTime.put("Time",chosentime);
        final String myTime = chosenMachine + chosenDate + chosentime;
        BookingTimesRef.document(chosenDate + chosentime).set(BookedTime).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d(TAG,"Time has been booked");
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    BookedTime.put("Machine",chosenMachine);
                    db.collection("users").document(currentUser.getEmail()).collection("MyTimes").document(myTime).set(BookedTime).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Log.d(TAG,"Time is now in MyTimes aswell");
                                finish();
                            }
                        }
                    });
                }else {
                    Log.d(TAG,"Something went wrong booking time");
                }
            }
        });
    }
}
