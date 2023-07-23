package com.example.internshiptask3.taskmanagment.insights;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.internshiptask3.R;

import java.util.ArrayList;
import java.util.List;

public class AssignedTaskAdapter extends BaseAdapter {
//    public AssignedTaskAdapter(@NonNull Context context, @NonNull ArrayList<AssignedDataModel> assignedTaskList) {
//        super(context, R.layout.custom_assign, assignedTaskList);
//    }
//
//    public View getView(int position, @Nullable  View view, @NonNull ViewGroup viewGroup) {
//        AssignedDataModel assignedDataModel = getItem(position);
//        if (view == null){
//            view = LayoutInflater.from(getContext()).inflate(R.layout.custom_assign, viewGroup, false);
//        }
//
//        TextView titleTv = (TextView) view.findViewById(R.id.assigntask_title);
//        TextView subTv = (TextView) view.findViewById(R.id.assigntask_subject);
//        TextView timeTv = (TextView) view.findViewById(R.id.adue_date);
//
//
//        titleTv.setText(assignedDataModel.getTaskTitle());
//        subTv.setText(assignedDataModel.getTaskSubject());
//        timeTv.setText(assignedDataModel.getDueDate());
//        return view;
//    }

    Context context;
    ArrayList<AssignedDataModel> assignedTaskList;
    //LayoutInflater layoutInflater;

    public AssignedTaskAdapter(Context context, ArrayList<AssignedDataModel> assignedTaskList) {
        super();
        this.context = context;
        this.assignedTaskList = assignedTaskList;

//        layoutInflater = LayoutInflater.from(context);
    }

    public void updateList(ArrayList<AssignedDataModel> assignedArrayTaskList){
        assignedTaskList = assignedArrayTaskList;
        notifyDataSetChanged();
    }



    @Override
    public int getCount() {
        if(assignedTaskList == null) {
            return 0;
        }else {
            return assignedTaskList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        AssignedTaskAdapter.ViewHolder holder;

        if (view == null) {
            holder = new AssignedTaskAdapter.ViewHolder();

            view = LayoutInflater.from(context).inflate(R.layout.custom_assign, viewGroup, false);
            holder.titleTv = (TextView) view.findViewById(R.id.assigntask_title);
            holder.subTv = (TextView) view.findViewById(R.id.assigntask_subject);
            holder.timeTv = (TextView) view.findViewById(R.id.adue_date);
            view.setTag(holder);
        }else {
            holder = (AssignedTaskAdapter.ViewHolder) view.getTag();
        }

        AssignedDataModel assignedDataModel = assignedTaskList.get(position);
        holder.titleTv.setText(assignedDataModel.getTaskTitle());
        holder.subTv.setText(assignedDataModel.getTaskSubject());
        holder.timeTv.setText(assignedDataModel.getDueDate());
        return view;
    }

    public static class ViewHolder {
        TextView titleTv, subTv, timeTv;
    }
}
