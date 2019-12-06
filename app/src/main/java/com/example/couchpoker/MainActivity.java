package com.example.couchpoker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.autofill.AutofillValue;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.couchpoker.custom_controls.AvailableServer;
import com.example.couchpoker.networking.TCPConnection;
import com.example.couchpoker.networking.UDPReceiver;

import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static LayoutInflater inflater;
    public static Context context;
    private UDPReceiver receiver;

    public static Socket connectedSocket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inflater = (LayoutInflater) this.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = this.getApplicationContext();

        receiver = new UDPReceiver();
        receiver.dataReceived = this::onServerBroadcastReceive;

        Button b = findViewById(R.id.button_siup);
        b.setOnClickListener((View v)->{
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        });

        receiver.runReceiver();

        LinearLayout linearLayout = findViewById(R.id.serverList_linearLayout);
        /*for(int i=0; i<20; i++){
            AvailableServer srv = new AvailableServer("testsrv"+i, null);
            srv.onButtonPress = this::onConnectingToServer;
            linearLayout.addView(srv.getView());
        }*/
    }

    private void onConnectingToServer(InetAddress ipAddress){
        runOnUiThread(()->{
            //some animation or shit, need to disable availability to press other buttons anyway
        });
        new Thread(()->{
            connectedSocket = TCPConnection.getConnection(ipAddress);
            if(connectedSocket==null){
                runOnUiThread(()->{
                    Toast.makeText(this,"Server not responding", Toast.LENGTH_LONG).show();
                });
                return;
            }
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        }).start();

    }

    private void onServerBroadcastReceive(UDPReceiver.DataReceivedArgs args){
        LinearLayout linearLayout = findViewById(R.id.serverList_linearLayout);
        if(!isServerExistsAlreadyOnList(linearLayout, args))
            runOnUiThread(()->{
                AvailableServer srv = new AvailableServer(args.serverName, args._ipAddress, linearLayout.getChildCount());
                srv.onButtonPress = this::onConnectingToServer;
                linearLayout.addView(srv.getView());
            });
    }

    private boolean isServerExistsAlreadyOnList(LinearLayout listLayout, UDPReceiver.DataReceivedArgs args){
        for(int i=0; i<listLayout.getChildCount(); i++){
            if(((TextView)listLayout.getChildAt(i).findViewById(R.id.textView_serverName)).getText().equals(args.serverName) &&
                    ((TextView)listLayout.getChildAt(i).findViewById(R.id.textView_ipAddress)).getText().equals(args.ipAddress)){
                return true;
            }
        }
        return false;
    }
}
