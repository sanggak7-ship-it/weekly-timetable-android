package com.example.timetable;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import org.json.JSONArray;
import java.util.HashSet;
import java.util.Locale;

public class MainActivity extends Activity {
    private WebView webView;
    @Override public void onCreate(Bundle state){
        super.onCreate(state);
        applySystemBars();
        webView=new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(false);
        webView.getSettings().setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        webView.addJavascriptInterface(new Bridge(),"Android");
        webView.setWebViewClient(new WebViewClient(){
            @Override public boolean shouldOverrideUrlLoading(WebView view,WebResourceRequest request){return !"file".equals(request.getUrl().getScheme());}
        });
        setContentView(webView);
        webView.loadUrl("file:///android_asset/index.html");
        ReminderScheduler.createChannel(this);
    }
    @Override protected void onResume(){super.onResume();ReminderScheduler.rescheduleAll(this);}
    @Override public void onBackPressed(){if(webView.canGoBack())webView.goBack();else super.onBackPressed();}
    private void applySystemBars(){
        boolean light=Store.theme(this).equals("light");
        getWindow().setStatusBarColor(light?0xfff6f7fb:0xff101114);
        getWindow().setNavigationBarColor(light?0xffe9ecf2:0xff1b1d22);
        getWindow().getDecorView().setSystemUiVisibility(light?android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR|android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR:0);
    }
    private void requestAlerts(){
        if(Build.VERSION.SDK_INT>=33&&checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},7);return;
        }
        requestExactAlarmAccess();
    }
    @Override public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] results){
        super.onRequestPermissionsResult(requestCode,permissions,results);
        if(requestCode==7&&results.length>0&&results[0]==PackageManager.PERMISSION_GRANTED)requestExactAlarmAccess();
    }
    private void requestExactAlarmAccess(){
        if(Build.VERSION.SDK_INT>=31){
            AlarmManager manager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
            if(!manager.canScheduleExactAlarms()){
                Intent intent=new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,Uri.parse("package:"+getPackageName()));
                try{startActivity(intent);}catch(Exception ignored){}
            }
        }
    }
    private class Bridge {
        @JavascriptInterface public String getEvents(){return Store.events(MainActivity.this);}
        @JavascriptInterface public String getFont(){return Store.font(MainActivity.this);}
        @JavascriptInterface public String getTheme(){return Store.theme(MainActivity.this);}
        @JavascriptInterface public int getWidgetTransparency(){return Store.widgetTransparency(MainActivity.this);}
        @JavascriptInterface public String getTitles(){return Store.titles(MainActivity.this);}
        @JavascriptInterface public void syncEvents(String json){
            try{
                JSONArray checked=Store.valid(json);
                runOnUiThread(()->{
                    ReminderScheduler.cancelAll(MainActivity.this);
                    getSharedPreferences(Store.PREFS,0).edit().putString(Store.EVENTS,checked.toString()).apply();
                    ReminderScheduler.rescheduleAll(MainActivity.this);
                    Widgets.refreshAll(MainActivity.this);
                });
            }catch(Exception ignored){}
        }
        @JavascriptInterface public void setFont(String font){
            if(!font.equals("jua")&&!font.equals("pretendard"))return;
            runOnUiThread(()->{getSharedPreferences(Store.PREFS,0).edit().putString(Store.FONT,font).apply();Widgets.refreshAll(MainActivity.this);});
        }
        @JavascriptInterface public void setTheme(String theme){
            if(!theme.equals("dark")&&!theme.equals("light"))return;
            runOnUiThread(()->{getSharedPreferences(Store.PREFS,0).edit().putString(Store.THEME,theme).apply();applySystemBars();Widgets.refreshAll(MainActivity.this);});
        }
        @JavascriptInterface public void setWidgetTransparency(int value){
            if(value<0||value>100)return;
            runOnUiThread(()->{getSharedPreferences(Store.PREFS,0).edit().putInt(Store.WIDGET_TRANSPARENCY,value).apply();Widgets.refreshAll(MainActivity.this);});
        }
        @JavascriptInterface public void syncTitles(String json){
            try{
                JSONArray input=new JSONArray(json),valid=new JSONArray();HashSet<String> seen=new HashSet<>();
                for(int i=0;i<input.length()&&valid.length()<100;i++){
                    String title=input.optString(i,"").trim();String key=title.toLowerCase(Locale.ROOT);
                    if(title.isEmpty()||title.length()>40||!seen.add(key))continue;
                    valid.put(title);
                }
                runOnUiThread(()->getSharedPreferences(Store.PREFS,0).edit().putString(Store.TITLES,valid.toString()).apply());
            }catch(Exception ignored){}
        }
        @JavascriptInterface public void requestAlerts(){runOnUiThread(()->MainActivity.this.requestAlerts());}
    }
}
