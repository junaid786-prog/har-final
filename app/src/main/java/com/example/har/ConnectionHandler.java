package com.example.har;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ConnectionHandler implements IMessageDelegator {
    private static final String TAG = "ConnectionHandler";
    private static final int TIMEOUT = 5000; // Timeout for socket operations

    private Socket socket;
    private BufferedWriter writer;

    public boolean connect(String serverUrl, int serverPort) {
        try {
            System.out.println("Connecting");
            socket = new Socket(serverUrl, serverPort);
            socket.setSoTimeout(TIMEOUT);

            System.out.println("Connected");
            //OutputStream outputStream = socket.getOutputStream();
            //writer = new BufferedWriter(new OutputStreamWriter(outputStream));

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
            Log.e(TAG, "Error connecting to server: " + e.getMessage());
            return false;
        }
    }

    public void sendData(String data) {
        try {
            if (socket != null && socket.isConnected() && writer != null) {
                writer.write(data);
                writer.flush();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error sending data: " + e.getMessage());
        }
    }

    public void sendMessage(String message){
        sendData(message);
    }
    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error disconnecting: " + e.getMessage());
        }
    }
}
