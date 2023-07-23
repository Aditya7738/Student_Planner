package com.example.internshiptask3.taskmanagment.Attachments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.internshiptask3.R;
import com.example.internshiptask3.taskmanagment.TaskManagement;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class AttachmentAdapter extends RecyclerView.Adapter<AttachmentAdapter.ViewHolder> {

    Context context;
    List<AttachmentModel> imgList;

    ShapeableImageView uploadedImage;

    String tasktitle;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public AttachmentAdapter(Context context, List<AttachmentModel> imgList, ShapeableImageView uploadedImage, String tasktitle) {
        this.context = context;
        this.imgList = imgList;
        this.uploadedImage = uploadedImage;
        this.tasktitle = tasktitle;
    }

    @NonNull
    @Override
    public AttachmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_attachlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttachmentAdapter.ViewHolder holder, int position) {
        AttachmentModel attachmentModel = imgList.get(position);

        Uri imageurl = Uri.parse(attachmentModel.getImageUrl());
        File file = new File(imageurl.getPath());
        String filename = file.getName();

        holder.fileTv.setText(filename);
        Picasso.get().load(imageurl).into(holder.sampleIv);

        holder.deleteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                String uid = null;
                if (firebaseUser != null) {
                    uid = firebaseUser.getUid();
                }

                CollectionReference collectionReference = null;
                if (uid != null) {
                    collectionReference = firestore.collection("users").document(uid).collection("TaskDetails");
                }

                CollectionReference taskCollectionReference = collectionReference;
                if (collectionReference != null) {
                    collectionReference.whereEqualTo("TaskTitle", tasktitle).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                        String documentID = documentSnapshot.getId();
                                        CollectionReference imageCollection = taskCollectionReference
                                                .document(documentID).collection("ImageUrls");

                                        imageCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                                    String documentID = documentSnapshot.getId();
                                                    imageCollection.document(documentID)
                                                            .delete()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    Toast.makeText(context, "Image is deleted", Toast.LENGTH_SHORT).show();
                                                                    context.startActivity(new Intent(context, AttachmentList.class).putExtra("taskname", tasktitle));
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(context, "Image is not deleted", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(context, "Task is not found", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Picasso.get().load(imageurl).into(uploadedImage);

            }
        });
    }

    @Override
    public int getItemCount() {
        return imgList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView fileTv;
        ImageView sampleIv, deleteIv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fileTv = itemView.findViewById(R.id.fileName);
            sampleIv = itemView.findViewById(R.id.preview);
            deleteIv = itemView.findViewById(R.id.deleteImgTv);

            fileTv.setSelected(true);
            fileTv.setEllipsize(TextUtils.TruncateAt.MARQUEE);


        }
    }
}
