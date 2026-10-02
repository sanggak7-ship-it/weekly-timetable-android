package com.example.timetable;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

public class TodayWidget extends AppWidgetProvider {
    static void updateAll(Context context){
        AppWidgetManager manager=AppWidgetManager.getInstance(context);
        int[] ids=manager.getAppWidgetIds(new ComponentName(context,TodayWidget.class));
        if(ids.length>0)new TodayWidget().onUpdate(context,manager,ids);
    }
    @Override public void onUpdate(Context context,AppWidgetManager manager,int[] ids){
        int day=LocalDate.now().getDayOfWeek().getValue()%7;
        String[] days={"일","월","화","수","목","금","토"};
        ArrayList<JSONObject> todays=new ArrayList<>();JSONArray events=Store.parsed(context);
        for(int i=0;i<events.length();i++){JSONObject event=events.optJSONObject(i);if(event!=null&&event.optInt("day",-1)==day)todays.add(event);}
        todays.sort(Comparator.comparingInt(event->event.optInt("start")));
        PendingIntent open=PendingIntent.getActivity(context,0,new Intent(context,MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        for(int id:ids){
            android.os.Bundle options=manager.getAppWidgetOptions(id);
            int width=options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH,250);
            int height=options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT,120);
            RemoteViews views=new RemoteViews(context.getPackageName(),R.layout.today_widget);
            views.setImageViewBitmap(R.id.today_widget_image,WidgetImage.renderToday(context,width,height,days[day],todays));
            views.setOnClickPendingIntent(R.id.today_widget_image,open);
            manager.updateAppWidget(id,views);
        }
    }
    @Override public void onAppWidgetOptionsChanged(Context context,AppWidgetManager manager,int id,android.os.Bundle options){onUpdate(context,manager,new int[]{id});}
}
