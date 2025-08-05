package com.example.macrotester;

import android.net.Uri;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.PortUnreachableException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.jar.JarEntry;

import ModelGroup.MacroEvent;
import ModelGroup.MacroResult;
import UtilGroup.ApiHelper;
import UtilGroup.BuildConfig;
import UtilGroup.Configuration;
import UtilGroup.EventHistory;

public class MacroManager {
    private EventHistory mEventHistory;
    private Gson gson;
    private ApiHelper mApi;
    private Configuration mConfig;


    public MacroManager(Configuration config){
        mEventHistory = new EventHistory(false);
        gson = new Gson();
        mApi = new ApiHelper();
        mConfig = config;
    }


    public String ConvertToMacroEvent(ArrayList<MacroEvent> jobs){
        return gson.toJson(jobs);
    }

    public MacroResult LoadMacroResult() throws IOException{
        mEventHistory.writeEventHistory("API 호출하기 위해 저장된 데이터 불러오기");
        MacroResult event_result;
        try{
            event_result = new MacroResult(
                    Integer.valueOf(mConfig.getItem("result_comid"))
                    , Integer.valueOf(mConfig.getItem("result_deviceid"))
                    , Integer.valueOf(mConfig.getItem("result_macroidx"))
                    , mConfig.getItem("result_sitename")
            );

            event_result.setSearchResult(mConfig.GetBooleanItem("result_macro_ok"));
            event_result.setStartTime(mConfig.getItem("result_startTime"));
            event_result.setEndTime(mConfig.getItem("result_endTime"));
            event_result.setSiteWaitTime(Long.valueOf(mConfig.getItem("result_siteWaiteTime")));
            event_result.setRemarks(mConfig.getItem("result_remarks"));
            event_result.setIPAddress(mConfig.getItem("result_ipaddress"));
        }
        catch (Exception ex){
            event_result = new MacroResult(
                    Integer.valueOf(mConfig.getItem("result_comid"))
                    , Integer.valueOf(mConfig.getItem("result_deviceid"))
                    , Integer.valueOf(mConfig.getItem("result_macroidx"))
                    , mConfig.getItem("result_sitename")
            );
        }

        return event_result;
    }

    @Nullable
    public ArrayList<MacroEvent> LoadMacroDataDebug(String ComID, String deviceID) throws IOException {
        mEventHistory.writeEventHistory("매크로 목록 조회(LoadMacroDataDebug)");
        String resUrl = "https://hello.com/api/procedure_r.asp";
//        String resUrl = "https://hello.com/api/procedure_r.asp?p_device_id=" + deviceID;

        //        resUrl = "https://warningpass.com/api/wcheck/getlist/";
        Uri builtUri =
                Uri.parse(resUrl).buildUpon()
                        .appendQueryParameter("p_device_id", deviceID)
                        .appendQueryParameter("com_id", ComID)
                        .build();
        mEventHistory.writeEventHistory("매크로 목록 조회 API : " + builtUri.toString());

        String response = "";
        try {
            response = mApi.CallRestApiUntillDebug(BuildConfig.APPLICATION_ID, "", builtUri.toString());
            mEventHistory.writeEventHistory("매크로 목록 조회 응답(LoadMacroDataDebug) : " + response);

        } catch(Exception ex){
            mEventHistory.writeEventHistory("CallRestApiUntillDebug 호출 에러 : " + ex.getMessage());
        }

        if(response.length() == 0) {
            mEventHistory.writeEventHistory("LoadMacroDataDebug Size 0");
            return null;
        }

        if(response.equalsIgnoreCase("fail")){
            mEventHistory.writeEventHistory("LoadMacroDataDebug Faile");
            return null;
        }
        else{
            // todo json to class
            ArrayList<MacroEvent> jobs = new ArrayList<>();
            TypeToken<ArrayList<MacroEvent>> collectionType = new TypeToken<ArrayList<MacroEvent>>(){};
            jobs = gson.fromJson(response, collectionType);

            if(jobs.size() == 0)
            {
                mEventHistory.writeEventHistory("LoadMacroDataDebug Size 0");
                return null;
            }
            else{
                return jobs;
            }
        }
    }

    @Nullable
    public ArrayList<MacroEvent> LoadMacroData(String ComID, String deviceID) throws IOException {
        mEventHistory.writeEventHistory("매크로 목록 조회(LoadMacroData)");
        String resUrl = "https://hello.com/api/procedure_r.asp";
//        String resUrl = "https://hello.com/api/procedure_r.asp?p_device_id=" + deviceID;

        //        resUrl = "https://warningpass.com/api/wcheck/getlist/";
        Uri builtUri =
                Uri.parse(resUrl).buildUpon()
                        .appendQueryParameter("p_device_id", deviceID)
                        .appendQueryParameter("com_id", ComID)
                        .build();
        mEventHistory.writeEventHistory("매크로 목록 조회 API : " + builtUri.toString());

        String response = "";
        try {
            response = mApi.CallRestApiUntill(BuildConfig.APPLICATION_ID, "", builtUri.toString());
            mEventHistory.writeEventHistory("매크로 목록 조회 응답(LoadMacroData) : " + response);

        } catch(Exception ex){
            mEventHistory.writeEventHistory("CallRestApiUntill 호출 에러 : " + ex.getMessage());
            throw ex;
        }

        if(response.length() == 0) {
            mEventHistory.writeEventHistory("LoadMacroData Size 0");
            return null;
        }

        if(response.equalsIgnoreCase("fail")){
            mEventHistory.writeEventHistory("LoadMacroData Faile");
            return null;
        }
        else{
            // todo json to class
            ArrayList<MacroEvent> jobs = new ArrayList<>();
            TypeToken<ArrayList<MacroEvent>> collectionType = new TypeToken<ArrayList<MacroEvent>>(){};
            jobs = gson.fromJson(response, collectionType);

            if(jobs.size() == 0)
            {
                mEventHistory.writeEventHistory("LoadMacroData Size 0");
                return null;
            }
            else{
                return jobs;
            }
        }
    }

    public void     SaveMacroResult(MacroResult event) throws IOException{
        mEventHistory.writeEventHistory("매크로 결과저장");

        String remarks = URLEncoder.encode(event.getRemarks(), "UTF-8");
        String reqUrl = "https://hello.com/api/procedure_s.asp";
        Uri builtUri =
                Uri.parse(reqUrl).buildUpon()
                        .appendQueryParameter("p_macro_idx", String.valueOf(event.getMacroIdx()))
                        .appendQueryParameter("p_device_id", String.valueOf(event.getDeviceID()))
                        .appendQueryParameter("p_macro_mode", "")
                        .appendQueryParameter("p_site_wait_time", String.valueOf(event.getSiteWaitTime()))
                        .appendQueryParameter("p_start_datetime", event.getStartTime())
                        .appendQueryParameter("p_end_datetime", event.getEndTime())
                        .appendQueryParameter("p_result", String.valueOf(event.getSearchResult()))
                        .appendQueryParameter("p_remarks", remarks)
                        .appendQueryParameter("p_insert_ip_address", event.getIPAddress())
                        .appendQueryParameter("com_id", String.valueOf(event.getComID()))
                        .build();

        mEventHistory.writeEventHistory("매크로 결과저장 API : " + builtUri.toString());

        String response = "";

        try{
            response = mApi.CallRestApiUntill(BuildConfig.APPLICATION_ID, "", builtUri.toString());

            EventHistory eventHistory = this.mEventHistory;
            eventHistory.writeEventHistory("매크로 결과저장 API RESULT: " + response);

        } catch (Exception ex){
            //mEventHistory.writeEventHistory(ex.getMessage());
        }
    }
}
