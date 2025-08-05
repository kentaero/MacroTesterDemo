package com.example.macrotester;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.INotificationSideChannel;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.internal.view.SupportMenu;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Timer;

import ModelGroup.MacroEvent;
import ModelGroup.MacroResult;
import UtilGroup.Configuration;
import UtilGroup.EventHistory;

public class MacroReceiver extends BroadcastReceiver {
    private NotificationManager mNotificationManager;
    private Configuration mConfig;
    private EventHistory mEventHistory;

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        mEventHistory = new EventHistory(false);
        mConfig = new Configuration(context, this.mEventHistory);

        try {
            this.mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            String action = intent.getAction();
            Log.e("jkseo", "MacroReceiver action : " + action);
            if (action.equalsIgnoreCase("START_MACRO_EVENT")) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ch99999")
                            .setSmallIcon(R.mipmap.ic_logo)
                            .setContentTitle("매크로 동작")
                            .setContentText("매크로가 동작중입니다.")
                            .setDefaults(Notification.DEFAULT_ALL) // 알림, 사운드 진동 설정
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    this.mNotificationManager.createNotificationChannel(new NotificationChannel(
                            "ch99999", "매크로앱", NotificationManager.IMPORTANCE_HIGH));

                    this.mNotificationManager.notify(99999, builder.build());
                }
                else{
                    this.mNotificationManager.notify(99999, new Notification.Builder(context).setSmallIcon(R.drawable.ic_logo_background).setColor(SupportMenu.CATEGORY_MASK).setWhen(System.currentTimeMillis()).setContentTitle("매크로 동작").setContentText("매크로가 동작 중입니다.").build());
                }
            }
            else if (action.equalsIgnoreCase("END_MACRO_EVENT")) {
                this.mNotificationManager.cancel(99999);
            }
            else if (action.equalsIgnoreCase("MACRO_CYCLE_ALARM")) {
                boolean isSchedulering = mConfig.GetBooleanItem("ScheduleFlag");

                if(isSchedulering)
                    MacroStart();
            }
            else if (action.equalsIgnoreCase("SAVE_MACRO_RESULT")) {
                MacroManager macroManager = new MacroManager(mConfig);
                MacroResult event = macroManager.LoadMacroResult();
                macroManager.SaveMacroResult(event);
            }
            else if (action.equalsIgnoreCase("SAVE_MACRO_RESULT_DEBUG")) {
                MacroManager macroManager = new MacroManager(mConfig);
                MacroResult event = macroManager.LoadMacroResult();
                macroManager.SaveMacroResult(event);
            }
        } catch (Exception e) {
            try {
                mEventHistory.writeEventHistory("receiver SAVE_MACRO_RESULT : " + e.getMessage());

                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                mEventHistory.writeEventHistory("receiver SAVE_MACRO_RESULT :" + sw.toString());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void MacroStart() throws Exception {
        try{

            mEventHistory.writeEventHistory("스케줄링 시작");

            MacroManager macroManager = new MacroManager(mConfig);
            ArrayList<MacroEvent> jobs = macroManager.LoadMacroData(mConfig.getItem("ComId"), mConfig.getItem("SmartId"));
            String data = "";
            if(jobs == null) {
                // 대기작업이 없어도 스케줄링 대기시간에 맞추어 계속 돈다.
                mEventHistory.writeEventHistory("시작할 작업이 없습니다.");
            }
            else {
                mEventHistory.writeEventHistory("스케줄링 작업 대기수량 : " + String.valueOf(jobs.size()));
                data = macroManager.ConvertToMacroEvent(jobs);
            }

            mConfig.setItem("Jobs", data);

            RunMacro("RunChromeMacro");
//            RunMacro("CheckFileDescriptor");
        }
        catch(Exception ex){
            Log.e("jkseo", "MacroStart error : " + ex.getMessage());
            mEventHistory.writeEventHistory("Receiver Macrostart error : " + ex.getMessage());
        }
    }

    private void RunMacro(String Method) throws Exception {
        String[] settingShow;

        if(Method == null || Method.length() == 0) {
            settingShow = new String[] {"su", "-c", "am", "instrument", "-w", "-r", "-e", "debug", "false","-e", "ACTION_NAME", "RunChromeMacro", "-e", "class", "com.example.macrotester.ExampleInstrumentedTest", "com.example.macrotester.test/androidx.test.runner.AndroidJUnitRunner"};
        }
        else{
            settingShow = new String[] {"su", "-c", "am", "instrument", "-w", "-r", "-e", "debug", "false","-e", "ACTION_NAME", Method, "-e", "class", "com.example.macrotester.ExampleInstrumentedTest", "com.example.macrotester.test/androidx.test.runner.AndroidJUnitRunner"};
        }

        try{
            Process p = Runtime.getRuntime().exec(settingShow);

            try {
                p.getErrorStream().close();
//                p.destroy();
            } catch (Exception e) {
                mEventHistory.writeEventHistory("RunMacro error : " + e.getMessage());
                Log.e("jkseo", e.getMessage());
            }
        }
        catch (Exception ex){
            Log.e("jkseo", ex.getMessage());
            mEventHistory.writeEventHistory("RunMacro error : " + ex.getMessage());
        }
    }

    public void DelayExec(long time) throws Exception {
        mEventHistory.writeEventHistory("스케줄링 대기 시작 : " + (time / 1000)  / 60 + "분(" + (time / 1000) + "초)");
        // 3분 기준 분할대기 처리한다.
        if (time > 180000) {
            boolean sleepNext = true;
            while (sleepNext) {
                try {
                    Thread.sleep(180000L);
                } catch (Exception e) {
                }
                time -= 180000;

                // 장시간 대기 로그기록
                mEventHistory.writeEventHistory("예약 대기 남은시간 (" + (time / 1000)  / 60 + "분)" + (time / 1000) + "초)");

                if (time < 180000) {
                    try {
                        Thread.sleep(time);
                    } catch (Exception e2) {
                    }
                    sleepNext = false;
                }
            }
            return;
        }
        try {
            Thread.sleep(time);
        } catch (Exception e3) {
        }
    }
}