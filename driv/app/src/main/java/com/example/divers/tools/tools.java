package com.example.divers.tools;

import android.content.SharedPreferences;

public class tools {

    // get val From Strig
   public String getDataFromShared(SharedPreferences sharedPreferences,String key, String valString){
        return  sharedPreferences.getString(key,valString);
    }

  /*  public void uploadImage(Activity activity, FirebaseAuth firebaseAuth, Uri url,String name,String email, Void func) {
        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imagesRef = storageRef.child("images/" + UUID.randomUUID() + ".jpg");
            String uid =   firebaseAuth.getCurrentUser().getUid();

            UploadTask uploadTask = imagesRef.putFile(url);
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
                    //   String name = "John Doe"; // Replace with the actual name
                    //    String phone = "1234567890"; // Replace with the actual phone number

                    // Create a new document in the "images" collection with a randomly generated UUID as the document ID
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    CollectionReference imagesRefFirestore = db.collection("users");
                    String uuid = UUID.randomUUID().toString();
                    // ImageData imageData = new ImageData(name, phone, imageUrl);
                    User user = new com.example.divers.models.User(name, email, imageUrl);
                    imagesRefFirestore.document(uid).set(user)
                            .addOnSuccessListener(documentReference -> {
                                func();
                                func();
                                Toast.makeText(RegisterActivity.this, "Upload successful: " + imageUrl, Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(RegisterActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(g, "Upload failed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(RegisterActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    } */
}
