package com.example.timetable;

import android.content.Context;

final class Widgets {
    static void refreshAll(Context context){
        TodayWidget.updateAll(context);
        ScheduleWidget.updateAll(context);
    }
}
