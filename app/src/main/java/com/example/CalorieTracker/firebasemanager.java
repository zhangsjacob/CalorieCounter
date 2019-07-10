package com.example.CalorieTracker;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class firebasemanager {
    private static firebasemanager firebasemanager;
    public static FirebaseAuth mAuth;
    public static FirebaseUser mUser;
    public static Boolean isOnline = false;
    public static User fUser;
    public static FirebaseDatabase db;
    public static String userID;


    //dealing with getting a user





    public static firebasemanager getManager(){
        if(firebasemanager == null || mUser == null){
            synchronized (firebasemanager.class){
                firebasemanager = new firebasemanager();
                initFireBase();
            }
        }
        return firebasemanager;
    }

    private static void initFireBase() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        userID = mAuth.getUid();
        db = FirebaseDatabase.getInstance();
        if(mAuth != null && userID != null) firebasemanager.reloadUser();
    }

    public void reloadUser(){

        db.getReference().child("users").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void  reloadUserState(Boolean Online){
        isOnline = Online;
    }

    public Boolean getUserState(){

        return isOnline;
    }

    public void reloadFirBase(){
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        userID = mAuth.getUid();
        fetchUserUpdate();
    }

    public void signOut(){
        mAuth.signOut();
    }
    public void updateUser(String name, Double height, int weight, int age,OnFailureListener fail,OnSuccessListener ok){
        firebasemanager.getManager().reloadFirBase();

        fUser.setWeight(weight);
        fUser.setHeight(height);
        fUser.setAge(age);
        fUser.setName(name);




        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        firebasemanager.getmUser().updateProfile(profileUpdate);


        DatabaseReference mDatabase = firebasemanager.getManager().getDB().getReference();
        mDatabase.child("users").child(getmAuth().getUid()).setValue(fUser).addOnSuccessListener(ok).addOnFailureListener(fail);

    }

    private void fetchUserUpdate(){
        DatabaseReference tempref = db.getReference().child("users").child(mAuth.getUid());
        //get user from firebase and set as the static user
        tempref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    public FirebaseUser getmUser(){
        return mUser;
    }

    public FirebaseAuth getmAuth(){
        return mAuth;
    }

    public User getfUser() {return fUser;}


    public FirebaseDatabase getDB(){return db;}
}
