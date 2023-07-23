package com.example.internshiptask3.taskmanagment;

import android.content.Context;
import android.content.Intent;
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
import com.example.internshiptask3.taskmanagment.taskdetails.ShowTaskDetails;
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

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    Context context;
    ArrayList<TaskDataModel> taskDataModelList;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();



    public RecyclerViewAdapter(Context context, ArrayList<TaskDataModel> taskDataModelList) {
        this.context = context;
        this.taskDataModelList = taskDataModelList;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        TaskDataModel taskDataModel = taskDataModelList.get(position);
        String taskTitle = taskDataModel.getTaskTitle();
        holder.taskTitle.setText(taskTitle);
        holder.taskSubject.setText(taskDataModel.getTaskSubject());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent showtaskIntent = new Intent(context, ShowTaskDetails.class);
                showtaskIntent.putExtra("duedate", taskDataModel.getDueDate());
                showtaskIntent.putExtra("title", taskDataModel.getTaskTitle());
                showtaskIntent.putExtra("subject", taskDataModel.getTaskSubject());
                showtaskIntent.putExtra("desc", taskDataModel.getTaskDesc());
                showtaskIntent.putExtra("priority", taskDataModel.getTaskPriority());

                context.startActivity(showtaskIntent);
            }
        });


        holder.editIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent updateIntent = new Intent(context, UpdateTaskActivity.class);
                updateIntent.putExtra("title", taskDataModel.getTaskTitle());
                updateIntent.putExtra("subject", taskDataModel.getTaskSubject());
                updateIntent.putExtra("desc", taskDataModel.getTaskDesc());
                updateIntent.putExtra("priority", taskDataModel.getTaskPriority());

                context.startActivity(updateIntent);
            }
        });

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

                CollectionReference finalCollectionReference = collectionReference;
                collectionReference.whereEqualTo("TaskTitle", taskTitle).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                    String documentID = documentSnapshot.getId();
                                    finalCollectionReference.document(documentID)
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(context, "Task is deleted", Toast.LENGTH_SHORT).show();
                                                    context.startActivity(new Intent(context, TaskManagement.class));
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(context, "Task is not deleted", Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                }else{
                                    Toast.makeText(context, "Task is not found", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


    }

    @Override
    public int getItemCount() {
            return taskDataModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView taskTitle, taskSubject;
        ImageView deleteIv, editIv;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            taskTitle = (TextView) itemView.findViewById(R.id.taskHead);
            taskSubject = (TextView) itemView.findViewById(R.id.subjectTxt);
            editIv = (ImageView) itemView.findViewById(R.id.editIv);
            deleteIv = (ImageView) itemView.findViewById(R.id.deleteIv);

            taskTitle.setSelected(true);
            taskTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        }
    }
}
