package com.example.internshiptask3.taskmanagment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.internshiptask3.R;
import com.example.internshiptask3.taskmanagment.notifications.NotificationReceiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddTaskActivity extends AppCompatActivity {

    EditText taskTitleEt, taskDescEt, taskPriEt, taskSubEt;
    DatePicker datePicker;
    TimePicker timePicker;
    Button storeBtn, displayTaskBtn;

    FirebaseAuth firebaseAuth;

    FirebaseFirestore firestore;

    String taskTitle, taskSub;

    public AlarmManager alarmManager;

    public PendingIntent pendingIntent;

    Calendar calendar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        taskTitleEt = (EditText) findViewById(R.id.tasktitle);
        taskDescEt = (EditText) findViewById(R.id.taskdesc);
        taskPriEt = (EditText) findViewById(R.id.priority);
        taskSubEt = (EditText) findViewById(R.id.subject);

        datePicker = (DatePicker) findViewById(R.id.datePicter);
        timePicker = (TimePicker) findViewById(R.id.timepicker);

        storeBtn = (Button) findViewById(R.id.addtaskBtn);
        displayTaskBtn = (Button) findViewById(R.id.displaytaskBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        calendar = Calendar.getInstance();

        createNotificationChannel();

        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
            }
        });

        displayTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddTaskActivity.this, TaskManagement.class));
            }
        });

        storeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                String uid = null;
                if (firebaseUser != null) {
                    uid = firebaseUser.getUid();
                }




                taskTitle = taskTitleEt.getText().toString();
                String taskDesc = taskDescEt.getText().toString();
                String taskPri = taskPriEt.getText().toString();
                taskSub = taskSubEt.getText().toString();

                int idayOfMonth = datePicker.getDayOfMonth();
                String dayOfMonth = String.valueOf(datePicker.getDayOfMonth());
                int imonth = datePicker.getMonth() + 1;
                String month = String.valueOf(imonth);
                int iyear = datePicker.getYear();
                String year = String.valueOf(datePicker.getYear());


                int iminute = timePicker.getMinute();
                String minute = String.valueOf(timePicker.getMinute());
                int ihour = timePicker.getHour();
                String hour = String.valueOf(timePicker.getHour());

                long selectedDateTime = idayOfMonth + imonth + iyear + iminute + ihour;


                //Date time = calendar.getTime();

                Log.d("Addtask", "selectedDateTime:" + selectedDateTime);

                String due_time;
                if(timePicker.getHour() > 12){
                    due_time = dayOfMonth + "/" + month + "/" + year + ", " + String.format("%02d", (timePicker.getHour()-12))+ ":" +
                            String.format("%02d", timePicker.getMinute()) + " PM";


                }else {
                    due_time = dayOfMonth + "/" + month + "/" + year + ", " + String.format("%02d", (timePicker.getHour()-12))+ ":" +
                            String.format("%02d", timePicker.getMinute()) + " AM";


                }

                String due_date = dayOfMonth + "/" + month + "/" + year + ", " + hour + ":" + minute;
                //String due_time = hour + ":" + minute;

                calendar.set(Calendar.YEAR, iyear);
                calendar.set(Calendar.MONTH, imonth);
                calendar.set(Calendar.DAY_OF_MONTH, idayOfMonth);
                calendar.set(Calendar.HOUR_OF_DAY, ihour);
                calendar.set(Calendar.MINUTE, iminute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                Date selectedtime = calendar.getTime();

//                int s = datePicker.getDayOfMonth() + i + datePicker.getYear() + timePicker.getHour() + timePicker.getMinute();
//                long datetime = (long) s - 24*60*60*1000;

                //String due_date = dayOfMonth +"/"+ month + "/" + year;

                CollectionReference collectionReference = null;
                if (uid != null) {
                    collectionReference = firestore.collection("users").document(uid).collection("TaskDetails");
                }

                Map<String, Object> taskDetails = new HashMap<>();
                taskDetails.put("TaskTitle", taskTitle);
                taskDetails.put("TaskDesc", taskDesc);
                taskDetails.put("TaskPriority", taskPri);
                taskDetails.put("TaskSubject", taskSub);
                //taskDetails.put("dayOfMonth", dayOfMonth);
                //taskDetails.put("month", month);
                //taskDetails.put("year", year);
                //taskDetails.put("minute", minute);
                //taskDetails.put("hour", hour);
                taskDetails.put("DueDate", due_time);
                taskDetails.put("selectedtime", selectedtime);


                collectionReference
                        .add(taskDetails)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AddTaskActivity.this, "Task details stored successfully", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(AddTaskActivity.this, TaskManagement.class));
                                } else {
                                    Toast.makeText(AddTaskActivity.this, "Could not store Task details", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

//                collectionReference.whereEqualTo("TaskTitle",taskTitle).get()
//                        .

                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                CharSequence ctaskTitle = taskTitle;
                CharSequence cdue_time = due_time;

                Intent intent = new Intent(AddTaskActivity.this, NotificationReceiver.class);
                intent.putExtra("tasktitle",ctaskTitle);
                intent.putExtra("time", cdue_time);



                pendingIntent = PendingIntent.getBroadcast(AddTaskActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

                long l = calendar.getTimeInMillis() - 24 * 60 * 60 * 1000;
                //Date time = calendar.getTime();
                Log.d("DATETIME", "selected" + l);

                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - 24*60*60*1000, pendingIntent);



            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "TaskRemainderChannel";
            String description = "Channel for task remainder";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("TaskRemainder", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, TaskManagement.class));
    }
}
