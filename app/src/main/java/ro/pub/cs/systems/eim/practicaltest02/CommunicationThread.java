package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Hashtable;

/**
 * Created by Apex on 13/05/2018.
 */

public class CommunicationThread extends Thread
{
    Socket clientSocket;
    PrintWriter printWriter;
    BufferedReader bufferedReader;
    ServerThread serverThread;


    public CommunicationThread(ServerThread serverThread, Socket clientSocket)
    {
        this.serverThread = serverThread;
        this.clientSocket = clientSocket;
    }



    @Override
    public void run() {
        super.run();

        try {
            printWriter = new PrintWriter(new BufferedOutputStream(clientSocket.getOutputStream()), true);
            bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String tag = bufferedReader.readLine();

            Message m = new Message(serverThread.getInformation(tag));

            printWriter.println(Message.toString(m));
            printWriter.flush();

            clientSocket.close();


            Log.d("QQ", "[Server] Client disconnected");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
