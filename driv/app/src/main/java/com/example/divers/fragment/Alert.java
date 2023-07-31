package com.example.divers.fragment;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.divers.R;
import com.example.divers.ref.admin;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.atomic.AtomicBoolean;


public class Alert extends Fragment {



    private ValueEventListener valueEventListener;
    String  path  ;
    String uid;


    private TextView spo2_value,heartbeat_value,temperature_value;



    public static boolean tempWarning=false;
    public static boolean oxyWarning=false;
    public static boolean heartWarning=false;

    LinearLayout no_alert_layout,alert_layout;

    public Alert() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.alert, container, false);

        DatabaseReference heartbeathRef, oximeterRef, temperatureRef;



        spo2_value = view.findViewById(R.id.oximeter_value);
        heartbeat_value =view.findViewById(R.id.heartbeat_value);
        temperature_value = view.findViewById(R.id.temperature_value);


        //warnings alert
        no_alert_layout = view.findViewById(R.id.no_alert_layout);
        alert_layout = view.findViewById(R.id.alert_layout);




        uid = admin.firebaseAuth.getCurrentUser().getUid();

        try{
            path =getArguments().getString("path");
            if (path != null){
                uid = path ;

            }
        }
        catch (Exception e){

        }


        heartbeathRef = FirebaseDatabase.getInstance().getReference().child("user").child(uid).child("beatsPerMinute");
        oximeterRef = FirebaseDatabase.getInstance().getReference().child("user").child(uid).child("spO2");
        temperatureRef=FirebaseDatabase.getInstance().getReference().child("user").child(uid).child("temperature");


        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child(uid);





        //hearbeat value from fbdb

        //database.child("dataoximeter").addValueEventListener(valueEventListener);


        heartbeathRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int value = dataSnapshot.getValue(Integer.class);
                    heartbeat_value.setText(String.valueOf(value)+" BPM");


                    if(value < 50 ||value > 90){

                        if (isAdded()) {
                            showWarning("Abnormal heartbeat");
                            heartbeat_value.setTextColor(getResources().getColor(R.color.red));
                        }

                        alert_layout.setVisibility(View.VISIBLE);
                        no_alert_layout.setVisibility(View.INVISIBLE);

                        heartbeat_value.setVisibility(View.VISIBLE);


                        heartWarning=true;

                    }else{


                        if (isAdded()) {
                            heartbeat_value.setTextColor(getResources().getColor(R.color.green_settings));
                            heartbeat_value.setVisibility(View.GONE);
                        }

                        heartWarning=false;


                        if(!tempWarning && !oxyWarning){
                            alert_layout.setVisibility(View.INVISIBLE);
                            no_alert_layout.setVisibility(View.VISIBLE);
                        }
                    }


                }else{
                    heartbeathRef.setValue(1);
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
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    double value = dataSnapshot.getValue(double.class);
                    spo2_value.setText(String.valueOf(value)+"% Spo2");


                    if(value < 90){

                        if (isAdded()) {
                            showWarning("Abnormal oxygen rate");
                            spo2_value.setTextColor(getResources().getColor(R.color.red));
                        }
                        alert_layout.setVisibility(View.VISIBLE);
                        no_alert_layout.setVisibility(View.INVISIBLE);


                        oxyWarning = true;


                        spo2_value.setVisibility(View.VISIBLE);

                    }else{

                        if (isAdded()) {
                            spo2_value.setTextColor(getResources().getColor(R.color.green_settings));
                            spo2_value.setVisibility(View.GONE);
                        }

                        oxyWarning = false;


                        if(!tempWarning && !heartWarning){
                            alert_layout.setVisibility(View.INVISIBLE);
                            no_alert_layout.setVisibility(View.VISIBLE);
                        }



                    }


                }else {
                    oximeterRef.setValue(1.0);
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
                    temperature_value.setText(value +" °C");


                    if(value<36 || value > 38){

                        if (isAdded()) {
                            showWarning("Abnormal body temperature");
                            temperature_value.setTextColor(getResources().getColor(R.color.red));
                        }

                        alert_layout.setVisibility(View.VISIBLE);
                        no_alert_layout.setVisibility(View.INVISIBLE);


                        tempWarning = true;

                        temperature_value.setVisibility(View.VISIBLE);

                    }else {

                        if (isAdded()) {
                            temperature_value.setTextColor(getResources().getColor(R.color.green_settings));
                            temperature_value.setVisibility(View.GONE);
                        }

                        tempWarning = false;


                        if(!oxyWarning && !heartWarning){
                            alert_layout.setVisibility(View.INVISIBLE);
                            no_alert_layout.setVisibility(View.VISIBLE);
                        }



                    }


                }else {
                    temperatureRef.setValue(1.0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });




        return  view;
    }











    private void showWarning(String description){

        if (getContext() == null) {
            // Handle the null context error
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View layout_dialog = LayoutInflater.from(requireContext()).inflate(R.layout.warning_dialog, null);
        builder.setView(layout_dialog);

        Button button_ok = layout_dialog.findViewById(R.id.button_ok);
        TextView description_txt = layout_dialog.findViewById(R.id.description);


        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);



        description_txt.setText(description);




        //btn annuler
        button_ok.setOnClickListener(view -> {

            dialog.dismiss();

        });


        //show dialog
        dialog.show();
    }




}