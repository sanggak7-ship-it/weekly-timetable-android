package com.example.timetable;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class WeekRowsService extends RemoteViewsService {
    @Override public RemoteViewsFactory onGetViewFactory(Intent intent){
        return new Factory(getApplicationContext(),intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID));
    }
    private static final class Factory implements RemoteViewsFactory {
        private final Context context;
        private final int widgetId;
        private final ArrayList<JSONObject> events=new ArrayList<>();
        Factory(Context context,int widgetId){this.context=context;this.widgetId=widgetId;}
        @Override public void onCreate(){onDataSetChanged();}
        @Override public void onDataSetChanged(){
            events.clear();JSONArray data=Store.parsed(context);
            for(int i=0;i<data.length();i++){JSONObject event=data.optJSONObject(i);if(event!=null)events.add(event);}
        }
        @Override public void onDestroy(){events.clear();}
        @Override public int getCount(){return 28;}
        @Override public RemoteViews getViewAt(int position){
            if(position<0||position>=28)return null;
            int width=AppWidgetManager.getInstance(context).getAppWidgetOptions(widgetId).getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH,280);
            RemoteViews row=new RemoteViews(context.getPackageName(),R.layout.widget_row);
            row.setImageViewBitmap(R.id.widget_row_image,WidgetImage.renderRow(context,width,position,events));
            return row;
        }
        @Override public RemoteViews getLoadingView(){return null;}
        @Override public int getViewTypeCount(){return 1;}
        @Override public long getItemId(int position){return position;}
        @Override public boolean hasStableIds(){return true;}
    }
}
