package com.example.timetable;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

public class ScheduleWidget extends AppWidgetProvider {
    static void updateAll(Context context){
        AppWidgetManager manager=AppWidgetManager.getInstance(context);
        int[] ids=manager.getAppWidgetIds(new ComponentName(context,ScheduleWidget.class));
        if(ids.length>0)new ScheduleWidget().onUpdate(context,manager,ids);
    }
    @SuppressWarnings("deprecation")
    @Override public void onUpdate(Context context,AppWidgetManager manager,int[] ids){
        Intent open=new Intent(context,MainActivity.class);
        PendingIntent pending=PendingIntent.getActivity(context,0,open,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        for(int id:ids){
            RemoteViews views=new RemoteViews(context.getPackageName(),R.layout.widget);
            int width=manager.getAppWidgetOptions(id).getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH,280);
            views.setImageViewBitmap(R.id.widget_header,WidgetImage.renderHeader(context,width));
            views.setOnClickPendingIntent(R.id.widget_header,pending);
            Intent service=new Intent(context,WeekRowsService.class);
            service.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,id);
            service.setData(Uri.parse(service.toUri(Intent.URI_INTENT_SCHEME)));
            views.setRemoteAdapter(R.id.widget_grid,service);
            manager.updateAppWidget(id,views);
            manager.notifyAppWidgetViewDataChanged(id,R.id.widget_grid);
        }
    }
    @Override public void onAppWidgetOptionsChanged(Context context,AppWidgetManager manager,int id,android.os.Bundle options){onUpdate(context,manager,new int[]{id});}
}
