package com.example.macrotester;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.example.macrotester.LoaderActivity;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "자동시작입니다.!", Toast.LENGTH_SHORT).show();

        // android os 12 이상부터는 pending intent 를 사용하여 자동실행 처리한다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Intent i = new Intent(context, LoaderActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

            try {
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        } else {
            Intent i = new Intent(context, LoaderActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            try {
                context.startActivity(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}