package com.example.rcvc;

import android.os.Looper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

public class Client extends Thread{

    MainActivity mainActivity;

    public Client(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    public void run(){
        connect("https://mintclub.org/");
    }

    public void connect(String args) {

        URL url;

        try {
            url = new URL(args);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            return;
        }

        String hostname = url.getHost();
        hostname = "mintclub.org";
        int port = 22222;

        try (Socket socket = new Socket(hostname, port)) {
            for(int i=0; i<5; i++) {
                Log.d("network", "hostname:" + hostname+" try:"+i);

                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                writer.println("tony is stupid");

                InputStream input = socket.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                String line;

                Log.d("network", "-------------------------");
                while ((line = reader.readLine()) != null) {
                    Log.d("network", line);
                }
            }
            socket.close();
        } catch (UnknownHostException ex) {

            Log.d("network","Server not found: " + ex.getMessage());

        } catch (IOException ex) {

            Log.d("network","I/O error: " + ex.getMessage());
        }
    }

}
