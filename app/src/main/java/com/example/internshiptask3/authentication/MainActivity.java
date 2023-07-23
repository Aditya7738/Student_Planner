package com.example.internshiptask3.authentication;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.internshiptask3.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    EditText stuNameEt, stuEmailEt, stuPassEt, stuCPassEt;
    Button registerBtn;
    TextView loginTxt;

    CheckBox privacyCheck;

    FirebaseAuth firebaseAuth;

    FirebaseFirestore firestore;

    ActivityResultLauncher<String[]> mPermissionResultLauncher;;
    private boolean isReadExternalStorageGranted;
    private boolean isPOSTNOTIFICATIONSGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stuNameEt = (EditText) findViewById(R.id.stuname);
        stuEmailEt = (EditText) findViewById(R.id.stuemail);
        stuPassEt = (EditText) findViewById(R.id.stupass);
        stuCPassEt = (EditText) findViewById(R.id.stucompass);
        loginTxt = (TextView) findViewById(R.id.loginTxt);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        privacyCheck = (CheckBox) findViewById(R.id.acceptcheck);

        firebaseAuth = FirebaseAuth.getInstance();

        firestore = FirebaseFirestore.getInstance();

        mPermissionResultLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                new ActivityResultCallback<Map<String, Boolean>>() {
                    @Override
                    public void onActivityResult(Map<String, Boolean> result) {
                        if(result.get(android.Manifest.permission.READ_EXTERNAL_STORAGE) != null){
                            isReadExternalStorageGranted = Boolean.TRUE.equals(result.get(android.Manifest.permission.READ_EXTERNAL_STORAGE));
                        }

//                        if (result.get(android.Manifest.permission.POST_NOTIFICATIONS) != null){
//                            isPOSTNOTIFICATIONSGranted = Boolean.TRUE.equals(result.get(android.Manifest.permission.POST_NOTIFICATIONS));
//                        }
                    }
                });

        runtimePermission();


        MaterialToolbar materialToolbar = (MaterialToolbar) findViewById(R.id.maintoolbar);
        setSupportActionBar(materialToolbar);

        loginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = stuNameEt.getText().toString();
                String email = stuEmailEt.getText().toString();
                String pass = stuPassEt.getText().toString();
                String cpass = stuCPassEt.getText().toString();
                boolean checked = privacyCheck.isChecked();

                if(name.isEmpty()){
                    stuNameEt.setError("Student's name is not entered");
                }
                if(email.isEmpty()){
                    stuEmailEt.setError("Email is not entered");
                }
                if(pass.isEmpty()){
                    stuPassEt.setError("Password is not entered");
                }
                if(pass.length() < 6){
                    stuPassEt.setError("Password length is less than 6");
                }
                if(cpass.isEmpty()){
                    stuCPassEt.setError("Confirm password is not entered");
                }

                if(!pass.equals(cpass)){
                    stuCPassEt.setError("Passwords are not matching");
                }

                if (!checked){
                    privacyCheck.setError("Didn't accept statements and conditions");
                }

                if(!name.isEmpty() && !email.isEmpty() && !pass.isEmpty() && pass.length() >= 6 && !cpass.isEmpty() && pass.equals(cpass) && checked) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                                if (firebaseUser != null) {
                                    firebaseUser.sendEmailVerification()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(MainActivity.this, "Registered successfully. Please check your email and verify", Toast.LENGTH_LONG).show();

                                            String uid = firebaseUser.getUid();

                                            DocumentReference documentReference = firestore.collection("users").document(uid);

                                            Map<String, Object> userData = new HashMap<>();
                                            userData.put("name", name);
                                            userData.put("email", email);

                                            documentReference.set(userData)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Toast.makeText(MainActivity.this, "User data stored successfully", Toast.LENGTH_LONG).show();
                                                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(MainActivity.this, "User data is not get stored", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });


                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(MainActivity.this, "User is not registered", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        }
                    });
                }

            }
        });

    }

    //@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void runtimePermission() {

        Log.d("MainActivity", "step 3");
        isReadExternalStorageGranted = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        isPOSTNOTIFICATIONSGranted = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;

        List<String> permissionList = new ArrayList<String>();

        if (!isReadExternalStorageGranted) {
            new AlertDialog.Builder(this)
                    .setTitle("Required Permission")
                    .setMessage("To upload your study material from internal storage, please give this permission manually. Click on Settings. \n " +
                            "App permissions -> Click on Files and media -> select Allow")
                    .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                        }
                    }).show();

            permissionList.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (!permissionList.isEmpty()) {
            Log.d("MainActivity", "step 5");
            mPermissionResultLauncher.launch(permissionList.toArray(new String[0]));
        }
    }
}