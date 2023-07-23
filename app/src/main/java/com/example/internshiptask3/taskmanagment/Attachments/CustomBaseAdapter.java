package com.example.internshiptask3.taskmanagment.Attachments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.internshiptask3.R;

import java.util.List;

public class CustomBaseAdapter extends BaseAdapter {

    Context context;
    List<AttachmentModel> docList;
    LayoutInflater layoutInflater;

    public CustomBaseAdapter(Context context, List<AttachmentModel> docList, LayoutInflater layoutInflater) {
        this.context = context;
        this.docList = docList;
        this.layoutInflater = layoutInflater;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if(docList == null) {
            return 0;
        }else {
            return docList.size();
        }
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        view = layoutInflater.inflate(R.layout.custom_doc_lv, null);
        TextView textView = (TextView) view.findViewById(R.id.docName);
//        textView.setText(docList.get(position));
        return view;
    }
}
