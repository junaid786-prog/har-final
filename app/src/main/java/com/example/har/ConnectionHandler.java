package com.example.har;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

public class ConnectionHandler implements IMessageDelegator {
    private static final String TAG = "ConnectionHandler";
    private static final int TIMEOUT = 5000; // Timeout for socket operations

    private Socket socket;
    private BufferedWriter writer;
    Context context;
    Handler handler;

    ConnectionHandler(Context ctx, Handler handler){
        this.context = ctx;
        this.handler = handler;
    }
    public void connect(String host, int port) {
        new ConnectTask().execute(host, String.valueOf(port));
    }

    private class ConnectTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String host = params[0];
            int port = Integer.parseInt(params[1]);
            try {
                Log.d(TAG, "Connecting to: " + host + " with port: " + port);
                socket = new Socket(host, port);
                socket.setSoTimeout(TIMEOUT);
                handler.obtainMessage(Constants.SOCKET_CONNECTED).sendToTarget();
                Log.d(TAG, "Connected");

                return true;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e(TAG, "Error: Malformed URL");
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                handler.obtainMessage(Constants.SOCKET_ERROR).sendToTarget();
                Log.e(TAG, "Error connecting to server: " + e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // Handle the result if needed
        }
    }

    // AsyncTask to send data in the background
    private class SendDataTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String data = params[0];
            try {
                OutputStream outputStream = socket.getOutputStream();
                Log.d("Sensor", "Sensor Data: Before BufferedWriter" );
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));


                if (socket != null && socket.isConnected() && writer != null) {
                    writer.write(data);
                    writer.flush();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error sending data: " + e.getMessage());
            }
            return null;
        }
    }

    public void sendDataInBackground(String data) {
        new SendDataTask().execute(data);
    }

    public void sendMessage(String message) {
        sendDataInBackground(message);
    }

    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                handler.obtainMessage(Constants.SOCKET_DISCONNECTED).sendToTarget();
                socket.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error disconnecting: " + e.getMessage());
        }
    }
}
