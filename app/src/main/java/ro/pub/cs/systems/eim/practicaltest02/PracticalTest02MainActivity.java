package ro.pub.cs.systems.eim.practicaltest02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

public class PracticalTest02MainActivity extends AppCompatActivity {

    ServerThread serverThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);


        final View serverPortEditText = findViewById(R.id.serverPortEditText);
        View connectButton = findViewById(R.id.connectButton);
        connectButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if (serverThread == null) {
                    int port = Integer.parseInt(((EditText) serverPortEditText).getText().toString());
                    serverThread = new ServerThread(port);
                    serverThread.start();
                }
            }
        });

        final View addressEditText = findViewById(R.id.addressEditText);
        final View clientPortEditText = findViewById(R.id.clientPortEditText);
        final View resultTextView = findViewById(R.id.resultTextView);
        final View optionsSpinner = findViewById(R.id.optionsSpinner);

        View getForecastButton = findViewById(R.id.getForecastButton);
        getForecastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ipAddress = ((EditText)addressEditText).getText().toString();
                int port = Integer.parseInt(((EditText)clientPortEditText).getText().toString());
                String tag = ((Spinner)optionsSpinner).getSelectedItem().toString();

                ClientThread clientThread = new ClientThread(ipAddress, port, resultTextView, tag);
                clientThread.start();
            }
        });
    }

    @Override
    protected void onDestroy() {

        serverThread.stopThread();

        super.onDestroy();
    }
}
