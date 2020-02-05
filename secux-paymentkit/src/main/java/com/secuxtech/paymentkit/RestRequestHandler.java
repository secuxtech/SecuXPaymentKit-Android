package com.secuxtech.paymentkit;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RestRequestHandler {

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

    public String processGetRequest(String path, String authorization) {
        String response = "";
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestProperty("Authorization", "Basic " + authorization);
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();

            // 获得返回值
            InputStream in = connection.getInputStream();
            response = getResponse(in);
            Log.i("response", response);

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public String processPostRequest(String path) {
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

            InputStream in = connection.getInputStream();
            response = getResponse(in);

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public String processPostRequest(String path, JSONObject param) {
        String paramStr = param.toString();
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


            InputStream in = connection.getInputStream();
            response = getResponse(in);

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
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
