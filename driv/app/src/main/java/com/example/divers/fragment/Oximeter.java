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


public class Oximeter extends Fragment {



    private SwitchMaterial SwichOnOf;
    private TextView isActivatedText;

    private TextView spo2_value;
    private ValueEventListener valueEventListener;

    String  path  ;
    String uid;

    ProgressBar progressBar;

    public static boolean isDisabled = true;


    public Oximeter() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.oximeter, container, false);

        DatabaseReference  oximeterRef;

        SwichOnOf =view.findViewById(R.id.switch1);
        spo2_value = view.findViewById(R.id.oximeter_value);

        isActivatedText = view.findViewById(R.id.isActivatedText);

        uid = admin.firebaseAuth.getCurrentUser().getUid();

        progressBar = view.findViewById(R.id.oxymeterProgressBar);


        try{
            path =getArguments().getString("path");
            if (path != null){
                uid = path ;

            }
        }
        catch (Exception e){

        }
        oximeterRef = FirebaseDatabase.getInstance().getReference().child("user").child(uid).child("spO2");


        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child(uid);


        database.child("ActiveOxi").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Retrieve the boolean value from the snapshot
                Boolean value= false;
                try{
                    value = dataSnapshot.getValue(Boolean.class);
                    if(Boolean.TRUE.equals(value)){
                        spo2_value.setVisibility(View.VISIBLE);
                        isDisabled = true;

                    }
                    else{
                        spo2_value.setVisibility(View.INVISIBLE);
                        }
                } catch (Exception ignored){

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
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur
            }
        });
        SwichOnOf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    spo2_value.setVisibility(View.VISIBLE);
                    isDisabled = true;

                    database.child("ActiveOxi").setValue(true);
                    isActivatedText.setText("ON");
                    if (isAdded()) {
                        isActivatedText.setTextColor(requireActivity().getResources().getColor(R.color.green_settings));
                    }


                } else {
                    spo2_value.setVisibility(View.INVISIBLE);
                    database.child("ActiveOxi").setValue(false);
                    progressBar.setProgress(0);
                    isDisabled = false;
                    isActivatedText.setText("OFF");
                    if (isAdded()) {
                        isActivatedText.setTextColor(requireActivity().getResources().getColor(R.color.red));
                    }                }
            }
        });




        //database.child("dataoximeter").addValueEventListener(valueEventListener);

        // Attach ValueEventListener for oximeter
        oximeterRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    double value = dataSnapshot.getValue(double.class);
                    spo2_value.setText("Average oxygen saturation level : "+String.valueOf(value)+" %");


                    if(isDisabled) {
                        progressBar.setProgress((int) value);
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



        return  view;
    }







}