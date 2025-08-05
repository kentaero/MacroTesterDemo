package UtilGroup;

import android.os.Environment;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import org.json.JSONArray;

/* loaded from: classes.dex */
public class ShellExecuter {
    public EventHistory mEventHistory;

    public  ShellExecuter(EventHistory eventHistory){
        this.mEventHistory = eventHistory;
    }

    public void execReCycle(boolean isCycle) {
        Log.e("jkseo", "스케쥴링 여부 : " + isCycle);
        try {
            Executer executer = new Executer();
            if (isCycle) {
                String[] noti = {"su", "-c", "am", "broadcast", "-a", "MACRO_CYCLE_ALARM", "com.example.macrotester"};
                executer.exec(noti);
                try {
                    Thread.sleep(1000L);
                } catch (Exception e) {
                }
            }
        } catch (Exception e3) {
            try{
                this.mEventHistory.writeEventHistory("ADB 앱호출 테스트 에러 : " + e3.toString());
            }
            catch (Exception we){
                Log.e("jkseo", "ADB 앱호출 테스트 에러 : " + e3.toString());
            }
        }
    }
    public void execOpenApp(){
        Log.e("jkseo", "ShellExecuter execOpenApp");
        try {
            Executer executer = new Executer();
            String[] noti = {"su", "-c", "am", "start", "-n", "com.example.macrotester/.LoaderActivity"};
            executer.exec(noti);
            try {
                Thread.sleep(1000L);
            } catch (Exception e) {
            }
        } catch (Exception e3) {
            try{
                this.mEventHistory.writeEventHistory("ADB 앱호출 테스트 에러 : " + e3.toString());
            }
            catch (Exception we){
                Log.e("jkseo", "ADB 앱호출 테스트 에러 : " + e3.toString());
            }
        }
    }
    public void execSaveHisreal(){
        Log.e("jkseo", "ShellExecuter execSaveHisreal");
        try {
            Executer executer = new Executer();
            String[] noti = {"su", "-c", "am", "broadcast", "-a", "SAVE_MACRO_RESULT", "com.example.macrotester"};
            executer.exec(noti);
            try {
                Thread.sleep(1000L);
            } catch (Exception e) {
            }
        } catch (Exception e3) {
            try{
                this.mEventHistory.writeEventHistory("ADB 앱호출 테스트 에러 : " + e3.toString());
            }
            catch (Exception we){
                Log.e("jkseo", "ADB 앱호출 테스트 에러 : " + e3.toString());
            }
        }
    }

    public void execSaveHisrealDebug(){
        Log.e("jkseo", "ShellExecuter execSaveHisrealDebug");
        try {
            Executer executer = new Executer();
            String[] noti = {"su", "-c", "am", "broadcast", "-a", "SAVE_MACRO_RESULT_DEBUG", "com.example.macrotester"};
            executer.exec(noti);
            try {
                Thread.sleep(1000L);
            } catch (Exception e) {
            }
        } catch (Exception e3) {
            try{
                this.mEventHistory.writeEventHistory("ADB 앱호출 테스트 에러 : " + e3.toString());
            }
            catch (Exception we){
                Log.e("jkseo", "ADB 앱호출 테스트 에러 : " + e3.toString());
            }
        }
    }
    public void execNoti(boolean isShow) {
        Log.e("jkseo", "ShellExecuter execNoti isShow : " + isShow);
        try {
            Executer executer = new Executer();
            if (isShow) {
                String[] noti = {"su", "-c", "am", "broadcast", "-a", "START_MACRO_EVENT", "com.example.macrotester"};
                executer.exec(noti);
                try {
                    Thread.sleep(1000L);
                } catch (Exception e) {
                }
            } else {
                String[] noti2 = {"su", "-c", "am", "broadcast", "-a", "END_MACRO_EVENT", "com.example.macrotester"};
                executer.exec(noti2);
                try {
                    Thread.sleep(1000L);
                } catch (Exception e2) {
                }
            }
        } catch (Exception e3) {
            try{
                this.mEventHistory.writeEventHistory("ADB 앱호출 테스트 에러 : " + e3.toString());
            }
            catch (Exception we){
                Log.e("jkseo", "ADB 앱호출 테스트 에러 : " + e3.toString());
            }
        }
    }


    public void execDump()
    {
        Log.e("jkseo", "ShellExecuter execCommand mode : ");

        File basePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MacroApp/ScreenDump");
        if (!basePath.exists()) {
            basePath.mkdirs();
        }

        String filePath = basePath + "/" + "dump.xml";

        String[] keyevent1 = {"su",  "uiautomator", "dump", filePath};

        Executer executer = new Executer();
        executer.exec(keyevent1);
    }
    public void execFingerAirplaneMode(boolean isMode) {
        String mode = isMode ? "1" : "0";
        String modeBoolean = isMode ? "true":"false";

        try {
            Log.e("jkseo", "ShellExecuter execArplaneMode mode : " + mode);
            String[] settingShow = {"su", "-c", "am", "start", "-a", "android.settings.AIRPLANE_MODE_SETTINGS"};
//            String[] settingShow = {"su", "settings", "put", "global", "airplane_mode_on", mode, "am", "broadcast", "-a", "android.intent.action.AIRPLANE_MODE", "--ez", "state", modeBoolean};
            String[] keyevent1 = {"su", "-c", "input", "keyevent", "22"};
            String[] keyevent2 = {"su", "-c", "input", "keyevent", "22"};
            String[] keyevent3 = {"su", "-c", "input", "keyevent", "23"};
            Executer executer = new Executer();
            executer.exec(settingShow);
            try {
                Thread.sleep(3000L);
            } catch (Exception e) {
                Log.e("jkseo", "ADB 앱호출 테스트 에러 : " + e.toString());
            }
            executer.exec(keyevent1);
            try {
                Thread.sleep(3000L);
            } catch (Exception e2) {
                Log.e("jkseo", "ADB 앱호출 테스트 에러 : " + e2.toString());
            }
            executer.exec(keyevent2);
            try {
                Thread.sleep(3000L);
            } catch (Exception e3) {
                Log.e("jkseo", "ADB 앱호출 테스트 에러 : " + e3.toString());
            }
            executer.exec(keyevent3);
            try {
                Thread.sleep(3000L);
            } catch (Exception e4) {
                Log.e("jkseo", "ADB 앱호출 테스트 에러 : " + e4.toString());
            }
        } catch (Exception e5) {
            Log.e("jkseo", "ADB 앱호출 테스트 에러 : " + e5.toString());
        }
    }
    // 메모리 해제를 위해 사용하지 않음.
    @Deprecated
    public void execCommandAirplanMode(boolean isMode) {
        String mode = isMode ? "1" : "0";
        String modeBoolean = isMode ? "true":"false";

        try {
            Log.e("jkseo", "ShellExecuter execArplaneMode mode : " + mode);

//            su settings put global airplane_mode_on 1
//            am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true
//
//            su settings put global airplane_mode_on 0
//            am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true

            if(isMode){
                String suCommand = "su";
                Process suProcess = Runtime.getRuntime().exec(suCommand);

                // 2. 비행기 모드를 활성화하기 위한 명령어를 실행합니다.
                String enableAirplaneModeCommand = "settings put global airplane_mode_on 1 && am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true";
                suProcess.getOutputStream().write((enableAirplaneModeCommand + "\n").getBytes());
                suProcess.getOutputStream().flush();
            }
            else{
                String suCommand = "su";
                Process suProcess = Runtime.getRuntime().exec(suCommand);

                // 2. 비행기 모드를 활성화하기 위한 명령어를 실행합니다.
                String enableAirplaneModeCommand = "settings put global airplane_mode_on 0 && am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true";
                suProcess.getOutputStream().write((enableAirplaneModeCommand + "\n").getBytes());
                suProcess.getOutputStream().flush();
            }

            try {
                Thread.sleep(3000L);
            } catch (Exception e) {
                Log.e("jkseo", "ADB 앱호출 테스트 에러 : " + e.toString());
            }
        } catch (Exception e5) {
            try{
                this.mEventHistory.writeEventHistory("ADB 앱호출 테스트 에러 : " + e5.toString());
            }
            catch (Exception we){
                Log.e("jkseo", "ADB 앱호출 테스트 에러 : " + e5.toString());
            }
        }
    }

    public void execCheckFileDescriptorCommandAirplanMode(boolean isMode) {
        String mode = isMode ? "1" : "0";
        String modeBoolean = isMode ? "true":"false";

        String suCommand = "su";
        Process suProcess = null;
        try {
            Log.e("jkseo", "ShellExecuter execCheckFileDescriptorCommandAirplanMode : " + mode);

            if(isMode){
                suProcess = Runtime.getRuntime().exec(suCommand);

                // 2. 비행기 모드를 활성화하기 위한 명령어를 실행합니다.
                String enableAirplaneModeCommand = "settings put global airplane_mode_on 1 && am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true";
                suProcess.getOutputStream().write((enableAirplaneModeCommand + "\n").getBytes());
                suProcess.getOutputStream().flush();
            }
            else{
                suProcess = Runtime.getRuntime().exec(suCommand);

                // 2. 비행기 모드를 활성화하기 위한 명령어를 실행합니다.
                String enableAirplaneModeCommand = "settings put global airplane_mode_on 0 && am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true";
                suProcess.getOutputStream().write((enableAirplaneModeCommand + "\n").getBytes());
                suProcess.getOutputStream().flush();
            }

            try {
                Thread.sleep(3000L);
            } catch (Exception e) {
                Log.e("jkseo", "ADB 앱호출 테스트 에러 : " + e.toString());
            }
        } catch (Exception e5) {
            try{
                this.mEventHistory.writeEventHistory("ADB 앱호출 테스트 에러 : " + e5.toString());
            }
            catch (Exception we){
                Log.e("jkseo", "ADB 앱호출 테스트 에러 : " + e5.toString());
            }
        }
        finally {
            if(suProcess != null){
                suProcess.destroy();
            }
        }
    }


    public boolean isColseApp() {
        try {
            Executer executer = new Executer();
            String[] recentApp = {"su", "-c", "dumpsys", "window", "a|grep", "/", "|cut -d", "{", "-f2", "|cut -d", "/", "-f1"};
            JSONArray apps = executer.execRecentApps(recentApp);
            Log.e("jkseo", "isColseApp : " + apps.getString(apps.length() - 1));
            return !apps.getString(apps.length() - 1).equalsIgnoreCase("com.android.chrome");
        } catch (Exception e) {

            try{
                this.mEventHistory.writeEventHistory("ADB 앱호출 테스트 에러 : " + e.toString());
            }
            catch (Exception we){
                Log.e("jkseo", "ADB 앱호출 테스트 에러 : " + e.toString());
            }

            return false;
        }
    }

    public void execString(String... cmd){
        Executer executer = new Executer();
        executer.exec(cmd);
        try {
            Thread.sleep(1000L);
        } catch (Exception e) {
            try{
                this.mEventHistory.writeEventHistory("ADB 앱호출 테스트 에러 : " + e.toString());
            }
            catch (Exception we){
                Log.e("jkseo", "ADB 앱호출 테스트 에러 : " + e.toString());
            }
        }
    }

    public void Reboot() {
        Executer executer = new Executer();
        String[] cmd = new String[]{"su", "-c", "reboot"};
        try {
            // 재부팅이 안될때를 대비해서 3번 반복한다.
            executer.execWaitFor(cmd);
            Thread.sleep(3000L);
            executer.execWaitFor(cmd);
            Thread.sleep(3000L);
            executer.execWaitFor(cmd);
            Thread.sleep(3000L);

        } catch (Exception e) {
            try{
                this.mEventHistory.writeEventHistory("ADB 앱호출 테스트 에러 : " + e.toString());
            }
            catch (Exception we){
                Log.e("jkseo", "ADB 앱호출 테스트 에러 : " + e.toString());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class Executer {
        public Executer() {
        }

        public void execWaitFor(String... command){
            StringBuffer output = new StringBuffer();
            Process p = null;
            try {
                Runtime.getRuntime().exec(command).waitFor();
            } catch (Exception e5) {
                Log.e("jkseo", "ShellExecuter Executer error : " + e5.toString());
                e5.printStackTrace();
            }
        }
        public void exec(String... command) {
            StringBuffer output = new StringBuffer();
            Process p = null;
            try {
                p = Runtime.getRuntime().exec(command);
                try {
                    p.getErrorStream().close();
                } catch (Exception e) {
                    Log.e("jkseo", e.getMessage());
                }
                try {
                    p.getInputStream().close();
                } catch (Exception e2) {
                    Log.e("jkseo", e2.getMessage());
                }
                try {
                    p.getOutputStream().close();
                } catch (Exception e3) {
                    Log.e("jkseo", e3.getMessage());
                }

                // 아래구절은 의미가 없기에 주석처리한다. by 2024.02.12 jkseo
//                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
//                while (true) {
//                    try {
//                        String line = reader.readLine();
//                        if (line != null) {
//                            output.append(line + "\n");
//                        } else {
//                            // 리소스 해제
//                            if(p!=null)
//                            {
//                                p.destroy();
//                            }
//                            output = null;
//                            return;
//                        }
//                    } catch (Exception e4) {
//                        Log.e("jkseo", e4.getMessage());
//                        return;
//                    }
//                }
            } catch (Exception e5) {
                Log.e("jkseo", "ShellExecuter Executer error : " + e5.toString());
                e5.printStackTrace();
            }
        }

        public JSONArray execRecentApps(String... command) {
            JSONArray output = new JSONArray();
            Process p = null;
            try {
                p = Runtime.getRuntime().exec(command);
                p.waitFor();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                while (true) {
                    try {
                        String line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        output.put(line.split(" ")[1]);
                    } catch (Exception e) {
                    }
                }
            } catch (Exception e2) {
                Log.e("jkseo", "ShellExecuter Executer error : " + e2.toString());
                e2.printStackTrace();
            }

            try {
                p.getErrorStream().close();
            } catch (Exception e) {
                Log.e("jkseo", e.getMessage());
            }
            try {
                p.getInputStream().close();
            } catch (Exception e2) {
                Log.e("jkseo", e2.getMessage());
            }
            try {
                p.getOutputStream().close();
            } catch (Exception e3) {
                Log.e("jkseo", e3.getMessage());
            }

            return output;
        }
    }
}