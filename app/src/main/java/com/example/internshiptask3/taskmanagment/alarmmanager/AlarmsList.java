package com.example.internshiptask3.taskmanagment.alarmmanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.internshiptask3.R;
import com.example.internshiptask3.taskmanagment.TaskManagement;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AlarmsList extends AppCompatActivity {

    PendingIntent pendingIntent;

    SetAlarmActivity setAlarmActivity = new SetAlarmActivity();

    AlarmManager alarmManager;

    FloatingActionButton addAlarmFAB;


    RecyclerView recyclerView;

    AlarmRVAdapter alarmRVAdapter;

    ArrayList<AlarmDataModel> alarmDataModelList;

    FirebaseFirestore firestore;

    FirebaseAuth firebaseAuth;

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);


        addAlarmFAB = (FloatingActionButton) findViewById(R.id.addAlarm);

        alarmManager = setAlarmActivity.alarmManager;

        pendingIntent = setAlarmActivity.pendingIntent;

        recyclerView = (RecyclerView) findViewById(R.id.alarmList);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            uid = firebaseUser.getUid();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        alarmDataModelList = new ArrayList<>();

        alarmRVAdapter = new AlarmRVAdapter(this, alarmDataModelList);

        recyclerView.setAdapter(alarmRVAdapter);

        EventChangeListener();

        addAlarmFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AlarmsList.this, SetAlarmActivity.class));
            }
        });

        //onSwitchOff();
    }

    private void EventChangeListener() {
        CollectionReference collectionReference = firestore
                .collection("users").document(uid).collection("AlarmDetails");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Toast.makeText(AlarmsList.this, "Error", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (value != null) {
                    alarmDataModelList.clear();
                    for(DocumentChange documentChange: value.getDocumentChanges()){
                        if (documentChange.getType() == DocumentChange.Type.ADDED){

                            alarmDataModelList.add(documentChange.getDocument().toObject(AlarmDataModel.class)); // here is error

                        }

                        alarmRVAdapter.notifyDataSetChanged();
                    }
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, TaskManagement.class));
    }
}