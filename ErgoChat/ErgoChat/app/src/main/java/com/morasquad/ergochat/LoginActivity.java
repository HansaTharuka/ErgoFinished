package com.morasquad.ergochat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;


public class LoginActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private Button mLoginButton;


    private FirebaseAuth mAuth;
    private ProgressDialog mLoginProgress;
    private DatabaseReference mUserDatabase;




    private TextInputLayout mLoginEmail;
    private TextInputLayout mLoginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_chat);

        mAuth = FirebaseAuth.getInstance();

        Toolbar mToolBar = (Toolbar)findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Login");

        mLoginProgress = new ProgressDialog(this);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mLoginEmail =(TextInputLayout)findViewById(R.id.login_email);
        mLoginPassword =(TextInputLayout)findViewById(R.id.login_password);
        mLoginButton =(Button) findViewById(R.id.login_btn);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String logemail=mLoginEmail.getEditText().getText().toString();
                String logpassword=mLoginPassword.getEditText().getText().toString();

                if (!TextUtils.isEmpty(logemail)|| !TextUtils.isEmpty(logpassword)){


                    mLoginProgress.setTitle("Logging in");
                    mLoginProgress.setMessage("Please wait while we check your credentials.");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();

                    loginUser(logemail,logpassword);
                }
            }
        });


    }

    private void loginUser(String logemail, String logpassword) {

        mAuth.signInWithEmailAndPassword(logemail,logpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if(task.isSuccessful()){

            mLoginProgress.dismiss();
            String current_user_id =mAuth.getCurrentUser().getUid();
            String deviceToken = FirebaseInstanceId.getInstance().getToken();

            mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    checkEmailVerified();



                }
            });



        }else{

            mLoginProgress.hide();
            Toast.makeText(LoginActivity.this,"Cannot sign in , Please check the form and try again",Toast.LENGTH_LONG).show();
        }



    }
});
    }
    private void checkEmailVerified() {

        FirebaseUser users = FirebaseAuth.getInstance().getCurrentUser();
        boolean emailVerified =users.isEmailVerified();
        if(!emailVerified){
            Toast.makeText(LoginActivity.this,"Verify the Email address",Toast.LENGTH_SHORT).show();
            mAuth.signOut();
            //finish();
        }else{

            Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();
        }
    }



    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
        Intent intenttoHome = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intenttoHome);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
