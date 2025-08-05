package com.example.macrotester;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

public class JobSchedulerHelper {
    public static void scheduleJob(Context context) {
        ComponentName componentName = new ComponentName(context, MacroService.class);
        JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                .setPeriodic(5000) // 5 초마다 실행 (5,000 밀리초)
                .setPersisted(true) // 장기 실행을 위해 필요한 경우 설정
                .build();

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(jobInfo);
    }
}
