package com.example.krist.time2washproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import dialog.zoftino.com.dialog.MyDatePickerFragment;

import static com.example.krist.time2washproject.LoginActivity.TAG;

//https://www.youtube.com/watch?v=yrpimdBRk5Q&list=PLrnPJCHvNZuDR7-cBjRXssxYK0Y5EEKzr&index=3
public class AlertReceiver extends BroadcastReceiver {
    NotificationHelper notificationHelper;
    ProfileActivity profileActivity;
    String channelName;
    String channelTitle;
    String channelMessage;
    int channelImage;
    int indexID;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();


    @Override
    public void onReceive(Context context, Intent intent) {
        if (notificationHelper == null) {
            notificationHelper = new NotificationHelper(context);
        }
            assert profileActivity != null;
            profileActivity.getApplicationContext();

        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        String machine = intent.getStringExtra("machine");
        String[] channelIDs = MyDatePickerFragment.getChannelID();

        indexID = MyDatePickerFragment.getIndexID();
        if (indexID > channelIDs.length-1) {
            indexID = 0;
        }

        switch (channelIDs[indexID]) {
            case "24":
                channelName = "Channel 24h";
                channelTitle = "24 Hour Warning";
                channelMessage = "Remember your Wash Time tomorrow";
                channelImage = R.drawable.ic_calender;
                break;

            case "1":
                channelName = "Channel 1h";
                channelTitle = "One Hour Warning";
                channelMessage = "Remember your Wash Time in 1 hour";
                channelImage = R.drawable.ic_clock;
                break;

            case "Now":
                channelName = "Channel Now";
                channelTitle = "Time2Wash!";
                channelMessage = "Your Wash Time begins now";
                channelImage = R.drawable.ic_wash;
                profileActivity.onBackPressed();
                break;

            case "Done":
                channelName = "Channel Done";
                channelTitle = "All Done!";
                channelMessage = "Your Wash Time has now ended";
                channelImage = R.drawable.ic_happy;
                deleTimes(date, time, machine);
                break;
        }
        MyDatePickerFragment.setIndexID(indexID + 1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationHelper.CreateChannels(channelIDs[indexID], channelName);
        }

        NotificationCompat.Builder builder = notificationHelper.getChannelNotification(channelIDs[indexID], channelTitle, channelMessage, channelImage);

        notificationHelper.getNotificationManager().notify(indexID, builder.build());
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

