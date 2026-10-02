package com.example.timetable;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import org.json.JSONArray;
import org.json.JSONObject;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

final class ReminderScheduler {
    static final String CHANNEL="schedule_reminders";
    static void createChannel(Context context){
        NotificationManager manager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel=new NotificationChannel(CHANNEL,"시간표 알림",NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("일정 시작 전 알림");manager.createNotificationChannel(channel);
    }
    static PendingIntent pending(Context context,JSONObject event){
        String id=event.optString("id");
        Intent intent=new Intent(context,ReminderReceiver.class).setAction("com.example.timetable.REMINDER").setData(android.net.Uri.parse("timetable://reminder/"+android.net.Uri.encode(id)));
        intent.putExtra("eventId",id);
        return PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
    }
    static void cancelAll(Context context){
        AlarmManager manager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        JSONArray events=Store.parsed(context);
        for(int i=0;i<events.length();i++){JSONObject e=events.optJSONObject(i);if(e!=null)manager.cancel(pending(context,e));}
    }
    static void rescheduleAll(Context context){
        JSONArray events=Store.parsed(context);
        for(int i=0;i<events.length();i++){JSONObject e=events.optJSONObject(i);if(e!=null&&e.optBoolean("reminder"))schedule(context,e);}
    }
    static void schedule(Context context,JSONObject event){
        int day=event.optInt("day",-1),start=event.optInt("start",-1),lead=event.optInt("reminderLead",10);
        if(day<0||day>6||start<420||start>=1260)return;
        ZoneId zone=ZoneId.systemDefault();
        ZonedDateTime now=ZonedDateTime.now(zone);
        int javaDay=day==0?7:day;
        LocalDate date=now.toLocalDate();
        int days=(javaDay-date.getDayOfWeek().getValue()+7)%7;
        ZonedDateTime trigger=date.plusDays(days).atStartOfDay(zone).plusMinutes(start-lead);
        if(!trigger.isAfter(now))trigger=trigger.plusWeeks(1);
        AlarmManager manager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pending=pending(context,event);
        if(Build.VERSION.SDK_INT>=31&&manager.canScheduleExactAlarms())manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,trigger.toInstant().toEpochMilli(),pending);
        else if(Build.VERSION.SDK_INT<31)manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,trigger.toInstant().toEpochMilli(),pending);
        else manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,trigger.toInstant().toEpochMilli(),pending);
    }
}
