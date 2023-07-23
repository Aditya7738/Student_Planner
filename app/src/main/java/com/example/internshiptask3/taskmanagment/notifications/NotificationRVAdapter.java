package com.example.internshiptask3.taskmanagment.notifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.internshiptask3.R;
import com.example.internshiptask3.taskmanagment.RecyclerViewAdapter;
import com.example.internshiptask3.taskmanagment.TaskDataModel;

import java.util.ArrayList;

public class NotificationRVAdapter extends RecyclerView.Adapter<NotificationRVAdapter.ViewHolder> {

    Context context;
    ArrayList<NotificationDataModel> notificationList;

    public NotificationRVAdapter(Context context, ArrayList<NotificationDataModel> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_cardview, parent,false);
        return new NotificationRVAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationRVAdapter.ViewHolder holder, int position) {
        NotificationDataModel dataModel = notificationList.get(position);

        holder.taskTitle.setText(dataModel.getTasktitle());
        //holder.taskDueDate.setText(dataModel.getSelectedtime());
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                send to task by db.where.get
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView taskTitle, taskDueDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            taskTitle = (TextView) itemView.findViewById(R.id.ntasktile);
            taskDueDate = (TextView) itemView.findViewById(R.id.ndue_time);
        }
    }
}
