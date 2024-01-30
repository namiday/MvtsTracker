package com.example.mvtstracker;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class NetworkTask extends AsyncTask<Void, Void, Void> {

    private String dstAddress;
    private int dstPort;
    private static final String TAG = "MainActivity";

    public NetworkTask(String addr, int port) {
        dstAddress = addr;
        dstPort = port;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d(TAG, "doInBackground begin");
        Socket socket = null;
        try {
            socket = new Socket(dstAddress, dstPort);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("Hello from Android");
            Log.d(TAG, "doInBackground message");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}