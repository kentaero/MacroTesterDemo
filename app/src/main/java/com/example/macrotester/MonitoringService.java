package com.example.macrotester;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.internal.view.SupportMenu;

import UtilGroup.Configuration;
import UtilGroup.EventHistory;

public class MonitoringService extends Service {

    private static final String CHANNEL_ID = "ch99999";
    Notification.Builder noti;

    private Configuration mConfig;
    private EventHistory mEventHIstory;
    private Handler handler = new Handler();
    private Runnable runnable;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("jkseo", "Service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("jkseo", "Service started");

        mEventHIstory = new EventHistory(false);
        mConfig = new Configuration(this,mEventHIstory);
        
        MakeNoti();
        AppCheck();

        return START_NOT_STICKY; // 서비스가 중지되어도 재시작되지 않도록 설정
    }

    @Override
    public IBinder onBind(Intent intent) {
        // 바인딩이 필요하지 않은 서비스는 null을 반환
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 서비스 종료 시 필요한 정리 작업을 수행

        // 서비스 종료 시 Runnable을 제거
        handler.removeCallbacks(runnable);
    }

    private void AppCheck() {
        // todo 분단위로 앱실행내역을 Log에 표시한다.
        runnable = new Runnable() {
            @Override
            public void run() {
                // 현재 실행 중인 앱 목록을 가져와 로그에 출력
                Log.d("MyForegroundService", "현재 실행 중인 앱 목록: ..."); // 앱 목록을 가져오는 로직은 필요시 추가

                // 스케쥴 실행중
                if(mConfig.GetBooleanItem(""))
                {
                    // todo api 호출하여 비가동 상태로 확인
                    // todo app 실행
                }
                // 1분 후에 다시 실행
                handler.postDelayed(this, 60 * 3000); // 1분 단위 반복
            }
        };

        // Runnable 실행 시작
        handler.post(runnable);

    }

    private void MakeNoti(){
        // 알림 채널 생성
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_logo)
                .setContentTitle("매크로 동작")
                .setContentText("매크로가 동작중입니다.")
                .setDefaults(Notification.DEFAULT_ALL) // 알림, 사운드 진동 설정
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(new NotificationChannel(
                    CHANNEL_ID, "매크로앱", NotificationManager.IMPORTANCE_HIGH));
            manager.notify(99999, builder.build());
        }
        else{
            manager.notify(99999, builder.build());
        }
    }

}