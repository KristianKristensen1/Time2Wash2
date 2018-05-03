package com.example.krist.time2washproject;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class myBookingPopup extends Activity {

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
        WashingTime wt = (WashingTime) extras.getSerializable("testag");

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
    }
}
