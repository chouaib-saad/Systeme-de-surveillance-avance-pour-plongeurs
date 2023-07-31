package com.example.divers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {

    private ImageView logoImage;
    private TextView titleText;


    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        logoImage = findViewById(R.id.logoImage);
        titleText = findViewById(R.id.titleText);


        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();


        animateLogo();
        animateTitle();
    }

    private void animateLogo() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation start event
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Animation end event
                startNextActivity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeat event
            }
        });
        logoImage.startAnimation(animation);
    }

    private void animateTitle() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        titleText.startAnimation(animation);
    }

    private void startNextActivity() {

        checkUserLoggedIn();
        finish(); // Optional: Finish the splash activity to prevent going back to it
    }


    @Override
    public void onBackPressed() {

        //do nothing..

    }







    private void checkUserLoggedIn() {
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    // User is logged in, redirect to the main activity
                    startMainActivity();
                } else {
                    // No user is logged in, redirect to the login activity
                    startLoginActivity();
                }
            }
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // Optional: Finish the splash activity to prevent going back to it
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
        finish(); // Optional: Finish the splash activity to prevent going back to it
    }


}