package com.example.internshiptask3.taskmanagment.Attachments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.internshiptask3.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AttachmentList extends AppCompatActivity {
    FloatingActionButton addImgFAB;

    RecyclerView recyclerView;

    FirebaseFirestore firestore;

    FirebaseAuth firebaseAuth;

    String uid;

    AttachmentAdapter attachmentAdapter;

    List<AttachmentModel> imgList;
    CollectionReference collectionReference;
    CollectionReference imageCollection;

    ShapeableImageView previewImg;

    TextView infoTxt;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atttachment_list);

        MaterialToolbar materialToolbar = (MaterialToolbar) findViewById(R.id.imglistToolbar);
        setSupportActionBar(materialToolbar);

        previewImg = (ShapeableImageView) findViewById(R.id.uploadedImage);
        //previewImg.setVisibility(View.GONE);

        imgList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.imgList);

        infoTxt = (TextView) findViewById(R.id.info);
        infoTxt.setVisibility(View.GONE);



        addImgFAB = (FloatingActionButton) findViewById(R.id.addImgFABbtn);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            uid = firebaseUser.getUid();
        }

        if (uid != null) {
            collectionReference = firestore
                    .collection("users").document(uid).collection("TaskDetails");
            //imageCollection = finalCollectionReference.document(documentID).collection("ImageUrls");
        }

        String tasktitle = getIntent().getStringExtra("taskname");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        attachmentAdapter = new AttachmentAdapter(this, imgList, previewImg, tasktitle);

        recyclerView.setAdapter(attachmentAdapter);





        addImgFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(AttachmentList.this, Attachment.class);
                intent2.putExtra("aTasktitle", tasktitle);
                startActivity(intent2);

            }
        });


        Log.d("ATTACH", "STEP 1");

        CollectionReference finalCollectionReference = collectionReference;
        collectionReference.whereEqualTo("TaskTitle", tasktitle).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {

                            Log.d("ATTACH", "STEP 2");
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);

                            String documentID = documentSnapshot.getId();

                            imageCollection = finalCollectionReference.document(documentID).collection("ImageUrls");

                            imageCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {

                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    Log.d("ATTACH", "STEP 3");
                                    if (error != null) {
                                        Log.d("ATTACH", "STEP 4");
                                        Toast.makeText(AttachmentList.this, "Error", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    imgList.clear();
                                    if (value != null) {
                                        Log.d("ATTACH", "STEP 5");
                                        Log.d("ATTACHMENT", "INSIDE IMGCOL VAL");
                                        for (DocumentChange documentChange : value.getDocumentChanges()) {
                                            if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                                Log.d("ATTACH", "STEP 6");
                                                Log.d("ATTACHMENT", "INSIDE IMGCOL VAL if");
                                                imgList.add(documentChange.getDocument().toObject(AttachmentModel.class)); // here is error

                                            }
                                            attachmentAdapter.notifyDataSetChanged();

                                        }
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(AttachmentList.this, "Task is not found", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        if(imgList.size() < 1){
            infoTxt.setVisibility(View.VISIBLE);
        }
    }
}