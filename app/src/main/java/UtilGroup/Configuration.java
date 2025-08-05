package UtilGroup;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.metrics.Event;
import android.util.Log;

import androidx.preference.PreferenceManager;

public class Configuration {
    public Context contextOfApplication;
    private EventHistory mEventHistory;
    private final String TAG = "CONFIG";

    public  Configuration(Context context, EventHistory eventHistory){

        this.contextOfApplication = context;
        this.mEventHistory = eventHistory;
    }

    public void setItem(String key, String value) {
        Log.d( TAG, "Put " + key +" (value : " + value + " )");

//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.contextOfApplication);
        SharedPreferences preferences = contextOfApplication.getSharedPreferences("NAT_Macrotester", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
        editor.commit();
    }

    public void setItem(String key, Boolean value) {
//        Log.d( TAG, "jkseo Put " + key +" (value : " + value + " )");

//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.contextOfApplication);
        SharedPreferences preferences = contextOfApplication.getSharedPreferences("NAT_Macrotester", MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
        editor.commit();
    }

    public String getItem(String key) {
//        Log.d(TAG, "jkseo Get " + key);
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.contextOfApplication);
        SharedPreferences preferences = contextOfApplication.getSharedPreferences("NAT_Macrotester", MODE_PRIVATE);

        return preferences.getString(key, "");
    }

    public boolean GetBooleanItem(String key) {
        Log.d(TAG, "jkseo Get " + key);
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.contextOfApplication);
        SharedPreferences preferences = contextOfApplication.getSharedPreferences("NAT_Macrotester", MODE_PRIVATE);

        return preferences.getBoolean(key, false);
    }
}
