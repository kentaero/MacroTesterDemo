package UtilGroup;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.interpolator.view.animation.FastOutLinearInInterpolator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import kotlinx.coroutines.selects.WhileSelectKt;

public class ApiHelper{
    private boolean finish = false;
    private String apiResponse = "aaa";
    private final int LIMIT_RESPONSE_TIME = 60;
    private int responseCnt = 0;
    public ApiHelper() {

        try{
            DisableSSL();
        }
        catch (Exception ex){
            Log.e("jkseo", "DisableSSl error:" + ex.getMessage());
        }
    }
    public String CallRestApiUntillDebug(String hostName, String methodType, String api)  throws Exception {
        responseCnt = 0;
        apiResponse = "";
        finish = false;

        Thread thread = new Thread() {
            public void run() {
                BufferedReader reader = null;
                try{
                    URL url = new URL(api);
                    HttpsURLConnection myConnection = (HttpsURLConnection) url.openConnection();

                    myConnection.setConnectTimeout(5000);      // 5 second
                    myConnection.setRequestMethod("GET");
                    myConnection.setRequestProperty("User-Agent", hostName);
                    myConnection.setRequestProperty("Accept",
                            "application/vnd.github.v3+json");
                    myConnection.setDoInput(true);

                    String responseString = "";

                    if (myConnection.getResponseCode() == 200) {
                        InputStream responseBody = myConnection.getInputStream();
                        InputStreamReader responseBodyReader =
                                new InputStreamReader(responseBody, "UTF-8");

                        reader = new BufferedReader(responseBodyReader);
                        StringBuffer buffer = new StringBuffer();
                        int read;
                        char[] chars = new char[1024];
                        while ((read = reader.read(chars)) != -1)
                            buffer.append(chars, 0, read);


                        responseString = buffer.toString();

                        // debug
                        buffer = null;
                        reader.close();
                        responseBodyReader.close();
                        responseBody.close();
                    } else {

                        responseString = "fail";
                    }
                    apiResponse = responseString;

                    // debug
//                    myConnection.disconnect();
                    finish = true;

                    if(reader != null)
                    {
                        reader.close();
                        reader = null;
                    }
                }
                catch(IOException  ex){
                    apiResponse = "";
                    finish = true;

                    Log.e("jkseo", "CallRestApiUntill Error : " + ex.getMessage());
                    // 상위 메소드로 예외를 던짐
                    throw new RuntimeException("REST API 호출 중 오류 발생", ex);
                }
            }
        };

        thread.start();

        while(!WatingUntill())
        {

        }

        // Thread에서 발생한 예외를 여기서 처리하여 상위 메소드로 반환
        if (thread.getState() == Thread.State.TERMINATED && !apiResponse.equals("fail") && apiResponse.isEmpty()) {
            throw new Exception("REST API 호출 중 오류 발생");
        }

        return apiResponse;
    }

    public String CallRestApiUntill(String hostName, String methodType, String api)  {
        responseCnt = 0;
        apiResponse = "";
        finish = false;

        new Thread() {
            public void run() {
                BufferedReader reader = null;
                try{
                    URL url = new URL(api);
                    HttpsURLConnection myConnection = (HttpsURLConnection) url.openConnection();

                    myConnection.setConnectTimeout(5000);      // 5 second
                    myConnection.setRequestMethod("GET");
                    myConnection.setRequestProperty("User-Agent", hostName);
                    myConnection.setRequestProperty("Accept",
                            "application/vnd.github.v3+json");
                    myConnection.setDoInput(true);

                    String responseString = "";

                    if (myConnection.getResponseCode() == 200) {
                        InputStream responseBody = myConnection.getInputStream();
                        InputStreamReader responseBodyReader =
                                new InputStreamReader(responseBody, "UTF-8");

                        reader = new BufferedReader(responseBodyReader);
                        StringBuffer buffer = new StringBuffer();
                        int read;
                        char[] chars = new char[1024];
                        while ((read = reader.read(chars)) != -1)
                            buffer.append(chars, 0, read);


                        responseString = buffer.toString();

                        // debug
                        buffer = null;
                        reader.close();
                        responseBodyReader.close();
                        responseBody.close();
                    } else {

                        responseString = "fail";
                    }
                    apiResponse = responseString;

                    // debug
                    myConnection.disconnect();
                }
                catch(Exception ex){
                    apiResponse = "";
                    Log.e("jkseo", "CallRestApiUntill Error : " + ex.getMessage());
                }
                finally {
                    finish = true;

                    if(reader != null)
                    {
                        try{
                            reader.close();
                            reader = null;
                        }
                        catch (IOException ioe1){
                            Log.e("jkseo", "CallRestApiUntill Error : " + ioe1.getMessage());

                        }
                    }
                }
            }
        }.start();

        while(!WatingUntill())
        {

        }

        return apiResponse;
    }

    private boolean WatingUntill()
    {
        try{
            if(!finish)
                Thread.sleep(1000L);
        }
        catch (Exception ex){

        }

        return finish;
    }

    private void DisableSSL() throws Exception{
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType){
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }
}
