package com.example.divers;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.divers.fragment.AboutFragment;
import com.example.divers.fragment.AdminListFragment;
import com.example.divers.fragment.Alert;
import com.example.divers.fragment.HeartBeat;
//import com.example.divers.fragment.PressureFragment;
import com.example.divers.fragment.Oximeter;
import com.example.divers.fragment.SettingsFragment;
import com.example.divers.fragment.Temperature;
import com.example.divers.ref.admin;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements
       BottomNavigationView
                   .OnNavigationItemSelectedListener {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    Intent mServiceIntent;
    boolean check;
    BottomNavigationView bottomNavigationView;
   // private pushnotifbackground mYourService;

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private   View headerView;
    SharedPreferences sharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    Fragment fragment;
    String names;
    String urls;

    FirebaseFirestore db;


    //notif
    private TextView spo2_value,heartbeat_value,temperature_value;
    private ValueEventListener valueEventListener;
    String  path  ;
    String uid;



    //progress dialog
    private ProgressDialog progressDialog;


    @Override
    public void onBackPressed() {

        if(mDrawer.isOpen()){
            mDrawer.close();
        }else{
            finishAffinity();
        }
    }

    @Override
    protected void onDestroy() {
        //    stopService(mServiceIntent);
        //    Intent broadcastIntent = new Intent();
        //   broadcastIntent.setAction("restartservice");
        //   broadcastIntent.setClass(this, Restarter.class);
        //   this.sendBroadcast(broadcastIntent);
        super.onDestroy();

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //   mYourService = new pushnotifbackground();
        //    mServiceIntent = new Intent(this, mYourService.getClass());
        //    if (!isMyServiceRunning(mYourService.getClass())) {
        //       startService(mServiceIntent);
        //  }
        admin.firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView
                .setOnNavigationItemSelectedListener(MainActivity.this);
        bottomNavigationView.setSelectedItemId(R.id.heart);


        progressDialog = new ProgressDialog(this);



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // This will display an Up icon (<-), we will replace it with hamburger later
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);



        nvDrawer = (NavigationView) findViewById(R.id.nvView);
         headerView = nvDrawer.getHeaderView(0);





         //for notif//**
        DatabaseReference heartbeathRef, oximeterRef, temperatureRef;



        spo2_value = findViewById(R.id.oximeter_value);
        heartbeat_value =findViewById(R.id.heartbeat_value);
        temperature_value = findViewById(R.id.temperature_value);


        //for notif//**



        getDateforNav();

        // Setup drawer view
        setupDrawerContent(nvDrawer);




        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawer,
                toolbar,

                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        mDrawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
        if(admin.check){
           fragment = new AdminListFragment();

        }else {
             fragment = new HeartBeat();}

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        nvDrawer.setCheckedItem(R.id.nav_home);
        setTitle("Home");
        if(admin.check){
            bottomNavigationView.setVisibility(View.GONE);
        }















        //data from db : notification //



        uid = admin.firebaseAuth.getCurrentUser().getUid();


        try {
            path = getIntent().getStringExtra("path");
            if (path != null) {
                uid = path;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



        heartbeathRef = FirebaseDatabase.getInstance().getReference().child("user").child(uid).child("beatsPerMinute");
        oximeterRef = FirebaseDatabase.getInstance().getReference().child("user").child(uid).child("spO2");
        temperatureRef=FirebaseDatabase.getInstance().getReference().child("user").child(uid).child("temperature");


        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child(uid);





        //hearbeat value from fbdb

        //database.child("dataoximeter").addValueEventListener(valueEventListener);


        heartbeathRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int value = dataSnapshot.getValue(Integer.class);
                    //heartbeat_value.setText(String.valueOf(value)+" BPM");

                    if(value < 50 ||value > 90){


                        sendNotification("warning !","Abnormal heartbeat");
                        Fragment targetFragment = new Alert();

                        FragmentManager fragmentManager = getSupportFragmentManager();
                        Fragment existingFragment = fragmentManager.findFragmentById(R.id.flContent);

                        if (existingFragment != null && existingFragment instanceof Alert) {
                            // Fragment is already added
                            // You can access its instance and perform any necessary actions
                            Alert alertFragment = (Alert) existingFragment;
                            // Use the alertFragment instance as needed
                            // For example, you can call methods or access its fields

                            // Show a message indicating the fragment is already opened
                        } else {
                            // Fragment is not added, replace it with the target fragment
                            fragmentManager.beginTransaction()
                                    .replace(R.id.flContent, targetFragment)
                                    .commit();

                            bottomNavigationView.setSelectedItemId(R.id.alert);
                        }


                    }
                }else{
                    //heartbeathRef.setValue(1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });




        //oximeter value from fbdb


        // Attach ValueEventListener for oximeter
        oximeterRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    double value = dataSnapshot.getValue(double.class);
                    //spo2_value.setText(String.valueOf(value)+" Spo2");

                    if(value < 90){


                        sendNotification("warning !","Abnormal oxygen rate");

                        sendNotification("warning !","Abnormal heartbeat");
                        Fragment targetFragment = new Alert();

                        FragmentManager fragmentManager = getSupportFragmentManager();
                        Fragment existingFragment = fragmentManager.findFragmentById(R.id.flContent);

                        if (existingFragment != null && existingFragment instanceof Alert) {
                            // Fragment is already added
                            // You can access its instance and perform any necessary actions
                            Alert alertFragment = (Alert) existingFragment;
                            // Use the alertFragment instance as needed
                            // For example, you can call methods or access its fields

                            // Show a message indicating the fragment is already opened
                        } else {
                            // Fragment is not added, replace it with the target fragment
                            fragmentManager.beginTransaction()
                                    .replace(R.id.flContent, targetFragment)
                                    .commit();

                            bottomNavigationView.setSelectedItemId(R.id.alert);
                        }


                    }

                }else {
                    //oximeterRef.setValue(1.0);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });


        //temperature value from fbdb

        temperatureRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    double value = dataSnapshot.getValue(double.class);
                    //temperature_value.setText(String.valueOf(value)+" °C");

                    if(value<36 || value > 38){


                        sendNotification("warning !","Abnormal body temperature");

                        sendNotification("warning !","Abnormal heartbeat");
                        Fragment targetFragment = new Alert();

                        FragmentManager fragmentManager = getSupportFragmentManager();
                        Fragment existingFragment = fragmentManager.findFragmentById(R.id.flContent);

                        if (existingFragment != null && existingFragment instanceof Alert) {
                            // Fragment is already added
                            // You can access its instance and perform any necessary actions
                            Alert alertFragment = (Alert) existingFragment;
                            // Use the alertFragment instance as needed
                            // For example, you can call methods or access its fields

                            // Show a message indicating the fragment is already opened
                        } else {
                            // Fragment is not added, replace it with the target fragment
                            fragmentManager.beginTransaction()
                                    .replace(R.id.flContent, targetFragment)
                                    .commit();

                            bottomNavigationView.setSelectedItemId(R.id.alert);
                        }




                    }


                }else {
                    //temperatureRef.setValue(1.0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });






    }       //on Create

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        if (item.getItemId() == R.id.nav_logout) {//  mDrawer.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }


    @SuppressLint("NonConstantResourceId")
    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = new HeartBeat();
        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_home:
                if(!admin.check){
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    fragmentClass = HeartBeat.class;
                }else {
                    fragmentClass = AdminListFragment.class;
                }

                break;
            case R.id.nav_logout:
                userLogout();
                //   fragmentClass = homeeFragment.class;


                //  break;
            case R.id.nav_settings:
                bottomNavigationView.setVisibility(View.GONE);

                fragmentClass = SettingsFragment.class;
                break;
            case R.id.nav_about:
                bottomNavigationView.setVisibility(View.GONE);
                fragmentClass = AboutFragment.class;

                break;

            default:
                if(!admin.check){
                    fragmentClass = HeartBeat.class;
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }else {
                    fragmentClass = AdminListFragment.class;
                }

        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            if(!admin.check){
            fragment = new HeartBeat();}
            else {
                fragment = new AdminListFragment();
            }
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.syncState();
    }

    HeartBeat heartBeat = new HeartBeat();
    Oximeter oximeter = new Oximeter();
    Temperature temperature = new Temperature();
    Alert alert = new Alert();

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.heart:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flContent, heartBeat)
                        .commit();
                return true;

            case R.id.oximeter:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flContent, oximeter)
                        .commit();
                return true;

            case R.id.temperature:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flContent, temperature)
                        .commit();
                return true;

            case R.id.alert:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flContent, alert)
                        .commit();
                return true;

            default:
                return false;
        }
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    void getDateforNav(){


        FirebaseUser currentUser = admin.firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            getDataFromPref("name","url");
            if(check){
              //  getDataFromPref(names,urls);
                setData(names,urls);
            }else {
                getdb(uid);
            }


        }else {
            Toast.makeText(MainActivity.this,"null uid warnning",Toast.LENGTH_SHORT).show();
        }

    }
    void  setData(String name , String url){
        TextView nameTextView = headerView.findViewById(R.id.myname);
        nameTextView.setText(name);

        // Load the image into a circular ImageView in the navigation header
        ImageView imageView = headerView.findViewById(R.id.myimage);
        Glide.with(getApplicationContext())
                .load(url)
                .circleCrop()
                .into(imageView);

    }
   void getDataFromPref(String name , String url){

        if( !sharedPreferences.contains("name") && !sharedPreferences.contains("url")){
            check = false;

        }else {
              names  = sharedPreferences.getString(name,"noname");
             urls =sharedPreferences.getString(url,"nourl");
            check = true;
        }}

   void getdb(String uid) {
           DocumentReference userRef = db.collection("users").document(uid);
           userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
               @Override
               public void onSuccess(DocumentSnapshot documentSnapshot) {
                   if (documentSnapshot.exists()) {
                       String name = documentSnapshot.getString("fullName");
                       String imageUrl = documentSnapshot.getString("url");
                       String email = documentSnapshot.getString("email");
                       String phone = documentSnapshot.getString("phone");
                       setData(name,imageUrl);
                       SaveData("name",name);
                       SaveData("url",imageUrl);
                       SaveData("phone",phone);
                       SaveData("email", email);




                   }else {
                       Toast.makeText(MainActivity.this, "there no Data warning", Toast.LENGTH_SHORT).show();
                   }
               }
           });
       }
    void SaveData(String key,String data){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,data);
        editor.apply();

    }
    void ClearPref(){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }






    private void userLogout() {

        ClearPref();
        //deconnect the user
        FirebaseAuth.getInstance().signOut();
        admin.firebaseAuth.signOut();

        progressDialog.setMessage("Logout..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the login activity
                Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
                startActivity(intent);
                progressDialog.dismiss();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },200);
            }
        },2500);
    }












    //alert notification and data change //





    //send notifications function
    private void sendNotification(String title, String content) {




            String NOTIFICATION_CHANNEL_ID = "SmartDrive_multiple_locations";
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,"My Notification"
                        ,NotificationManager.IMPORTANCE_DEFAULT);


                //config
                notificationChannel.setDescription("Channel description");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);

            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this,NOTIFICATION_CHANNEL_ID);
            builder.setContentTitle(title)
                    .setContentText(content)
                    .setAutoCancel(false)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));

            Notification notification = builder.build();
            notificationManager.notify(new Random().nextInt(),notification);



    }       //notification end function





}

