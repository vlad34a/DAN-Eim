package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Apex on 13/05/2018.
 */

public class ServerThread extends Thread {

    ServerSocket serverSocket;
    int port;
    Hashtable<String, String> cacheTable = new Hashtable<String, String>();

    public ServerThread(int port)
    {
        this.port = port;
    }

    public void stopThread()
    {
        interrupt();
        try {
            serverSocket.close();
            Log.d("QQ", "[Server] Closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getInformation(String tag)
    {
        if (cacheTable.containsKey(tag))
        {
            Log.d("QQ","[Server] Info found in hashtable / cached");
            return cacheTable.get(tag);
        }
        else
        {
            Log.d("QQ","[Server] Info not found - http requesting");

            HttpClient httpClient = new DefaultHttpClient();
            ResponseHandler<String> responseHandler = new BasicResponseHandler();

            // -- cerere GET
            HttpGet httpGet = new HttpGet("https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22Bucuresti%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys");
            try {
                String response = httpClient.execute(httpGet, responseHandler);

                JSONObject content = new JSONObject(response);
                response = content.getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("wind").getString("direction");


                // -- cerere POST
                HttpPost httpPost = new HttpPost("http://www.codingvision.net");

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("attribute1", "value1"));
                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);

                httpPost.setEntity(urlEncodedFormEntity);

                String response2 = httpClient.execute(httpPost, responseHandler);
                Document document = Jsoup.parse(response2);

                response = (document.getElementsByTag("script").get(0)).data();

                cacheTable.put(tag, response);

                return cacheTable.get(tag);

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }




        }
    }

    @Override
    public void run()
    {
        super.run();

        ArrayList<CommunicationThread> clientThreads = new ArrayList<CommunicationThread>();

        try {
            serverSocket = new ServerSocket(port);

            Log.d("QQ", "[Server] Started");

            while (!Thread.currentThread().isInterrupted())
            {
                Socket clientSocket = serverSocket.accept();
                Log.d("QQ", "[Server] Client connected");
                CommunicationThread comThread = new CommunicationThread(this, clientSocket);
                comThread.start();
                clientThreads.add(comThread);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
