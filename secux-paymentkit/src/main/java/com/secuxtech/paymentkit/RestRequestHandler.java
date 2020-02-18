package com.secuxtech.paymentkit;

import android.util.Log;

import androidx.core.util.Pair;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RestRequestHandler {

    final public static String TAG = "secux-paymentkit";

    public void processURLRequest(){

        HttpURLConnection connection = null;
        try{
            URL url = new URL("https://www.xxx.com/");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);
            connection.setDoOutput(true);// 使用 URL 連線進行輸出
            connection.setDoInput(true);// 使用 URL 連線進行輸入
            connection.setUseCaches(false);// 忽略快取
// 建立輸出流，並寫入資料
            OutputStream outputStream = connection.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeBytes("username=admin&password=888888");
            dataOutputStream.close();
            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
// 當正確響應時處理資料
                StringBuffer response = new StringBuffer();
                String line;
                BufferedReader responseReader =
                        new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
// 處理響應流，必須與伺服器響應流輸出的編碼一致
                while (null != (line = responseReader.readLine())) {
                    response.append(line);
                }
                responseReader.close();
                Log.d("secux-paymentkit", response.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null!= connection) {
                connection.disconnect();
            }
        }
    }

    public Pair<Boolean, String> processGetRequest(String path, String authorization) {
        Boolean result = false;
        String response = "";
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestProperty("Authorization", "Basic " + authorization);
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();

            Integer responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                InputStream in = connection.getInputStream();
                response = getResponse(in);
                Log.i("response", response);
            }else{
                InputStream errIn = connection.getErrorStream();
                response = getResponse(errIn);
                //String errormsg = connection.getResponseMessage();
                Log.e(TAG, "Server request response code = " + response);
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            response = e.getMessage();
        }
        return new Pair<>(result, response);
    }

    public Pair<Boolean, String> processPostRequest(String path) {
        Boolean result = false;
        String response = "";
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Content-Length", "0");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            Integer responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                InputStream in = connection.getInputStream();
                response = getResponse(in);
                result = true;
            }else{
                InputStream errIn = connection.getErrorStream();
                response = getResponse(errIn);
                //String errormsg = connection.getResponseMessage();
                Log.e(TAG, "Server request response code = " + response);
            }
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            response = e.getMessage();
        }
        return new Pair<>(result, response);
    }

    public Pair<Boolean, String> processPostRequest(String path, JSONObject param) {
        String paramStr = param.toString();
        Boolean result = false;
        String response = "";
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Content-Length", String.valueOf(paramStr.length()));
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            DataOutputStream out = new DataOutputStream(connection.getOutputStream());

            out.writeBytes(paramStr);
            out.flush();
            out.close();

            Integer responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                InputStream in = connection.getInputStream();
                response = getResponse(in);
                result = true;
            }else{
                InputStream errIn = connection.getErrorStream();
                response = getResponse(errIn);
                //String errormsg = connection.getResponseMessage();
                Log.e(TAG, "Server request response code = " + response);
            }

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            response = e.getMessage();
        }

        return new Pair<>(result, response);
    }

    public Pair<Boolean, String> processPostRequest(String path, JSONObject param, String token) {
        String paramStr = param.toString();
        Boolean result = false;
        String response;
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestProperty("Charset", "UTF-8");

            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("Content-Length", String.valueOf(paramStr.length()));
            connection.connect();

            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(paramStr);
            out.flush();
            out.close();

            Integer responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                InputStream in = connection.getInputStream();
                response = getResponse(in);
                result = true;
            }else{
                InputStream errIn = connection.getErrorStream();
                response = getResponse(errIn);
                //String errormsg = connection.getResponseMessage();
                Log.e(TAG, "Server request response code = " + response);
            }

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            response = e.getMessage();
        }

        return new Pair<>(result, response);
    }

    private String getResponse(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder builder = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return builder.toString();
    }


}
