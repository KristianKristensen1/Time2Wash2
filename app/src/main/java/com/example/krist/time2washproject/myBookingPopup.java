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
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;


public class myBookingPopup extends Activity {

    private static final String TAG = "BookTime debug";
    Button btnOK;
    Button btnCancel;
    TextView tvTimeOfBooking;
    TextView tvNameOfMachine;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_booking_popup);
        btnCancel = findViewById(R.id.myBookingPopUp_Cancel_btn);
        btnOK = findViewById(R.id.myBookingPopUP_OK_btn);
        tvTimeOfBooking = findViewById(R.id.myBookingPopUp_timeOfBooking_tv);
        tvNameOfMachine = findViewById(R.id.myBookingPopUp_Machine_tv);

        Bundle extras = getIntent().getExtras();
        final WashingTime wt = (WashingTime) extras.getSerializable("testag");

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
                finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                removeTime(wt.getDate(),wt.getTime(),wt.getMachine());
            }
        });
    }
    public void removeTime(String chosenDate, String chosentime, String chosenMachine){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String docNameMachine = chosenDate + chosentime;
        final String docNameUser = chosenMachine + chosenDate + chosentime;
        DocumentReference BookingTimesRef = db.collection("washing_machines").document(chosenMachine).collection("BookedTimes").document(docNameMachine);
        BookingTimesRef.delete().addOnCompleteListener(new OnCompleteListener<Void>(){
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d(TAG,"Time deleted");
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    db.collection("users").document(currentUser.getEmail()).collection("MyTimes").document(docNameUser).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Time deleted from MyTimes");
                                finish();
                            }
                        }
                    });
                }else {
                    Log.d(TAG,"Something went wrong deleting time");
                }
            }
        });
    }
}
