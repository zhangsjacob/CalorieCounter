package com.example.CalorieTracker;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.CalorieTracker.Fragments.HomeFragment;
import com.example.CalorieTracker.Fragments.ProfileFragment;
import com.example.CalorieTracker.Fragments.settingsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
    import androidx.appcompat.app.ActionBarDrawerToggle;

    import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static com.example.CalorieTracker.RegisterActivity.REQUESTCODE;


public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
//    FirebaseAuth mAuth;
//    FirebaseUser currentUser;
    private  ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebasemanager.getManager();
        setContentView(R.layout.activity_home2);
        Toolbar toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);



        //nav setup
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

//        firebasemanager.getManager().reloadFirBase();

        updateNavHeader();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        updateNavHeader();
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            /*
            getSupportActionBar().setTitle("Home");
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
            */
            //Intent homeReturn = new Intent(this,HomeActivity.class);
            //startActivity(homeReturn);

        } else if (id == R.id.nav_profile) {
            getSupportActionBar().setTitle("Profile");

            getSupportFragmentManager().beginTransaction().replace(R.id.container,  profileFragment == null ? profileFragment = new ProfileFragment() : profileFragment).commit();
        } else if (id == R.id.nav_settings) {
            getSupportActionBar().setTitle("Settings");
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new settingsFragment()).commit();
        } else if (id == R.id.nav_signout) {
            firebasemanager.getManager().signOut();
            firebasemanager.getManager().reloadUserState(false);
            Intent loginActivity = new Intent(Home.this, LoginActivity.class);
            loginActivity.putExtra("Value", 190);
            loginActivity.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginActivity);
            finish();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

private ImageView navUserPhoto;

    public void updateNavHeader(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.nav_user_name);
        TextView navUserMail = headerView.findViewById(R.id.nav_user_mail);
        navUserPhoto = headerView.findViewById(R.id.nav_user_photo);

        navUserMail.setText(firebasemanager.getManager().getmUser() == null ? "" : firebasemanager.getManager().getmUser().getEmail());
        navUserName.setText(firebasemanager.getManager().getmUser() == null ? "" : firebasemanager.getManager().getmUser().getDisplayName());





        Glide.with(Home.this).load(firebasemanager.getManager().getmUser() == null ? "" : firebasemanager.getManager().getmUser().getPhotoUrl()).into(navUserPhoto);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null && resultCode == RESULT_OK && requestCode == 9810){
            //the user has picked a photo...
           Uri pickedImageUri = data.getData();

            updatePhoto(pickedImageUri);
        }
    }

    public void  updatePhoto(Uri pickedImageUri){

        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImageUri.getLastPathSegment());
        imageFilePath.putFile(pickedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(final Uri uri) {

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(uri)
                                .build();

                        firebasemanager.getManager().getmUser().updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        profileFragment.updateProfileImage(firebasemanager.getManager().getmUser().getPhotoUrl());

                                        Glide.with(Home.this).load(firebasemanager.getManager().getmUser().getPhotoUrl()).into(navUserPhoto);
                                    }
                                });

                    }
                });

            }
        });

    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);

            return path;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
