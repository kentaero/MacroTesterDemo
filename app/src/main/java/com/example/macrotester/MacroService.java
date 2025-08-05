package com.example.macrotester;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import UtilGroup.Configuration;
import UtilGroup.EventHistory;

public class MacroService extends JobService {
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable task = new Runnable() {
        @Override
        public void run() {
            // TestCase 호출한다.
            RunMacro("");
            // 다음 실행을 예약
            handler.postDelayed(this, 30000); // 30초
        }
    };
    private Configuration mConfig;
    private Context mContext;
    private EventHistory mEventHistory;

    public MacroService() {
        mContext = getApplicationContext();
        mEventHistory = new EventHistory(false);
        mConfig = new Configuration(this.mContext, this.mEventHistory);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        // 30초마다 실행할 작업을 구현합니다.
        // 스케줄링 대기중인가/ 동작중인가
        // 매크로 대기중인가/ 동작중인가

        // 스케줄링 동작중
        if(Boolean.valueOf(mConfig.getItem("ScheduleFlag")))
        {
            if (!Boolean.valueOf(mConfig.getItem("xxxxxx")))
            {
                // 매크로 대기중
                // 매크로 호출
                RunMacro("SchedulringTest");
            }
            return true;
        }
        else
            return false;

        // 작업이 완료되면 다음 실행을 예약하거나 반환합니다.
        // 반환값에 따라 시스템이 작업을 다시 예약할지 여부가 결정됩니다.
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // onStartJob()에서 실행 중인 작업을 중지해야 할 때 호출됩니다.
        // 반환값에 따라 재시작 여부가 결정됩니다.
        return true; // 재시작
    }

    private void RunMacro(String Method) {
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
            } catch (Exception e) {
                Log.e("jkseo", e.getMessage());
            }
        }
        catch (Exception ex){
            Log.e("jkseo", ex.getMessage());
        }
    }
}