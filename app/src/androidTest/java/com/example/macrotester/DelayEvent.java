package com.example.macrotester;

import androidx.test.uiautomator.UiDevice;

import UtilGroup.EventHistory;

/* loaded from: classes.dex */
public class DelayEvent {
    private EventHistory mEventHistory;
    private UiDevice mDevice = null;
    private int mCenterX = 0;
    private int mCenterY = 0;

    public DelayEvent(EventHistory eventHistory) {
        this.mEventHistory = eventHistory;
    }

    public void setDeviceObject(UiDevice device, int centerX, int centerY) {
        this.mDevice = device;
        this.mCenterX = centerX;
        this.mCenterY = centerY;
    }

    public void exec(long time) {
        EventHistory eventHistory = this.mEventHistory;
//        eventHistory.writeEventHistory("화면 대기 시작(" + (time / 1000) + "초)");
        if (time > 180000) {
            boolean sleepNext = true;
            while (sleepNext) {
                try {
                    Thread.sleep(180000L);
                } catch (Exception e) {
                }
                time -= 180000;
                EventHistory eventHistory2 = this.mEventHistory;
//                eventHistory2.writeEventHistory("화면 3분씩 분할 대기(" + (time / 1000) + "초)");
                if (this.mDevice != null) {
//                    this.mEventHistory.writeEventHistory("장시간 대기시 프로세스 종료방지를 위한 상태창 클릭이벤트 발생(20 , 20)");
                    this.mDevice.click(20, 20);
                }
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
}