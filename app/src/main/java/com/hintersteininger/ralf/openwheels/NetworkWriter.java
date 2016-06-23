package com.hintersteininger.ralf.openwheels;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by ralf on 09.06.16.
 */
public class NetworkWriter{

    private BufferedWriter bufferedWriter;

    public NetworkWriter(Context context) throws IOException {
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(Connection.connectionSocket.getOutputStream()));
    }

    public void write (String text) throws IOException {
        bufferedWriter.write(text+"\r\n");
        bufferedWriter.flush();
    }

    public void close () throws IOException {
        bufferedWriter.close();
    }
}
