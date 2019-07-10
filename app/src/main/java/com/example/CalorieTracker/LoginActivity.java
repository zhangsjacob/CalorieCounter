package com.example.CalorieTracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.CalorieTracker.firebasemanager.mUser;


public class LoginActivity extends AppCompatActivity {
//    private FirebaseAuth mAuth;
    //create items required
    private EditText userMail, userPassword;
    private Button loginButton;
    private ProgressBar loginProgress;
    private Intent HomeActivity;
    private ImageView loginPhoto;
    private Button regButton;

    private Boolean isReal = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        mAuth = FirebaseAuth.getInstance();

        HomeActivity = new Intent(LoginActivity.this, Home.class);



        //get items required
        loginPhoto = findViewById(R.id.loginPhoto);
        userMail = findViewById(R.id.login_email);
        userPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        regButton = findViewById(R.id.register_button);
        loginProgress = findViewById(R.id.login_progress);
        loginProgress.setVisibility(View.INVISIBLE);

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerActivity = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(registerActivity);
                finish();
            }
        });




        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //swaps visibility so you see loading gif
                loginProgress.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.INVISIBLE);

                //take the editText fields
                final String mail = userMail.getText().toString();
                final String password = userPassword.getText().toString();

                if(mail.isEmpty() || password.isEmpty()){
                    showMessage("Please complete all fields");
                    loginProgress.setVisibility(View.INVISIBLE);
                    loginButton.setVisibility(View.VISIBLE);
                }

                else{
                    signIn(mail, password);
                }


            }
        });

    }


    private void showMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    private void signIn(String mail, String password){
        firebasemanager.getManager().getmAuth().signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    loginProgress.setVisibility(View.INVISIBLE);
                    loginButton.setVisibility(View.VISIBLE);
                    isReal = false;
                    firebasemanager.getManager().reloadFirBase();
                    firebasemanager.getManager().reloadUserState(true);
                    updateUI();
                }
                else{
                    showMessage("wrong username or password");
                    loginProgress.setVisibility(View.INVISIBLE);
                    loginButton.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void updateUI(){
        startActivity(HomeActivity);
        finish();
    }
    



    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        if(intent.getIntExtra("Value",1) != 190 && firebasemanager.getManager().getDB() != null){
            updateUI();
        }

    }



}
