package com.example.divers.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.divers.R;

public class PressureFragment extends Fragment {

    public PressureFragment() {
// Constructeur vide
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pressure, container, false);

// Ajoutez ici les éléments spécifiques à votre nouvelle fragment

        return view;
    }
}