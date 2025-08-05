package UtilGroup;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.PortUnreachableException;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

import kotlin.text.UStringsKt;

public class NetworkHelper {
    private Context mContext;
    private Configuration mConfig;

    public NetworkHelper(Context context, Configuration config)
    {
        this.mContext = context;
        this.mConfig = config;
    }

    public String GetIPAddress(){
        return getIPAddress(this.mContext);

//        if(isNetworkAvailable()) {
//            return getIpAddress();
//        }
//        else{
//            return "";
//        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private String getIPAddress(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    return getWifiIPAddress();
                } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    return getMobileIPAddress();
                }
            }
        }
        return "N/A";
    }

    private String getWifiIPAddress() {
        WifiManager wifiManager = (WifiManager) this.mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();

        // IP 주소를 포맷팅하여 문자열로 반환
        return String.format(
                "%d.%d.%d.%d",
                (ipAddress & 0xFF),
                (ipAddress >> 8 & 0xFF),
                (ipAddress >> 16 & 0xFF),
                (ipAddress >> 24 & 0xFF)
        );
    }

    private String getMobileIPAddress() {
        try {
            // 모바일 네트워크의 IP 주소를 직접 얻는 코드 추가
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "N/A";
    }

    public void CallGetPublicIp(){
        // AsyncTask를 사용하여 네트워크 작업을 백그라운드에서 수행
        new GetPublicIpTask().execute();
    }

    private class GetPublicIpTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return getPublicIp();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null && !result.isEmpty()) {
                mConfig.setItem("result_ipaddress", result);
            } else {
                mConfig.setItem("result_ipaddress", result);
            }
        }
    }

    private String getPublicIp() {
        String apiUrl = "https://api64.ipify.org?format=json";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            String myIP = GetJsonValue( response.toString());
            return myIP;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "N/A";
    }

    private String GetJsonValue(String jsonString){
        try {
//            String jsonString = "{\"ip\":\"182.225.168.108\"}";
//            String jsonString = "{\"ip\":\"2406:5900:706b:f050:f798:13f3:d045:9488\"}";

            // Gson을 사용하여 JSON을 JsonObject로 변환
            JsonObject jsonObject = new Gson().fromJson(jsonString, JsonObject.class);

            // "ip" 키에 해당하는 값 추출
            String ipValue = jsonObject.get("ip").getAsString();

            return ipValue;
        } catch (Exception e) {
            e.printStackTrace();

            return "ERROR";
        }
    }
}
