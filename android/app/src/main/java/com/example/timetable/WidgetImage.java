package com.example.timetable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import org.json.JSONObject;
import java.util.List;
import java.util.Locale;

final class WidgetImage {
    private static final int[] ORDER={1,2,3,4,5,6,0};
    private static final String[] DAYS={"월","화","수","목","금","토","일"};
    private static final float TIME_WIDTH=42f;
    private static final float ROW_HEIGHT=30f;

    private static float scale(Context context){return Math.min(context.getResources().getDisplayMetrics().density,1.5f);}
    private static int width(int widthDp){return Math.max(280,Math.min(widthDp,480));}
    private static Bitmap bitmap(int width,float height,float scale){return Bitmap.createBitmap(Math.round(width*scale),Math.round(height*scale),Bitmap.Config.ARGB_8888);}
    private static Typeface font(Context context){
        try{return Typeface.createFromAsset(context.getAssets(),Store.font(context).equals("jua")?"fonts/BMJUA.ttf":"fonts/Pretendard-Regular.otf");}
        catch(Exception e){return Typeface.DEFAULT;}
    }
    private static int background(Context context,boolean light){
        int alpha=255*(100-Store.widgetTransparency(context))/100;
        return Color.argb(alpha,light?248:21,light?249:23,light?252:28);
    }
    private static int color(String value){try{return Color.parseColor(value);}catch(Exception e){return 0xff91b7e5;}}

    static Bitmap renderHeader(Context context,int requestedWidth){
        int width=width(requestedWidth);float factor=scale(context);
        Bitmap bitmap=bitmap(width,64,factor);Canvas canvas=new Canvas(bitmap);canvas.scale(factor,factor);
        Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);boolean light=Store.theme(context).equals("light");
        paint.setColor(background(context,light));canvas.drawRoundRect(0,0,width,64,12,12,paint);
        paint.setTypeface(font(context));paint.setColor(light?0xff17202d:Color.WHITE);paint.setTextSize(16);paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("주간 시간표",12,23,paint);
        paint.setTextSize(11);paint.setTextAlign(Paint.Align.CENTER);
        float column=(width-TIME_WIDTH)/7f;
        for(int i=0;i<7;i++){
            paint.setColor(light?0xff354056:0xffdce4f3);
            canvas.drawText(DAYS[i],TIME_WIDTH+(i+0.5f)*column,51,paint);
        }
        paint.setColor(light?0xff9da8bb:0xff58606e);paint.setStrokeWidth(1);
        canvas.drawLine(0,63,width,63,paint);
        return bitmap;
    }

    static Bitmap renderRow(Context context,int requestedWidth,int position,List<JSONObject> events){
        int width=width(requestedWidth),minute=420+position*30;float factor=scale(context);
        Bitmap bitmap=bitmap(width,ROW_HEIGHT,factor);Canvas canvas=new Canvas(bitmap);canvas.scale(factor,factor);
        Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);boolean light=Store.theme(context).equals("light");
        paint.setColor(background(context,light));canvas.drawRect(0,0,width,ROW_HEIGHT,paint);
        paint.setColor(light?0xffc5cbd6:0xff414650);paint.setStrokeWidth(0.7f);
        canvas.drawLine(0,ROW_HEIGHT-0.4f,width,ROW_HEIGHT-0.4f,paint);
        float column=(width-TIME_WIDTH)/7f;
        for(int i=0;i<=7;i++){float x=TIME_WIDTH+i*column;canvas.drawLine(x,0,x,ROW_HEIGHT,paint);}
        paint.setTypeface(font(context));paint.setColor(light?0xff40506a:0xffc0c8d8);paint.setTextSize(9);paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(String.format(Locale.KOREA,"%02d:%02d",minute/60,minute%60),4,19,paint);
        for(int columnIndex=0;columnIndex<7;columnIndex++){
            int day=ORDER[columnIndex];JSONObject active=null;
            for(JSONObject event:events){if(event.optInt("day",-1)==day&&event.optInt("start",-1)<=minute&&minute<event.optInt("end",-1)){active=event;break;}}
            if(active==null)continue;
            float left=TIME_WIDTH+columnIndex*column+1,right=left+column-2;
            int accent=color(active.optString("color"));
            paint.setColor(Color.argb(90,Color.red(accent),Color.green(accent),Color.blue(accent)));
            canvas.drawRect(left,0,right,ROW_HEIGHT,paint);
            paint.setColor(accent);canvas.drawRect(left,0,left+3,ROW_HEIGHT,paint);
            int span=(active.optInt("end")-active.optInt("start"))/30;
            if(minute!=active.optInt("start")+(span/2)*30)continue;
            paint.setColor(light?0xff17202d:Color.WHITE);paint.setTextSize(8);paint.setTextAlign(Paint.Align.LEFT);
            String title=active.optString("title");float allowed=column-8;
            while(title.length()>1&&paint.measureText(title)>allowed)title=title.substring(0,title.length()-1);
            if(!title.equals(active.optString("title"))){while(title.length()>1&&paint.measureText(title+"…")>allowed)title=title.substring(0,title.length()-1);title+="…";}
            canvas.drawText(title,left+5,19,paint);
        }
        return bitmap;
    }

    static Bitmap renderToday(Context context,int requestedWidth,int requestedHeight,String day,List<JSONObject> events){
        int width=Math.max(220,Math.min(requestedWidth,420));
        int height=Math.max(110,Math.min(requestedHeight,300));float factor=scale(context);
        Bitmap bitmap=bitmap(width,height,factor);Canvas canvas=new Canvas(bitmap);canvas.scale(factor,factor);
        Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);boolean light=Store.theme(context).equals("light");
        paint.setColor(background(context,light));canvas.drawRoundRect(0,0,width,height,15,15,paint);
        paint.setTypeface(font(context));paint.setColor(light?0xff17202d:Color.WHITE);paint.setTextSize(17);
        canvas.drawText("오늘 일정 · "+day+"요일",16,29,paint);
        paint.setColor(light?0xffa8afbc:0xff454a54);paint.setStrokeWidth(1);
        canvas.drawLine(16,42,width-16,42,paint);
        if(events.isEmpty()){
            paint.setColor(light?0xff38465d:0xffc6d0e3);paint.setTextSize(13);
            canvas.drawText("등록된 일정이 없습니다",16,70,paint);
            return bitmap;
        }
        int visible=Math.min(events.size(),Math.max(1,(height-55)/29));
        for(int i=0;i<visible;i++){
            JSONObject event=events.get(i);float y=68+i*29;int start=event.optInt("start");
            paint.setColor(color(event.optString("color")));canvas.drawRoundRect(16,y-13,21,y+6,3,3,paint);
            paint.setColor(light?0xff38465d:0xffc6d0e3);paint.setTextSize(13);
            canvas.drawText(String.format(Locale.KOREA,"%02d:%02d",start/60,start%60),29,y,paint);
            paint.setColor(light?0xff17202d:Color.WHITE);String title=event.optString("title");
            while(title.length()>1&&paint.measureText(title)>width-105)title=title.substring(0,title.length()-1);
            if(!title.equals(event.optString("title")))title+="…";
            canvas.drawText(title,88,y,paint);
        }
        return bitmap;
    }
}
