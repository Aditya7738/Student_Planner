package com.example.internshiptask3.taskmanagment.notifications;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.internshiptask3.R;
import com.example.internshiptask3.taskmanagment.RecyclerViewAdapter;
import com.example.internshiptask3.taskmanagment.TaskDataModel;
import com.example.internshiptask3.taskmanagment.TaskManagement;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NotificationList extends AppCompatActivity {

    RecyclerView recyclerView;

    NotificationRVAdapter notificationRVAdapter;

    ArrayList<NotificationDataModel> notificationList;

    FirebaseFirestore firestore;

    FirebaseAuth firebaseAuth;

    String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        recyclerView = (RecyclerView) findViewById(R.id.notificationList);

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        uid = firebaseUser.getUid();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notificationList = new ArrayList<>();

        notificationRVAdapter = new NotificationRVAdapter(this, notificationList);

        recyclerView.setAdapter(notificationRVAdapter);

        EventChangeListener();
    }

    private void EventChangeListener() {
        CollectionReference collectionReference = firestore
                .collection("users").document(uid).collection("Notifications");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Toast.makeText(NotificationList.this, "Error", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (value != null) {
                    for(DocumentChange documentChange: value.getDocumentChanges()){
                        if (documentChange.getType() == DocumentChange.Type.ADDED){

                            notificationList.add(documentChange.getDocument().toObject(NotificationDataModel.class)); // here is error

                        }

                        notificationRVAdapter.notifyDataSetChanged();
                    }
                }

            }
        });
    }
}