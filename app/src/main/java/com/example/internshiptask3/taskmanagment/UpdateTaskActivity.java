package com.example.internshiptask3.taskmanagment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.internshiptask3.R;
import com.example.internshiptask3.taskmanagment.taskdetails.ShowTaskDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class UpdateTaskActivity extends AppCompatActivity {

    EditText taskTitleEt, taskDescEt, taskPriEt, taskSubEt;
    Button updatetaskBtn, displayTaskBtn;

    DatePicker datePicker;
    TimePicker timePicker;

    FirebaseAuth firebaseAuth;

    FirebaseFirestore firestore;

    String oldtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_task);

        taskTitleEt = (EditText) findViewById(R.id.utasktitle);
        taskDescEt = (EditText) findViewById(R.id.utaskdesc);
        taskPriEt = (EditText) findViewById(R.id.upriority);
        taskSubEt = (EditText) findViewById(R.id.usubject);

        datePicker = (DatePicker) findViewById(R.id.udatePicter);
        timePicker = (TimePicker) findViewById(R.id.utimepicker);

        updatetaskBtn = (Button) findViewById(R.id.updatetaskBtn);
        displayTaskBtn = (Button) findViewById(R.id.displaytaskBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        Intent data = getIntent();

        oldtitle = data.getStringExtra("title");
        taskTitleEt.setText(data.getStringExtra("title"));
        taskDescEt.setText(data.getStringExtra("desc"));
        taskPriEt.setText(data.getStringExtra("priority"));
        taskSubEt.setText(data.getStringExtra("subject"));

        updatetaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                String uid = null;
                if (firebaseUser != null) {
                    uid = firebaseUser.getUid();
                }
                String title = taskTitleEt.getText().toString();
                String desc = taskDescEt.getText().toString();
                String priority = taskPriEt.getText().toString();
                String subject = taskSubEt.getText().toString();


                String dayOfMonth = String.valueOf(datePicker.getDayOfMonth());
                String month = String.valueOf(datePicker.getMonth());
                String year = String.valueOf(datePicker.getYear());

                String minute = String.valueOf(timePicker.getMinute());
                String hour = String.valueOf(timePicker.getHour());

                String due_date = dayOfMonth +"/"+ month + "/" + year + ", " + hour + ":" + minute;

                CollectionReference collectionReference = null;
                if (uid != null) {
                    collectionReference = firestore.collection("users").document(uid).collection("TaskDetails");
                }

                Map<String, Object> taskDetails = new HashMap<>();
                taskDetails.put("TaskTitle", title);
                taskDetails.put("TaskDesc", desc);
                taskDetails.put("TaskPriority", priority);
                taskDetails.put("TaskSubject", subject);
                taskDetails.put("DueDate", due_date);

                CollectionReference finalCollectionReference = collectionReference;
                collectionReference.whereEqualTo("TaskTitle", oldtitle).get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful() && !task.getResult().isEmpty()){
                                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                            String documentID = documentSnapshot.getId();
                                            finalCollectionReference.document(documentID).update(taskDetails)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Toast.makeText(UpdateTaskActivity.this, "Data updated", Toast.LENGTH_SHORT).show();
                                                            Boolean isTaskUpdated = true;

                                                            Intent updatedTask = new Intent(UpdateTaskActivity.this, ShowTaskDetails.class);
                                                            Bundle bundle = new Bundle();
                                                            bundle.putString("new duedate", due_date);
                                                            bundle.putString("new tasktitle", title);
                                                            bundle.putString("new subject", subject);
                                                            bundle.putString("new desc", desc);
                                                            bundle.putString("new priority", priority);
                                                            bundle.putBoolean("updated", true);
                                                            updatedTask.putExtras(bundle);
                                                            startActivity(updatedTask);
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(UpdateTaskActivity.this, "Data is not updated", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }else {
                                            Toast.makeText(UpdateTaskActivity.this, "Data not found", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });




            }
        });

        displayTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UpdateTaskActivity.this, TaskManagement.class));
            }
        });




    }
}