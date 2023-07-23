package com.example.internshiptask3.taskmanagment.alarmmanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AlarmRVAdapter extends RecyclerView.Adapter<AlarmRVAdapter.ViewHolder> {

    Context context;

    ArrayList<AlarmDataModel> alarmDataModelList;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public AlarmRVAdapter(Context context, ArrayList<AlarmDataModel> alarmDataModelList) {
        this.context = context;
        this.alarmDataModelList = alarmDataModelList;
    }

    @NonNull
    @Override
    public AlarmRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.alarm_listview, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmRVAdapter.ViewHolder holder, int position) {
        AlarmDataModel alarmDataModel = alarmDataModelList.get(position);

        String event = alarmDataModel.getEvent();

        holder.eventTv.setText(alarmDataModel.getEvent());
        holder.timeTv.setText(alarmDataModel.getSelectedtime());

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
                    collectionReference = firestore.collection("users").document(uid).collection("AlarmDetails");
                }

                CollectionReference finalCollectionReference = collectionReference;
                collectionReference.whereEqualTo("event", event).get()
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
                                                    Toast.makeText(context, "Alarm is deleted", Toast.LENGTH_SHORT).show();
                                                    context.startActivity(new Intent(context, AlarmsList.class));
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(context, "Alarm is not deleted", Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                } else {
                                    Toast.makeText(context, "Task is not found", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }


    @Override
    public int getItemCount() {
        return alarmDataModelList.size();
    }

//    private void onSwitchOff() {
//
//        if (alarmManager == null){
//            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        }
//
//        alarmManager.cancel(pendingIntent);
//
//        //Toast.makeText(setAlarmActivity, "Alarm cancelled", Toast.LENGTH_SHORT).show();
//    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView timeTv, eventTv;
        ImageView deleteIv;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            timeTv = itemView.findViewById(R.id.setTime);

            eventTv = itemView.findViewById(R.id.event);
            eventTv.setSelected(true);
            eventTv.setEllipsize(TextUtils.TruncateAt.MARQUEE);

            deleteIv = itemView.findViewById(R.id.deleteAlarmIv);


        }
    }
}
