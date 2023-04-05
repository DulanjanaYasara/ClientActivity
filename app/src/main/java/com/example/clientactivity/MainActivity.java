package com.example.clientactivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    EditText serverIp, smessage;
    TextView chat;
    Button connectPhones, sent;
    String serverIpAddress = "", msg = "", str;
    Handler handler = new Handler();
    Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chat = (TextView) findViewById(R.id.chat);
        serverIp = (EditText) findViewById(R.id.server_ip);
        smessage = (EditText) findViewById(R.id.smessage);
        sent = (Button) findViewById(R.id.sent_button);
        sent.setEnabled(false);
        sent.setOnClickListener(v -> {
            if(smessage.getText() != null && !smessage.getText().toString().equals("")){
                Thread sentThread = new Thread(new sentMessage());
                sentThread.start();
            }
        });
        connectPhones = (Button) findViewById(R.id.connect_phones);
        connectPhones.setOnClickListener(v -> {
            serverIpAddress = serverIp.getText().toString();
            if (!serverIpAddress.equals("")) {
                Thread clientThread = new Thread(new
                        ClientThread());
                clientThread.start();
            }
        });
    }

    class sentMessage implements Runnable {
        @Override
        public void run() {
            try {
                DataOutputStream os = new
                        DataOutputStream(socket.getOutputStream());
                str = smessage.getText().toString();
                str = str + "\n";
                msg = msg + "Client  : " + str;
                handler.post(() -> {
                    chat.setText(msg);
                    smessage.setText("");
                });
                os.writeBytes(str);
                os.flush();

            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public class ClientThread implements Runnable {
        @Override
        public void run() {
            try {
                InetAddress serverAddr =
                        InetAddress.getByName(serverIpAddress);
                socket = new Socket(serverAddr, 10000); //create client socket
                handler.post(() ->{
                    chat.setText("Connected to server");
                    smessage.setEnabled(true);
                    sent.setEnabled(true);
                } );

                /*******************************************
                 setup i/p streams
                 ******************************************/
                DataInputStream in = new
                        DataInputStream(socket.getInputStream());
                String line = null;
                while ((line = in.readLine()) != null) {
                    msg = msg + "Server : " + line + "\n";
                    handler.post(() -> chat.setText(msg));
                }
                in.close();
                socket.close();

            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}