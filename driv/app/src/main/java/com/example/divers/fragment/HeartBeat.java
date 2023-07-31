package com.example.divers.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.divers.R;
import com.example.divers.ref.admin;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class HeartBeat extends Fragment {
    private SwitchMaterial SwichOnOf;
    private  TextView isActivatedText;

    private TextView heartbeat_value;
    private ValueEventListener valueEventListener;
    String  path  ;
    String uid;




    public HeartBeat() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.heart_beat, container, false);

        DatabaseReference heartbeathRef;

        SwichOnOf =view.findViewById(R.id.switch1);
        heartbeat_value =view.findViewById(R.id.heartbeat_value);

        isActivatedText = view.findViewById(R.id.isActivatedText);



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


        heartbeathRef = FirebaseDatabase.getInstance().getReference().child("user").child(uid).child("beatsPerMinute");

        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child(uid);





        database.child("ActiveHeart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Retrieve the boolean value from the snapshot
                Boolean value= false;
                try{
                    value = dataSnapshot.getValue(Boolean.class);
                    if(Boolean.TRUE.equals(value)){
                        heartbeat_value.setVisibility(View.VISIBLE);
                    }
                    else{
                        heartbeat_value.setVisibility(View.INVISIBLE);
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
                    heartbeat_value.setVisibility(View.VISIBLE);
                    database.child("ActiveHeart").setValue(true);
                    isActivatedText.setText("ON");
                    if (isAdded()) {
                        isActivatedText.setTextColor(requireActivity().getResources().getColor(R.color.green_settings));
                    }


                } else {
                    heartbeat_value.setVisibility(View.INVISIBLE);
                    database.child("ActiveHeart").setValue(false);
                    isActivatedText.setText("OFF");
                    if (isAdded()) {
                        isActivatedText.setTextColor(requireActivity().getResources().getColor(R.color.red));
                    }                }
            }
        });
        valueEventListener = new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Retrieve the double value from the dataSnapshot
                Double value =0.0;
                try{ value = dataSnapshot.getValue(Double.class);}
                catch (Exception ignored){}


                if (value != null) {
                    // Set the double value to the TextView
                    heartbeat_value.setText(String.valueOf(value)+" BPM");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        };



        //database.child("dataoximeter").addValueEventListener(valueEventListener);



        heartbeathRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int value = dataSnapshot.getValue(Integer.class);
                    heartbeat_value.setText(String.valueOf(value)+" BPM");
                }else{
                    heartbeathRef.setValue(1);
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