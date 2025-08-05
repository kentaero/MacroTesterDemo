package com.example.macrotester;

import static androidx.test.espresso.Espresso.onView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;

import androidx.preference.PreferenceManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.SdkSuppress;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import ModelGroup.MacroEvent;
import ModelGroup.MacroResult;
import ModelGroup.RandomEvent;
import UtilGroup.Configuration;
import UtilGroup.EventHistory;
import UtilGroup.NetworkHelper;
import UtilGroup.ShellExecuter;

import androidx.test.rule.ServiceTestRule;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class ExampleInstrumentedTest {
    private Context mContext;
    private ChromeMacro cMacro;
    private UiDevice mDevice;
    private Configuration mConfig;
    private DelayEvent mDelayEvent;
    private String action_name = "";
    private EventHistory mEventHistory;
    private AirPlanModeHelper mAirPlanModeHelper;
    private Boolean isStay = false;

    private ShellExecuter shell;
    private ArrayList<MacroEvent> Jobs = new ArrayList<>();
    private Gson gson = new Gson();
    private NetworkHelper nHelper;

    private MacroManager mMacroManager;

//    @Rule
//    public ActivityScenarioRule<LoaderActivity> activityScenarioRule = new ActivityScenarioRule<>(LoaderActivity.class);

    @Before
    public  void Init() throws Exception{
        mContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        mEventHistory = new EventHistory(false);
        mConfig = new Configuration(this.mContext,this.mEventHistory);
        shell =  new ShellExecuter(mEventHistory);

        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        cMacro = new ChromeMacro(this.mContext, this.mEventHistory, this.mDevice);
        nHelper = new NetworkHelper(this.mContext, this.mConfig);
        mMacroManager = new MacroManager(mConfig);

        // ver 1.6.1
        // debug noti 대신 포그라운드 서비스를 실행하여 해보자 안죽어보자!! 살아보자!!
        shell.execNoti(true);

        mConfig.setItem("IsRunning", "1");
        Bundle extras = InstrumentationRegistry.getArguments();
        action_name = extras.getString("ACTION_NAME");

        Log.e("jkseo", "action_name : " + action_name);

        if (!this.mDevice.isScreenOn()) {
            this.mDevice.wakeUp();
        }
        mDelayEvent = new DelayEvent(new EventHistory(false));

        this.mAirPlanModeHelper = new AirPlanModeHelper(mContext, this.mEventHistory);

        // SharedPreferences 객체 가져오기
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.mContext);

        // 특정 키로 저장된 설정 값을 읽기
        String smart_id = sharedPreferences.getString("smart_id", "");

        //Log.e("jkseo", config.getItem("smart_id"));

        WindowManager wm = (WindowManager) this.mContext.getSystemService("window");
        wm.getDefaultDisplay();
    }

    @Test
    public void UseTest()  {

        try{
            if(action_name == null || action_name.length() == 0)
            {
                action_name = "";
//                action_name = "RunChromeMacro";
//                action_name = "CheckFileDescriptor";
//                action_name = "useAppContext";
//                action_name = "AllCloseTab";

                // <string name="Job`s">[{&quot;AirPlanModeSec&quot;:60000,&quot;DeviceID&quot;:1000,&quot;IsActive&quot;:true,&quot;Keyword&quot;:&quot;다음&quot;,&quot;MacroIdx&quot;:56,&quot;MacroMode&quot;:&quot;M0100001&quot;,&quot;MacroRunDelayMin&quot;:1,&quot;MacroSetDelayMin&quot;:1,&quot;Model&quot;:&quot;M0001&quot;,&quot;RndClickData&quot;:&quot;&quot;,&quot;ScrollForwardLimitCnt&quot;:5,&quot;SearchType&quot;:&quot;M0200002&quot;,&quot;SiteName&quot;:&quot;개발테스트용2(다음)&quot;,&quot;SiteUrl&quot;:&quot;m.daum.net&quot;,&quot;SiteWaitTime&quot;:1},{&quot;AirPlanModeSec&quot;:60000,&quot;DeviceID&quot;:1000,&quot;IsActive&quot;:true,&quot;Keyword&quot;:&quot;네이버&quot;,&quot;MacroIdx&quot;:57,&quot;MacroMode&quot;:&quot;M0100001&quot;,&quot;MacroRunDelayMin&quot;:1,&quot;MacroSetDelayMin&quot;:1,&quot;Model&quot;:&quot;M0001&quot;,&quot;RndClickData&quot;:&quot;&quot;,&quot;ScrollForwardLimitCnt&quot;:15,&quot;SearchType&quot;:&quot;M0200001&quot;,&quot;SiteName&quot;:&quot;개발테스트용3(네이버로그인)&quot;,&quot;SiteUrl&quot;:&quot;nid.naver.com&quot;,&quot;SiteWaitTime&quot;:1},{&quot;AirPlanModeSec&quot;:60000,&quot;DeviceID&quot;:1000,&quot;IsActive&quot;:true,&quot;Keyword&quot;:&quot;토토&quot;,&quot;MacroIdx&quot;:55,&quot;MacroMode&quot;:&quot;M0100001&quot;,&quot;MacroRunDelayMin&quot;:1,&quot;MacroSetDelayMin&quot;:1,&quot;Model&quot;:&quot;M0001&quot;,&quot;RndClickData&quot;:&quot;&quot;,&quot;ScrollForwardLimitCnt&quot;:3,&quot;SearchType&quot;:&quot;M0200003&quot;,&quot;SiteName&quot;:&quot;개발테스트용(토토)&quot;,&quot;SiteUrl&quot;:&quot;스폰서&quot;,&quot;SiteWaitTime&quot;:1}]</string>
            }

            if (action_name.equalsIgnoreCase("cleanLog")){
                mEventHistory.cleanOldLog(7);
            }
            else if (action_name.equalsIgnoreCase("useAppContext")){
                mEventHistory.writeEventHistory("call useAppContext");
                useAppContext();
            }
            else if(action_name.equalsIgnoreCase("useAppContextNomal")){
                mEventHistory.writeEventHistory("call useAppContextNomal");

                useAppContextNomal();
            }
            else if(action_name.equalsIgnoreCase("ReCycleCallApi")){
                // api 호출만 무한반복해보자
                while(true){
//                    ArrayList<MacroEvent> jobs = mMacroManager.LoadMacroData(mConfig.getItem("ComId"), mConfig.getItem("SmartId"));
                    ArrayList<MacroEvent> jobs = mMacroManager.LoadMacroDataDebug(mConfig.getItem("ComId"), mConfig.getItem("SmartId"));
                    String data = "";
                    if(jobs == null) {
                        // 대기작업이 없어도 스케줄링 대기시간에 맞추어 계속 돈다.
                        mEventHistory.writeEventHistory("시작할 작업이 없습니다.");
                    }
                    else {
                        mEventHistory.writeEventHistory("스케줄링 작업 대기수량 : " + String.valueOf(jobs.size()));
                        data = mMacroManager.ConvertToMacroEvent(jobs);
                    }
                }

            }
            else if (action_name.equalsIgnoreCase("CheckFileDescriptor")){
                try{
                    mEventHistory.writeEventHistory("call CheckFileDescriptor");

                    CheckDescriptorRunChromeMacro();

                    if (mConfig.GetBooleanItem("ScheduleFlag"))
                    {
                        // 재사용 대기시간 처리
                        int  reCycleTime = Integer.valueOf(mConfig.getItem("MacroSetDelayMin"));
                        if(reCycleTime > 0)
                        {
//                        if(mAirPlanModeHelper.getAirplaneMode() == 1)
//                        {
//                            // 비행기모드가 안풀린경우 다시한번 처리한다.
//                            this.mEventHistory.writeEventHistory("비행기모드 해제 : UseTest");
//
//                            mAirPlanModeHelper.settingXiaomiAirplane(false);
//                            this.mDelayEvent.exec(10000L);
//                        }

                            // 직접 api 조회 및
                            this.mEventHistory.writeEventHistory("재사용 대기시간 설정(분) : " + String.valueOf(reCycleTime));
                            WaitingForNextTask(reCycleTime, true);

                            ArrayList<MacroEvent> jobs = mMacroManager.LoadMacroData(mConfig.getItem("ComId"), mConfig.getItem("SmartId"));
                            String data = "";
                            if(jobs == null) {
                                // 대기작업이 없어도 스케줄링 대기시간에 맞추어 계속 돈다.
                                mEventHistory.writeEventHistory("시작할 작업이 없습니다.");
                            }
                            else {
                                mEventHistory.writeEventHistory("스케줄링 작업 대기수량 : " + String.valueOf(jobs.size()));
                                data = mMacroManager.ConvertToMacroEvent(jobs);
                            }

                            mConfig.setItem("Jobs", data);

                            //재귀호출
                            UseTest();
                        }else {
                            // 재사용시간이 0이면 단발성 실행으로 처리한다.
                            // 매크로 스케쥴 해제
                            mConfig.setItem("ScheduleFlag", false);
                        }
                    }
                    else
                    {
                        // 초기화 해제
//                    mConfig.setItem("Jobs", "");
                        // 매크로 스케쥴 해제
                        mConfig.setItem("ScheduleFlag", false);
                    }
                }
                catch (Exception ex)
                {
                    Log.e("jkseo", "RunChromeMacro error :" + ex.getMessage());
                    Log.e("jkseo", "RunChromeMacro error :" + ex.getStackTrace().toString());
                }
            }
            else if (action_name.equalsIgnoreCase("openApp")){
                cMacro.OpenApp("com.android.chrome");
            }
            else if (action_name.equalsIgnoreCase("AllCloseTab")){
                cMacro.OpenApp("com.android.chrome");
                this.mDelayEvent.exec(5000L);

                this.cMacro.clickTabSwitchBtn();
                this.mDelayEvent.exec(3000L);
                this.cMacro.clickMenuBtn();
                this.mDelayEvent.exec(1000L);
                this.cMacro.clickAllClose();
            }
            else if (action_name.equalsIgnoreCase("WaitingForNextTask")) {
                // 스케줄 대기 테스트
                mConfig.setItem("ScheduleFlag", true);

                int  sleepTime = Integer.valueOf(mConfig.getItem("MacroSetDelayMin"));
                WaitingForNextTask(sleepTime, false);

            }
            else if (action_name.equalsIgnoreCase("GoogleCloseAlarm")){
                cMacro.Close_GoogleLogin("로그아옷");
            }
            else if (action_name.equalsIgnoreCase("ControlAirPlanMode")){
                ControlAirPlanMode();
            }
            else if (action_name.equalsIgnoreCase("RunRandomScroll")){
                RunRandomScroll();
            }
            else if (action_name.equalsIgnoreCase("ChangeSearchEngine")){
                ChangeSearchEngine();
            }
            else if (action_name.equalsIgnoreCase("DeleteSearchHistory")){
                DeleteSearchHistory();
            }
            else if (action_name.equalsIgnoreCase("CloseApp")){
                CloseApp();
            }
            else if (action_name.equalsIgnoreCase("RunChromeMacro")){
                try{
                    RunChromeMacro();

                    // 스케쥴이 존재하지 않아도 재사용 처리로직을 탄다?
                    if (mConfig.GetBooleanItem("ScheduleFlag"))
                    {
                        // 재사용 대기시간 처리
                        int  reCycleTime = Integer.valueOf(mConfig.getItem("MacroSetDelayMin"));
                        if(reCycleTime > 0)
                        {
                            // 직접 api 조회 및
                            this.mEventHistory.writeEventHistory("재사용 대기시간 설정(분) : " + String.valueOf(reCycleTime));
                            WaitingForNextTask(reCycleTime, true);

                            // 비행기모드 체크하여 오프상태라면 해제한다.
                            if(mAirPlanModeHelper.getAirplaneMode() == 1)
                            {
                                this.mEventHistory.writeEventHistory("비행기모드 강제 해제 처리");
                                mAirPlanModeHelper.setCommandAirplane(false);
                                this.mDelayEvent.exec(5000L);
                            }

                            // 재부팅 설정이 되어있다면 매크로 동작횟수를 증가시키고, 설정 횟수에 도달하면 재부팅을 실행한다.
                            int RebootOnWorkLimit = Integer.valueOf(mConfig.getItem("RebootOnWorkLimit"));
                            mEventHistory.writeEventHistory("재부팅 설정 횟수 : " + String.valueOf(RebootOnWorkLimit));
                            int WorkingCount =  Integer.valueOf(mConfig.getItem("WorkingCount"));
                            mEventHistory.writeEventHistory("재부팅 대기 횟수(증가전) : " + String.valueOf(WorkingCount));

                            if(RebootOnWorkLimit > 0){
                                WorkingCount = WorkingCount + 1;
                                mConfig.setItem("WorkingCount", String.valueOf(WorkingCount));
//                                mEventHistory.writeEventHistory("재부팅 대기 횟수(증가후) : " + String.valueOf(WorkingCount));

                                if(WorkingCount >= RebootOnWorkLimit)
                                {
                                    mConfig.setItem("WorkingCount", String.valueOf(0));
                                    mEventHistory.writeEventHistory("재부팅 시작");

                                    // 설정 횟수 도달하면 재부팅
                                    ShellExecuter executer = new ShellExecuter(mEventHistory);
                                    executer.Reboot();

                                    mEventHistory.writeEventHistory("재부팅 실패!! 재시작 로직 수행합니다.!!");

                                    // 재부팅이 안되면 다음에 시도하도록 다시루틴 처리한다.
//                                    return;
                                }
                            }

                            ArrayList<MacroEvent> jobs = mMacroManager.LoadMacroData(mConfig.getItem("ComId"), mConfig.getItem("SmartId"));
                            String data = "";
                            if(jobs == null) {
                                // 대기작업이 없어도 스케줄링 대기시간에 맞추어 계속 돈다.
                                mEventHistory.writeEventHistory("시작할 작업이 없습니다.");
                            }
                            else {
                                mEventHistory.writeEventHistory("스케줄링 작업 대기수량 : " + String.valueOf(jobs.size()));
                                data = mMacroManager.ConvertToMacroEvent(jobs);
                            }

                            mConfig.setItem("Jobs", data);

                            //재귀호출
                            UseTest();

                        }else {
                            // 재사용시간이 0이면 단발성 실행으로 처리한다.
                            // 매크로 스케쥴 해제
                            mConfig.setItem("ScheduleFlag", false);
                        }
                    }
                    else
                    {
                        // 매크로 스케쥴 해제
                        // debug
//                    mConfig.setItem("Jobs", "");
                        mConfig.setItem("ScheduleFlag", false);
                    }

                    // 앱 정상종료 처리
                    mEventHistory.writeEventHistory("기존 앱 정상종료 처리");
                    assertTrue(true);
                }
                catch (Exception ex)
                {
                    this.mEventHistory.writeEventHistory(ex.getMessage());
                    Log.e("jkseo", "RunChromeMacro error :" + ex.getMessage());
                    Log.e("jkseo", "RunChromeMacro error :" + ex.getStackTrace().toString());

                    // 에러가발생하면 재실행할 수 있도록 처리한다.
                    //재귀호출
                    if (mConfig.GetBooleanItem("ScheduleFlag"))
                    {
                        // debug 재실행 처리, 재귀호출을 1분단위로 자꾸 반복하다보니 동작중 멈춤현상이 발생하여 계속 재실행하도록 처리한다.
                        action_name = "useAppContext";
                        UseTest();
                    }

                }
            }
            else if(action_name.equalsIgnoreCase("SchedulringTest"))
            {
                // 알림아이콘 표시 before event 에서 수행
                // 1분의 체류시간동안 대기
                mConfig.setItem("IsRunning", "1");
                mDevice.pressHome();

                // 크롬 브라우저 실행
                cMacro.OpenApp("com.android.chrome");
                this.mDelayEvent.exec(10000L);

                cMacro.tabbarSize();

                // todo 체류시간(분) 입력받아서 랜덤으로 초단위 구성한다
                // 체류시간(랜덤) : 최소 30 sec ~ 사용자 입력시간 1분
                long randomStaySec = cMacro.randomRange(30000L, 1000 * 60);
                int rndScrollStep = 0;
                this.mEventHistory.writeEventHistory("랜덤 스크롤 : 체류시간 " + String.valueOf(randomStaySec/1000/60) + "분" + String.valueOf(randomStaySec/1000) + "초");

                if(randomStaySec > 0) {
                    isStay = true;
                    Timer cntTimer;
                    TimerTask cntTimerTask;

                    cntTimer = new Timer();
                    cntTimerTask = new TimerTask() {
                        @Override
                        public void run() {
                            Log.e("jkseo", "체류시간 종료 call");

                            if(isStay){
                                Log.e("jkseo", "체류시간 종료");
                                isStay=false;
                            }
                        }
                    };

                    cntTimer.schedule(cntTimerTask, randomStaySec, randomStaySec);
                    while(isStay){

                    } // end while

                    cntTimerTask.cancel();
                    cntTimer.cancel();
                }
                mConfig.setItem("IsRunning", "0");
                // 알림아이콘 해제 after event 에서 수행
            }
        }
        catch(Exception ex){
            try{
                this.mEventHistory.writeEventHistory("(SaveMacroHisreal) 결과를 중에러발생 :" + ex.getMessage());

                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                this.mEventHistory.writeEventHistory("(SaveMacroHisreal) 결과를 중에러발생 :" + sw.toString());
            }
            catch (IOException ioe){
                Log.e("jkseo", ioe.getMessage());
            }
        }
        catch (Throwable throwable){
            try{
                this.mEventHistory.writeEventHistory("(SaveMacroHisreal) 결과를 중에러발생 :" + throwable.getMessage());

                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                throwable.printStackTrace(pw);
                this.mEventHistory.writeEventHistory("(SaveMacroHisreal) 결과를 중에러발생 :" + sw.toString());
            }
            catch (IOException ioe){
                Log.e("jkseo", ioe.getMessage());
            }
        }
    }

    private void WaitingForNextTask(int sleepTime, boolean isRandom) throws Exception {
        long tempTime = (long)(sleepTime * 1000 * 60);
        long time = (long)(sleepTime * 1000 * 60);

        if(isRandom){
            long stayMin = time/3;

            if(stayMin < 1000) stayMin = 1000;

            time = cMacro.randomRange(stayMin, time);
        }

        mEventHistory.writeEventHistory("대기시작 : " + (time / 1000)  / 60 + "분(" + (time / 1000) + "초)");
        // 장시간 대기시 화면꺼짐방지 클릭 및 로그기록
        mDevice.click(20,20);

        // 3분 기준 분할대기 처리한다. 180000
        if (time > 180000) {
            boolean sleepNext = true;
            while (sleepNext) {
                try {
                    Thread.sleep(180000);

                    // 종료처리
                    if(CheckStop() == 0) {
                        sleepNext = false;
                        break;
                    }
                } catch (Exception e) {
                }
                time -= 180000;

                // 장시간 대기시 화면꺼짐방지 클릭 및 로그기록
                mDevice.click(20,20);
                Log.e("jkseo", "남은시간 :" + (time / 1000)  / 60 + "분(" + (time / 1000) + "초)");
//                mEventHistory.writeEventHistory("남은시간 :" + (time / 1000)  / 60 + "분(" + (time / 1000) + "초)");

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

    private void CloseApp() throws Exception{
//        this.cMacro.CloseApps();
        try{
            this.mEventHistory.writeEventHistory("최근 앱 종료");

            this.mDevice.pressHome();
            this.mDelayEvent.exec(1000L);

            this.mDevice.pressRecentApps();

            this.mDelayEvent.exec(1000L);

            List<UiObject2> appClosebuttons = this.mDevice.findObjects(By.res("com.android.systemui:id/dismiss_task"));

            int closeCount = appClosebuttons.size();

            if(appClosebuttons!= null)
            {
                closeCount = appClosebuttons.size();

                for (int i = 0; i < closeCount; i++) {

                    try{
                        appClosebuttons.get(i).click();
                        this.mDelayEvent.exec(1000L);
                    }
                    catch (Exception subEx){
                        this.mEventHistory.writeEventHistory("CloseApp Error : " + subEx.getMessage());

                    }
                }

                // 샤오미는 동작앱 또한 삭제처리되므로, 제외한다.
                // xiaomi
//                UiObject2 allclearButton = this.mDevice.findObject(By.res("com.android.systemui:id/clearAnimView"));
//
//                if (allclearButton != null){
//                    allclearButton.click();
//                }
            }
        }
        catch (Exception ex){
            this.mEventHistory.writeEventHistory("CloseApp Error : " + ex.getMessage());
        }
    }

    private String GetCurrentTime(String formater){
        long now = System.currentTimeMillis();
        // yyyyMMdd, yyyy-MM-dd HH:mm:ss
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formater);
        String currTime = simpleDateFormat.format(now);

        return currTime;
    }

    private void CheckFileDescriptorCount(String checkPoint) throws Exception{
        int pid = android.os.Process.myPid();
        // /proc/{pid}/fd/ 디렉터리 경로 생성
        String fdPath = "/proc/" + pid + "/fd/";

        // 열린 파일 디스크립터 수 확인
        int fileDescriptorCount = countFileDescriptors(fdPath);

        this.mEventHistory.writeEventHistory("[" + checkPoint + "] FileDescrip count : " + String.valueOf(fileDescriptorCount));
    }

    private int countFileDescriptors(String fdPath) {
        try {
            // /proc/{pid}/fd/ 디렉터리 내의 파일 수 세기
            File fdDir = new File(fdPath);
            File[] files = fdDir.listFiles();
            if (files != null) {
                return files.length;
            } else {
                Log.e("FileDescriptorCount", "파일 디스크립터를 확인하는 중 오류가 발생했습니다.");
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    private void CheckDescriptorRunChromeMacro() throws Exception
    {
        CheckFileDescriptorCount("before CheckDescriptorRunChromeMacro");

        if(LoadMacroData())
        {
            int JobsCount = Jobs.size();
            int ReworkCount = 0;
            int ComID = Integer.valueOf(mConfig.getItem("ComId"));

            MacroEvent event = null;
            MacroResult event_result = null;
            for (int i = 0; i<Jobs.size(); i++)
            {
                event = Jobs.get(i);
                event_result = new MacroResult(ComID,  event.getDeviceID(), event.getMacroIdx(), event.getSiteName());

                // 외부 공인 ip를 가져온다. 비동기라 브라우저를 실행하고 값을 집어넣도록 처리한다.
                nHelper.CallGetPublicIp();

                event_result.setStartTime(GetCurrentTime("yyyy-MM-dd HH:mm:ss"));

                mConfig.setItem("Model", event.getModel());
                mConfig.setItem("MacroRunDelayMin", String.valueOf(event.getMacroRunDelayMin()));
                mConfig.setItem("MacroSetDelayMin", String.valueOf(event.getMacroSetDelayMin()));

                this.mEventHistory.writeEventHistory("매크로 시작");
                // 크롬 브라우저 실행
                cMacro.OpenApp("com.android.chrome");

                this.mDelayEvent.exec(5000L);

                cMacro.tabbarSize();

                event_result.setIPAddress(mConfig.getItem("result_ipaddress"));

                // 동작도중 오류가 발생하더라도 후작업은 순차적으로 처리되도록 한다.
                try{
                    if(CheckStop() == 0) {
                        event_result.setEndTime(GetCurrentTime("yyyy-MM-dd HH:mm:ss"));
                        break;
                    }

                    this.mEventHistory.writeEventHistory("검색대상 사이트 : " + event.getSiteName());

                    // 스크롤 주소창을 보기위해 스크롤 상단 이동
                    cMacro.drag_ScrollToTopOrBottom(true);

                    Boolean isclickSearchBar = false;
                    isclickSearchBar = cMacro.ClickURLBar();

                    if(CheckStop() == 0)
                    {   event_result.setEndTime(GetCurrentTime("yyyy-MM-dd HH:mm:ss"));
                        break;
                    }

                    // 검새입력방식 분기 (URL, 검색입력란)
//                    M0100001	키워드
//                    M0100002	URL
                    Boolean isFindSearchBar = false;
                    if(event.getMacroMode().equalsIgnoreCase("M0100001")){
                        // 키워드 방식 검색
                        this.mDelayEvent.exec(1000L);
                        // 한영 키보드문제, 덤프관련(한번씩 안떠짐) 문제가 발생하여, url에서 검색어를 입력하면 검색되는 방식으로 변경한다.
                        // [27,825][171,936]
                        this.mEventHistory.writeEventHistory("검색모드 : 키워드 방식");
                        isFindSearchBar = cMacro.InputSearchKeyword(event.getKeyword(), isclickSearchBar);
                    }
                    else if(event.getMacroMode().equalsIgnoreCase("M0100002")){
                        // URL 방식 검색
                        this.mDelayEvent.exec(1000L);
                        this.mEventHistory.writeEventHistory("검색모드 : URL 방식");
                        isFindSearchBar = cMacro.InputSearchKeyword(event.getKeyword(), isclickSearchBar);
                    }

                    // 검색창 또는 주소창을 찾지못하면 다음키워드로 넘긴다.
                    this.mEventHistory.writeEventHistory("검색어 입력 결과 : " + String.valueOf(isFindSearchBar));
                    CheckFileDescriptorCount("CheckDescriptorRunChromeMacro");

                    if(!isFindSearchBar)
                    {
                        this.mEventHistory.writeEventHistory("갬색어 입력실패하여 다음 순번 매크로 이동");
                        event_result.setEndTime(GetCurrentTime("yyyy-MM-dd HH:mm:ss"));
                        event_result.setRemarks("갬색어 입력실패하여 다음 순번 매크로 이동");
                        event_result.setSearchResult(false);

                        SaveMacroHisreal(event_result);
                        continue;
                    }

                    if(CheckStop() == 0) {
                        event_result.setEndTime(GetCurrentTime("yyyy-MM-dd HH:mm:ss"));
                        break;
                    }

                    boolean searchResult = false;

                    if(event.getMacroMode().equalsIgnoreCase("M0100002")) {
                        searchResult = true;
                    }
                    else{
                        searchResult =  cMacro.searchFindKey(event.getSiteUrl(), event.getSearchType(), event.getScrollForwardLimitCnt());
                    }

                    CheckFileDescriptorCount("CheckDescriptorRunChromeMacro");

                    if(CheckStop() == 0) {
                        event_result.setEndTime(GetCurrentTime("yyyy-MM-dd HH:mm:ss"));
                        break;
                    }

                    if(searchResult) {
                        this.mEventHistory.writeEventHistory("사이트 찾기 성공");
                        event_result.setSearchResult(true);

                        this.mDelayEvent.exec(2000L);

                        if(CheckStop() == 0) {
                            event_result.setEndTime(GetCurrentTime("yyyy-MM-dd HH:mm:ss"));
                            break;
                        }

                        String domain = event.getSiteUrl();

                        // 주소변환 : 프로토콜 제거
                        if(domain.toLowerCase().contains("http://") || domain.toLowerCase().contains("https://"))
                        {
                            URI uri = new URI(domain);
                            domain = uri.getHost();
                        }

                        this.mEventHistory.writeEventHistory("사이트 도메인 : " + domain);

                        // 체류시간(랜덤) 계산
                        // 사용자입력시간(분) -> 초단위 변환
                        // 최소 사용자 입력시간 / 3 부터 최대 사용자 입력시간
                        long stayMax = (event.getSiteWaitTime() *60) * 1000;
                        long stayMin = stayMax/3;
                        long randomStaySec = cMacro.randomRange(stayMin, stayMax);
                        this.mEventHistory.writeEventHistory("체류시간 설정 : " + String.valueOf(event.getSiteWaitTime()) + "분");
                        this.mEventHistory.writeEventHistory("체류시간 계산 : " + String.valueOf(randomStaySec/1000/60) + "분 또는 " + String.valueOf(randomStaySec/1000) + "초");
                        event_result.setSiteWaitTime(randomStaySec);

                        Queue<RandomEvent> rndEvents = null;

                        if(randomStaySec > 0) {
                            boolean scrollResult = false;
                            boolean scrollTopNDown = false;
                            boolean scrollTopNDownResult = true;

                            int scrollCount = 0;
                            int scrollStep  = 0;
                            int actionCount = 1;
                            Random random = new Random();

                            isStay = true;
                            Timer cntTimer;
                            TimerTask cntTimerTask;
                            cntTimer = new Timer();
                            cntTimerTask = new TimerTask() {
                                @Override
                                public void run() {
                                    /* 빈공백으로 두니 자꾸 죽는경우가 발생하여 일부러 Log를 찍어둔다. */
                                    Log.e("jkseo", "체류시간 종료 1");

                                    if(isStay){
//                                        Log.e("jkseo", "체류시간 종료2");
                                        isStay=false;
                                    }
                                }
                            };

                            // 간혹 스크롤객체를 못얻어와서 스크롤이동이 안되는경우가 발생하여 딜레이 1초 준다.
                            this.mDelayEvent.exec(1000);

                            cntTimer.schedule(cntTimerTask, randomStaySec);
                            while(isStay){
                                if(CheckStop() == 0) {
                                    event_result.setEndTime(GetCurrentTime("yyyy-MM-dd HH:mm:ss"));
                                    break;
                                }

                                if(actionCount == 1)
                                {
                                    // 여러번 반복하므로, Log는 한번만 남기자
                                    this.mEventHistory.writeEventHistory("체류중");
                                }

                                if(event.getRndClickData() == null || event.getRndClickData().length() == 0)
                                {
                                    if(actionCount == 1)
                                    {
                                        // 여러번 반복하므로, Log는 한번만 남기자
                                        this.mEventHistory.writeEventHistory("동작이벤트 미등록 기본 랜덤로직으로 처리됩니다.");
                                    }

                                    // 동작이벤트가 입력되지 않은경우
                                    // 페이지를 맨아래까지 이동하고, 다시 최상단으로 이동한 후 랜덤클릭 액션 처리한다.
                                    if(!scrollResult)
                                    {
                                        scrollStep = (int)cMacro.randomRange(30L, 110L);

                                        // 랜덤 스크롤 이벤트만 처리한다.
//                                    if(!this.cMacro.custom_ScrollToTopOrBottom(false, scrollStep))
//                                        scrollResult = true;
//
//                                    if(scrollResult)
//                                        this.cMacro.flingToBeginning();

                                        if(!scrollTopNDownResult)
                                        {
                                            // 랜덤 스크롤 시작하자
                                            scrollCount = scrollCount + 1;
                                        }

                                        if(scrollCount < 1)
                                        {
                                            scrollTopNDownResult = this.cMacro.custom_ScrollToTopOrBottom(scrollTopNDown, scrollStep);
                                        }
                                        else
                                        {
                                            // 스크롤 방향 정하기
                                            if(((int)(Math.random() * 10)) >= 4)
                                                scrollTopNDown = true;
                                            else
                                                scrollTopNDown = false;

                                            this.cMacro.custom_ScrollToTopOrBottom(scrollTopNDown, scrollStep);
                                        }

                                        // 화면꺼짐 방지 클릭
                                        this.mDevice.click(20,20);
                                        this.mDelayEvent.exec((int)cMacro.randomRange(1000L * 10L, 1000L * 30L));
                                        actionCount = actionCount + 1;
                                    }
                                    else{
                                        // 체류시간이 남아있는 경우 계속해서 랜덤클릭 처리한다.
                                        this.mEventHistory.writeEventHistory("동작이벤트 : " + String.valueOf(actionCount) + "회차 실행");

                                        if(this.cMacro.randomClick(domain)){
                                            // return 이 돌아오면 페이지가 이동되었다는 뜻이므로
                                            // 스크롤 이벤트를 넣어준다.
                                            scrollResult = false;
                                        }

                                        actionCount = actionCount + 1;
                                    }

                                } // if(event.getRndClickData() == null)
                                else{
                                    // 동작이벤트 입력된 경우

                                    if(rndEvents == null){
                                        this.mEventHistory.writeEventHistory("동작이벤트 파싱");
                                        rndEvents = ParseEvent(event.getRndClickData());

                                        if(rndEvents==null)
                                        {
                                            // 동작이벤트 구문 파싱이 실패한 경우
                                            this.mEventHistory.writeEventHistory("동작이벤트 파싱이 실패하여 기본랜덤로직으로 처리됩니다.");
                                            event.setRndClickData(null);

                                            continue;
                                        }
                                    } // end if rndEvents == null
                                    else {
                                        // 동작이벤트 처리
                                        if (rndEvents.size() == 0)
                                        {
                                            event.setRndClickData(null);
                                            continue;
                                        }
                                        RandomEvent rndEvent = rndEvents.peek();

                                        this.mEventHistory.writeEventHistory("동작이벤트 : " + String.valueOf(actionCount) + "회차 " + rndEvent.getCommand() + " 실행");
                                        Boolean rndEventResult = ExecRandomEvent(rndEvent);

                                        if(rndEvent.getCommand().equalsIgnoreCase("rndScroll"))
                                        {
                                            // 랜덤스크롤 이벤트일 경우 체류시간 까지 해당이벤트만 반복처리한다.
                                            this.mEventHistory.writeEventHistory("랜덤스크롤 이벤트 반복처리");
                                        }
                                        else if(rndEvent.getCommand().equalsIgnoreCase("random"))
                                        {
                                            // 랜덤클릭 이벤트일 경우 체류시간 까지 해당이벤트만 반복처리한다.
                                            this.mEventHistory.writeEventHistory("랜덤클릭 이벤트 반복처리");
                                            rndEvents.poll();
                                            event.setRndClickData(null);
                                        }
                                        else {
                                            rndEvents.poll();
                                        }

                                        actionCount = actionCount + 1;
                                    }
                                }
                            } // end while

                            cntTimerTask.cancel();
                            cntTimer.cancel();
                        }
                        this.mEventHistory.writeEventHistory("체류시간 종료 2");

                        CheckFileDescriptorCount("CheckDescriptorRunChromeMacro");

                        // 화면꺼짐 방지 클릭
                        this.mDevice.click(20,20);

                        event_result.setEndTime(GetCurrentTime("yyyy-MM-dd HH:mm:ss"));
                        SaveMacroHisreal(event_result);
                    }
                    else {
                        this.mEventHistory.writeEventHistory("사이트 찾기 실패");
                        event_result.setSearchResult(false);
                        event_result.setEndTime(GetCurrentTime("yyyy-MM-dd HH:mm:ss"));
                        event_result.setRemarks("입력된 조건으로 사이트를 찾을 수 없습니다. 다음 순번 매크로 이동");

                        SaveMacroHisreal(event_result);

                        // 검색실패하더라도 종료루틴은 처리한다.
//                        continue;
                    }

                    this.mDelayEvent.exec(2000L);

                    // 검색기록 삭제
                    cMacro.clickMenuBtn();
                    cMacro.clickAccessRecord();
                    cMacro.clickAccessRecordDeleteBtn();
                    cMacro.clickAccessRecordDeleteOption1();
                    cMacro.clickAccessRecordDeleteOption2();
                    cMacro.clickAccessRecordDelete();
                    cMacro.clickAccessRecordDelete1();
                    this.mDelayEvent.exec(1000L);

                    // 검색기록 종료
                    mDevice.pressBack();
                    this.mDelayEvent.exec(1000L);

                    // 페이징 종료
                    this.cMacro.clickTabSwitchBtn();
                    this.mDelayEvent.exec(3000L);
                    this.cMacro.clickMenuBtn();
                    this.mDelayEvent.exec(1000L);
                    this.cMacro.clickAllClose();
                    this.mDelayEvent.exec(1000L);

                    // home
                    this.mDevice.pressHome();

                    CheckFileDescriptorCount("CheckDescriptorRunChromeMacro");
                    // 비행모드 10sec
                    // 그외는 공통처리
                    if (mConfig.getItem("Model").equals("M0001")){
                        // 갤럭시는 비행모드 설정이 달라서 별도로 처리
                        EventHistory eventHistory10 = this.mEventHistory;
                        this.mEventHistory.writeEventHistory("현재비행기모드상태 체크 : " + mAirPlanModeHelper.getAirplaneMode());
                        if (mAirPlanModeHelper.getAirplaneMode() == 0) {
                            mAirPlanModeHelper.setCommandAirplane(true);
                            this.mEventHistory.writeEventHistory("비행기모드 ON");
                            this.mDevice.pressHome();
                            this.mEventHistory.writeEventHistory("비행기모드 화면 대기 10초");
                            this.mDelayEvent.exec(1000L * 10L);
                        }
                        mAirPlanModeHelper.setCommandAirplane(false);

                        this.mEventHistory.writeEventHistory("비행기모드 OFF");
                        this.mDelayEvent.exec(5000L);

                        // todo 네트워크 등록에 실패하였습니다. 알림창 닫기
                        cMacro.CloseAlarm_NetworkError("확인");
                    }
                    else{
                        // 샤오미
                        EventHistory eventHistory10 = this.mEventHistory;
                        this.mEventHistory.writeEventHistory("현재비행기모드상태 체크 : " + mAirPlanModeHelper.getAirplaneMode());
                        if (mAirPlanModeHelper.getAirplaneMode() == 0) {
                            mAirPlanModeHelper.setCommandAirplane(true);
                            this.mEventHistory.writeEventHistory("비행기모드 ON");
                            this.mDevice.pressHome();
                            this.mEventHistory.writeEventHistory("비행기모드 화면 대기 10초");
                            this.mDelayEvent.exec(1000L * 10L);
                        }
                        mAirPlanModeHelper.setCommandAirplane(false);
                        this.mEventHistory.writeEventHistory("비행기모드 OFF");
                        this.mDelayEvent.exec(5000L);
                    }
                    CheckFileDescriptorCount("CheckDescriptorRunChromeMacro");
                    // home
                    this.mDevice.pressHome();

                    // 재실행 대기시간 처리
                    int  sleepTime = Integer.valueOf(mConfig.getItem("MacroRunDelayMin"));
                    if(sleepTime > 0)
                    {
                        this.mEventHistory.writeEventHistory("재실행 대기시간 설정(분) : " + String.valueOf(sleepTime));
                        WaitingForNextTask(sleepTime, true);
                    }

                    if(CheckStop() == 0) {
                        event_result.setEndTime(GetCurrentTime("yyyy-MM-dd HH:mm:ss"));
                        break;
                    }
                }
                catch(Exception ex){
                    Log.e("jkseo", ex.getMessage());
                }

                // 홈 버튼 누르기
                mDevice.pressHome();
            }

            // 환경설정에 Job 초기화
//                    mConfig.setItem("Jobs", "");
            if(CheckStop() == 0)
            {
                event_result.setEndTime(GetCurrentTime("yyyy-MM-dd HH:mm:ss"));
                event_result.setRemarks("매크로 작업 강제 종료");
                SaveMacroHisreal(event_result);

                this.mEventHistory.writeEventHistory("매크로 작업 강제 종료");
            }
            else if(CheckStop() == 1){

                this.mDevice.pressHome();
            }
        }
    }
    private void RunChromeMacro() throws Exception
    {
        CheckFileDescriptorCount("before RunChromeMacro");

        if(LoadMacroData())
        {
            int JobsCount = Jobs.size();
            int ReworkCount = 0;
            int ComID = Integer.valueOf(mConfig.getItem("ComId"));

            MacroEvent event = null;
            MacroResult event_result = null;
            for (int i = 0; i<Jobs.size(); i++)
            {
                event = Jobs.get(i);
                event_result = new MacroResult(ComID,  event.getDeviceID(), event.getMacroIdx(), event.getSiteName());

                // 외부 공인 ip를 가져온다. 비동기라 브라우저를 실행하고 값을 집어넣도록 처리한다.
                nHelper.CallGetPublicIp();

                event_result.setStartTime(GetCurrentTime("yyyy-MM-dd HH:mm:ss"));

                mConfig.setItem("Model", event.getModel());
                mConfig.setItem("MacroRunDelayMin", String.valueOf(event.getMacroRunDelayMin()));
                mConfig.setItem("MacroSetDelayMin", String.valueOf(event.getMacroSetDelayMin()));

                this.mEventHistory.writeEventHistory("매크로 시작");
                // 크롬 브라우저 실행
                cMacro.OpenApp("com.android.chrome");

                this.mDelayEvent.exec(5000L);

                cMacro.tabbarSize();

                event_result.setIPAddress(mConfig.getItem("result_ipaddress"));

                // 동작도중 오류가 발생하더라도 후작업은 순차적으로 처리되도록 한다.
                try{
                    if(CheckStop() == 0) {
                        event_result.setEndTime(GetCurrentTime("yyyy-MM-dd HH:mm:ss"));
                        break;
                    }

                    this.mEventHistory.writeEventHistory("검색대상 사이트 : " + event.getSiteName());

                    // 스크롤 주소창을 보기위해 스크롤 상단 이동
                    cMacro.drag_ScrollToTopOrBottom(true);

                    Boolean isclickSearchBar = false;
                    isclickSearchBar = cMacro.ClickURLBar();

                    if(CheckStop() == 0)
                    {   event_result.setEndTime(GetCurrentTime("yyyy-MM-dd HH:mm:ss"));
                        break;
                    }

                    // 검새입력방식 분기 (URL, 검색입력란)
//                    M0100001	키워드
//                    M0100002	URL
                    Boolean isFindSearchBar = false;
                    if(event.getMacroMode().equalsIgnoreCase("M0100001")){
                        // 키워드 방식 검색
                        this.mDelayEvent.exec(1000L);
                        // 한영 키보드문제, 덤프관련(한번씩 안떠짐) 문제가 발생하여, url에서 검색어를 입력하면 검색되는 방식으로 변경한다.
                        // [27,825][171,936]
                        this.mEventHistory.writeEventHistory("검색모드 : 키워드 방식");
                        isFindSearchBar = cMacro.InputSearchKeyword(event.getKeyword(), isclickSearchBar);
                    }
                    else if(event.getMacroMode().equalsIgnoreCase("M0100002")){
                        // URL 방식 검색
                        this.mDelayEvent.exec(1000L);
                        this.mEventHistory.writeEventHistory("검색모드 : URL 방식");
                        isFindSearchBar = cMacro.InputSearchKeyword(event.getKeyword(), isclickSearchBar);
                    }

                    // 검색창 또는 주소창을 찾지못하면 다음키워드로 넘긴다.
                    this.mEventHistory.writeEventHistory("검색어 입력 결과 : " + String.valueOf(isFindSearchBar));
//                    CheckFileDescriptorCount("CheckDescriptorRunChromeMacro");

                    if(!isFindSearchBar)
                    {
                        this.mEventHistory.writeEventHistory("갬색어 입력실패하여 다음 순번 매크로 이동");
                        event_result.setEndTime(GetCurrentTime("yyyy-MM-dd HH:mm:ss"));
                        event_result.setRemarks("갬색어 입력실패하여 다음 순번 매크로 이동");
                        event_result.setSearchResult(false);

                        SaveMacroHisreal(event_result);
                        continue;
                    }

                    if(CheckStop() == 0) {
                        event_result.setEndTime(GetCurrentTime("yyyy-MM-dd HH:mm:ss"));
                        break;
                    }

                    boolean searchResult = false;

                    if(event.getMacroMode().equalsIgnoreCase("M0100002")) {
                        searchResult = true;
                    }
                    else{
                        searchResult =  cMacro.searchFindKey(event.getSiteUrl(), event.getSearchType(), event.getScrollForwardLimitCnt());
                    }

//                    CheckFileDescriptorCount("CheckDescriptorRunChromeMacro");

                    if(CheckStop() == 0) {
                        event_result.setEndTime(GetCurrentTime("yyyy-MM-dd HH:mm:ss"));
                        break;
                    }

                    if(searchResult) {
                        this.mEventHistory.writeEventHistory("사이트 찾기 성공");
                        event_result.setSearchResult(true);

                        this.mDelayEvent.exec(2000L);

                        if(CheckStop() == 0) {
                            event_result.setEndTime(GetCurrentTime("yyyy-MM-dd HH:mm:ss"));
                            break;
                        }

                        String domain = event.getSiteUrl();

                        // 주소변환 : 프로토콜 제거
                        if(domain.toLowerCase().contains("http://") || domain.toLowerCase().contains("https://"))
                        {
                            URI uri = new URI(domain);
                            domain = uri.getHost();
                        }

                        this.mEventHistory.writeEventHistory("사이트 도메인 : " + domain);

                        // 체류시간(랜덤) 계산
                        // 사용자입력시간(분) -> 초단위 변환
                        // 최소 사용자 입력시간 / 3 부터 최대 사용자 입력시간
                        long stayMax = (event.getSiteWaitTime() *60) * 1000;
                        long stayMin = stayMax/3;
                        long randomStaySec = cMacro.randomRange(stayMin, stayMax);
                        this.mEventHistory.writeEventHistory("체류시간 설정 : " + String.valueOf(event.getSiteWaitTime()) + "분");
                        this.mEventHistory.writeEventHistory("체류시간 계산 : " + String.valueOf(randomStaySec/1000/60) + "분 또는 " + String.valueOf(randomStaySec/1000) + "초");
                        event_result.setSiteWaitTime(randomStaySec);

                        Queue<RandomEvent> rndEvents = null;

                        if(randomStaySec > 0) {
                            boolean scrollResult = false;
                            boolean scrollTopNDown = false;
                            boolean scrollTopNDownResult = true;

                            int scrollCount = 0;
                            int scrollStep  = 0;
                            int actionCount = 1;
                            Random random = new Random();

                            isStay = true;
                            Timer cntTimer;
                            TimerTask cntTimerTask;
                            cntTimer = new Timer();
                            cntTimerTask = new TimerTask() {
                                @Override
                                public void run() {
                                    /* 빈공백으로 두니 자꾸 죽는경우가 발생하여 일부러 Log를 찍어둔다. */
                                    Log.e("jkseo", "체류시간 종료 1");

                                    if(isStay){
//                                        Log.e("jkseo", "체류시간 종료2");
                                        isStay=false;
                                    }
                                }
                            };

                            // 간혹 스크롤객체를 못얻어와서 스크롤이동이 안되는경우가 발생하여 딜레이 1초 준다.
                            this.mDelayEvent.exec(1000);

                            cntTimer.schedule(cntTimerTask, randomStaySec);
                            while(isStay){
                                if(CheckStop() == 0) {
                                    event_result.setEndTime(GetCurrentTime("yyyy-MM-dd HH:mm:ss"));
                                    break;
                                }

                                if(actionCount == 1)
                                {
                                    // 여러번 반복하므로, Log는 한번만 남기자
                                    this.mEventHistory.writeEventHistory("체류중");
                                }

                                if(event.getRndClickData() == null || event.getRndClickData().length() == 0)
                                {
                                    if(actionCount == 1)
                                    {
                                        // 여러번 반복하므로, Log는 한번만 남기자
                                        this.mEventHistory.writeEventHistory("동작이벤트 미등록 기본 랜덤로직으로 처리됩니다.");
                                    }

                                    // 동작이벤트가 입력되지 않은경우
                                    // 페이지를 맨아래까지 이동하고, 다시 최상단으로 이동한 후 랜덤클릭 액션 처리한다.
                                    if(!scrollResult)
                                    {
                                        scrollStep = (int)cMacro.randomRange(30L, 110L);

                                        // 랜덤 스크롤 이벤트만 처리한다.
//                                    if(!this.cMacro.custom_ScrollToTopOrBottom(false, scrollStep))
//                                        scrollResult = true;
//
//                                    if(scrollResult)
//                                        this.cMacro.flingToBeginning();

                                        if(!scrollTopNDownResult)
                                        {
                                            // 랜덤 스크롤 시작하자
                                            scrollCount = scrollCount + 1;
                                        }

                                        if(scrollCount < 1)
                                        {
                                            scrollTopNDownResult = this.cMacro.custom_ScrollToTopOrBottom(scrollTopNDown, scrollStep);
                                        }
                                        else
                                        {
                                            // 스크롤 방향 정하기
                                            if(((int)(Math.random() * 10)) >= 4)
                                                scrollTopNDown = true;
                                            else
                                                scrollTopNDown = false;

                                            this.cMacro.custom_ScrollToTopOrBottom(scrollTopNDown, scrollStep);
                                        }

                                        // 화면꺼짐 방지 클릭
                                        this.mDevice.click(20,20);
                                        this.mDelayEvent.exec((int)cMacro.randomRange(1000L * 10L, 1000L * 30L));
                                        actionCount = actionCount + 1;
                                    }
                                    else{
                                        // 체류시간이 남아있는 경우 계속해서 랜덤클릭 처리한다.
                                        this.mEventHistory.writeEventHistory("동작이벤트 : " + String.valueOf(actionCount) + "회차 실행");

                                        if(this.cMacro.randomClick(domain)){
                                            // return 이 돌아오면 페이지가 이동되었다는 뜻이므로
                                            // 스크롤 이벤트를 넣어준다.
                                            scrollResult = false;
                                        }

                                        actionCount = actionCount + 1;
                                    }

                                } // if(event.getRndClickData() == null)
                                else{
                                    // 동작이벤트 입력된 경우

                                    if(rndEvents == null){
                                        this.mEventHistory.writeEventHistory("동작이벤트 파싱");
                                        rndEvents = ParseEvent(event.getRndClickData());

                                        if(rndEvents==null)
                                        {
                                            // 동작이벤트 구문 파싱이 실패한 경우
                                            this.mEventHistory.writeEventHistory("동작이벤트 파싱이 실패하여 기본랜덤로직으로 처리됩니다.");
                                            event.setRndClickData(null);

                                            continue;
                                        }
                                    } // end if rndEvents == null
                                    else {
                                        // 동작이벤트 처리
                                        if (rndEvents.size() == 0)
                                        {
                                            event.setRndClickData(null);
                                            continue;
                                        }
                                        RandomEvent rndEvent = rndEvents.peek();

                                        this.mEventHistory.writeEventHistory("동작이벤트 : " + String.valueOf(actionCount) + "회차 " + rndEvent.getCommand() + " 실행");
                                        Boolean rndEventResult = ExecRandomEvent(rndEvent);

                                        if(rndEvent.getCommand().equalsIgnoreCase("rndScroll"))
                                        {
                                            // 랜덤스크롤 이벤트일 경우 체류시간 까지 해당이벤트만 반복처리한다.
                                            this.mEventHistory.writeEventHistory("랜덤스크롤 이벤트 반복처리");
                                        }
                                        else if(rndEvent.getCommand().equalsIgnoreCase("random"))
                                        {
                                            // 랜덤클릭 이벤트일 경우 체류시간 까지 해당이벤트만 반복처리한다.
                                            this.mEventHistory.writeEventHistory("랜덤클릭 이벤트 반복처리");
                                            rndEvents.poll();
                                            event.setRndClickData(null);
                                        }
                                        else {
                                            rndEvents.poll();
                                        }

                                        actionCount = actionCount + 1;
                                    }
                                }
                            } // end while

                            cntTimerTask.cancel();
                            cntTimer.cancel();
                        }
                        this.mEventHistory.writeEventHistory("체류시간 종료 2");

                        // 화면꺼짐 방지 클릭
                        this.mDevice.click(20,20);

                        event_result.setEndTime(GetCurrentTime("yyyy-MM-dd HH:mm:ss"));
                        SaveMacroHisreal(event_result);
                    }
                    else {
                        this.mEventHistory.writeEventHistory("사이트 찾기 실패");
                        event_result.setSearchResult(false);
                        event_result.setEndTime(GetCurrentTime("yyyy-MM-dd HH:mm:ss"));
                        event_result.setRemarks("입력된 조건으로 사이트를 찾을 수 없습니다. 다음 순번 매크로 이동");

                        SaveMacroHisreal(event_result);

                        // 검색실패하더라도 종료루틴은 처리한다.
//                        continue;
                    }

                    this.mDelayEvent.exec(2000L);
                    // 중간중간 스탭확인변수
                    boolean isOKStep = false;

                    // 검색기록 삭제
                    boolean isSelectedMenu = cMacro.clickMenuBtn();

                    // 그래도 메뉴가 클릭이 되지 않는경우 검색기록 로직을 제외한다.
                    if(isSelectedMenu)
                    {
                        isOKStep = cMacro.clickAccessRecord();

                        if(isOKStep) isOKStep = cMacro.clickAccessRecordDeleteBtn();
                        if(isOKStep) isOKStep = cMacro.clickAccessRecordDeleteOption1();
                        if(isOKStep) isOKStep = cMacro.clickAccessRecordDeleteOption2();
                        if(isOKStep) isOKStep = cMacro.clickAccessRecordDelete();
                        if(isOKStep) isOKStep = cMacro.clickAccessRecordDelete1();

//                        cMacro.clickAccessRecord();
//                        cMacro.clickAccessRecordDeleteBtn();
//                        cMacro.clickAccessRecordDeleteOption1();
//                        cMacro.clickAccessRecordDeleteOption2();
//                        cMacro.clickAccessRecordDelete();
//                        cMacro.clickAccessRecordDelete1();
                        this.mDelayEvent.exec(1000L);

                        // 검색기록 종료
                        mDevice.pressBack();
                        this.mDelayEvent.exec(1000L);
                    }

                    if(CheckStop() == 0) {
                        event_result.setEndTime(GetCurrentTime("yyyy-MM-dd HH:mm:ss"));
                        break;
                    }

                    // 페이징 종료
                    boolean isPaging = this.cMacro.clickTabSwitchBtn();
                    mEventHistory.writeEventHistory("페이징 버튼 클릭 결과 : " + String.valueOf(isPaging));

                    if(isPaging){
                        this.mDelayEvent.exec(3000L);
                        if(this.cMacro.clickMenuBtn()){
                            this.mDelayEvent.exec(1000L);
                            this.cMacro.clickAllClose();
                            this.mDelayEvent.exec(1000L);
                        }

                    }

                    // home
                    this.mDevice.pressHome();

                    if(CheckStop() == 0) {
                        event_result.setEndTime(GetCurrentTime("yyyy-MM-dd HH:mm:ss"));
                        break;
                    }

                    // 비행모드 10sec
                    // 그외는 공통처리
                    if (mConfig.getItem("Model").equals("M0001")){
                        // 갤럭시는 비행모드 설정이 달라서 별도로 처리
                        EventHistory eventHistory10 = this.mEventHistory;
                        this.mEventHistory.writeEventHistory("현재비행기모드상태 체크 : " + mAirPlanModeHelper.getAirplaneMode());
                        if (mAirPlanModeHelper.getAirplaneMode() == 0) {
//                            mAirPlanModeHelper.settingAirplane(true);
                            mAirPlanModeHelper.setCommandAirplane(true);
                            this.mEventHistory.writeEventHistory("비행기모드 ON");
                            this.mDevice.pressHome();
                            this.mEventHistory.writeEventHistory("비행기모드 화면 대기 10초");
                            this.mDelayEvent.exec(1000L * 10L);
                        }
//                        mAirPlanModeHelper.settingAirplane(false);
                        mAirPlanModeHelper.setCommandAirplane(false);

                        this.mEventHistory.writeEventHistory("비행기모드 OFF");
                        this.mDelayEvent.exec(5000L);

                        // todo 네트워크 등록에 실패하였습니다. 알림창 닫기
                        cMacro.CloseAlarm_NetworkError("확인");
                    }
                    else{
                        // 샤오미
                        EventHistory eventHistory10 = this.mEventHistory;
                        this.mEventHistory.writeEventHistory("현재비행기모드상태 체크 : " + mAirPlanModeHelper.getAirplaneMode());
                        if (mAirPlanModeHelper.getAirplaneMode() == 0) {
                            mAirPlanModeHelper.setCommandAirplane(true);
                            this.mEventHistory.writeEventHistory("비행기모드 ON");
                            this.mDevice.pressHome();
                            this.mEventHistory.writeEventHistory("비행기모드 화면 대기 10초");
                            this.mDelayEvent.exec(1000L * 10L);
                        }
                        mAirPlanModeHelper.setCommandAirplane(false);
                        this.mEventHistory.writeEventHistory("비행기모드 OFF");
                        this.mDelayEvent.exec(5000L);
                    }
//                    CheckFileDescriptorCount("CheckDescriptorRunChromeMacro");
                    // home
                    this.mDevice.pressHome();

                    if(CheckStop() == 0) {
                        event_result.setEndTime(GetCurrentTime("yyyy-MM-dd HH:mm:ss"));
                        break;
                    }

                    // 재실행 대기시간 처리
                    int  sleepTime = Integer.valueOf(mConfig.getItem("MacroRunDelayMin"));
                    if(sleepTime > 0)
                    {
                        this.mEventHistory.writeEventHistory("재실행 대기시간 설정(분) : " + String.valueOf(sleepTime));
                        WaitingForNextTask(sleepTime, true);
                    }

                    if(CheckStop() == 0) {
                        event_result.setEndTime(GetCurrentTime("yyyy-MM-dd HH:mm:ss"));
                        break;
                    }
                }
                catch(Exception ex){
                    Log.e("jkseo", ex.getMessage());
                    this.mEventHistory.writeEventHistory("error" + ex.getMessage());
                }
                finally {
                    // 홈 버튼 누르기
                    mDevice.pressHome();

                    // 최근앱 종료
                    CloseApp();
                }
            }

            if(CheckStop() == 0)
            {
                event_result.setEndTime(GetCurrentTime("yyyy-MM-dd HH:mm:ss"));
                event_result.setRemarks("매크로 작업 강제 종료");
                SaveMacroHisreal(event_result);

                this.mEventHistory.writeEventHistory("매크로 작업 강제 종료");
            }
            else if(CheckStop() == 1){
                this.mDevice.pressHome();
            }
        }
    }
    private Boolean ExecRandomEvent(RandomEvent rndEvent) throws Exception{
        boolean result = false;
        boolean isScrollAway = false;
        String rndEventCommand = rndEvent.getCommand();

        try{
            switch (rndEvent.getCommand())
            {
                case "addr":{
                    this.mEventHistory.writeEventHistory("좌표 클릭 X : " + rndEvent.getParam1() + ", Y : " + rndEvent.getParam2());
                    result = this.cMacro.ClickAddress(Integer.valueOf(rndEvent.getParam1()), Integer.valueOf(rndEvent.getParam2()));

                    if(result){
                        // 클릭 후 사이트가 로딩되도록 대기한다.
                        this.mDelayEvent.exec(3000L);

                        // 사이트가 변경되었는지 확인한다?? 일단 미정
                    }
                } break;
                case "field":{} break;
                case "caption":{} break;
                case "back":{
                    result = this.cMacro.ClickBackButton();
                } break;
                case "random":{
                    result = true;
                } break;
                case "bScroll":{
                    result = this.cMacro.custom_ScrollToTopOrBottom(false, (int)this.cMacro.randomRange(10L, 70L));

                    if(result && Integer.valueOf(rndEvent.getParam1()) == 0)
                    {
                        // 맨 하단까지 스크롤이 가도록 재귀호출한다.
                        ExecRandomEvent(rndEvent);
                    }
                } break;
                case "tScroll":{
                    result = this.cMacro.custom_ScrollToTopOrBottom(true, (int)this.cMacro.randomRange(10L, 70L));

                    if(result && Integer.valueOf(rndEvent.getParam1()) == 0)
                    {
                        // 맨 상단까지 스크롤이 가도록 재귀호출한다.
                        ExecRandomEvent(rndEvent);
                    }
                } break;
                case "rndScroll":{
                    if((int)this.cMacro.randomRange(0L, 1L) == 0)
                        isScrollAway = true;
                    else
                        isScrollAway = false;

                    result = this.cMacro.custom_ScrollToTopOrBottom(isScrollAway, (int)this.cMacro.randomRange(10L, 70L));
                } break;
            }
            this.mEventHistory.writeEventHistory(rndEventCommand + " 처리 결과 : " + String.valueOf(result));
        }
        catch (Exception ex){
            this.mEventHistory.writeEventHistory(rndEventCommand+ " 처리중 에러발생 : " + ex.getMessage());
        }
        return result;
    }

    private Queue<RandomEvent> ParseEvent(String rndClickData) throws Exception{
        Queue<RandomEvent> events = new LinkedList<RandomEvent>();

        // ex) addr=67,294;bscroll=1;field=btn_hamb_wt;caption=한식;rndScroll=0
        String[] parts = rndClickData.split(RandomEvent.EVENT_TOKEN);

        for (int i=0;i<parts.length;i++){
            String[] keyValue = parts[i].split(RandomEvent.NAVE_VALUE_TOKEN);

            if(keyValue.length == 1)
            {
                // back,random 명령어 같은 단일 구문
                if(keyValue[0].equalsIgnoreCase("back"))
                {
                    RandomEvent event = new RandomEvent();
                    event.setCommand(keyValue[0]);
                    events.add(event);
                }
                else if(keyValue[0].equalsIgnoreCase("random"))
                {
                    RandomEvent event = new RandomEvent();
                    event.setCommand(keyValue[0]);
                    events.add(event);

                    // 동작이벤트가 등록되면 이후에 등록된 이벤트는 처리하지 않음을 의미하므로 이벤트 분석은 종료처리한다.
                    break;
                }
                else{
                    this.mEventHistory.writeEventHistory("존재하지 않는 이벤트 명령어입니다. : " + keyValue[0]);
                    continue;
                }
            }else{
                if(keyValue[0].equalsIgnoreCase("addr"))
                {
                    // addr 은 매개변수가 2개 입력된다. x,y
                    String[] valuePrameter = keyValue[1].split(RandomEvent.VALUE_TOKEN);

                    if(valuePrameter.length == 1)
                    {
                        this.mEventHistory.writeEventHistory("addr 이벤트 명령어 파라미터가 잘못입력되었습니다. : " + keyValue[1]);
                        continue;
                    }

                    RandomEvent event = new RandomEvent();
                    event.setCommand(keyValue[0]);
                    event.setParam1(valuePrameter[0]);
                    event.setParam2(valuePrameter[1]);
                    events.add(event);
                }
                else{
                    if(RandomEvent.ContainsCommand(keyValue[0])){
                        RandomEvent event = new RandomEvent();
                        event.setCommand(keyValue[0]);
                        event.setParam1(keyValue[1]);
                    }
                    else{
                        this.mEventHistory.writeEventHistory("존재하지 않는 이벤트 명령어입니다. : " + keyValue[0]);
                        continue;
                    }
                }
            }
        }

        return events;
    }

    private void RecentAppActivity() throws Exception{
        // todo 최근앱 클릭 또는 활성화 브로드 캐스트 수행
        this.mDevice.pressRecentApps();
        List<UiObject2> appClosebuttons = this.mDevice.findObjects(By.res("com.android.systemui:id/dismiss_task"));

        int closeCount = appClosebuttons.size();

        if (closeCount>0){
            // [107,454][209,567]
            // 최근앱클릭한다.
//                this.mDevice.click(107, 454);
            this.mDevice.click(this.mDevice.getDisplayWidth()/2, this.mDevice.getDisplayHeight()/2);
//                this.OpenApp("com.example.macrotester");
//                UiObject appBackground = this.mDevice.findObject(new UiSelector().description("Auto Macro"));
//                appBackground.click();
        }
    }

    // 환경
    private boolean LoadMacroData() throws Exception {
        boolean result = false;

        this.mEventHistory.writeEventHistory("환경설정에서 매크로 정보 불러오기 ");
        // 환경설정에서 매크로 설정정보 불러오기
        String content = mConfig.getItem("Jobs");

        if(content == null || content.length()==0) {
            this.mEventHistory.writeEventHistory("환경설정에서 매크로 정보 불러오기 결과 : 0 ");
            return false;
        }

        TypeToken<ArrayList<MacroEvent>> MacroEventType = new TypeToken<ArrayList<MacroEvent>>(){};
        Jobs = gson.fromJson(content, MacroEventType);

        this.mEventHistory.writeEventHistory("환경설정에서 매크로 정보 불러오기 결과 : " + String.valueOf(Jobs.size()));

        if(Jobs.size() == 0)
            result = false;
        else
            result = true;

        return result;
    }

    // 검색결과를 등록한다.
    private void SaveMacroHisreal(MacroResult event) throws Exception{

        mEventHistory.writeEventHistory("(SaveMacroHisreal) 결과를 등록한다.");
        try{
            // todo config에 실적을 등록한다.
            mConfig.setItem("result_comid", String.valueOf(event.getComID()));
            mConfig.setItem("result_deviceid", String.valueOf(event.getDeviceID()));
            mConfig.setItem("result_macroidx", String.valueOf(event.getMacroIdx()));
            mConfig.setItem("result_sitename", event.getSiteName());

            mConfig.setItem("result_macro_ok", event.getSearchResult());
//        mConfig.setItem("result_macromode", "");
            mConfig.setItem("result_startTime", event.getStartTime());
            mConfig.setItem("result_endTime", event.getEndTime());
            mConfig.setItem("result_siteWaiteTime", String.valueOf(event.getSiteWaitTime()));
            mConfig.setItem("result_remarks", event.getRemarks());
            mConfig.setItem("result_ipaddress", event.getIPAddress());

            shell.execSaveHisreal();
        }
        catch(Exception ex){
            mEventHistory.writeEventHistory("(SaveMacroHisreal) error :" + ex.getMessage());

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            mEventHistory.writeEventHistory("(SaveMacroHisreal) error :" + sw.toString());

            throw ex;
        }
    }
    // 매크로 종료여부  1 : 동작중 / 0 : 종료
    private int CheckStop(){
        int result = Integer.valueOf(mConfig.getItem("IsRunning"));

        return result;
    }

    private ActivityScenario<TesterActivity> scenario;

    public void useAppContextNomal() throws Exception {
        // Context of the app under test.
        //        assertEquals("com.example.macrotester", this.mContext.getPackageName());
        try{
            String launcherPackage = this.mDevice.getLauncherPackageName();
            // am start -n com.example.macrotester/.LoaderActivity

            shell.execOpenApp();
            mDelayEvent.exec(3000L);
        }
        catch (Exception ex)
        {
            mEventHistory.writeEventHistory(ex.getMessage());
        }
    }
    public void useAppContext() throws Exception {
        // Context of the app under test.
        //        assertEquals("com.example.macrotester", this.mContext.getPackageName());
        try{
        String launcherPackage = this.mDevice.getLauncherPackageName();
            // am start -n com.example.macrotester/.LoaderActivity

            shell.execOpenApp();

            mDelayEvent.exec(1000L);

            // 버튼 클릭
            UiObject2 macroButton = mDevice.wait(Until.findObject(By.res("com.example.macrotester:id/btnRegSchedule")), 500);
            if (macroButton != null) {

                if(macroButton.getText().equalsIgnoreCase("매크로 중지"))
                {
                    mDevice.click(macroButton.getVisibleBounds().left+1, macroButton.getVisibleBounds().top+1);
//                    macroButton.click();
                    mDelayEvent.exec(10000L);
                    mDevice.click(macroButton.getVisibleBounds().left+1, macroButton.getVisibleBounds().top+1);
                    mDelayEvent.exec(7000L);

//                    macroButton.click();
                }
                else{
                    mDevice.click(macroButton.getVisibleBounds().left+1, macroButton.getVisibleBounds().top+1);
//                    macroButton.click();
                }
            }
            else {
                mEventHistory.writeEventHistory("매크로 실행버튼 획득 불가");

                // 무한반복 로직 삽입한다.
                mDevice.pressHome();

                useAppContext();
            }
        }
        catch (Exception ex)
        {
            mEventHistory.writeEventHistory(ex.getMessage());
        }
    }
    public void ControlAirPlanMode() throws Exception {
        mDevice.pressHome();

        // 갤럭시
        EventHistory eventHistory = this.mEventHistory;
        eventHistory.writeEventHistory(" 체크 : " + mAirPlanModeHelper.getAirplaneMode());
//        if (mAirPlanModeHelper.getAirplaneMode() == 0) {
//            mAirPlanModeHelper.settingAirplane(true);
//            this.mEventHistory.writeEventHistory("비행기모드 ON");
//            this.mDevice.pressHome();
//            this.mEventHistory.writeEventHistory("비행기모드 화면 대기 3초");
//            this.mDelayEvent.exec(3000L);
//        }
//        mAirPlanModeHelper.settingAirplane(false);
//        this.mEventHistory.writeEventHistory("비행기모드 OFF");
//        this.mDelayEvent.exec(10000L);

        // todo 네트워크 등록에 실패하였습니다. 알림창 닫기
        cMacro.CloseAlarm_NetworkError("확인");

        // 샤오미
        mAirPlanModeHelper.setCommandAirplane(true);
                this.mDelayEvent.exec(10000L);
        mAirPlanModeHelper.setCommandAirplane(false);
        this.mDevice.pressHome();
    }
    public void RunRandomScroll() throws Exception{
        // 홈 버튼 누르기
        mDevice.pressHome();
        cMacro.OpenApp("com.android.chrome");
        this.mDelayEvent.exec(5000L);

        Boolean isclickSearchBar = false;
//        isclickSearchBar = cMacro.ClickSearchBar();
        isclickSearchBar = cMacro.ClickSearchFieldOfDump( "Google 검색");

        // 키워드 입력
        cMacro.InputSearchKeywordForCommand("서종근 인물 검색");

        // 알림창 닫기
        cMacro.ClickChromeInfoBox();

        this.mDelayEvent.exec(1000L);
        cMacro.drag_ScrollToTopOrBottom(false);
        this.mDelayEvent.exec(2000L);
        cMacro.drag_RandomScroll();
        this.mDelayEvent.exec(2000L);
        cMacro.drag_RandomScroll();
        this.mDelayEvent.exec(2000L);
        cMacro.drag_RandomScroll();
        this.mDelayEvent.exec(2000L);
        cMacro.drag_RandomScroll();
        this.mDelayEvent.exec(2000L);

        Log.e("jkseo", "랜덤 스크롤 종료");
        mDevice.pressHome();
    }

    public void ChangeSearchEngine() throws Exception{
        // 홈 버튼 누르기
        mDevice.pressHome();

        cMacro.OpenApp("com.android.chrome");
        cMacro.tabbarSize();

        // 메뉴
        cMacro.clickMenuBtn();
        this.mDelayEvent.exec(1000L);

        // 메뉴 스크롤을 해야한다.
        cMacro.drag_ScrollToTopOrBottom(false);
        cMacro.clickSettingMenuItem();
        this.mDelayEvent.exec(1000L);

        // 검색엔진 클릭 안됨 확인필요
        cMacro.clickSearchEngine();
        this.mDelayEvent.exec(1000L);
        cMacro.clickSelectSearchEngine("naver.com");
        this.mDelayEvent.exec(1000L);

        mDevice.pressBack();
        mDevice.pressBack();

        // 메뉴
        cMacro.clickMenuBtn();
        this.mDelayEvent.exec(1000L);
        //설정
        cMacro.clickSettingMenuItem();
        this.mDelayEvent.exec(1000L);
        cMacro.clickSearchEngine();
        this.mDelayEvent.exec(1000L);
        cMacro.clickSelectSearchEngine("google.com");
        this.mDelayEvent.exec(1000L);

        mDevice.pressBack();
        mDevice.pressBack();
    }
    public void DeleteSearchHistory() throws Exception {
        Log.e("jkseo", "call DeleteSearchHistory ");

        if(!action_name.equalsIgnoreCase("DeleteSearchHistory")) return;

        // 홈 버튼 누르기
        mDevice.pressHome();

        cMacro.OpenApp("com.android.chrome");

        // 검색기록 삭제
        cMacro.clickMenuBtn();
        cMacro.clickAccessRecord();
        cMacro.clickAccessRecordDeleteBtn();
        cMacro.clickAccessRecordDeleteOption1();
        cMacro.clickAccessRecordDeleteOption2();
        cMacro.clickAccessRecordDelete();
        cMacro.clickAccessRecordDelete1();
        this.mDelayEvent.exec(1000L);

        // 검색기록 종료
        mDevice.pressBack();
        this.mDelayEvent.exec(1000L);

        // 종료
        // ? 크롬앱이 종료안됨 별도 테스트 필요
        // 크롬등 앱이 안닫힘.
//        cMacro.CloseApps();


        Log.e("jkseo", "검색기록 삭제 종료");
        // 종료
        this.mDevice.pressHome();

    }

    @After
    public void CloseAppTest() throws Exception {
        Log.e("jkseo", "End UseTest");

        if(mConfig == null)
        {
            mContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            mEventHistory = new EventHistory(false);

            mConfig = new Configuration(this.mContext,this.mEventHistory);
        }

        // 매크로 동작상태 변경 : 작동 -> 중지
        // 트레이아이콘 해제
        mConfig.setItem("IsRunning", "0");
        shell.execNoti(false);

        // 스케쥴링중이라면 다시 시작한다.
        // 3회 이상부터 랜덤하게 프로세스가 죽어버리는 현상이 발생한다. 브로드캐스트 말고, 직접 api 호출해서 가져오는걸로 변환진행
//        boolean scheduler = mConfig.GetBooleanItem("ScheduleFlag");
//        this.shell.execReCycle(scheduler);
    }
}