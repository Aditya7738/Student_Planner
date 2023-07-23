package com.example.internshiptask3.taskmanagment.taskdetails;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.internshiptask3.R;
import com.example.internshiptask3.taskmanagment.AddTaskActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class EditNote extends AppCompatActivity {

    Button backBtn, saveBtn;

    EditText noteEt;
    FirebaseAuth firebaseAuth;

    FirebaseFirestore firestore;

    private CollectionReference collectionReference = null;

    String tasktitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);


        saveBtn = (Button) findViewById(R.id.saveBtn);
        noteEt = (EditText) findViewById(R.id.editNote);

        firebaseAuth = FirebaseAuth.getInstance();

        firestore = FirebaseFirestore.getInstance();

        tasktitle = getIntent().getStringExtra("Tasktitle");

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String uid = null;
        if (firebaseUser != null) {
            uid = firebaseUser.getUid();
        }


        if (uid != null) {
            collectionReference = firestore
                    .collection("users").document(uid).collection("TaskDetails");
        }

        retrieveNotes();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String notes = noteEt.getText().toString();

                Map<String, Object> noteData = new HashMap<>();
                noteData.put("Notes", notes);

                CollectionReference finalCollectionReference = collectionReference;
                collectionReference.whereEqualTo("TaskTitle", tasktitle).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                    String documentID = documentSnapshot.getId();
                                    finalCollectionReference.document(documentID)
                                            .set(noteData, SetOptions.merge())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(EditNote.this, "Notes are saved", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(EditNote.this, "Notes is not added", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                }else {
                                    Toast.makeText(EditNote.this, "Task not found", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

            }
        });


    }

    private void retrieveNotes() {
        collectionReference.whereEqualTo("TaskTitle", tasktitle)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()){
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            String documentId = documentSnapshot.getId();

                            collectionReference.document(documentId)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot result = task.getResult();
                                        noteEt.setText(String.valueOf(result.get("Notes")));
                                    }else {
                                        Toast.makeText(EditNote.this, "Didn't get notes", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }else{
                            Toast.makeText(EditNote.this, "Task not found", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}