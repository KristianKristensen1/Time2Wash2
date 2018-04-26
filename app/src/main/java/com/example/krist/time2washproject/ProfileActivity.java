package com.example.krist.time2washproject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    TextView etUsername;

    private FirebaseAuth mAuth;
    ArrayList<WashingTime> myTimes;
    private WashingTimeAdaptor washingTimeAdaptor;
    private ListView washingTimeListView;
    MyService myService;
    Button btBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Intent intent = new Intent(ProfileActivity.this, MyService.class);
        startService(intent);


        etUsername = findViewById(R.id.profileActivity_userName_textView);
        btBook = findViewById(R.id.profileActivity_book_time_button);

        etUsername.setText(currentUser.getEmail());

        Intent binderIntent = new Intent(this, MyService.class);
        bindService(binderIntent, mConnection, Context.BIND_AUTO_CREATE);

        /*
        washingTimeAdaptor = new WashingTimeAdaptor(this, myService.getMyTimes());
        washingTimeListView = findViewById(R.id.profileActivity_myTimes_listView);
        washingTimeListView.setAdapter(washingTimeAdaptor);
        */

        btBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(ProfileActivity.this, BookingActivity.class);
                startActivity(profileIntent);
            }
        });

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
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };
}
