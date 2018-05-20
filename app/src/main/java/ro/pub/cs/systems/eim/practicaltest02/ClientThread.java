package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Apex on 15/05/2018.
 */

public class ClientThread extends Thread
{
    String ipAddress;
    int port;
    PrintWriter printWriter;
    BufferedReader bufferedReader;
    View resultsTextView;
    String tag;

    public ClientThread(String ipAddress, int port, View resultsTextView, String tag)
    {
        this.ipAddress = ipAddress;
        this.port = port;
        this.resultsTextView = resultsTextView;
        this.tag = tag;
    }

    @Override
    public void run() {
        super.run();


        try {
            Socket clientSocket = new Socket(ipAddress, port);

            printWriter = new PrintWriter(new BufferedOutputStream(clientSocket.getOutputStream()), true);
            bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            printWriter.println(tag);

            String recvString;
            while((recvString = bufferedReader.readLine()) != null)
            {
                try {
                    final Message m = Message.fromString(recvString);
                    Log.d("QQ", "Recv: " + m.s);

                    resultsTextView.post(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView)resultsTextView).setText(((TextView)resultsTextView).getText().toString() + " " + m.s);
                        }
                    });

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
