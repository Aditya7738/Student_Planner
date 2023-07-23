package com.example.internshiptask3.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.internshiptask3.R;
import com.example.internshiptask3.taskmanagment.AddTaskActivity;
import com.example.internshiptask3.taskmanagment.TaskManagement;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    EditText emailEt, passEt;
    Button loginBtn;
    TextView registerTxt;

    TextView forgotTv;

    FirebaseAuth firebaseAuth;

    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEt = (EditText) findViewById(R.id.stulemail);
        passEt = (EditText) findViewById(R.id.stulpass);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        registerTxt = (TextView) findViewById(R.id.registerTxt);
        forgotTv = (TextView) findViewById(R.id.fpasstxt);

        firebaseAuth = FirebaseAuth.getInstance();

        MaterialToolbar materialToolbar = (MaterialToolbar) findViewById(R.id.logintoolbar);
        setSupportActionBar(materialToolbar);

        forgotTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailf = emailEt.getText().toString();
                firebaseAuth.sendPasswordResetEmail(emailf).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Reset password email have sent", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        registerTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEt.getText().toString();
                String pass = passEt.getText().toString();
                if(email.isEmpty()){
                    emailEt.setError("Email is not entered");
                }
                if(pass.isEmpty()){
                    passEt.setError("Password is not entered");
                }

                if (!email.isEmpty() && !pass.isEmpty()){
                    firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, TaskManagement.class));
                            }else{
                                Toast.makeText(LoginActivity.this, "Either email or password is wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }


            }
        });
    }
}