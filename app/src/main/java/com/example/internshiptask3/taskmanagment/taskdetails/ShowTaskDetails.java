package com.example.internshiptask3.taskmanagment.taskdetails;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.internshiptask3.R;
import com.example.internshiptask3.taskmanagment.Attachments.Attachment;
import com.google.android.material.appbar.MaterialToolbar;

public class ShowTaskDetails extends AppCompatActivity {

    TextView duedateTxt, taskTitleTv, taskDescTv, taskPriTv, taskSubTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_task_details);

        MaterialToolbar materialToolbar = (MaterialToolbar) findViewById(R.id.showtasktoolbar);
        setSupportActionBar(materialToolbar);

        duedateTxt = (TextView) findViewById(R.id.showtaskduedate);
        taskTitleTv = (TextView) findViewById(R.id.showtasktitle);
        taskDescTv = (TextView) findViewById(R.id.showtaskdesc);
        taskPriTv = (TextView) findViewById(R.id.showpriority);
        taskSubTv = (TextView) findViewById(R.id.showsubject);

        taskTitleTv.setSelected(true);
        taskTitleTv.setEllipsize(TextUtils.TruncateAt.MARQUEE);



        Bundle bundle = getIntent().getExtras();

        boolean istaskupdated = bundle.getBoolean("updated");

        if(istaskupdated){

            duedateTxt.setText(bundle.getString("new duedate"));

            taskTitleTv.setText(bundle.getString("new tasktitle"));

            taskDescTv.setText(bundle.getString("new desc"));
            taskPriTv.setText(bundle.getString("new priority"));
            taskSubTv.setText(bundle.getString("new subject"));
        }else {

            Intent data = getIntent();

            duedateTxt.setText(data.getStringExtra("duedate"));

            taskTitleTv.setText(data.getStringExtra("title"));

            taskDescTv.setText(data.getStringExtra("desc"));
            taskPriTv.setText(data.getStringExtra("priority"));
            taskSubTv.setText(data.getStringExtra("subject"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.editNote){
            Intent noteIntent = new Intent(ShowTaskDetails.this, EditNote.class);
            noteIntent.putExtra("Tasktitle", taskTitleTv.getText().toString());
            startActivity(noteIntent);
            return true;
        } else if (id == R.id.attachFiles) {
            Intent attachIntent = new Intent(ShowTaskDetails.this, Attachment.class);
            attachIntent.putExtra("aTasktitle", taskTitleTv.getText().toString());
            startActivity(attachIntent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}