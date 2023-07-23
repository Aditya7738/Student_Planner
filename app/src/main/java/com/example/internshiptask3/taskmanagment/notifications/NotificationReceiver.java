package com.example.internshiptask3.taskmanagment.notifications;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.internshiptask3.R;
import com.example.internshiptask3.taskmanagment.AddTaskActivity;
import com.example.internshiptask3.taskmanagment.TaskManagement;
import com.example.internshiptask3.taskmanagment.alarmmanager.AlarmsList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NotificationReceiver extends BroadcastReceiver {
    MediaPlayer mediaPlayer;

    FirebaseAuth firebaseAuth;

    FirebaseFirestore firestore;

    CharSequence taskName, selectedTime;

    @Override
    public void onReceive(Context context, Intent intent) {


        taskName = intent.getCharSequenceExtra("tasktitle");
        selectedTime = intent.getCharSequenceExtra("time");

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String uid = null;
        if (firebaseUser != null) {
            uid = firebaseUser.getUid();
        }

        CollectionReference collectionReference = null;
        if (uid != null) {
            collectionReference = firestore
                    .collection("users").document(uid).collection("Notifications");
        }



        mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_NOTIFICATION_URI);
        //mediaPlayer.setLooping(true);
        mediaPlayer.start();

        Intent nextActivity = new Intent(context, NotificationList.class);

        nextActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, nextActivity, PendingIntent.FLAG_IMMUTABLE);
        //mediaPlayer.stop();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "TaskRemainder");
        builder.setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentTitle(taskName)  //notification title can event
                .setContentText("Due Tomorrow")  //content text get by intent
                .setAutoCancel(false)
                .setDefaults(NotificationCompat.DEFAULT_ALL)  //
                .setPriority(NotificationCompat.PRIORITY_HIGH) //pop-up on lock screen
                .setContentIntent(pendingIntent);

        //showin notification
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }
        notificationManagerCompat.notify(123, builder.build());



        Map<String, Object> notificationDetails = new HashMap<>();
        notificationDetails.put("tasktitle", taskName);
        notificationDetails.put("selectedtime", selectedTime);

        if (collectionReference != null) {
            collectionReference
                    .add(notificationDetails)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Notification details stored successfully", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(context, "Could not store Notification details", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }


    }


}
