package com.example.divers.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.divers.R;
import com.example.divers.ref.admin;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class Temperature extends Fragment {

    private SwitchMaterial SwichOnOf;
    private TextView isActivatedText;

    private TextView temperature_value;
    private ValueEventListener valueEventListener;
    String  path  ;
    String uid;

    ProgressBar progressBar;
    public static boolean isDisabled = true;



    public Temperature() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.temperature, container, false);

        DatabaseReference temperatureRef;

        SwichOnOf =view.findViewById(R.id.switch1);
        temperature_value = view.findViewById(R.id.temperature_value);

        isActivatedText = view.findViewById(R.id.isActivatedText);

        progressBar = view.findViewById(R.id.temperatureProgressBar);

        uid = Objects.requireNonNull(admin.firebaseAuth.getCurrentUser()).getUid();


        /*

        try{
            assert getArguments() != null;
            path =getArguments().getString("path");
            if (path != null){
                uid = path ;

            }
        }
        catch (Exception ignored){

        }

         */


        temperatureRef=FirebaseDatabase.getInstance().getReference().child("user").child(uid).child("temperature");


        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child(uid);


        database.child("ActiveTemp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Retrieve the boolean value from the snapshot
                Boolean value= false;
                try{
                    value = dataSnapshot.getValue(Boolean.class);
                    if(Boolean.TRUE.equals(value)){
                        temperature_value.setVisibility(View.VISIBLE);
                        isDisabled = true;

                    }
                    else{
                        temperature_value.setVisibility(View.VISIBLE);}
                } catch (Exception e){

                }


                // Set the switch based on the retrieved boolean value

                if (value != null) {
                    SwichOnOf.setChecked(value);
                    if(value){
                        isActivatedText.setText("ON");
                        if (isAdded()) {
                            isActivatedText.setTextColor(requireActivity().getResources().getColor(R.color.green_settings));
                        }


                    }else{

                        isActivatedText.setText("OFF");
                        if (isAdded()) {
                            isActivatedText.setTextColor(requireActivity().getResources().getColor(R.color.red));
                        }


                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur
            }
        });
        SwichOnOf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    temperature_value.setVisibility(View.VISIBLE);
                    isDisabled = true;

                    database.child("ActiveTemp").setValue(true);
                    isActivatedText.setText("ON");
                    if (isAdded()) {
                        isActivatedText.setTextColor(requireActivity().getResources().getColor(R.color.green_settings));
                    }


                } else {
                    temperature_value.setVisibility(View.INVISIBLE);
                    progressBar.setProgress(0);
                    isDisabled = false;
                    database.child("ActiveTemp").setValue(false);
                    isActivatedText.setText("OFF");
                    if (isAdded()) {
                        isActivatedText.setTextColor(requireActivity().getResources().getColor(R.color.red));
                    }                }
            }
        });

        temperatureRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    double value = dataSnapshot.getValue(double.class);
                    temperature_value.setText("Average temperature in the house : "+String.valueOf(value)+" °C");

                    if(isDisabled) {
                        progressBar.setProgress((int) value);
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







}