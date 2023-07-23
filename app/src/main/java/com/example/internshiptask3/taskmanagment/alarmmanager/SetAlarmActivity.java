package com.example.internshiptask3.taskmanagment.alarmmanager;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.internshiptask3.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SetAlarmActivity extends AppCompatActivity {

    TextInputEditText evenEt;
    Button setAlarmBtn, setTimeBtn, displayBtn;

    public String event, selectedTime;

    TextView selectedTimeTv;

    MaterialTimePicker timePicker;

    TimePicker alarmtimePicker;

    Calendar calendar;

    FirebaseAuth firebaseAuth;

    FirebaseFirestore firestore;

    public AlarmManager alarmManager;

    public PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        evenEt = (TextInputEditText) findViewById(R.id.eventName);
        setAlarmBtn = (Button) findViewById(R.id.setAlarm);
        //setTimeBtn = (Button) findViewById(R.id.set_time);
        displayBtn = (Button) findViewById(R.id.displayAlarm);
        //selectedTimeTv = (TextView) findViewById(R.id.selectedTimeTxt);
        alarmtimePicker = (TimePicker) findViewById(R.id.alarmtimepicker);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        createNotificationChannel();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String uid = null;
        if (firebaseUser != null) {
            uid = firebaseUser.getUid();
        }

        CollectionReference collectionReference = null;
        if (uid != null) {
            collectionReference = firestore
                    .collection("users").document(uid).collection("AlarmDetails");
        }

        displayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SetAlarmActivity.this, AlarmsList.class));
            }
        });



        calendar = Calendar.getInstance();


        CollectionReference finalCollectionReference = collectionReference;

        setAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                event = evenEt.getText().toString();

                String stime = "Selected time";

                if (alarmtimePicker.getHour() > 12) {
                    stime = String.format("%02d", (alarmtimePicker.getHour() - 12)) + ":" +
                            String.format("%02d", alarmtimePicker.getMinute()) + " PM";
                    //selectedTimeTv.setText(selectedTime);

                } else {
                    stime = alarmtimePicker.getHour() + ":" + alarmtimePicker.getMinute() + " AM";
                    //selectedTimeTv.setText(selectedTime);
                }


                calendar.set(Calendar.HOUR_OF_DAY, alarmtimePicker.getHour());
                calendar.set(Calendar.MINUTE, alarmtimePicker.getMinute());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);




                Map<String, Object> alarmDetail = new HashMap<>();
                alarmDetail.put("selectedtime", stime);
                alarmDetail.put("event", event);

                CharSequence cevent = event;
                CharSequence ctime = stime;


                if (finalCollectionReference != null) {
                    finalCollectionReference
                            .add(alarmDetail)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SetAlarmActivity.this, "Your alarm is set", Toast.LENGTH_SHORT).show();

                                        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                                        Intent intent = new Intent(SetAlarmActivity.this, AlarmReceiver.class);
                                        intent.putExtra("event", cevent);
                                        intent.putExtra("time", ctime);


                                        pendingIntent = PendingIntent.getBroadcast(SetAlarmActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

                                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                                        Toast.makeText(SetAlarmActivity.this, "Alarm details stored successfully", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(SetAlarmActivity.this, AlarmsList.class));
                                    } else {
                                        Toast.makeText(SetAlarmActivity.this, "Could not store Alarm details", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }


            //////////////////////////////////////////////////////////////////

        });


    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "EventRemainderChannel";
            String description = "Channel for event remainder";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("EventRemainder", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}