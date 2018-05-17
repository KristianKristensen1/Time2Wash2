package com.example.krist.time2washproject;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    TextView etUsername;

    private FirebaseAuth mAuth;
    ArrayList<WashingTime> myTimes = new ArrayList<>();
    private WashingTimeAdaptor washingTimeAdaptor;
    private ListView washingTimeListView;
    MyService myService;
    Button btBook;
    FirebaseUser currentUser;
    IntentFilter filter = new IntentFilter();
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        etUsername = findViewById(R.id.profileActivity_userName_textView);
        btBook = findViewById(R.id.profileActivity_book_time_button);
        etUsername.setText(currentUser.getDisplayName());
        progressBar = findViewById(R.id.profileActivity_progressBar);

        washingTimeAdaptor = new WashingTimeAdaptor(this, myTimes);
        washingTimeListView = findViewById(R.id.profileActivity_myTimes_listView);
        washingTimeListView.setAdapter(washingTimeAdaptor);

        //ListView with overview of the different cities. Inspired from lab exercise 4.
        washingTimeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent startMyBookingMenuIntent = new Intent(ProfileActivity.this, myBookingPopup.class);
                WashingTime wt = myTimes.get(position);
                startMyBookingMenuIntent.putExtra("testag", wt);
                startActivity(startMyBookingMenuIntent);
            }
        });
        btBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(ProfileActivity.this, BookingActivity.class);
                startActivity(profileIntent);
            }
        });
        filter.addAction(myService.serviceTaskLoadMyTimes);

        new ProgressBarClass().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (myService == null){
            Intent binderIntent = new Intent(this, MyService.class);
            bindService(binderIntent, mConnection, Context.BIND_AUTO_CREATE);
            LocalBroadcastManager.getInstance(this).registerReceiver(onBackgroundServiceResult, filter);
        }else{
            myService.loadMyTimes();
        }
    }

    /*https://www.youtube.com/watch?v=dvWrniwBJUw*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                break;

            case R.id.Change:
        }
        return true;
    }

    //https://developer.android.com/guide/components/bound-services.html
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            myService = binder.getService();
            myService.loadMyTimes();
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    //https://stackoverflow.com/questions/14001963/finish-all-activities-at-a-time
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }

    private BroadcastReceiver onBackgroundServiceResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getAction();
            if(result==null){
                //Handle error
            }
                handleBackgroundResult(result);}
    };
    private void handleBackgroundResult(String result){

        if (result == myService.serviceTaskLoadMyTimes){
            myTimes = myService.myTimes;
            washingTimeAdaptor.washingTimes = myTimes;
            washingTimeAdaptor.notifyDataSetChanged();
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

    class ProgressBarClass extends AsyncTask<Void, Integer, Integer>
    {
        int minuteInMilli = 1000;
        int numberOfMinutes = 120;
        //String timeLeft;
        int minutesLeft;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressBar.setMax(numberOfMinutes);
        }

        @Override
        protected void onProgressUpdate(Integer... values){
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            for (int i=0; i<numberOfMinutes;i++){
                publishProgress(i);

                minutesLeft = numberOfMinutes - i;

                /*timeLeft = (String.valueOf(minutesLeft) + " minutes left");
                textView.setText(timeLeft);*/

                try{
                    Thread.sleep(minuteInMilli);
                }
                catch (InterruptedException ie){
                    ie.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(), "Your Wash Time has now ended.", Toast.LENGTH_LONG).show();
        }
    }
}
