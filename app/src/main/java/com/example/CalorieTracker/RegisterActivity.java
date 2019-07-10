package com.example.CalorieTracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.CalorieTracker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class RegisterActivity extends AppCompatActivity {
    ImageView ImgUserPhoto;
    public static int PReqCode = 1;
    public static int REQUESTCODE = 1;
    Uri pickedImageUri;
    private EditText userEmail, userPassword, userPasswordConfirm, userName;
    private ProgressBar registerLoading;
    private Button registerButton;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //get user text

        userEmail = findViewById(R.id.register_email);
        userPassword = findViewById(R.id.register_password);
        userPasswordConfirm = findViewById(R.id.register_passwordConfirm);
        userName = findViewById(R.id.register_name);
        registerLoading = findViewById(R.id.login_progress);
        registerButton = findViewById(R.id.login_button);

        registerLoading.setVisibility(View.INVISIBLE);

        mDatabase = FirebaseDatabase.getInstance().getReference();


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerButton.setVisibility(View.INVISIBLE);
                registerLoading.setVisibility(View.VISIBLE);
                final String email = userEmail.getText().toString();
                final String name = userName.getText().toString();
                final String password = userPassword.getText().toString();
                final String passwordConfirm = userPasswordConfirm.getText().toString();

                if (pickedImageUri == null) {
                    showMessage("please select a photo");
                    checkAndRequestForPermission();
                    registerButton.setVisibility(View.VISIBLE);
                    registerLoading.setVisibility(View.INVISIBLE);
                    return;
                }

                    if(email.isEmpty() || name.isEmpty() || password.isEmpty() || !password.equals(passwordConfirm)
                ){
                    //lets them know they missed something

                    showMessage("Please verify all fields");
                    registerButton.setVisibility(View.VISIBLE);
                    registerLoading.setVisibility(View.INVISIBLE);
                }
                else{
                    //everything is fine
                    CreateUserAccount(email,name,password);
                }
            }
        });



        //get user photo
        ImgUserPhoto = findViewById(R.id.regUserPhoto);
        ImgUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >=22){
                    checkAndRequestForPermission();
                }
                else{
                    openGallery();
                }
            }
        });
    }
    private void updateUI(){
        Intent homeActivity = new Intent(getApplicationContext(), Home.class);
        startActivity(homeActivity);
        finish();
    }




    private void updateUserInfo(final String name, Uri pickedImageUri, final FirebaseUser currentUser){
        //upload user photo
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImageUri.getLastPathSegment());
        imageFilePath.putFile(pickedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //if it works...

                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //feed uri that has the user profile pic
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();



                        currentUser.updateProfile(profileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            showMessage("register complete!");
                                            updateUI();
                                        }
                                    }
                                });
                    }
                });
            }
        });
    }





    private void CreateUserAccount(final String email, final String name, String password){
        firebasemanager.getManager().getmAuth().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            showMessage("Account Created!");
                            //update profile pic and name
                            User user = new User(name,email);
                            firebasemanager.getManager().reloadFirBase();
                            mDatabase.child("users").child(firebasemanager.getManager().getmAuth().getUid()).setValue(user).addOnFailureListener(RegisterActivity.this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Log.e("ErrorTag",e.getMessage());
                                }
                            }).addOnSuccessListener(RegisterActivity.this, new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.e("ErrorTag","");
                                }
                            });
                                updateUserInfo(name, pickedImageUri, firebasemanager.getManager().getmUser());
                        }
                        else{
                            showMessage("Could not Create Account");
                            registerButton.setVisibility(View.VISIBLE);
                            registerLoading.setVisibility(View.INVISIBLE);

                        }
                    }
                });
    }

    private void showMessage(String message){
        Toast.makeText(RegisterActivity.this,message, Toast.LENGTH_SHORT).show();
    }

    private void openGallery(){
        //opens gallery intent and waits for user to select an image
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESTCODE);

        }
    private void checkAndRequestForPermission(){
        //do we have permission to read external storage?
        if(ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(RegisterActivity.this,"Please accept the permission request",Toast.LENGTH_SHORT);
            }
            else{
                ActivityCompat.requestPermissions(RegisterActivity.this,
                new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode);

            }
        }
        else{
            openGallery();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == REQUESTCODE && data != null){
            //the user has picked a photo...

            pickedImageUri = data.getData();
            ImgUserPhoto.setImageURI(pickedImageUri);
        }
    }
}
