package com.example.divers;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.divers.models.UserInfo;
import com.example.divers.ref.admin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class RegisterActivity  extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 123 ;
    private TextView GoToSignIn;
    private EditText fullName, email,  phone, password;
    private Button btnSignUp;
    private String nameS, emailS, phoneS,  passwordS;

    private FirebaseDatabase firebaseDatabase;
    private ProgressDialog progressDialog;
    private Spinner spinner;
    private TextView label;
    private Uri imageUri;
    private ImageView myPhoto;
    private LinearLayout myPhoto_button;
    SharedPreferences sharedPreferences ;

    private static Boolean isPictureSelected = false;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        GoToSignIn = findViewById(R.id.loginRedirectText);
        btnSignUp = findViewById(R.id.signup_button);
        fullName = findViewById(R.id.name);
        email = findViewById(R.id.signup_email);
        // cin = findViewById(R.id.cinSignUp);

        password = findViewById(R.id.signup_password);
        myPhoto = findViewById(R.id.img);
        myPhoto_button = findViewById(R.id.myPhoto_button);
        phone = findViewById(R.id.phone);


        progressDialog = new ProgressDialog(this);
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Please wait...!");
                progressDialog.show();
                progressDialog.setCancelable(false);
                if (validate()){
                    progressDialog.dismiss();
                    String user_email = email.getText().toString().trim();
                    String user_password = password.getText().toString().trim();
                    admin.firebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                sendEmailVerification();

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    Toast.makeText(RegisterActivity.this, "Done!", Toast.LENGTH_SHORT).show();

                }

            }
        });

        GoToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LogInActivity.class));
            }
        });



    }   /////

    private void sendEmailVerification() {
        FirebaseUser user = admin.firebaseAuth.getCurrentUser();
        if (user != null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        //sendUserData();
                        uploadImage();
                        Toast.makeText(RegisterActivity.this, "Registration done!", Toast.LENGTH_SHORT).show();
                        admin.firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(RegisterActivity.this, LogInActivity.class));
                        progressDialog.dismiss();
                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

   // private void sendUserData() {
    //    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
     //   DatabaseReference myRef = firebaseDatabase.getReference("Users");
     //   User user = new com.example.divers.models.User(nameS, emailS, phoneS);
                //User(nameS, emailS, phoneS);
     //   Toast.makeText(this, ""+user.toString(), Toast.LENGTH_SHORT).show();
      //  myRef.child(""+firebaseAuth.getUid()).setValue(user);

    //}

    @SuppressLint("UseCompatLoadingForDrawables")
    private boolean validate() {
        boolean result = false;
        nameS = fullName.getText().toString();
        emailS = email.getText().toString();
        phoneS = phone.getText().toString();


        passwordS = password.getText().toString();
        if (nameS.isEmpty() || nameS.length()<8){
            fullName.setError("fullName is invalid!");
        } else if (emailS.isEmpty() || !emailS.matches("[a-zA-Z\\d]+@[a-z]+\\.+[a-z]+")) {
            email.setError("Enter valid Email");

        }
            else if (phoneS.length() != 8){
            phone.setError("phone invalid!");

        } else if (passwordS.isEmpty() || passwordS.length()<8) {
            password.setError("Minimum 9 character required");

        }else if(!isPictureSelected){
            myPhoto.setImageDrawable(getResources().getDrawable(R.drawable.ic_error));
            Toast.makeText(this, "please select a photo ..", Toast.LENGTH_SHORT).show();
            }
        else {
            result = true;
        }
        progressDialog.dismiss();
        return result;

    }


    public void selectImage(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                //myPhoto.setImageBitmap(bitmap);
                myPhoto.setImageDrawable(getResources().getDrawable(R.drawable.ic_done));
                isPictureSelected = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadImagee(View view) {
        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imagesRef = storageRef.child("images/" + UUID.randomUUID() + ".jpg");

            UploadTask uploadTask = imagesRef.putFile(imageUri);
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                // Continue with the task to get the download URL
                return imagesRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String imageUrl = downloadUri.toString();
                    Toast.makeText(RegisterActivity.this, "Upload successful: " + imageUrl, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(RegisterActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }

    }


    //***************************************************

    public void uploadImage() {
        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imagesRef = storageRef.child("images/" + UUID.randomUUID() + ".jpg");
           String uid =   admin.firebaseAuth.getCurrentUser().getUid();

            UploadTask uploadTask = imagesRef.putFile(imageUri);
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                // Continue with the task to get the download URL
                return imagesRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String imageUrl = downloadUri.toString();
                 //   String name = "John Doe"; // Replace with the actual name
                //    String phone = "1234567890"; // Replace with the actual phone number

                    // Create a new document in the "images" collection with a randomly generated UUID as the document ID
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    CollectionReference imagesRefFirestore = db.collection("users");
                    String uuid = UUID.randomUUID().toString();
                   // ImageData imageData = new ImageData(name, phone, imageUrl);
                    UserInfo user = new UserInfo(nameS, emailS, phoneS,imageUrl);
                    imagesRefFirestore.document(uid).set(user)
                            .addOnSuccessListener(documentReference -> {
                                SaveData("name",nameS);
                                SaveData("url",imageUrl);
                                SaveData("email",emailS);
                                SaveData("phone",phoneS);
                                //Toast.makeText(RegisterActivity.this, "Upload successful: " + imageUrl, Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(RegisterActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(RegisterActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(RegisterActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }
    void SaveData(String key,String data){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,data);
        editor.apply();

    }





}
