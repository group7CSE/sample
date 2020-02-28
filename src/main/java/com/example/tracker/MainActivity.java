package com.example.tracker;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.widget.TooltipCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Intent;
import android.widget.TextView;

import java.util.*;

//necessary for mail to admin

import java.util.Properties;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //defining view objects
    private EditText editTextEmail, editTextName, editTextRollno;
    private Button buttonSignup;
    private TextView textViewSignin, textViewOther;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    String name, email, rollno, pass, designation = "Select";

    Database database = new Database(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        //if getCurrentUser does not returns null

        Log.e("FirebaseAuth", "" + firebaseAuth.getCurrentUser());
        if (firebaseAuth.getCurrentUser() != null) {
            //that means user is already logged in
            //so close this activity
            finish();

            //and open profile activity
            startActivity(new Intent(getApplicationContext(), UserActivity.class));

        }

        //initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextRollno = (EditText) findViewById(R.id.editTextRollno);


        textViewSignin = (TextView) findViewById(R.id.textViewSignin);
        textViewOther = (TextView)findViewById(R.id.textViewOther);

        buttonSignup = (Button) findViewById(R.id.buttonSignup);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.names));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0)
                    designation = "Select";
                if (i == 1) {
                    designation = "Student";
                } else if (i == 2) {
                    designation = "Staff";
                }
                System.out.println(designation);

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //user details table
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        progressDialog = new ProgressDialog(this);

        buttonSignup.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);
        textViewOther.setOnClickListener(this);
    }

    //Password generation
    // Java code to generate random password
    // Here we are using random() method of util class in Java
    // This our Password generating method
    // We have use static here, so that we not to make any object for it
    static String geek_Password(int len) {
        Log.e("Geek _password", "Generating password using random() : ");
        Log.e("", "Your new password is : ");

        // A strong password has Cap_chars, Lower_chars,
        // numeric value and symbols. So we are using all of
        // them to generate our password
        String Capital_chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String Small_chars = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String pass = "";

        String values = Capital_chars + Small_chars +
                numbers ;

        // Using random method
        Random rndm_method = new Random();

        for (int i = 0; i < len; i++) {
            // Use of charAt() method : to get character value
            // Use of nextInt() as it is scanning the value as int
            pass += values.charAt(rndm_method.nextInt(values.length()));

        }
        Log.e("", "" + pass);
        return pass;
    }

    private void registerUser() {

        name = editTextName.getText().toString().trim();
        email = editTextEmail.getText().toString().trim().toLowerCase();
        rollno = editTextRollno.getText().toString().trim().toLowerCase();
        pass = geek_Password(8);

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this,"Please enter name",Toast.LENGTH_LONG).show();
            return;
        }
       if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(rollno)) {
            Toast.makeText(this, "Please enter designation", Toast.LENGTH_LONG).show();
            return;

        }

        Toast.makeText(this, "After successful verification your email and password will be sent to your mail!", Toast.LENGTH_LONG).show();

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Log.e("Register","password");
                        //checking if success
                        if (task.isSuccessful()) {
                            finish();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));

                            //sending mail to admin

                            //Getting content for email
                            String email1 = "dheepigaraja@gmail.com".toString().trim();
                            String subject = "Request for tracking".toString().trim();
                            String message = "Track me too!\nMy name : " + name + "\nMy email : " + email + "\nMy Rollno : " + rollno
                                    + "\nPassword generated for this user : " + pass.toString().trim();

                            //Creating SendMail object
                            SendMail sm = new SendMail(email1, subject, message);
                            sm.execute();

                            database.add_UserDetails(name, rollno, email, pass);

                            //Executing sendmail to send email
                        } else {
                            //display some message here
                            Toast.makeText(MainActivity.this, "Registration Error", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }

                });

    }

    @Override
    public void onClick(View view) {

        if (view == buttonSignup) {
                registerUser();
        }
        if(view == textViewSignin){
            //open login activity when user taps on the already registered textview
                startActivity(new Intent(this, LoginActivity.class));
        }
        if(view == textViewOther)
            startActivity(new Intent(this,OtherOTPLogin.class));

    }
}