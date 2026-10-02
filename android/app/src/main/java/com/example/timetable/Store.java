package com.example.timetable;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;

final class Store {
    static final String PREFS="schedule", EVENTS="events", FONT="font", THEME="theme", WIDGET_TRANSPARENCY="widget_transparency", TITLES="titles";
    static String events(Context context){return context.getSharedPreferences(PREFS,0).getString(EVENTS,"[]");}
    static String font(Context context){return context.getSharedPreferences(PREFS,0).getString(FONT,"pretendard");}
    static String theme(Context context){return context.getSharedPreferences(PREFS,0).getString(THEME,"dark");}
    static int widgetTransparency(Context context){return context.getSharedPreferences(PREFS,0).getInt(WIDGET_TRANSPARENCY,0);}
    static String titles(Context context){return context.getSharedPreferences(PREFS,0).getString(TITLES,"[]");}
    static JSONArray parsed(Context context){try{return new JSONArray(events(context));}catch(Exception e){return new JSONArray();}}
    static JSONArray valid(String json) throws Exception {
        JSONArray input=new JSONArray(json), output=new JSONArray();
        if(input.length()>500)throw new IllegalArgumentException("Too many events");
        for(int i=0;i<input.length();i++){
            JSONObject e=input.getJSONObject(i);
            int day=e.getInt("day"),start=e.getInt("start"),end=e.getInt("end");
            String id=e.getString("id"),title=e.getString("title");
            if(day<0||day>6||start<420||end>1260||end<=start||start%30!=0||end%30!=0||id.length()>100||title.length()>100)throw new IllegalArgumentException("Invalid event");
            int lead=e.optInt("reminderLead",10);
            if(lead!=5&&lead!=10&&lead!=30&&lead!=60)throw new IllegalArgumentException("Invalid lead");
            output.put(e);
        }
        return output;
    }
}
