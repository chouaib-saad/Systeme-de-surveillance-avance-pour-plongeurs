package com.example.divers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.divers.ref.admin;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LogInActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText loginEmail, loginPassword;
    private TextView signupRedirectText;
    private Button loginButton;
    private FirebaseAuth auth;
    TextView forgotPassword;

    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         Date currentTime = Calendar.getInstance().getTime();
         String expiryDateString = "21-05-2025"; // upcoming date
           DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

          try {
               Date expiryDate = formatter.parse(expiryDateString);
               if(System.currentTimeMillis() < expiryDate.getTime()){

                   setContentView(R.layout.activity_log_in);


            }
             }catch (Exception e){}



          progressDialog = new ProgressDialog(this);








        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signUpRedirectText);
        forgotPassword = findViewById(R.id.forgot_password);
        auth=FirebaseAuth.getInstance();


        // Create an ArrayAdapter to populate the Spinner with the choices
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.choices, android.R.layout.simple_spinner_item);

        // Specify the layout to use for the Spinner's dropdown list
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the ArrayAdapter as the Spinner's adapter


        // Set the OnItemSelectedListener to listen for Spinner selection changes





        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                        showProgressDialog(LogInActivity.this,"Connecting..",true);


                String email = loginEmail.getText().toString().trim();
                String pass = loginPassword.getText().toString().trim();

                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (!pass.isEmpty()) {
                        auth.signInWithEmailAndPassword(email,pass)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Toast.makeText(LogInActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                        if(email.equals("admin@admin.mail")){
                                            admin.check = true;
                                        }else {
                                            admin.check =false;
                                        }

                                        startActivity(new Intent(LogInActivity.this, MainActivity.class));
                                        showProgressDialog(LogInActivity.this,"Connecting..",false);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LogInActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                        showProgressDialog(LogInActivity.this,"Connecting..",false);

                                    }
                                });
                    } else {
                        showProgressDialog(LogInActivity.this,"Connecting..",false);
                        loginPassword.setError("Empty fields are not allowed");
                    }
                } else if (email.isEmpty()) {
                    showProgressDialog(LogInActivity.this,"Connecting..",false);
                    loginEmail.setError("Empty fields are not allowed");
                } else {
                    showProgressDialog(LogInActivity.this,"Connecting..",false);
                    loginEmail.setError("Please enter correct email");
                }
            }
        });

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogInActivity.this, RegisterActivity.class));




            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogInActivity.this,ForgetPasswordActivity.class));
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }









    //progressbar
    private void showProgressDialog(Context context, String message, boolean show) {
        if (show) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(message);
            progressDialog.setCancelable(false);
            progressDialog.show();
        } else {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }




}