package UtilGroup;

import android.os.Environment;
import android.os.ParcelUuid;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.io.FilenameUtils;

/* loaded from: classes.dex */
public class EventHistory {
    private static final Object objLock = new Object();
    private String mEventHistPath;

    public File LOG_PATH = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MacroApp/History/");
    private String LogDate;

    public EventHistory(boolean isNew) {
        this.mEventHistPath = null;
        this.mEventHistPath = getEventHistFileName(isNew);

        Log.e("jkseo", "mEventHistPath : " + this.mEventHistPath);
    }

    private String getEventHistFileName(boolean isNew) {
        long now = System.currentTimeMillis();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String date = simpleDateFormat.format(now);
        LogDate = date;

        File basePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+BuildConfig.APPLICATION_ID+"/History/" + date);
        LOG_PATH = basePath;

        if (!basePath.exists()) {
            // 현재일 기준 7일 이전 로그를 삭제한다.
            cleanOldLog(7);

            basePath.mkdirs();
        }

        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+BuildConfig.APPLICATION_ID+"/History/" + date + "/" + date + ".txt";
        if(isNew) {
            if (new File(filePath).exists()) {
                String path = FilenameUtils.getFullPath(filePath);
                String nm = FilenameUtils.getBaseName(filePath);
                String ext = FilenameUtils.getExtension(filePath);
                int i = 1;
                while (true) {
                    File file = new File(path + nm + "_(" + i + ")." + ext);
                    if (!file.exists()) {
                        return file.getPath();
                    }
                    i++;
                }
            }
        }

        return filePath;
    }
    public void writeEventHistory(String data) throws IOException {
        synchronized (objLock) {
            try{
                writeHistory(data);
            }
            catch (Exception ex) {
                try {
                    // 혹시 사용중 에러나면 추가로 생성하도록 처리한다.
                    this.mEventHistPath = null;
                    this.mEventHistPath = getEventHistFileName(true);
                }
                catch (Exception sub_ex) {
                    Log.e("jkseo", sub_ex.getMessage());

                }
            }
        }
    }


    public String writeHistory(String data) throws IOException {
        Log.e("jkseo", data);
        long now = System.currentTimeMillis();
        SimpleDateFormat currDateFormat  = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String eventTime = simpleDateFormat.format(now);
        String currDate = currDateFormat.format(now);

        // 현재일자와 최초로그 실행일자가 다르다면 파일을 새롭게 생성한다.
        if(!LogDate.equalsIgnoreCase(currDate)){
            Log.e("jkseo", "new logfile logDate : " + LogDate + " <> CurrDate : " + currDate);

            this.mEventHistPath = null;
            this.mEventHistPath = getEventHistFileName(true);
        }

        String logData = eventTime + " : " + data;
        String infoPath = null;
        BufferedWriter buf = null;
        try {
            try {
                File basePath = new File(mEventHistPath);

                buf = new BufferedWriter(new FileWriter(this.mEventHistPath, true));
                buf.append((CharSequence) logData);
                buf.newLine();
                buf.close();
                buf.close();
            } catch (Exception e) {
                e.printStackTrace();
                infoPath = null;
                if (buf != null) {
                    buf.close();
                }
            }
            return infoPath;
        } catch (Throwable th) {
            if (buf != null) {
                try {
                    buf.close();
                } catch (Exception e2) {
                }
            }
            throw th;
        }
    }

    public void cleanOldLog(int day)  {
        try{
            writeEventHistory("Log Test");
        }
        catch (Exception ex){

        }

        /// 현재 날짜 계산
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, (day * -1)); // 일주일 전 날짜

        Date oneWeekAgo = calendar.getTime();

        // 현재 날짜의 문자열 표현 (yyyyMMdd)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String currentDate = dateFormat.format(new Date());

        // 주어진 경로의 모든 폴더 가져오기
        File parentDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + BuildConfig.APPLICATION_ID + "/History/");
        File[] folders = parentDirectory.listFiles(File::isDirectory);

        if (folders != null) {
            for (File folder : folders) {
                String folderName = folder.getName();

                try {
                    // yyyymmdd, yyyymmdd_(xx) 포맷의 폴더를 모두 지우기 위해 폴더명 앞 8자리를 잘라비교한다.
                    Date folderDate = dateFormat.parse(folderName.substring(0,8));
                    if (folderDate.before(oneWeekAgo)) {
                        // 폴더가 일주일 이전의 것이면 삭제
                        deleteFolder(folder);
                        System.out.println("Deleted old folder: " + folderName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFolder(file);
                } else {
                    file.delete();
                }
            }
        }
        folder.delete();
    }
}
