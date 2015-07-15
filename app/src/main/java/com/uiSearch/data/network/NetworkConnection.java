package com.uiSearch.data.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class NetworkConnection implements Callable<String> {

    private static final String CONTENT_TYPE_LABEL = "Content-Type";
    private static final String CONTENT_TYPE_VALUE_JSON = "application/json; charset=utf-8";
    private static final int CONNECTION_TIMEOUT=15000;
    private static final int READ_TIMEOUT=10000;

    private URL url;
    private String response;
    private String TAG=getClass().getCanonicalName();

    public NetworkConnection(){
    }

    public void setUrl(String url) throws MalformedURLException {
        this.url = new URL(url);
    }

    public NetworkConnection(String url) throws MalformedURLException {
        this.url = new URL(url);
    }

    private String getResponseFromApi() {
        try {
            HttpURLConnection connection=createGetCient();
            Log.d(TAG,"Response Code: " + connection.getResponseCode());
            InputStream in = new BufferedInputStream(connection.getInputStream());
            response = getStringFromInputStream(in);
            Log.d(TAG,"response from server:"+response);
        } catch (IOException e) {
            e.printStackTrace();
        }
          return response;
    }

    private HttpURLConnection createGetCient() throws  IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", "");
        conn.setRequestMethod("GET");
        conn.setRequestProperty(CONTENT_TYPE_LABEL,
                CONTENT_TYPE_VALUE_JSON );
        conn.setConnectTimeout(CONNECTION_TIMEOUT);
        conn.setReadTimeout(READ_TIMEOUT);
        return conn;
    }

    private String getStringFromInputStream(InputStream inputStream) {
        String response = "";
        try {
            String line;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    inputStream, "iso-8859-1"), 8);
            StringBuilder stringbuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringbuilder.append(line + "\n");
            }
            inputStream.close();
            response = stringbuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override public String call() throws Exception {
        if(url==null)
            throw new MalformedURLException("No url set");
        return  getResponseFromApi();
    }

   }