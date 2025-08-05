package com.example.macrotester;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

public class AlarmManagerHelper {
    public static void setRepeatingAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MacroReceiver.class);
        intent.setAction("MACRO_CYCLE_ALARM");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        // 30초마다 알람 등록
        long intervalMillis = 10 * 1000; // 10초
        long triggerAtMillis = SystemClock.elapsedRealtime() + intervalMillis;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android 6.0 이상에서는 setExactAndAllowWhileIdle()을 사용
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingIntent);
        } else {
            // 그 외의 경우에는 setExact()을 사용
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingIntent);
        }
    }
}
