package com.example.divers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {
    private Button backToMain,btnReset;
    private EditText email;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        backToMain=findViewById(R.id.backToMain);
        btnReset=findViewById(R.id.btnReset);
        email=findViewById(R.id.emailReset);
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail=email.getText().toString().trim();
                progressDialog.setMessage("Please wait!");
                progressDialog.show();
                if(userEmail.isEmpty() || !userEmail.contains("@") || !userEmail.contains(".")){
                    email.setError("Email is invalid!");
                    progressDialog.dismiss();
                }else{
                    firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ForgetPasswordActivity.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(ForgetPasswordActivity.this,MainActivity.class));
                                progressDialog.dismiss();
                            }else{
                                Toast.makeText(ForgetPasswordActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });

        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgetPasswordActivity.this, LogInActivity.class));

            }
        });
    }
}