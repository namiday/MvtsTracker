package com.example.mvtstracker;

import android.util.Log;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendHttpPostRequest {
    private static final String TAG = "MainActivity";

    public void sendPost() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "sendHelloToUnityServer inside");

                    String targetUrl = "http://192.168.1.11:8080/"; // Unity server IP and port
                    URL url = new URL(targetUrl);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    try {
                        // Set up the connection properties
                        urlConnection.setRequestMethod("POST");
                        urlConnection.setDoOutput(true);
                        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                        // Send the POST data
                        OutputStream out = urlConnection.getOutputStream();
                        out.write("data=hello".getBytes());
                        out.flush();
                        out.close();

                        // Check the response code
                        int responseCode = urlConnection.getResponseCode();
                        System.out.println("POST Response Code :: " + responseCode);

                        // Optional: Handle the response...
                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}
