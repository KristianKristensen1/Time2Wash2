package com.example.krist.time2washproject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //lav lokale vaskemaskine objekter?

        CollectionReference WashingMyTimeRef = db.collection("users").document(currentUser.getEmail()).collection("MyTimes"); // Kan bruges til at hente tider? og måske ens egne tider nested
        WashingMyTimeRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //Toast.makeText(getActivity(),"it happened" + document.getData().get("Name"),Toast.LENGTH_LONG).show();
                        //Log.d(TAG,document.getId() + "=>" + document.getData());
                        WashingTime washingTimeTest = document.toObject(WashingTime.class);
                        Log.d("Test","" + washingTimeTest.getMachine() + " " + washingTimeTest.getDate() + " " + washingTimeTest.getTime());
                        myTimes.add(washingTimeTest);

                    }
                }else{
                    Log.d("Test", "something whent wrong getting myTimes");
                }
                washingTimeAdaptor.notifyDataSetChanged();
            }
        });

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
                //startActivity(new Intent(ProfileActivity.this, myBookingPopup.class));
            }
        });


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
