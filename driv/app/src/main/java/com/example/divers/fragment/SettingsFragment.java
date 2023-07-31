package com.example.divers.fragment;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.divers.R;
import com.example.divers.LogInActivity;
import com.example.divers.ref.admin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class SettingsFragment extends Fragment{
    private static final int PICK_IMAGE_REQUEST = 123;
    SharedPreferences sharedPreferences;

   private String name,phone,url;
   private  ImageView edit1,edit2,edit3,edit4;
   private TextView userName1, userPhone, userEmail, userName2;
   private Button ButtonUpdate;
   private ImageView imageView;
   boolean mbooleanName;
   boolean mbooleanImage;
   
   LinearLayout modifyPassword_btn;
   ImageView buttLogout;

   private   Uri imageUri;
   private   String uid;
   private  String imageurl;
   private FirebaseFirestore db;
  private   FirebaseUser user;
  private TextView navHeaderName;
  private ImageView navHeaderImage;
  private boolean imagecheck = false;



  //firebase
  private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private FirebaseUser Currentuser;
//progress dialog
private ProgressDialog progressDialog;
//alert dialog
private AlertDialog alertDialog;


    public SettingsFragment() {
        // Required empty public constructor
    }






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_settings, container, false);
        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userName1 = view.findViewById(R.id.userName);
        userName2 = view.findViewById(R.id.userName2);
        userPhone =view.findViewById(R.id.userPhone);
        imageView= view.findViewById(R.id.userPicture);
        userEmail =view.findViewById(R.id.userEmail);
        buttLogout =view.findViewById(R.id.buttLogout);
        modifyPassword_btn =view.findViewById(R.id.modifyPassword_btn);



        progressDialog = new ProgressDialog(getContext());



        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        Currentuser = firebaseAuth.getCurrentUser();

        buttLogout = view.findViewById(R.id.buttLogout);



        
        /*
        edit1 =view.findViewById(R.id.edit1);
        edit2 =view.findViewById(R.id.edit2);
        edit3 =view.findViewById(R.id.edit3);
        edit4 =view.findViewById(R.id.edit4);
        edit1.setOnClickListener(this);
        edit2.setOnClickListener(this);
        edit3.setOnClickListener(this);
        edit4.setOnClickListener(this);
        ButtonUpdate = view.findViewById(R.id.update);
        ButtonUpdate.setOnClickListener(this);

        userName1.setEnabled(false);
        userEmail.setEnabled(false);
        passwordView.setEnabled(false);
        userPhone.setEnabled(false);



       try {
           uid = admin.firebaseAuth.getCurrentUser().getUid();
       }catch(Exception e) {

       }

        db = FirebaseFirestore.getInstance();
         user = admin.firebaseAuth.getCurrentUser();
         
         */



        setvalFromShared();
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //ButtonUpdate.setVisibility(View.VISIBLE);

                selectImage();
            }
        });


        modifyPassword_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showChangePasswordDialog();

            }
        });


        buttLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogout();
            }
        });


        return view;
    }
    void setvalFromShared(){


        url = sharedPreferences.getString("url","nourl");
        Log.d(TAG, "this url :" + url);
        Glide.with(getActivity())
                .load(url)
                .circleCrop()
                .into(imageView);

        userName1.setText(sharedPreferences.getString("name","noname"));
        userPhone.setText(sharedPreferences.getString("phone", "nophone"));
        userEmail.setText(sharedPreferences.getString("email","noemail"));


    }


    void SaveData(String key,String data){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,data);
        editor.apply();

    }

    public void uploadImage() {
        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imagesRef = storageRef.child("images/" + UUID.randomUUID() + ".jpg");


            UploadTask uploadTask = imagesRef.putFile(imageUri);
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return imagesRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String imageUrl = downloadUri.toString();
                    updateData("url",imageUrl);

                  //  updateuser update = new updateuser(name,phone,url);
              //      updateNavigationHeader();

                } else {
                    //Toast.makeText(getActivity(), "Upload failed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            //Toast.makeText(getActivity(), "No image selected", Toast.LENGTH_SHORT).show();
        }}
        public void selectImage() {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
        }
        @Override
       public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                imageUri = data.getData();
                imagecheck =true;
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                    imageView.setImageBitmap(bitmap);
                    uploadImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        void updatePassword(String newPassword){



            user.updatePassword(newPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Password updated successfully
                                Toast.makeText(getActivity(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                // Failed to update password
                                Toast.makeText(getActivity(), "Failed to update password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }

        void  updateEmail(String newEmail){


            user.updateEmail(newEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Email updated successfully
                                Toast.makeText(getActivity(), "Email updated successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                // Failed to update email
                                Toast.makeText(getActivity(), "Failed to update email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }

        void  updateData(String key, String data){
            Map<String, Object> updates = new HashMap<>();

            updates.put(key, data);
            DocumentReference documentReference=  db.collection("users").document(uid);

            documentReference.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Field updated successfully
                        //    Toast.makeText(getActivity(), "Field updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to update field
                         //   Toast.makeText(getActivity(), "Failed to update field", Toast.LENGTH_SHORT).show();
                        }
                    });

        }

        void Editflied(EditText editText){
            ButtonUpdate.setVisibility(View.VISIBLE);

        editText.setEnabled(true);

        }


        
        /*

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit1:
                Editflied(userName1);
                // Handle click for button1
                break;
            case R.id.edit2:
                Editflied(userPhone);

                // Handle click for button2
                break;
            // Add more cases for other views
            case R.id.edit3:
                Editflied(userEmail);
                // Handle click for button2
                break;
            case R.id.edit4:
                Editflied(passwordView);
                // Handle click for button2
                break;
            case R.id.update:
                performAsyncOperation();
                // Handle click for button2
                break;

            default:
                break;
        }
    }
   boolean  checkData(EditText editText){
        if(editText.isEnabled() && !editText.getText().equals("")){
            return true;

        }
        return false;

    }
   void update(){
    //    for(int i =0 ; i<3 ; i++){

      //  }
       if(checkData(userName1)){
           String data = userName1.getText().toString();
           updateData("fullName",data );


       }
       if(checkData(userPhone)){
           String phone = userPhone.getText().toString();
           updateData("phone",phone );
         //  SaveToShared("phone",phone);


       }
       if(checkData(userEmail)){
           String email = userEmail.getText().toString();
           updateEmail( email);
           updateData("email",email);
         //  SaveToShared("email",email);

       }
       if(checkData(passwordView)){
           updatePassword( passwordView.getText().toString());

       }
       if(imagecheck){

           uploadImage();
        //   SaveToShared("url",url);


       }


    }
   
         */
    
    void SaveToShared(String key , String value){
       SharedPreferences.Editor editor =    sharedPreferences.edit();
       editor.putString(key,value);
    }

    private void updateNavigationHeader() {
        if (getActivity() != null) {
            NavigationView navigationView = getActivity().findViewById(R.id.nvView);
            View headerView = navigationView.getHeaderView(0);
            navHeaderName = headerView.findViewById(R.id.myname);
            navHeaderImage = headerView.findViewById(R.id.myimage);


            String  names  = sharedPreferences.getString("name","noname");
            String  urls =sharedPreferences.getString("url","nourl");
            navHeaderName.setText(names);
              ImageView headimage = headerView.findViewById(R.id.myimage) ;
            Glide.with(getActivity())
                    .load(urls)
                    .circleCrop()
                    .into(headimage);
        }
    }

    
    /*
    private void performAsyncOperation() {
        // Create a CompletableFuture representing the asynchronous operation
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            // Simulate a long-running operation
            try {
                update();
                ClearPref();

                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Return the result of the operation
            return "Async operation completed";
        });

        // Perform other tasks while waiting for the result
        // ...

        // Wait for the result of the asynchronous operation
        try {
            String result = completableFuture.get();

            // Handle the result (e.g., update UI)
         //   Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();

            // Recreate the activity
            getActivity().recreate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
  
     */
    
    
    void ClearPref(){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }







    public void showChangePasswordDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.RoundedDialogStyle);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.change_password_dialog, null);
        builder.setView(view);

        EditText current_passwordEditText = view.findViewById(R.id.current_password);
        EditText newPasswordEditText = view.findViewById(R.id.new_password_edittext);
        EditText confirmPasswordEditText = view.findViewById(R.id.confirm_password_edittext);
        TextView titleTextView = view.findViewById(R.id.dialog_title_textview);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        Button confirmButton = view.findViewById(R.id.confirm_button);

        // Set the title of the dialog
        titleTextView.setText("Nouveau mot de passe");

        // Set the click listener for the cancel button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the dialog when Cancel is clicked
                alertDialog.dismiss();
            }
        });

        // Set the click listener for the confirm button
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the new password and confirm password from the EditText fields
                String current_password = current_passwordEditText.getText().toString();
                String newPassword = newPasswordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                if(current_password.isEmpty()){
                    current_passwordEditText.setError("Veuillez entrer votre mot de passe actuel");
                    current_passwordEditText.requestFocus();
                }
                else if (newPassword.isEmpty()) {
                    newPasswordEditText.setError("Veuillez entrer un nouveau mot de passe");
                    newPasswordEditText.requestFocus();
                } else if (confirmPassword.isEmpty()) {
                    confirmPasswordEditText.setError("Veuillez confirmer le mot de passe");
                    confirmPasswordEditText.requestFocus();
                } else if (!newPassword.equals(confirmPassword)) {
                    confirmPasswordEditText.setError("Les mots de passe ne correspondent pas");
                    confirmPasswordEditText.requestFocus();
                } else {
                    // The passwords match and are not empty
                    // Proceed with further actions (e.g., update the password in the database)

                    showProgressDialog(requireContext(),"attendez..",true);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {


                            changePassword(current_password,newPassword);

                        }
                    },1500);


                    // Dismiss the dialog
                    alertDialog.dismiss();
                }

            }
        });

        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }




    private void changePassword(String currentPassword, String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Create a credential with the user's email and current password
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

            // Reauthenticate the user with the provided credential
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> reauthTask) {
                            if (reauthTask.isSuccessful()) {
                                // User has been successfully reauthenticated

                                // Update the password
                                user.updatePassword(newPassword)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // Password updated successfully
                                                    Toast.makeText(requireContext(), "Mot de passe mis à jour avec succès", Toast.LENGTH_SHORT).show();
                                                    showProgressDialog(requireContext(), "attendez..", false);
                                                } else {
                                                    // An error occurred while updating the password
                                                    Toast.makeText(requireContext(), "Échec de la mise à jour du mot de passe", Toast.LENGTH_SHORT).show();
                                                    showProgressDialog(requireContext(), "attendez..", false);
                                                }
                                            }
                                        });
                            } else {
                                // Reauthentication failed, show an error message
                                Toast.makeText(requireContext(), "Échec de l'authentification. Vérifiez votre mot de passe actuel.", Toast.LENGTH_SHORT).show();
                                showProgressDialog(requireContext(), "attendez..", false);
                            }
                        }
                    });
        } else {
            Toast.makeText(getContext(), "l'utilisateur ne pas existe", Toast.LENGTH_SHORT).show();
        }
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
                Intent intent = new Intent(getContext(), LogInActivity.class);
                startActivity(intent);
                progressDialog.dismiss();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requireActivity().finish();
                    }
                },200);
            }
        },2500);
    }






}