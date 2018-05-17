package com.example.krist.time2washproject;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

public class ProgressBarService extends Service {

    public static final String serviceTaskProgressUpdate = "ProgressUpdate";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    int minuteInMilli = 1000;
    int numberOfMinutes = 120;
    //String timeLeft;
    int minutesLeft;
    int value;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        task.execute();
        return START_STICKY;
    }

    public void BroadcastSender(String Result) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Result);
        broadcastIntent.putExtra("maxValue",numberOfMinutes);
        broadcastIntent.putExtra("valueNow", value);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    //private void doBackgroundThing() {
        AsyncTask<Void, Integer, Integer> task = new AsyncTask<Void, Integer, Integer>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //progressBar.setMax(numberOfMinutes);
                //sæt 120 min til
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                //progressBar.setProgress(values[0]);
                value = values[0];
            }

            @Override
            protected Integer doInBackground(Void... voids) {
                for (int i = 0; i < numberOfMinutes; i++) {
                    publishProgress(i);
                    minutesLeft = numberOfMinutes - i;

            /*timeLeft = (String.valueOf(minutesLeft) + " minutes left");
            textView.setText(timeLeft);*/

                    try {
                        BroadcastSender(serviceTaskProgressUpdate);
                        Thread.sleep(minuteInMilli);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer result) {
                super.onPostExecute(result);
                Toast.makeText(getApplicationContext(), "Your Wash Time has now ended.", Toast.LENGTH_LONG).show();
            }
        };

    //}
}

