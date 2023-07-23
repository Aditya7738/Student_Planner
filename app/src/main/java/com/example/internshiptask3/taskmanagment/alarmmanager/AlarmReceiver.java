package com.example.internshiptask3.taskmanagment.alarmmanager;

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

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.internshiptask3.R;

public class AlarmReceiver extends BroadcastReceiver {
    MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
//        AlarmDataModel alarmDataModel = new AlarmDataModel();
        CharSequence eventName = intent.getCharSequenceExtra("event");
        CharSequence selectedTime = intent.getCharSequenceExtra("time");

        mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_ALARM_ALERT_URI);
        //mediaPlayer.setLooping(true);
        mediaPlayer.start();

        Intent nextActivity = new Intent(context, AlarmsList.class);

        nextActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, nextActivity, PendingIntent.FLAG_IMMUTABLE);
        //mediaPlayer.stop();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "EventRemainder");
        builder.setSmallIcon(R.drawable.baseline_alarm_24)
                .setContentTitle(eventName)  //notification title can event
                .setContentText(selectedTime)  //content text get by intent
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


    }


}
