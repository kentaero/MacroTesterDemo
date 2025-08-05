package com.example.macrotester;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ModelGroup.MacroEvent;
import UtilGroup.ApiHelper;
import UtilGroup.BuildConfig;
import UtilGroup.Configuration;
import UtilGroup.EventHistory;
import UtilGroup.ShellExecuter;

public class LoaderActivity extends AppCompatActivity  implements
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback{
    private Context mContext;
    private Button btnRunAirplanMode, btnDeleteSearchhistory, btnShowLogHistory
            ,btnShowNoti, btnHideNoti, btnAppInstall, btnRegSchedule;

    private long backBtnTime = 0;

    private Configuration mConfig;
    private EventHistory mEventHistory;

    private Gson gson;
    private boolean isAppInstalled, isScheduling = false;

    private final String MACRO_PACKAGE1 = "com.example.macrotester.test";

    private ApiHelper mApi = new ApiHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //타이틀바 제거
        getSupportActionBar().hide();
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_loader);

        mContext = getApplicationContext();

        // 권한 요청을 수행하는 함수 호출
        requestStoragePermission();

        InitControlData();

        AddListner();

        // api 24 이상부터 사용
//        if(Build.VERSION.SDK_INT >= 24)
//        {
//            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//            StrictMode.setVmPolicy(builder.build());
//        }

        if(mConfig.getItem("SmartId") == null || mConfig.getItem("SmartId").equalsIgnoreCase("")) {
            LoadDefaultData();
        }
        else{
            LoadConfiguration();
        }

        try {
            // 환경설정 구성
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayout, new SettingPreferenceFragment())
                    .commit();
        } catch (Exception ex){
            Log.e("jkseo",ex.getMessage());
        }

        // 매크로 동작중 앱이종료된 경우, 20초 후 재실행 처리
        if(mConfig.GetBooleanItem("ScheduleFlag"))
        {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 재실행 전 한번더 체크
                    if(mConfig.GetBooleanItem("ScheduleFlag")){
                        Toast.makeText(mContext, "매크로 재시작 합니다.", Toast.LENGTH_SHORT).show();
                        ExecRecyle();
                    }
                }
            }, 20000); // 20초 후에 실행
        }
    }

    private void LoadConfiguration() {
        isScheduling = mConfig.GetBooleanItem("ScheduleFlag");

        String RebootOnWorkLimit = mConfig.getItem("RebootOnWorkLimit");
        if(RebootOnWorkLimit.equalsIgnoreCase(""))
            mConfig.setItem("RebootOnWorkLimit", "0");

        String WorkingCount = mConfig.getItem("WorkingCount");
        if(RebootOnWorkLimit.equalsIgnoreCase(""))
            mConfig.setItem("WorkingCount", "0");

        ChangeSchedulingStatus();
    }

    private void ChangeSchedulingStatus(){
        if(isScheduling)
        {
            btnRegSchedule.setText("매크로 중지");
            btnRegSchedule.setBackgroundColor(Color.RED);
        }
        else{
            btnRegSchedule.setBackgroundColor(Color.BLUE);
            btnRegSchedule.setText("매크로 실행");
        }
    }
    private void LoadDefaultData() {
        mConfig.setItem("SmartId", "");
        mConfig.setItem("ComId", "");

        // 재부팅을 위한 매크로 동작횟수, 기준횟수 초기화
        mConfig.setItem("WorkingCount", "0");
        mConfig.setItem("RebootOnWorkLimit", "0");

        // 대기목록이 없을경우 한번실행 후 종료한다.
        mConfig.setItem("MacroSetDelayMin", "1");
        // 재실행시간
//        mConfig.setItem("MacroRunDelayMin", String.valueOf(0));
        // 재사용시간
//        mConfig.setItem("MacroSetDelayMin", String.valueOf(0));
        mConfig.setItem("ScheduleFlag", false);
    }

    private Boolean CheckNumber(String userInput){
        // 숫자만 포함된 값인지 확인
        if (!userInput.matches("\\d+")) {
            // 숫자가 아닌 경우, 사용자에게 경고 메시지 표시
            Toast.makeText(this, "숫자만 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void SaveCurrentConfig(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        mConfig.setItem("SmartId", prefs.getString("SmartId", ""));
        mConfig.setItem("ComId", prefs.getString("ComId", ""));
        mConfig.setItem("RebootOnWorkLimit", prefs.getString("RebootOnWorkLimit", "0"));
        mConfig.setItem("MacroSetDelayMin", "1");

//        mConfig.setItem("MacroSetDelayMin", prefs.getString("MacroSetDelayMin", "1"));
//        mConfig.setItem("MacroRunDelayMin", prefs.getString("MacroRunDelayMin", "1"));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        SaveCurrentConfig();
    }

    private void AddListner() {
        btnRunAirplanMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RunMacro("ControlAirPlanMode");
            }
        });

        btnDeleteSearchhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //debug 다른앱 값 전송
//                try {
//                    Intent intent = new Intent();
//                    intent.setAction("com.example.MACRO_SHARE_DATA_ACTION");
//                    intent.putExtra("shared_data", mConfig.getItem("ComId"));
//
//                    // 다른 앱에 Intent 전송
//                    startActivity(intent);
//                }
//                catch (Exception aex)
//                {
//                    Log.d("jkseo", "jkseo : " + aex.getMessage());
//                }
                RunMacro("DeleteSearchHistory");
            }
        });

        btnShowLogHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowLogHistory();
            }
        });

        btnShowNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExecNoti(true);
            }
        });

        btnHideNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExecNoti(false);
            }
        });

        btnAppInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAppInstalled) {
                    ShellExecuter shellExec = new ShellExecuter(mEventHistory);
                    shellExec.execString("su", "-c", "pm", "install", "-t", "-r", Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/app-debug-androidTest.apk");

                    Toast.makeText(mContext, "테스트앱 설치진행중입니다.", Toast.LENGTH_LONG);

                    while (!isInstalled(MACRO_PACKAGE1)) {
                        try {
                            // 일정 시간 동안 대기
                            isAppInstalled = isInstalled(MACRO_PACKAGE1);
                            Thread.sleep(1000); // 1초 대기
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    Intent intent = new Intent(Intent.ACTION_DELETE);
                    intent.setData(Uri.parse("package:" + MACRO_PACKAGE1));
                    startActivity(intent);

                    isAppInstalled = isInstalled(MACRO_PACKAGE1);
                    // com.example.macrotester.test
//                    startActivity(new Intent("android.intent.action.DELETE", Uri.parse("package:" + MACRO_PACKAGE1)));
                }
            }
        });

        btnRegSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isScheduling) {
                    try {
                        // 재부팅용 동작횟수 초기화
                        mConfig.setItem("WorkingCount", String.valueOf(0));

                        SaveCurrentConfig();

                        // todo 유효성 검사
                        if(mConfig.getItem("ComId").equalsIgnoreCase("") || mConfig.getItem("ComId").length() == 0)
                        {
                            Toast.makeText(mContext, "회사ID 입력하세요!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(mConfig.getItem("SmartId").equalsIgnoreCase("") || mConfig.getItem("SmartId").length() == 0)
                        {
                            Toast.makeText(mContext, "스마트폰ID 입력하세요!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        
                        StartScheduler();
                    } catch (Exception ex) {
                        Log.e("jkseo", "call StartScheduler error : " + ex.getMessage());
                    }
                } else {
                    StopScheduler();
                }
            }
        });
        SharedPreferences prefs;
//        SharedPreferences.Editor pEditor;

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        pEditor = prefs.edit();
        prefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
                Log.d("jkseo", "클릭된 Preference의 key는 " + key);

                SaveCurrentConfig();
            }
        });

//        SharedPreferences prefs;
//        prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        prefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
//                    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
//                        Log.d("jkseo","클릭된 Preference의 key는 "+key);
//                    }
//                });
    }


    private void OpenChromeApp(){
        PackageManager packageManager = mContext.getPackageManager();

        Intent intent = packageManager.getLaunchIntentForPackage("com.android.chrome");

        if (null != intent)
            mContext.startActivity(intent);
    }
    // 스케줄 시간때 발생
    private void StartScheduler() throws IOException {
        isScheduling = true;
        mConfig.setItem("ScheduleFlag", true);
        ChangeSchedulingStatus();

        ExecRecyle();
    }

    private void StopScheduler() {
        isScheduling = false;
        //스케줄링 중지
        mConfig.setItem("ScheduleFlag", false);
        //매크로가 현재 동작중이라면 중지시킨다.
        mConfig.setItem("IsRunning", "0");

        ChangeSchedulingStatus();
    }

//    @Nullable
//    private ArrayList<MacroEvent> LoadMacroData(String deviceID) throws IOException {
//        mEventHistory.writeEventHistory("매크로 목록 조회(LoadMacroData)");
//
//        String resUrl = "https://letsgo119.com/api/procedure_r.asp";
//        Uri builtUri =
//                Uri.parse(resUrl).buildUpon()
//                        .appendQueryParameter("p_device_id", deviceID)
//                        .build();
//
//        String response = mApi.CallRestApi(BuildConfig.APPLICATION_ID, "", builtUri.toString());
//
//        mEventHistory.writeEventHistory("매크로 목록 조회 응답(LoadMacroData) : " + response);
//
//        if(response.equalsIgnoreCase("fail")){
//            mEventHistory.writeEventHistory("LoadMacroData Faile!");
//            return null;
//        }
//        else{
//            // todo json to class
//            ArrayList<MacroEvent> jobs = new ArrayList<>();
//            TypeToken<ArrayList<MacroEvent>> collectionType = new TypeToken<ArrayList<MacroEvent>>(){};
//            jobs = gson.fromJson(response, collectionType);
//
//            if(jobs.size() == 0)
//            {
//                mEventHistory.writeEventHistory("LoadMacroData Size 0");
//                return null;
//            }
//            else{
//                return jobs;
//            }
//        }
//    }

    private void InitControlData() {
        btnRunAirplanMode = (Button)findViewById(R.id.btnRunAirplanMode);
        btnDeleteSearchhistory = (Button)findViewById(R.id.btnDeleteSearchhistory);
        btnShowLogHistory = (Button)findViewById(R.id.btnShowLogHistory);

        btnShowNoti = (Button)findViewById(R.id.btnShowNoti);
        btnHideNoti = (Button)findViewById(R.id.btnHideNoti);
        btnAppInstall = (Button)findViewById(R.id.btnAppInstall);
        btnRegSchedule = (Button)findViewById(R.id.btnRegSchedule);

        TextView txtVersion = (TextView)findViewById(R.id.txtVersion);
//        txtVersion.setText(BuildConfig.VERSION_NAME);
        txtVersion.setText(getCurrentVersion());

        mEventHistory = new EventHistory(false);
        mConfig = new Configuration(this.mContext, this.mEventHistory);

        gson = new Gson();

        isAppInstalled = isInstalled(MACRO_PACKAGE1);

        long now = System.currentTimeMillis();
        String logDate = new SimpleDateFormat("yyyyMMdd").format(now);

        mConfig.setItem("LOG_DATE", logDate);
    }

    private String getCurrentVersion() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "N/A";
        }
    }

    private boolean isInstalled(String str) {
        try {
            getPackageManager().getPackageInfo(str, 0);
            btnAppInstall.setText("앱 삭제");

            btnAppInstall.setBackgroundColor(Color.RED);

            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            btnAppInstall.setText("앱 설치");

            btnAppInstall.setBackgroundColor(Color.GREEN);
            return false;
        }
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

    private void ExecRecyle(){

        // ver 1.6.2
//        Intent monitorService = new Intent(this.mContext, MonitoringService.class);
//
//        if (Build.VERSION.SDK_INT >= 26) startForegroundService(monitorService); //안드로이드 9 이상
//        else startService(monitorService); //안드로이드 9 미만

        Log.e("jkseo", "ShellExecuter ExecRecyle isShow : ");
        try {
            String[] noti = {"su", "-c", "am", "broadcast", "-a", "MACRO_CYCLE_ALARM", "com.example.macrotester"};
            Process p = Runtime.getRuntime().exec(noti);


//            adb shell am broadcast -a com.example.ACTION_MY_BROADCAST


            try {
                p.getErrorStream().close();
                Thread.sleep(1000L);
            } catch (Exception e) {
                Log.e("jkseo", e.getMessage());
            }
        } catch (Exception e3) {
            Log.e("jkseo", "ADB 앱호출 테스트 에러 : " + e3.toString());
        }
    }
    private void ExecNoti(boolean isShow){
        Log.e("jkseo", "ShellExecuter execNoti isShow : " + isShow);
        try {

            if (isShow) {
                String[] noti = {"su", "-c", "am", "broadcast", "-a", "START_MACRO_EVENT", "com.example.macrotester"};
                Process p = Runtime.getRuntime().exec(noti);

                try {
                    p.getErrorStream().close();
                    Thread.sleep(1000L);
                } catch (Exception e) {
                    Log.e("jkseo", e.getMessage());
                }
            } else {
                String[] noti2 = {"su", "-c", "am", "broadcast", "-a", "END_MACRO_EVENT", "com.example.macrotester"};
                Process p = Runtime.getRuntime().exec(noti2);
                try {
                    p.getErrorStream().close();
                    Thread.sleep(1000L);
                } catch (Exception e2) {
                }
            }
        } catch (Exception e3) {
            Log.e("jkseo", "ADB 앱호출 테스트 에러 : " + e3.toString());
        }
    }
    // 권한 요청을 수행하는 함수
    private void requestStoragePermission() {
        // storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]
                        {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE
                                , Manifest.permission.POST_NOTIFICATIONS
                                , Manifest.permission.SYSTEM_ALERT_WINDOW
                        }, 2);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

            Intent iLoader = new Intent();
            Intent iTester = new Intent();

            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
//                i.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);

                iLoader.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                iLoader.setData(Uri.parse("package:" + packageName));
                startActivity(iLoader);

                // tester 앱은 권한요청을 안해도 되나??

//                packageName = "com.example.macrotester";
//                iTester.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
//                iTester.setData(Uri.parse("package:" + packageName));
//                startActivity(iTester);
            }
        }

        // superuser permission
        final Runtime rt = Runtime.getRuntime();
        try
        {
            rt.exec("su");
        }
        catch(IOException e)
        {
            Log.e("jkseo", e.getMessage());
        }
    }

    private void ShowLogHistory() {
        Intent intent = new Intent();
        intent.setAction("com.sec.android.app.myfiles.PICK_DATA");
        intent.putExtra("CONTENT_TYPE", "*/*");
        intent.putExtra("FOLDERPATH", mEventHistory.LOG_PATH.getPath());

//        startActivity(intent);
        startActivityForResult(intent, 1000);

    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == -1 && i == 1000) {
            Uri data = intent.getData();
            Intent intent2 = new Intent("android.intent.action.VIEW");
            intent2.setDataAndType(data, "text/plain");
            try{
                startActivity(intent2);
            }
            catch (Exception ex){
                Log.e("jkseo", ex.getMessage());
            }
        }
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {

        // Instantiate the new Fragment
        final Bundle args = pref.getExtras();
        final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
                getClassLoader(),
                pref.getFragment());
        fragment.setArguments(args);
        fragment.setTargetFragment(caller, 0);
        // Replace the existing Fragment with the new Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .addToBackStack(null)
                .commit();
        return true;

    }

    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;

        if (0 <= gapTime && 2000 >= gapTime){
            super.onBackPressed();
        }
        else{
            backBtnTime = curTime;
            Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

}