package com.example.internshiptask3.taskmanagment.Attachments;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.internshiptask3.R;
import com.example.internshiptask3.taskmanagment.TaskManagement;
import com.example.internshiptask3.taskmanagment.taskdetails.ShowTaskDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Attachment extends AppCompatActivity {
    Button uploadBtn, selectBtn, displayListBtn, cancelUploadBtn;

    ImageView noteIv;

    final int IMG_REQUEST_ID = 1;

    private Uri uri;

    String photoUrl;

    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseAuth firebaseAuth;

    FirebaseFirestore firestore;
    CollectionReference collectionReference = null;

    Intent intent;

    ListView imageLv;

    List<AttachmentModel> imagenamelist;

    String uid = null;

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
        if (result.getResultCode() == Activity.RESULT_OK){
            Intent data = result.getData();
            if (data != null && data.getData() != null){
                uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    noteIv.setImageBitmap(bitmap);
                    selectBtn.setEnabled(false);
                    uploadBtn.setEnabled(true);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    );

    String tasktitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachment);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        noteIv = (ImageView) findViewById(R.id.uploadedImage);
        //imageLv = (ListView) findViewById(R.id.imageList);
        selectBtn = (Button) findViewById(R.id.upload_doc);
        uploadBtn = (Button) findViewById(R.id.save_docBtn);
        cancelUploadBtn = (Button) findViewById(R.id.cancelUploadBtn);
        displayListBtn = (Button) findViewById(R.id.showImgsBtn);

        //noteIv.setVisibility(View.GONE);
        //imageLv.setVisibility(View.VISIBLE);

        cancelUploadBtn.setEnabled(false);

        imagenamelist = new ArrayList<>();

        uploadBtn.setEnabled(false);
        requestPermission();

        //saveBtn.setEnabled(false);
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();


        if (firebaseUser != null) {
            uid = firebaseUser.getUid();
        }


        if (uid != null) {
            collectionReference = firestore
                    .collection("users").document(uid).collection("TaskDetails");
        }

        tasktitle = getIntent().getStringExtra("aTasktitle");



        cancelUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectBtn.setEnabled(true);
                uploadBtn.setEnabled(false);
                noteIv.setImageResource(R.drawable.default_image);
                cancelUploadBtn.setEnabled(false);
            }
        });

        displayListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(Attachment.this, AttachmentList.class);
                intent2.putExtra("taskname", tasktitle);
                startActivity(intent2);
            }
        });

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select one image"), IMG_REQUEST_ID);


                launcher.launch(intent);
                //noteIv.setVisibility(View.VISIBLE);
                cancelUploadBtn.setEnabled(true);
            }
        });

//        ArrayAdapter<AttachmentModel> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, imagenamelist);
//        imageLv.setAdapter(arrayAdapter);

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //uri = intent.getData();
                if(uri != null){
                final ProgressDialog progressDialog = new ProgressDialog(Attachment.this);
                progressDialog.setTitle("Uploading...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                //.child("picture/" + UUID.randomUUID().toString()
                    StorageReference reference = storageReference.child("picture/" + uri.getLastPathSegment());

                    reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(Attachment.this, "Image saved in storage", Toast.LENGTH_SHORT).show();

                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onSuccess(Uri uri) {
                                    if (uri != null){
                                        //noteIv.setVisibility(View.GONE);
                                        //imageLv.setVisibility(View.VISIBLE);

                                        photoUrl = uri.toString();
                                        storeUrl(photoUrl);

                                        //AttachmentModel attachmentModel = new AttachmentModel(uri);

                                        selectBtn.setEnabled(true);
                                        selectBtn.setText("Select another image");
                                        noteIv.setImageResource(R.drawable.default_image);

                                        uploadBtn.setEnabled(false);
                                    }
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Attachment.this, "Didn't get download url", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Attachment.this, "Image not saved in storage", Toast.LENGTH_SHORT).show();
                                }
                            });
                }else{
                    Toast.makeText(Attachment.this, "Uri is null", Toast.LENGTH_LONG).show();
                }
            }
        });

//        CustomBaseAdapter customBaseAdapter = new CustomBaseAdapter(this,);
//        docLv.setAdapter(customBaseAdapter);
    }

    CollectionReference imageCollection = null;
    private void storeUrl(String photoUrl) {
        Map<String, Object> noteData = new HashMap<>();
        noteData.put("ImageUrl", photoUrl);


        CollectionReference finalCollectionReference = collectionReference;
        collectionReference.whereEqualTo("TaskTitle", tasktitle).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {


                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);

                            String documentID = documentSnapshot.getId();

                            imageCollection = finalCollectionReference.document(documentID).collection("ImageUrls");

                            imageCollection.add(noteData)
//                                    .set(noteData, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
//                                            addToList(documentReference);
//may be need to add both url and filename in list
                                            Toast.makeText(Attachment.this, "Image added to database", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Attachment.this, "Image is not added", Toast.LENGTH_LONG).show();
                                        }
                                    });

                        }else {
                            Toast.makeText(Attachment.this, "Task not found", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, ShowTaskDetails.class));
    }

    private void requestPermission() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }
}