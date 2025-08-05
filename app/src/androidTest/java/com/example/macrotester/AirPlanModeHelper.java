package com.example.macrotester;

import android.content.Context;
import android.provider.Settings;

import UtilGroup.EventHistory;
import UtilGroup.ShellExecuter;

public class AirPlanModeHelper {
    public Context mContext;
    public EventHistory mEventHistory;

    public  AirPlanModeHelper(Context ctx, EventHistory eventHistory){
        mContext = ctx;
        mEventHistory = eventHistory;
    }
    public int getAirplaneMode() throws Exception {
        try {
//            this.mContext = InstrumentationRegistry.getContext();
            return Settings.System.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0);
        } catch (Exception e) {
            EventHistory eventHistory = this.mEventHistory;
            eventHistory.writeEventHistory("비행기모드 상태확인 오류발생 : " + e.getMessage());
            return 0;
        }
    }

    public void setFingerAirplane(boolean isMode) throws Exception {
        EventHistory eventHistory = this.mEventHistory;
        eventHistory.writeEventHistory("비행기모드 (손가락방식): " + isMode);
        ShellExecuter shellExecuter = new ShellExecuter(this.mEventHistory);
        shellExecuter.execFingerAirplaneMode(isMode);
    }

    public void setCommandAirplane(boolean isMode) throws Exception {
        EventHistory eventHistory = this.mEventHistory;
        eventHistory.writeEventHistory("비행기모드 (명령어방식): " + isMode);
        ShellExecuter shellExecuter = new ShellExecuter(this.mEventHistory);

//        shellExecuter.execCommandAirplanMode(isMode);
        shellExecuter.execCheckFileDescriptorCommandAirplanMode(isMode);
    }
}
