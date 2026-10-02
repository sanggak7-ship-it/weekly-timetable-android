package com.example.timetable;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import org.json.JSONArray;
import org.json.JSONObject;

public class ReminderReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context,Intent intent){
        String id=intent.getStringExtra("eventId");
        JSONArray events=Store.parsed(context);
        for(int i=0;i<events.length();i++){
            JSONObject e=events.optJSONObject(i);
            if(e==null||!e.optBoolean("reminder")||!e.optString("id").equals(id))continue;
            ReminderScheduler.schedule(context,e);
            if(Build.VERSION.SDK_INT>=33&&context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)return;
            ReminderScheduler.createChannel(context);
            Intent open=new Intent(context,MainActivity.class);
            PendingIntent pending=PendingIntent.getActivity(context,0,open,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
            int lead=e.optInt("reminderLead",10);
            Notification notification=new Notification.Builder(context,ReminderScheduler.CHANNEL)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(e.optString("title","일정"))
                .setContentText(lead+"분 후 일정이 시작됩니다.")
                .setContentIntent(pending).setAutoCancel(true).build();
            ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(id.hashCode(),notification);
            return;
        }
    }
}
