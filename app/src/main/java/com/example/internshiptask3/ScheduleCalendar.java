package com.example.internshiptask3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.internshiptask3.taskmanagment.AddTaskActivity;
import com.example.internshiptask3.taskmanagment.TaskManagement;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScheduleCalendar extends AppCompatActivity {

    CalendarView calendarView;
    EditText eventEt;
    Button addEventBtn;

    TextView eventTv;

    FirebaseFirestore firestore;

    FirebaseAuth firebaseAuth;

    ListView eventLv;

    ArrayList<String> eventList;

    String selectedDate;
    String event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_calendar);

        calendarView = (CalendarView) findViewById(R.id.calendarView);
        eventEt = (EditText) findViewById(R.id.event);
        addEventBtn = (Button) findViewById(R.id.addEvent);
        //eventTv = (TextView) findViewById(R.id.eventNameTv);
        eventLv = (ListView) findViewById(R.id.eventList);


        eventList = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String uid = null;
        if (firebaseUser != null) {
            uid = firebaseUser.getUid();
        }

        CollectionReference collectionReference = null;
        if (uid != null) {
            collectionReference = firestore
                    .collection("users").document(uid).collection("CalendarEvents");
        }

        CollectionReference finalCollectionReference = collectionReference;
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                int updatemonth = month + 1;
                selectedDate = day + "/" + updatemonth + "/" + year;
                finalCollectionReference
                        .whereEqualTo("Date", selectedDate)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
//                                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
//                                    String documentID = documentSnapshot.getId();
//                                    finalCollectionReference.document(documentID).get().
                                    eventList.clear();
                                    for (QueryDocumentSnapshot document: task.getResult()){
                                        eventList.add(String.valueOf(document.get("Event")));
                                    }
                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ScheduleCalendar.this, android.R.layout.simple_list_item_1, eventList);
                                    eventLv.setAdapter(arrayAdapter);
                                }else{
                                    eventList.clear();
                                    eventList.add("Event is not scheduled yet");
                                    ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>(ScheduleCalendar.this, android.R.layout.simple_list_item_1, eventList);
                                    eventLv.setAdapter(arrayAdapter2);
                                }
                            }
                        });
                    }
                });



        CollectionReference finalCollectionReference1 = collectionReference;
        addEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                event = eventEt.getText().toString();

                Map<String, Object> calendarEvents = new HashMap<>();
                calendarEvents.put("Date",selectedDate);
                calendarEvents.put("Event", event);

                finalCollectionReference1
                        .add(calendarEvents)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(ScheduleCalendar.this, "Event stored successfully", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(ScheduleCalendar.this, ScheduleCalendar.class));
                                }else {
                                    Toast.makeText(ScheduleCalendar.this, "Could not store event", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


    }
}