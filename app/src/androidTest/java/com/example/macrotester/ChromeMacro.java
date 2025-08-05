package com.example.macrotester;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiCollection;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiScrollable;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ModelGroup.ClickAddresItem;
import UtilGroup.EventHistory;
import UtilGroup.ShellExecuter;

public class ChromeMacro {
    private EventHistory mEventHistory;
    private Context mContext;
    private UiDevice mDevice;
    private DelayEvent mDelayEvent;

    private ScreenCapture mScreenCapture;
    private Ocr mOcr;
    private UiScrollable scrollable;

    /* 브라우저 및 기기 사이즈 */
    private int mTabbarSize = 0;
    private int mMacroHeight = 0;
    private int mDeviceHeight = 0;
    private int mDeviceWidth = 0;

    private int CurrScrollForwardCnt = 0;
    private int ScrollForwardLimitCnt = 0;
    private boolean isScrollTop = false;

    private ShellExecuter shellExecuter = null;

    public ChromeMacro(Context ctx, EventHistory eventHistory, UiDevice device) {
        this.mContext = ctx;
        this.mEventHistory = eventHistory;
        this.mDevice = device;

        mScreenCapture = new ScreenCapture(mDevice);
        mOcr = new Ocr(mDevice, mContext, this.mEventHistory);
        mDelayEvent = new DelayEvent(new EventHistory(false));
        shellExecuter = new ShellExecuter(this.mEventHistory);

        InitControls();
    }

    private void InitControls() {
        this.mDeviceWidth = this.mDevice.getDisplayWidth();
        this.mDeviceHeight = this.mDevice.getDisplayHeight();
    }
    public void OpenApp(String packageName) {
        try {
            String launcherPackage = this.mDevice.getLauncherPackageName();
            Assert.assertThat(launcherPackage, CoreMatchers.notNullValue());
            // 주석처리함 2023.11.19 by jkseo
//            this.mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), 3000L);

            Context appCtx = InstrumentationRegistry.getInstrumentation().getContext();
            Intent intent = appCtx.getPackageManager().getLaunchIntentForPackage(packageName);

            intent.setAction(Intent.ACTION_VIEW);
//            intent.setData(Uri.parse("about:blank"));
            intent.setData(Uri.parse("https://www.google.com/"));
            appCtx.startActivity(intent);

            // 주석처리함 2023.11.19 by jkseo
//            this.mDevice.wait(Until.hasObject(By.pkg(packageName).depth(0)), 1000L);
            this.mDelayEvent.exec(3000L);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("jkseo", "error : OpenApp / " + e.getMessage());
        }
    }


    // 브라우저 앱 닫기
    public void CloseApps() {
        try {
            shellExecuter.isColseApp();
            if (1 != 0) {
                this.mDevice.pressRecentApps();
                this.mDelayEvent.exec(4000L);

                Boolean result = false;
                File basePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/MacroApp/ScreenDump");

                if (!basePath.exists()) {
                    basePath.mkdirs();
                }

                File dumpFile = new File(basePath.getPath() + "/" + "dump.xml");
                mDevice.dumpWindowHierarchy(dumpFile);

                List<UiObject2> appClosebuttons = this.mDevice.findObjects(By.res("com.android.systemui:id/dismiss_task"));

                int closeCount = appClosebuttons.size();

                for (int i = 0; i < closeCount; i++) {
//                    if (appClosebuttons.get(i).getContentDescription() != null) {
//                        Log.e("jkseo", "appClosebuttons : " + i + "      " + appClosebuttons.get(i).getContentDescription());
//
//                        // 매크로 설정앱은 종료시키지 않는다.
////                        if (appClosebuttons.get(i).getContentDescription().equalsIgnoreCase("Auto Macro 앱 종료"))
////                            continue;
//                    }

                    appClosebuttons.get(i).click();
                    this.mDelayEvent.exec(1000L);
                }
            }
        } catch (Exception e) {
            try{
                this.mEventHistory.writeEventHistory("execCloseApps error :" + e.getMessage());
            }
            catch (IOException ioe){
                Log.e("jkseo", "execCloseApps error :" + ioe.getMessage());
            }
        }
    }


    // 브라우저 내 주소창 클릭
    public boolean ClickURLBar() throws Exception {
        this.mEventHistory.writeEventHistory("주소창 클릭");
        UiObject2 uiObject = WaitForeObject(By.res("com.android.chrome:id/search_box_text"));
        if (uiObject != null) {
            boolean uiObjectResult = ((Boolean) uiObject.clickAndWait(Until.newWindow(), 5000L)).booleanValue();
            return uiObjectResult;
        }
        return false;
    }

    // 뒤로가기 버튼 클릭
    public boolean ClickBackButton() throws Exception{
        this.mEventHistory.writeEventHistory("뒤로가기 클릭");
        return this.mDevice.pressBack();
    }

    public boolean ClickAddress(int x, int y) throws Exception{
        return this.mDevice.click(x,y);
    }

    public ArrayList<ClickAddresItem> GetAddresItemOfDumpXML(XmlPullParser parser) throws Exception {
        int eventType = parser.getEventType();
        String text, bounds, xmlName, objClass;

        ArrayList<ClickAddresItem> result = new ArrayList<ClickAddresItem>();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {

                text = "";
                bounds = "";
                xmlName = "";
                objClass = "";

                xmlName = parser.getName();
//                Log.e("jkseo", "find name : " + xmlName);


                if (xmlName.equalsIgnoreCase("node")) {
                    text = parser.getAttributeValue(null, "text");
                    objClass =parser.getAttributeValue(null, "class");
//                    Log.e("jkseo", "find nodetext : " + text);
                }


                if (text != null && text.length()>=2) {
                    bounds = parser.getAttributeValue(null, "bounds");
                    if (bounds != null) {

                        // Bounds 속성값 처리
//                        Log.e("jkseo", "find bounds : " + bounds);

                        String[] coordinates = bounds.split("\\[|\\]|,");
                        int left = Integer.parseInt(coordinates[1]);
                        int top = Integer.parseInt(coordinates[2]);
                        int right = Integer.parseInt(coordinates[4]);
                        int bottom = Integer.parseInt(coordinates[5]);

//                        Log.e("jkseo", "Left: " + left + ", Top: " + top +
//                                ", Right: " + right + ", Bottom: " + bottom);

                        ClickAddresItem clickItem = new ClickAddresItem(text, "", objClass, left, top, right, bottom);
                        result.add(clickItem);
                    }
                }
            }

            eventType = parser.next();
        }
        return result;
    }

    public Boolean  ClickOfDumpXML(XmlPullParser parser, String findString) throws Exception {
        int eventType = parser.getEventType();
        String text, bounds, xmlName;

        Boolean result = false;
        while (eventType != XmlPullParser.END_DOCUMENT) {

//            xmlName = parser.getName();
//            Log.e("jkseo", "all find name : " + xmlName);
//            text = parser.getAttributeValue(null, "text");
//            Log.e("jkseo", "all find nodetext : " + text);

            if (eventType == XmlPullParser.START_TAG) {
//                if (parser.isEmptyElementTag()) {
//                    eventType = parser.next();
//                    continue;
//                }

                text = "";
                bounds = "";
                xmlName = "";

                xmlName = parser.getName();
                Log.e("jkseo", "find name : " + xmlName);

                if (xmlName.equalsIgnoreCase("node")) {
                    text = parser.getAttributeValue(null, "text");
                    Log.e("jkseo", "find nodetext : " + text);
                }

                // naver 의 경우 덤프를 뜨면, NAVER m.naver.com 으로 표시되기에..;; contains 를 추가한다.
                if (text != null && (text.equals(findString) || text.contains(findString)) ) {
                    bounds = parser.getAttributeValue(null, "bounds");
                    if (bounds != null) {
                        result = true;
                        // Bounds 속성값 처리
//                        Log.e("jkseo", "find bounds : " + bounds);

                        String[] coordinates = bounds.split("\\[|\\]|,");
                        int left = Integer.parseInt(coordinates[1]);
                        int top = Integer.parseInt(coordinates[2]);
                        int right = Integer.parseInt(coordinates[4]);
                        int bottom = Integer.parseInt(coordinates[5]);

                        Log.e("jkseo", "Left: " + left + ", Top: " + top +
                                ", Right: " + right + ", Bottom: " + bottom);

                        if(findString.equalsIgnoreCase("Google 검색"))
                        {
                            // 검색돋보기 약간 우측을 클릭해준다.
                            left += 20;
                        }

                        this.mDevice.click(left, top);

                        break;
                    }
                }
            }

            eventType = parser.next();
        }
        return result;
    }

    // 웹사이트 내 검색창 클릭
    public boolean ClickSearchFieldOfDump(String findWindowText) throws Exception {
        this.mEventHistory.writeEventHistory("웹사이트 덤프 (dump type)");

        File basePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/MacroApp/ScreenDump");

        if (!basePath.exists()) {
            basePath.mkdirs();
        }

        File dumpFile = new File(basePath.getPath() + "/" + "dump.xml");

        // dump가 끝까지 생성되지 않는 경우가 발생해서 기존 파일을 지우고 생성해본다.
        if (dumpFile.exists()) {
            dumpFile.delete();
        }

        try {
            // google 검색버튼이 안나와서 테스트해본다. 실행주체가 다르면 그런가?
            this.mDelayEvent.exec(5000L);
            mDevice.dumpWindowHierarchy(dumpFile);

            InputStream is = new FileInputStream(dumpFile);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();

            // XML 파서에 파일 스트림 지정.
            parser.setInput(is, "UTF-8");

            Boolean result = ClickOfDumpXML(parser,findWindowText);

            this.mEventHistory.writeEventHistory("웹사이트 내 검색창 찾기 : " + findWindowText + " / " + String.valueOf(result));

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 객체 찾기 대기
    public UiObject2 WaitForeObject(BySelector getBySelector) throws InterruptedException {
        // 샤오미폰에서는 timeout 이 기능이 동작하지 않고, 너무 오래걸려서 수정처리함 2023.11.19 by jkseo
        //boolean waitForResult = ((Boolean) this.mDevice.wait(Until.hasObject(getBySelector), 5000L)).booleanValue();
//        if (waitForResult) {
//            Assert.assertThat(Boolean.valueOf(waitForResult), CoreMatchers.is(true));
//            UiObject2 uiObject = this.mDevice.findObject(getBySelector);
//            return uiObject;
//        }
//        return null;

        this.mDelayEvent.exec(3000L);
        UiObject2 uiObject = this.mDevice.findObject(getBySelector);

        if (uiObject != null) {
            Assert.assertThat(Boolean.valueOf(true), CoreMatchers.is(true));
            return uiObject;
        }
        return null;
    }

    public Boolean InputSearchKeywordForCommand(String keyword) throws Exception {
        this.mEventHistory.writeEventHistory("검색창 찾기");

//        String command = "su -c input text \"hwllo jkseo\"";
        boolean result = ClickSearchFieldOfDump( "Google 검색");
        this.mEventHistory.writeEventHistory("검색창 찾기 결과 : " + String.valueOf(result));
        if(!result) return false;

        mDelayEvent.exec(1000L);
        this.mEventHistory.writeEventHistory("검색창 키워드 입력 : " + keyword);

//        keyword = "tester!! keyword";
        String command = "su -c input text " + keyword;
        this.mDevice.executeShellCommand(command);
        this.mDevice.pressEnter();
        mDelayEvent.exec(3000L);

        return true;
    }

    public Boolean InputSearchKeyword(String keyword, boolean isClickSearchBar) throws Exception {
        this.mEventHistory.writeEventHistory("주소창 검색 키워드 입력 keyword : " + keyword);
        UiObject2 uiObject = WaitForeObject(By.res("com.android.chrome:id/url_bar"));
        if (!isClickSearchBar) {
            uiObject.clickAndWait(Until.newWindow(), 5000L);
        }
        // 포커스 후 입력이 잘 안되는 경우가 발생하여 2초 delay를 준다.
        this.mDelayEvent.exec(2000L);

        if(uiObject == null) return false;

        uiObject.setText(keyword);

        // 검색키워드 입력 후 Enter Key가 입력되지 않는 현상이 발생해서 2번 실행한다.
        this.mDevice.pressEnter();
        this.mDevice.pressEnter();
        this.mEventHistory.writeEventHistory("검색 키워드 입력후 엔터입력 ");
        this.mDelayEvent.exec(3000L);

        return true;
    }

    public String GetChromeUrl() throws Exception {
        UiObject2 uiObject = WaitForeObject(By.res("com.android.chrome:id/url_bar"));

        String currUrl = "";
        if (uiObject != null) {
            currUrl = uiObject.getText();
        }

        // http, https 프로토콜은 빠지고 가져온다.
        this.mEventHistory.writeEventHistory("주소창 값 : " + currUrl);

        return currUrl;
    }

    // 브라우저 알림창이 뜰 경우 닫기버튼을 찾아서 창을 닫는다.
    public boolean ClickChromeInfoBox() throws Exception {
        UiObject2 uiObject = WaitForeObject(By.res("com.android.chrome:id/infobar_close_button"));
        if (uiObject != null) {
//            this.mEventHistory.writeEventHistory("크롬 알림창 삭제 클릭");
            boolean uiObjectResult = ((Boolean) uiObject.clickAndWait(Until.newWindow(), 5000L)).booleanValue();
            if (uiObjectResult) {
                this.mDelayEvent.exec(1000L);
            }
            return uiObjectResult;
        }
        return false;
    }

    // Click 일치문자열(dump type)
    public boolean ClickSearchTextByDump(String findWindowText) throws Exception
    {
        Boolean result = false;
        File basePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/MacroApp/ScreenDump");
        if (!basePath.exists()) {
            basePath.mkdirs();
        }

        File dumpFile = new File(basePath.getPath() + "/" + "dump.xml");

        try {
            mDevice.dumpWindowHierarchy(dumpFile);
            InputStream is = new FileInputStream(dumpFile);

            // 갤럭시 A7(2017) 기준 : [60,1704][1020,1812]
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();

            // XML 파서에 파일 스트림 지정.
            parser.setInput(is, "UTF-8");
            result = ClickOfDumpXML(parser,findWindowText);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // Google 로그인창 닫기
    public boolean Close_GoogleLogin(String findWindowText) throws Exception
    {
        this.mEventHistory.writeEventHistory("알림 - 구글로그인 종료하기 : " + findWindowText);

        Boolean result = false;

        // 재귀호출함수이기 때문에 이벤트 로그 객체를 매번 새로 생성한다.
        EventHistory eventHistory = this.mEventHistory;
        try {
            this.mEventHistory.writeEventHistory("CloseAlarm_GoogleLogin 화면캡쳐 시작 ");
            String img = this.mScreenCapture.getCapture();
            EventHistory eventHistory2 = this.mEventHistory;
            eventHistory2.writeEventHistory("CloseAlarm_GoogleLogin 화면캡쳐 종료 " + img);
            if (img != null) {
                this.mEventHistory.writeEventHistory("CloseAlarm_GoogleLogin OCR 시작");
                this.mOcr.TesssReInit("kor");
                int[] p = this.mOcr.getSearchKeyword(img, findWindowText);
                EventHistory eventHistory3 = this.mEventHistory;
                eventHistory3.writeEventHistory("CloseAlarm_GoogleLogin OCR 종료 : " + p[0] + " : " + p[1]);

                if (p[0] > -1 && p[1] > -1) {
                    this.mDevice.click(p[0], p[1]);
                    return true;
                }

                p = this.mOcr.getSearchKeyword(img, "로그아옷");
                eventHistory3.writeEventHistory("CloseAlarm_GoogleLogin OCR 종료 : " + p[0] + " : " + p[1]);

                if (p[0] > -1 && p[1] > -1) {
                    this.mDevice.click(p[0], p[1]);
                    return true;
                }

                p = this.mOcr.getSearchKeyword(img, "로그아뭇");
                eventHistory3.writeEventHistory("CloseAlarm_GoogleLogin OCR 종료 : " + p[0] + " : " + p[1]);

                if (p[0] > -1 && p[1] > -1) {
                    this.mDevice.click(p[0], p[1]);
                    return true;
                }
            }
        }
        catch(Exception e)
        {
            Log.e("jkseo", e.getMessage());
        }
        return result;
    }

    public boolean Click_LinkSponser(String findWindowText, int linkOrder) throws Exception
    {
        this.mEventHistory.writeEventHistory("알림 - 스폰서링크 클릭하기 : " + findWindowText + "/ order : " + String.valueOf(linkOrder));

        Boolean result = false;

        EventHistory eventHistory = this.mEventHistory;
        try {
            this.mEventHistory.writeEventHistory("스폰서링크 화면캡쳐 시작 ");
            String img = this.mScreenCapture.getCapture();
            EventHistory eventHistory2 = this.mEventHistory;
            eventHistory2.writeEventHistory("스폰서링크 화면캡쳐 종료 " + img);

            int hGap = 20;

            if (img != null) {
                this.mEventHistory.writeEventHistory("스폰서링크 OCR 시작");
                this.mOcr.TesssReInit("kor");
                int[] p = this.mOcr.getSearchKeyword(img, findWindowText);
                EventHistory eventHistory3 = this.mEventHistory;
                eventHistory3.writeEventHistory("스폰서링크 OCR 종료 : " + p[0] + " : " + p[1]);
                if (p[0] > -1 && p[1] > -1) {
                    // 스폰서 그룹의 첫번째
                    this.mDevice.click(p[0], p[1]);
                    return true;
                }
            }
        }
        catch(Exception e)
        {
            Log.e("jkseo", e.getMessage());
        }
        return result;
    }

    public boolean PreadLoadPage(int loadCount) throws Exception {
        boolean result = true;
        EventHistory eventHistory = this.mEventHistory;

        // 간혹 Google에 로그인하기 창이 뜨는 경우 처리 로그아웃이 아니라 로그아옷 .;;;; 으로 인식한다 에레이
        boolean isGoogleClose = Close_GoogleLogin("로그아웃");

        // debug 구글 로그인창이 뜨고나면 크롬 view 객체를 못 읽어 와서 스크롤정보다, 덤프가 올바로 동작하지 않는다.
        // 구글 로그인창이 투명한 막에 쌓여있는듯. 뒤에있는 크롬 view 객체를 읽어오지 않는다.
        // 참나, 구글창이 안뜰때도 안되는 경우가 생긴다?.
//        this.drag_ScrollToTopOrBottom(true);

        eventHistory.writeEventHistory("페이지 미리로드(랜덤 5 ~ X) : " + String.valueOf(loadCount) + " Page");
        this.scrollable = new UiScrollable(new UiSelector().scrollable(true));
        this.scrollable.setAsVerticalList();

        boolean scrollResult = false;

        try {
            // 정해진 횟수만큼 페이지를 미리 로드한다.
            long rndDelay = 500L;
            for (int loop = 0; loop < loadCount; loop++) {
                scrollResult = this.scrollable.scrollForward(30);
                if(scrollResult) CurrScrollForwardCnt++;
//                else
//                {
//                    if (loop <= 2)
//                    {
//                        // 구글 로그인창이 떠서 스크롤이 안되는경우 대비해서 한번더 종료처리해본다.
//                        if(!isGoogleClose)
//                        {
//                            isGoogleClose = CloseAlarm_GoogleLogin("로그아옷");
//                        }
//                    }
//                    else
//                        break;
//                }

                eventHistory.writeEventHistory("scrollForward : " + scrollResult);

                rndDelay = randomRange(1000L * 5L, 1000L * 15L);
                this.mDelayEvent.exec(rndDelay);
                result = true;
            }
        } catch (Exception ex) {
            result = false;
        }
        return result;
    }

    public void flingToBeginning() throws Exception {
        this.scrollable.flingToBeginning(this.scrollable.getMaxSearchSwipes());
        this.mEventHistory.writeEventHistory("화면 처음으로 이동");
        this.isScrollTop = true;
        this.mDelayEvent.exec(1000L);
    }

    public boolean searchFindKey(String findKey, String searchType, int ScrollForwardLimitCnt) throws Exception {
        // 검색결과가 나올때까진 무한정 돌릴 수는 없으므로, 페이지 넘기는 횟수를 제한한다.

        int pageLoadCnt = 0;
        this.ScrollForwardLimitCnt = ScrollForwardLimitCnt;
        this.CurrScrollForwardCnt = 0;

        EventHistory eventHistory = this.mEventHistory;

        // google 로그인창을 없애기 위해 10초정도 대기한다.
        this.mDelayEvent.exec(10000L);

        boolean uiObjectResult = false;
        if (searchType.equalsIgnoreCase("M0200001")) {
            eventHistory.writeEventHistory("검색방식 : M0200001(페이지 찾기)");
            // 랜덤으로 로드처리한다.
            // 검색페이지 미리 로드 (5~`X random page)
            // 위에서 드레그로 한번 내려오므로 1번 차감한다. 5 -> 4
            pageLoadCnt = (int)this.randomRange(4, ScrollForwardLimitCnt);
            this.ScrollForwardLimitCnt = pageLoadCnt;

            boolean loadResult = PreadLoadPage(pageLoadCnt);
            this.mDelayEvent.exec(2000L);

            if(!loadResult)
            {
                eventHistory.writeEventHistory("객체를 못가져와서 에러가 발생하는 경우이므로 다시한번 시도한다.");
                this.mDevice.pressHome();
//                OpenApp("com.android.chrome");

                this.mDevice.pressRecentApps();
                this.mDelayEvent.exec(3000L);
                // 갤럭시는 최근앱이 카운슬러 스타일로 한개씩 표시되지만
                // 샤오미는 최근앱 2개부터 화면에 타일형태로 표시되며, 제일 최근앱은 좌측 중앙에 표시된다.
                this.mDevice.click((this.mDeviceWidth/2) - 20, this.mDeviceHeight/2);
                this.mDelayEvent.exec(1000L);

                loadResult = PreadLoadPage(pageLoadCnt);
            }
            eventHistory.writeEventHistory("검색되어진 결과중 특정단어 검색 시작 findKey : " + findKey);
            uiObjectResult = ChromeSearchMacroByChromeTool(findKey);
        }
        else if (searchType.equalsIgnoreCase("M0200002")) {
            eventHistory.writeEventHistory("검색방식 : M0200002(OCR)");

            boolean isGoogleClose = Close_GoogleLogin("로그아옷");

            eventHistory.writeEventHistory("검색되어진 결과중 특정단어 검색 시작 findKey : " + findKey);
            this.mOcr.TesssReInit("eng");
            uiObjectResult = ChromeSearchMacroByOCR(findKey);
        }
        else if (searchType.equalsIgnoreCase("M0200003")){
            eventHistory.writeEventHistory("검색방식 : M0200003(스폰서)");
            boolean isGoogleClose = Close_GoogleLogin("로그아옷");

            eventHistory.writeEventHistory("검색되어진 결과중 특정단어 검색 시작 findKey : " + findKey);
            this.mOcr.TesssReInit("kor");

            eventHistory.writeEventHistory("스크롤 가능 횟수 : " + String.valueOf(this.ScrollForwardLimitCnt));
            uiObjectResult = ChromeSearchMacroByOCR(findKey);

//            uiObjectResult = Click_LinkSponser("스폰서", 1);
        }

        EventHistory eventHistory2 = this.mEventHistory;
        eventHistory2.writeEventHistory("검색되어진 결과중 특정단어 검색 findKey : " + findKey + "    찾은데이터 클릭 결과 " + uiObjectResult);
        return uiObjectResult;
    }

    private boolean ChromeSearchMacroByChromeTool(String searchText) throws Exception {
        // 객체 LOCK 때문에 이벤트 로그 객체를 매번 새로 생성한다.
        EventHistory eventHistory = this.mEventHistory;
        eventHistory.writeEventHistory("ChromeSearchMacro : " + searchText);
        try {
            this.mEventHistory.writeEventHistory("페이지에서 찾기 시작");
            if(this.scrollable == null)
                this.scrollable = new UiScrollable(new UiSelector().scrollable(true));
            this.scrollable.setAsVerticalList();

            boolean searchResult = false;
            // Action Type : xMouse
            clickMenuBtn();
            clickMenuItemSearchStringOnPage();
            InputKeywordBySearchStringOnPage(searchText);
            // 브라우저가 검색어를 찾고 화면에 표시하는 시간을 가진다.
            this.mDelayEvent.exec(2000L);

            searchResult = checkResultBySearchStringOnPage();
            clickCloseMenuItemSearchStringOnPage();

            this.mEventHistory.writeEventHistory("페이지에서 찾기 결과 :" + String.valueOf(searchResult));

            if (searchResult) {
                // ocr type.
                // 샤오미폰 성능이 떨어져(?) 문자열 인식이 제대로 되지 않는 경우 발생
                // xaomi에서는 https://www.storebook.ai -> httpsi//WwwAstorebook,ai 으로 인식됨
                // dump 기능으로 대체 처리
//                this.mEventHistory.writeEventHistory("ChromeSearchMacro 화면캡쳐 시작 ");
//
//                String img = this.mScreenCapture.getCapture();
//                EventHistory eventHistory2 = this.mEventHistory;
//                eventHistory2.writeEventHistory("ChromeSearchMacro 화면캡쳐 종료 " + img);
//
//                if (img != null) {
//                    this.mEventHistory.writeEventHistory("ChromeSearchMacro OCR 시작");
//                    int[] p = this.mOcr.getSearchKeyword(img, searchText);
//                    EventHistory eventHistory3 = this.mEventHistory;
//                    eventHistory3.writeEventHistory("ChromeSearchMacro OCR 종료 : " + p[0] + " : " + p[1]);
//
//                    if (p[0] > -1 && p[1] > -1) {
//                        this.mDevice.click(p[0], p[1]);
//                        return true;
//                    }
//                }

                // dump type
                this.mEventHistory.writeEventHistory("ChromeSearchMacro 화면덤프 시작 ");

                boolean clickResult = ClickSearchTextByDump(searchText);
                this.mEventHistory.writeEventHistory("ChromeSearchMacro 덤프에서 일치문자열 찾기 종료 : " + String.valueOf(clickResult) );

                if(clickResult)
                    return clickResult;
            } // if (searchResult)

            // 중요 : 페이지 검색을 하기위해서는 필연적으로 화면스크롤을 위로 올려서 메뉴버튼을 생성시켜야한다(점세개)
            // 그럴경우 상하 스크롤 위치가 맨 하단까지 가지 않고 항상 반푼정도 위로 올라가있어서 검색결과 더보기등을 처리할 수가 없다.
            // 부득이하게 스크롤을 강제적으로 두번 더 내려서 상하 스크롤이 최하단까지 왔는지 확인하고 처리한다.
            boolean scrollResult = false;
            String moreResult = "";
            scrollResult = this.scrollable.scrollForward(70);
            this.CurrScrollForwardCnt++;

            scrollResult = this.scrollable.scrollForward(70);
            this.CurrScrollForwardCnt++;

            this.mEventHistory.writeEventHistory("페이지 이동가능 횟수 : "+ String.valueOf(this.ScrollForwardLimitCnt - this.CurrScrollForwardCnt));

            EventHistory eventHistory4 = this.mEventHistory;
            eventHistory4.writeEventHistory("scrollForward 70 : " + scrollResult);

            // 페이지 넘김 최대횟수를 넘기면 강제로 종료시킨다.
            if(this.CurrScrollForwardCnt < this.ScrollForwardLimitCnt)
            {
                if (scrollResult) {
                    return ChromeSearchMacroByChromeTool(searchText);
                }

                moreResult = clickMoreBtn(searchText);
                if (moreResult.equalsIgnoreCase("SEARCH")) {
                    return ChromeSearchMacroByChromeTool(searchText);
                }

                if (!moreResult.equalsIgnoreCase("FINISH") && moreResult.equalsIgnoreCase("ERROR")) {
                    flingToBeginning();
                    if (clickMoreBtn(searchText).equalsIgnoreCase("SEARCH")) {
                        return ChromeSearchMacroByChromeTool(searchText);
                    }
                }
            } // if(this.CurrScrollForwardCnt <= this.ScrollForwardLimitCnt)
            else{
                this.mEventHistory.writeEventHistory("현재 페이지 넘김 횟수가 최대치를 넘어 다음매크로 작업으로 이동합니다.");
            }

        } catch (Exception e) {
            EventHistory eventHistory5 = this.mEventHistory;
            eventHistory5.writeEventHistory("ChromeSearchMacro error " + e.toString());
        }
        return false;
    }

    private boolean ChromeSearchMacroByOCR(String searchText) throws Exception {
        // 재귀호출함수이기 때문에 이벤트 로그 객체를 매번 새로 생성한다.
        EventHistory eventHistory = this.mEventHistory;

        if(this.CurrScrollForwardCnt <= this.ScrollForwardLimitCnt) {
            eventHistory.writeEventHistory("스크롤 횟수 : " + this.CurrScrollForwardCnt);
            this.CurrScrollForwardCnt++;
        }
        else{
            eventHistory.writeEventHistory("스크롤 가능횟수 초과 종료");
            return false;
        }

        eventHistory.writeEventHistory("ChromeSearchMacro : " + searchText);
        try {
            this.mEventHistory.writeEventHistory("페이지에서 찾기 시작");
            this.scrollable = new UiScrollable(new UiSelector().scrollable(true));
            this.scrollable.setAsVerticalList();
            this.mEventHistory.writeEventHistory("ChromeSearchMacro 화면캡쳐 시작 ");
            String img = this.mScreenCapture.getCapture();
            EventHistory eventHistory2 = this.mEventHistory;
            eventHistory2.writeEventHistory("ChromeSearchMacro 화면캡쳐 종료 " + img);
            if (img != null) {
                this.mEventHistory.writeEventHistory("ChromeSearchMacro OCR 시작");

                int[] p = this.mOcr.getSearchKeyword(img, searchText);
                EventHistory eventHistory3 = this.mEventHistory;
                eventHistory3.writeEventHistory("ChromeSearchMacro OCR 종료 : " + p[0] + " : " + p[1]);

                if (p[0] > -1 && p[1] > -1) {
                    this.mDevice.click(p[0], p[1]);
                    return true;
                }
                boolean scrollResult = this.scrollable.scrollForward(70);
                EventHistory eventHistory4 = this.mEventHistory;
                eventHistory4.writeEventHistory("scrollForward 70 : " + scrollResult);
                if (scrollResult) {
                    return ChromeSearchMacroByOCR(searchText);
                }
                String moreResult = clickMoreBtn(searchText);
                if (moreResult.equalsIgnoreCase("SEARCH")) {
                    return ChromeSearchMacroByOCR(searchText);
                }
                if (!moreResult.equalsIgnoreCase("FINISH") && moreResult.equalsIgnoreCase("ERROR")) {
                    this.scrollable.flingToBeginning(this.scrollable.getMaxSearchSwipes());
                    this.mEventHistory.writeEventHistory("화면 처음으로 이동");
                    this.mDelayEvent.exec(10000L);
                    if (clickMoreBtn(searchText).equalsIgnoreCase("SEARCH")) {
                        return ChromeSearchMacroByOCR(searchText);
                    }
                }
            }
        } catch (Exception e) {
            EventHistory eventHistory5 = this.mEventHistory;
            eventHistory5.writeEventHistory("ChromeSearchMacro error " + e.toString());
        }
        return false;
    }

    // 브라우저 내 "검색결과 더보기" 링크 클릭하기
    private String clickMoreBtn(String searchText) throws Exception {
        String result = "NONE";
        try {
            boolean isLast = this.scrollable.flingToEnd(this.scrollable.getMaxSearchSwipes());
            EventHistory eventHistory = this.mEventHistory;
            eventHistory.writeEventHistory("화면 끝으로 이동 : " + isLast);
            if (isLast) {
                this.mDelayEvent.exec(5000L);
                UiObject fbar = this.mDevice.findObject(new UiSelector().resourceId("fbar"));
                if (fbar != null) {
                    UiObject moreBtn = this.mDevice.findObject(new UiSelector().className("android.widget.Button").textContains("결과 더보기"));
                    if (moreBtn == null) {
                        moreBtn = this.mDevice.findObject(new UiSelector().className("android.widget.Button").textContains("자세히 보기"));
                    }
                    if (moreBtn == null) {
                        moreBtn = this.mDevice.findObject(new UiSelector().className("android.widget.Button").textContains("검색결과 더보기"));
                    }
                    if (moreBtn == null) {
                        moreBtn = this.mDevice.findObject(new UiSelector().className("android.widget.Button").textContains("자세히 확인하기"));
                    }
                    if (moreBtn == null) {
                        moreBtn = this.mDevice.findObject(new UiSelector().className("android.widget.Button").textContains("더보기"));
                    }
                    if (moreBtn == null) {
                        moreBtn = this.mDevice.findObject(new UiSelector().className("android.widget.Button").textContains("더 많은 검색결과 보기"));
                    }
                    if (moreBtn != null) {
                        this.mEventHistory.writeEventHistory("화면 끝으로 도착, 더보기 O");
                        moreBtn.click();
                        this.mEventHistory.writeEventHistory("더보기 클릭");
                        this.mDelayEvent.exec(5000L);
                        showUrlBar(true, 1);
                        this.mDelayEvent.exec(5000L);
                        result = "SEARCH";
                    } else {
                        this.mEventHistory.writeEventHistory("화면 끝으로 도착, 더보기 X, 검색결과 더이상 없음");
                        result = "FINISH";
                    }
                }
            }
        } catch (Exception e) {
            result = "ERROR";
        }
        EventHistory eventHistory2 = this.mEventHistory;
        eventHistory2.writeEventHistory("clickMoreBtn 더보기 클릭 결과 : " + result);
        return result;
    }

    // 주소 입력창 찾기
    public boolean showUrlBar(boolean scrollMode, int cnt) throws Exception {
        UiObject2 urlBar;
        this.mEventHistory.writeEventHistory("검색창 찾기 : " + cnt);
        if (cnt > 3) {
            this.mDevice.pressBack();
        }
        try {
            // url 바 찾기위해선 스크롤을 위로 한번 올려주어야 한다.
            boolean scrollResult = this.scrollable.scrollBackward(70);
            urlBar = WaitForeObject(By.res("com.android.chrome:id/url_bar"));
        } catch (Exception e) {
            urlBar = null;
        }
        if (urlBar != null) {
            this.mEventHistory.writeEventHistory("urlBar 찾음");
            return true;
        }
        this.mEventHistory.writeEventHistory("urlBar 못 찾음");
        drag_ScrollToTopOrBottom(scrollMode);
        boolean nextScrollMode = !scrollMode;
        return showUrlBar(nextScrollMode, cnt + 1);
    }

    // 상하스크롤 이동하기
    public void drag_ScrollToTopOrBottom(boolean isTop) {
        try {
            int xPosition = this.mDeviceWidth / 2;
            if (isTop) {
                int yStartPosition = this.mMacroHeight / 4;
                int yEndPosition = this.mMacroHeight / 2;
                EventHistory eventHistory = this.mEventHistory;
                eventHistory.writeEventHistory("위로 스크롤(주소창 표시를 위함) : " + yStartPosition + "  ->  " + yEndPosition);
                this.mDevice.drag(xPosition, yStartPosition, xPosition, yEndPosition, 10);
            } else {
                int yStartPosition2 = this.mMacroHeight / 2;
                int yEndPosition2 = this.mMacroHeight / 4;
                EventHistory eventHistory2 = this.mEventHistory;
                eventHistory2.writeEventHistory("아래로 스크롤(주소창 표시를 위함) : " + yStartPosition2 + "  ->  " + yEndPosition2);
                this.mDevice.drag(xPosition, yStartPosition2, xPosition, yEndPosition2, 10);
            }
//            this.mDelayEvent.exec(5000L);
            this.mDelayEvent.exec(3000L);
        } catch (Exception e) {
        }
    }

    public Boolean custom_ScrollToTopOrBottom(boolean isTop, int step) {
        boolean  result = false;

        if(this.scrollable == null)
            this.scrollable = new UiScrollable(new UiSelector().scrollable(true));

        this.scrollable.setAsVerticalList();

        try {
            if (isTop) {
                EventHistory eventHistory = this.mEventHistory;
                eventHistory.writeEventHistory("위로 스크롤(주소창 표시를 위함) : ");
                result = this.scrollable.scrollBackward(step);

            } else {
                EventHistory eventHistory2 = this.mEventHistory;
                eventHistory2.writeEventHistory("아래로 스크롤(주소창 표시를 위함) : ");
                result =  this.scrollable.scrollForward(step);
                isScrollTop = false;
            }
            //            this.mDelayEvent.exec(5000L);
            this.mDelayEvent.exec(1000L);
        } catch (Exception e) {
        }

        return result;
    }

    public void short_scrollToTopShowMenu() throws Exception {
        int xPosition = (int) (this.mDeviceWidth * 0.2d);
        int yEndPosition = (int) (this.mDeviceHeight * 0.2d);
        int yStartPosition = (int) (this.mDeviceHeight * 0.6d);
        EventHistory eventHistory = this.mEventHistory;

        eventHistory.writeEventHistory("위로 스크롤(주소창 표시를 위함) : " + yStartPosition + "  ->  " + yEndPosition);
        this.mDevice.drag(xPosition, yEndPosition, xPosition, yStartPosition, 10);
    }

    public void tabbarSize() throws Exception {
        this.mEventHistory.writeEventHistory("화면사이즈 측정(W*H)");

        // [0,240][1080,1920]
        UiObject2 uiObject = WaitForeObject(By.res("com.android.chrome:id/share_button_wrapper"));
        if (uiObject != null) {
            this.mTabbarSize = uiObject.getVisibleBounds().bottom - uiObject.getVisibleBounds().top;
            EventHistory eventHistory = this.mEventHistory;
            eventHistory.writeEventHistory("mTabbarSize : " + this.mTabbarSize);

            this.mMacroHeight = this.mDeviceHeight - (this.mTabbarSize + 20);
        }
        else{
            UiObject2 uiObject2 = WaitForeObject(By.clazz("android.webkit.WebView"));
            if(uiObject2 != null){
                this.mTabbarSize = uiObject2.getVisibleBounds().top;
                EventHistory eventHistory = this.mEventHistory;
                eventHistory.writeEventHistory("mTabbarSize : " + this.mTabbarSize);

                this.mMacroHeight = this.mDeviceHeight - this.mTabbarSize;
            }
            else{
                // 갤럭시, 샤오미6a 구글 탭 사이즈
                this.mMacroHeight = this.mDeviceHeight - 50;
            }
        }
    }

    public long randomRange(long min, long max) {

        Random random = new Random();
        int minValue = (int)min;
        int maxValue = (int)max;

        if(minValue >= maxValue){
            maxValue = minValue + 2;
        }

        // 난수 발생
        int randomNumber = random.nextInt(maxValue - minValue + 1) + minValue;

        if(randomNumber > maxValue)
            randomNumber = maxValue;

        // 랜덤 로직 변경
//        return ((int) (Math.random() * ((max - min) + 1))) + min;

        return randomNumber;
    }

    public void drag_RandomScroll() throws Exception {
        this.mEventHistory.writeEventHistory("화면 랜덤 스크롤 시작");
        int xPosition = (int) (this.mDeviceWidth * 0.2d);
        int yEndPosition = (int) (this.mDeviceHeight * 0.2d);
        int yStartPosition = (int) (this.mDeviceHeight * 0.6d);
        int mode = (int) randomRange(0L, 1L);
        if (mode == 1) {
            this.mDevice.drag(xPosition, yStartPosition, xPosition, yEndPosition, 10);
        } else {
            this.mDevice.drag(xPosition, yEndPosition, xPosition, yStartPosition, 10);
        }
        this.mDelayEvent.exec(1000L);
        this.mEventHistory.writeEventHistory("화면 랜덤 스크롤 종료");
    }

    public void randomAction(boolean isClick, long stayDurationMin, long stayDurationMax) throws Exception {
        if (isClick) {
            EventHistory eventHistory = this.mEventHistory;
            eventHistory.writeEventHistory("화면 랜덤 액션 시작 isClick : " + isClick);
        }
        int minWidth = (int) (this.mDeviceWidth * 0.1d);
        int maxWidth = (int) (this.mDeviceWidth * 0.9d);
        int minHeight = (int) (this.mDeviceHeight * 0.2d);
        int maxHeight = (int) (this.mDeviceHeight * 0.8d);
        int i = this.mDeviceWidth / 2;
        int i2 = this.mDeviceHeight / 2;
        int randomRange = (int) randomRange(minWidth, maxWidth);
        int randomRange2 = (int) randomRange(minHeight, maxHeight);
        this.mDelayEvent.exec(5000L);
        drag_RandomScroll();
        this.mDelayEvent.exec(5000L);
        drag_RandomScroll();
        this.mDelayEvent.exec(5000L);
        drag_RandomScroll();
        this.mDelayEvent.exec(5000L);
        drag_RandomScroll();
        long sleepTime = randomRange(stayDurationMin, stayDurationMax);
        EventHistory eventHistory2 = this.mEventHistory;
        eventHistory2.writeEventHistory("방문페이지 체류 : : " + stayDurationMin + " ~ " + stayDurationMax + "   " + (sleepTime / 1000) + "초");
        this.mDelayEvent.exec(sleepTime);
        if (isClick) {
            this.mEventHistory.writeEventHistory("화면 랜덤 액션 종료");
        }
    }

    public boolean clickMenuBtn() throws Exception {
        // 스크롤을 위로 이동시켜주어야만 세로 점 3개 메뉴버튼이 고정적으로 보인다.
//        scrollToTopOrBottom(false);
//        scrollToTopOrBottomDrag(1);
//        scrollToTopOrBottomDrag(1);
//
//        scrollToTopOrBottomDrag(0);
        short_scrollToTopShowMenu();
        this.mDelayEvent.exec(2000L);

        try {
            // 1번 항목을 찾지 못하기에 주석처리한다 2023.11.19 by jkseo
//            UiObject2 uiObject1 =  WaitForeObject(By.res("com.android.chrome:id/labeled_menu_button_wrapper").desc("옵션 더보기"));

            UiObject2 uiObject1 = WaitForeObject(By.res("com.android.chrome:id/menu_button").descContains("옵션 더보기"));
            if (uiObject1 != null) {
                boolean uiObjectResult = ((Boolean) uiObject1.clickAndWait(Until.newWindow(), 5000L)).booleanValue();
                EventHistory eventHistory = this.mEventHistory;
                eventHistory.writeEventHistory("메뉴버튼 클릭1 : " + uiObjectResult);
                return uiObjectResult;
            }
            else{
                UiObject2 uiObject2 = WaitForeObject(By.res("com.android.chrome:id/menu_button").descContains("Chrome 맞춤설정 및 제어"));

                if (uiObject2 != null) {
                    boolean uiObjectResult2 = ((Boolean) uiObject2.clickAndWait(Until.newWindow(), 5000L)).booleanValue();
                    EventHistory eventHistory2 = this.mEventHistory;
                    eventHistory2.writeEventHistory("메뉴버튼 클릭2 : " + uiObjectResult2);
                    return uiObjectResult2;
                }
                else
                    return false;
            }

        } catch (Exception e) {
            this.mEventHistory.writeEventHistory("메뉴버튼 클릭 error false");
            return false;
        }
    }

    public boolean clickNewTab() throws Exception {
        try {
            UiObject2 uiObject1 = WaitForeObject(By.res("com.android.chrome:id/new_tab_button_wrapper"));
            UiObject2 uiObject2 = WaitForeObject(By.res("com.android.chrome:id/new_tab_button"));
            if (uiObject1 != null) {
                boolean uiObjectResult = ((Boolean) uiObject1.clickAndWait(Until.newWindow(), 5000L)).booleanValue();
                EventHistory eventHistory = this.mEventHistory;
                eventHistory.writeEventHistory("새페이지 버튼 클릭 : " + uiObjectResult);
                return uiObjectResult;
            }
            boolean uiObjectResult2 = ((Boolean) uiObject2.clickAndWait(Until.newWindow(), 5000L)).booleanValue();
            EventHistory eventHistory2 = this.mEventHistory;
            eventHistory2.writeEventHistory("새페이지 버튼 클릭 : " + uiObjectResult2);
            return uiObjectResult2;
        } catch (Exception e) {
            this.mEventHistory.writeEventHistory("메뉴버튼 클릭 error false");
            return false;
        }
    }

    public boolean clickMenuItemSearchStringOnPage() throws Exception {
        UiObject2 uiObject = WaitForeObject(By.res("com.android.chrome:id/menu_item_text").desc("페이지에서 찾기"));
        if (uiObject != null) {
            boolean uiObjectResult = ((Boolean) uiObject.clickAndWait(Until.newWindow(), 5000L)).booleanValue();
            EventHistory eventHistory = this.mEventHistory;
            eventHistory.writeEventHistory("메뉴에서 페이지 찾기 클릭 : " + uiObjectResult);
            return uiObjectResult;
        }
        return false;
    }

    public boolean InputKeywordBySearchStringOnPage(String keywrod) throws Exception {
        UiObject2 uiObject = WaitForeObject(By.res("com.android.chrome:id/find_query"));
        if (uiObject != null) {
            EventHistory eventHistory = this.mEventHistory;
            eventHistory.writeEventHistory("페이지에서 찾기창에서 검색어 입력 : " + keywrod);

            // todo 입력하고, enter
            uiObject.setText(keywrod);
            return true;
        }
        return false;
    }

    public boolean checkResultBySearchStringOnPage() throws Exception {
        UiObject2 uiObject = WaitForeObject(By.res("com.android.chrome:id/find_status"));
        if (uiObject != null) {
            boolean uiObjectResult = ((Boolean) uiObject.clickAndWait(Until.newWindow(), 5000L)).booleanValue();
            EventHistory eventHistory = this.mEventHistory;

            eventHistory.writeEventHistory("페이지에서 찾기 결과 확인 : " + uiObject.getText());

            if (uiObject.getText().equalsIgnoreCase("0/0"))
                return false;
            else
                return true;
        }
        return false;
    }

    public boolean clickCloseMenuItemSearchStringOnPage() throws Exception {
        UiObject2 uiObject = WaitForeObject(By.res("com.android.chrome:id/close_find_button"));
        if (uiObject != null) {
            boolean uiObjectResult = ((Boolean) uiObject.clickAndWait(Until.newWindow(), 2000L)).booleanValue();
            EventHistory eventHistory = this.mEventHistory;
            eventHistory.writeEventHistory("메뉴에서 페이지 찾기 닫기 버튼 클릭 : " + uiObjectResult);
            return uiObjectResult;
        }
        return false;
    }

    public boolean clickAccessRecord() throws Exception {
        UiObject2 uiObject = WaitForeObject(By.res("com.android.chrome:id/menu_item_text").desc("방문 기록"));
        if (uiObject != null) {
            boolean uiObjectResult = ((Boolean) uiObject.clickAndWait(Until.newWindow(), 5000L)).booleanValue();
            EventHistory eventHistory = this.mEventHistory;
            eventHistory.writeEventHistory("방문 기록 클릭 : " + uiObjectResult);
            return uiObjectResult;
        }
        return false;
    }

    public boolean clickAccessRecordDeleteBtn() throws Exception {
        UiObject2 uiObject = WaitForeObject(By.res("com.android.chrome:id/clear_browsing_data_button"));
        if (uiObject != null) {
            boolean uiObjectResult = ((Boolean) uiObject.clickAndWait(Until.newWindow(), 5000L)).booleanValue();
            EventHistory eventHistory = this.mEventHistory;
            eventHistory.writeEventHistory("인터넷 사용 기록 삭제 클릭 : " + uiObjectResult);
            return uiObjectResult;
        }
        return false;
    }

    public boolean clickAccessRecordDeleteOption1() throws Exception {
        UiObject2 uiObject = WaitForeObject(By.res("com.android.chrome:id/spinner"));
        if (uiObject != null) {
            boolean uiObjectResult = ((Boolean) uiObject.clickAndWait(Until.newWindow(), 5000L)).booleanValue();
            EventHistory eventHistory = this.mEventHistory;
            eventHistory.writeEventHistory("기간 버튼 클릭 : " + uiObjectResult);
            return uiObjectResult;
        }
        return false;
    }

    public boolean clickAccessRecordDeleteOption2() throws Exception {
        UiObject2 uiObject = WaitForeObject(By.res("android:id/text1").text("전체 기간"));
        if (uiObject != null) {
            boolean uiObjectResult = ((Boolean) uiObject.clickAndWait(Until.newWindow(), 5000L)).booleanValue();
            EventHistory eventHistory = this.mEventHistory;
            eventHistory.writeEventHistory("전체 기간 클릭 : " + uiObjectResult);
            return uiObjectResult;
        }
        return false;
    }

    public boolean clickAccessRecordDelete() throws Exception {
        UiObject2 uiObject = WaitForeObject(By.res("com.android.chrome:id/clear_button").text("인터넷 사용 기록 삭제"));
        if (uiObject != null) {
            boolean uiObjectResult = ((Boolean) uiObject.clickAndWait(Until.newWindow(), 5000L)).booleanValue();
            EventHistory eventHistory = this.mEventHistory;
            eventHistory.writeEventHistory("인터넷 사용 기록 삭제 클릭 : " + uiObjectResult);
            return uiObjectResult;
        }
        return false;
    }

    public boolean clickAccessRecordDelete1() throws Exception {
        UiObject2 uiObject = WaitForeObject(By.res("android:id/button1").text("삭제"));
        if (uiObject != null) {
            boolean uiObjectResult = ((Boolean) uiObject.clickAndWait(Until.newWindow(), 5000L)).booleanValue();
            EventHistory eventHistory = this.mEventHistory;
            eventHistory.writeEventHistory("삭제 클릭 클릭 : " + uiObjectResult);
            return uiObjectResult;
        }
        return false;
    }

    public boolean clickSettingMenuItem() throws Exception {

        //com.android.chrome:id/preferences_id
        UiObject2 uiObject = WaitForeObject(By.res("com.android.chrome:id/menu_item_text").desc("설정"));
        if (uiObject != null) {
            boolean uiObjectResult = ((Boolean) uiObject.clickAndWait(Until.newWindow(), 5000L)).booleanValue();
            EventHistory eventHistory = this.mEventHistory;
            eventHistory.writeEventHistory("설정 클릭 : " + uiObjectResult);
            return uiObjectResult;
        }

        return false;
    }

    public boolean clickSearchEngine() throws Exception {
        //com.android.chrome:id/preferences_id
        UiObject2 uiObject = WaitForeObject(By.res("com.android.chrome:id/title").desc("검색엔진"));
        if (uiObject != null) {
            boolean uiObjectResult = ((Boolean) uiObject.clickAndWait(Until.newWindow(), 5000L)).booleanValue();
            EventHistory eventHistory = this.mEventHistory;
            eventHistory.writeEventHistory("검색엔진 클릭 : " + uiObjectResult);
            return uiObjectResult;
        }

        // RecyclerView를 찾습니다.
        UiScrollable recyclerView = new UiScrollable(new UiSelector().className("com.android.chrome:id/recycler_view"));
        UiCollection recyclerViewItems = new UiCollection(new UiSelector().className("android.widget.TextView"));

        int itemCount = recyclerViewItems.getChildCount(new UiSelector());
        int titleCount = recyclerView.getChildCount(new UiSelector().resourceId("android:id/title"));
        int sumarmyCount = recyclerView.getChildCount(new UiSelector().resourceId("android:id/summary"));

        //UiObject uiItem = recyclerView.getChildByDescription(new UiSelector().description("");

// 원하는 특정 텍스트가 포함된 TextView 객체를 찾습니다.
        String targetText = "찾고자 하는 텍스트";
        UiObject2 targetTextView = null;

        for (int i = 0; i < itemCount; i++) {
            UiObject textView = recyclerViewItems.getChildByInstance(new UiSelector().className("android.widget.TextView"), i);
            if (textView != null && textView.getText() != null && textView.getText().contains(targetText)) {
                Log.d("jkseo", "find item");
                break;
            }
        }

// 원하는 TextView 객체를 찾았는지 확인합니다.
        if (targetTextView != null) {
            // 원하는 작업을 수행합니다.
            targetTextView.click(); // 예시로 TextView를 클릭하는 동작을 수행합니다.
        } else {
            // 원하는 TextView를 찾지 못한 경우에 대한 처리를 수행합니다.
        }

        return false;
    }

    public boolean clickSelectSearchEngine(String engineName) throws Exception {
        //com.android.chrome:id/name
        UiObject2 uiObject = WaitForeObject(By.res("com.android.chrome:id/name").desc(engineName));
        if (uiObject != null) {
            boolean uiObjectResult = ((Boolean) uiObject.clickAndWait(Until.newWindow(), 5000L)).booleanValue();
            EventHistory eventHistory = this.mEventHistory;
            eventHistory.writeEventHistory("검색엔진 클릭 " + engineName + " : " + uiObjectResult);
            return uiObjectResult;
        }
        return false;
    }

    public boolean clickTabSwitchBtn() throws Exception {
        this.mEventHistory.writeEventHistory("페이징 버튼 클릭");
        int xPosition = this.mDeviceWidth / 2;
        int yEndPosition = this.mMacroHeight / 4;
        int yStartPosition = this.mMacroHeight / 2;
        UiObject2 uiObject1 = WaitForeObject(By.res("com.android.chrome:id/tab_switcher_button_wrapper"));
        UiObject2 uiObject2 = WaitForeObject(By.res("com.android.chrome:id/tab_switcher_button"));
        if (uiObject1 == null && uiObject2 == null) {
            this.mDevice.drag(xPosition, yEndPosition, xPosition, yStartPosition, 10);
            UiObject2 uiObject12 = WaitForeObject(By.res("com.android.chrome:id/tab_switcher_button_wrapper"));
            UiObject2 uiObject22 = WaitForeObject(By.res("com.android.chrome:id/tab_switcher_button"));
            if (uiObject12 == null && uiObject22 == null) {
                this.mDevice.drag(xPosition, yStartPosition, xPosition, yEndPosition, 10);
                this.mDelayEvent.exec(1000L);
                return false;
            } else if (uiObject12 != null) {
                boolean uiObjectResult = ((Boolean) uiObject12.clickAndWait(Until.newWindow(), 5000L)).booleanValue();
                return uiObjectResult;
            } else {
                boolean uiObjectResult2 = ((Boolean) uiObject22.clickAndWait(Until.newWindow(), 5000L)).booleanValue();
                return uiObjectResult2;
            }
        } else if (uiObject1 != null) {
            boolean uiObjectResult3 = ((Boolean) uiObject1.clickAndWait(Until.newWindow(), 5000L)).booleanValue();
            return uiObjectResult3;
        } else {
            boolean uiObjectResult4 = ((Boolean) uiObject2.clickAndWait(Until.newWindow(), 5000L)).booleanValue();
            // 클릭을 해도 무조건 false 가 반환된다.;;
            //return uiObjectResult4;

            if(uiObject2 != null)
                return true;
            else return false;
        }
    }

    public boolean clickAllClose() throws Exception {
        UiObject2 uiObject = WaitForeObject(By.res("com.android.chrome:id/menu_item_text").desc("탭 모두 닫기"));
        if (uiObject != null) {
            boolean uiObjectResult = ((Boolean) uiObject.clickAndWait(Until.newWindow(), 5000L)).booleanValue();
            EventHistory eventHistory = this.mEventHistory;
            eventHistory.writeEventHistory("모두 닫기 버튼 클릭 : " + uiObjectResult);

            // 확인메세지가 떳지만 새창인지 확인을 못하는 경우가 버전에 따라서 발생한다.
            // 모든 탭을 닫을까요? 확인메세지
            if(uiObjectResult){
//                com.android.chrome:id/positive_button
                UiObject2 uiObject2 = WaitForeObject(By.res("com.android.chrome:id/positive_button"));

                if(uiObject2 != null) {
                    boolean uiObjectResult3 = ((Boolean) uiObject2.clickAndWait(Until.newWindow(), 5000L)).booleanValue();
                    eventHistory.writeEventHistory("모두 닫기 확인 버튼 클릭 : " + uiObjectResult3);
                }
//                아래로직 못찾음 ㅠㅠ
//                UiObject2 uiObject2 = WaitForeObject(By.res("com.android.chrome:id/menu_item_text").desc("탭 모두 닫기"));
            }
            else{
                UiObject2 uiObject2 = WaitForeObject(By.res("com.android.chrome:id/positive_button"));

                if(uiObject2 != null) {
                    eventHistory.writeEventHistory("모두 닫기 확인 버튼 강제 클릭 : " + uiObjectResult);
                    uiObject2.clickAndWait(Until.newWindow(), 5000L);
                    uiObjectResult = true;
                }
                else{
                    eventHistory.writeEventHistory("모두 닫기 확인 버튼 강제 클릭 : false ");
                    uiObjectResult = false;
                }
            }

            return uiObjectResult;
        }
        return false;
    }

    public boolean randomClick(String domain) throws Exception {
        this.mEventHistory.writeEventHistory("랜덤클릭 실행");

        Boolean result = false;
        File basePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/MacroApp/ScreenDump");

        if (!basePath.exists()) {
            basePath.mkdirs();
        }

        File dumpFile = new File(basePath.getPath() + "/" + "dump.xml");

        try {
            mDevice.dumpWindowHierarchy(dumpFile);

            InputStream is = new FileInputStream(dumpFile);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();

            // XML 파서에 파일 스트림 지정.
            parser.setInput(is, "UTF-8");

            ArrayList<ClickAddresItem> clickItems = GetAddresItemOfDumpXML(parser);

            // 클릭 체크 횟수
            int checkCount = 5;
            String currUrl = GetChromeUrl();
            String changeUrl = "";

            // 덤프자료를 4등분으로 나누어 랜덤으로 클릭이벤트를 수행해본다.
            // (사이트 최상단에는 대부분 로고가 있어서 로고를 클릭하여 메인으로 돌아가버림을 방지하기 위함)
            int devide = clickItems.size()/4;
            int rndStartY = (int)randomRange(1L,4L);
            int startI = devide*(rndStartY-1);

            int minTabSize = 0;
            if(isScrollTop)
                minTabSize = 160;
            else
                minTabSize = 50;

            this.mEventHistory.writeEventHistory("시작 위치 : " + String.valueOf(startI));
            for (int i = startI; i < clickItems.size(); i++) {
                //todo 5번정도 클릭이후 주소가 바뀌는지 확인해야한다.
                ClickAddresItem item = clickItems.get(i);
                if(item.getCaption() == null || item.getCaption().length() == 0)
                    continue;

                // Tabbar 는 클릭하지 못하도록 제외
                if(item.getTop() > minTabSize){
                    this.mEventHistory.writeEventHistory("클릭 위치 : " + item.getCaption());

                    this.mDevice.click(item.getLeft(), item.getTop());
                    this.mDelayEvent.exec(2000L);
                }

                if(i>0 && i%checkCount == 0)
                {
                    changeUrl = GetChromeUrl();

                    //주소확인하고 변경되었으면, 클릭종료
                    //단, 도메인을 이동한경우 원래도메인으로 돌아오도록 처리한다.
                    if(!currUrl.equalsIgnoreCase(changeUrl))
                    {
                        if(!changeUrl.contains(domain))
                        {
                            this.mEventHistory.writeEventHistory("다른 도메인주소로 이동되어 뒤로 이동합니다. : " + domain + " => " + changeUrl);
                            this.mDevice.pressBack();
                            this.mDelayEvent.exec(2000L);

                            changeUrl = GetChromeUrl();

                            // 안드로이드 상점등으로 이동하여 주소값이 안나오는 경우 다시한번 뒤로가기 처리한다
                            if(changeUrl == null || changeUrl.length() == 0)
                            {
                                this.mDevice.pressBack();
                                this.mDelayEvent.exec(2000L);

                                changeUrl = GetChromeUrl();
                            }

                            continue;
                        }
                        result = true;
                        this.mEventHistory.writeEventHistory("페이지 이동 : " + currUrl + " => " + changeUrl);
                        break;
                    }
                }
            }

            this.mEventHistory.writeEventHistory("랜덤클릭 결과 : " + String.valueOf(result));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean CloseAlarm_NetworkError(String findWindowText) throws Exception
    {
        this.mEventHistory.writeEventHistory("알림 - 네트워크 등록실패창 종료하기 : " + findWindowText);

        Boolean result = false;
        File basePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/MacroApp/ScreenDump");

        if (!basePath.exists()) {
            basePath.mkdirs();
        }

        File dumpFile = new File(basePath.getPath() + "/" + "dump.xml");

        try {
            mDevice.dumpWindowHierarchy(dumpFile);

            InputStream is = new FileInputStream(dumpFile);

            // android:id/button1
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();

            // XML 파서에 파일 스트림 지정.
            parser.setInput(is, "UTF-8");
            result = ClickOfDumpXML(parser,findWindowText);
            this.mEventHistory.writeEventHistory("알림 - 네트워크 등록실패창 종료하기 결과 : "+ String.valueOf(result));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public void ChromeDump() throws Exception{
        Boolean result = false;
        File basePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/MacroApp/ScreenDump");
        if (!basePath.exists()) {
            basePath.mkdirs();
        }

        File dumpFile = new File(basePath.getPath() + "/" + "dump.xml");

        mDevice.dumpWindowHierarchy(dumpFile);
        InputStream is = new FileInputStream(dumpFile);
    }
}
