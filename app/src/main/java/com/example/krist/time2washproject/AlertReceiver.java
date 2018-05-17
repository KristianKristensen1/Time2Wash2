package com.example.krist.time2washproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import dialog.zoftino.com.dialog.MyDatePickerFragment;

//https://www.youtube.com/watch?v=yrpimdBRk5Q&list=PLrnPJCHvNZuDR7-cBjRXssxYK0Y5EEKzr&index=3
public class AlertReceiver extends BroadcastReceiver {
    NotificationHelper notificationHelper;
    String channelName;
    String channelTitle;
    String channelMessage;
    int channelImage;
    int indexID;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (notificationHelper == null) {
            notificationHelper = new NotificationHelper(context);
        }

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
                break;

            case "Done":
                channelName = "Channel Done";
                channelTitle = "All Done!";
                channelMessage = "Your Wash Time has now ended";
                channelImage = R.drawable.ic_happy;

                // TODO: 16-05-2018 Fjern booket tid (start her, flyt til service) 
                break;
        }
        MyDatePickerFragment.setIndexID(indexID + 1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationHelper.CreateChannels(channelIDs[indexID], channelName);
        }

        NotificationCompat.Builder builder = notificationHelper.getChannelNotification(channelIDs[indexID], channelTitle, channelMessage, channelImage);

        notificationHelper.getNotificationManager().notify(indexID, builder.build());
    }
}
