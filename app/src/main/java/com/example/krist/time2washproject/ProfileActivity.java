package com.example.krist.time2washproject;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();


        etUsername = findViewById(R.id.profileActivity_userName_textView);

        etUsername.setText(currentUser.getEmail());

        washingTimeAdaptor = new WashingTimeAdaptor(this, myService.getMyTimes());
        washingTimeListView = findViewById(R.id.profileActivity_myTimes_listView);
        washingTimeListView.setAdapter(washingTimeAdaptor);

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
