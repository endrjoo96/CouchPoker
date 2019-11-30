package com.example.couchpoker.custom_controls;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.couchpoker.MainActivity;
import com.example.couchpoker.R;

import java.net.InetAddress;
import java.util.function.Consumer;

public class AvailableServer {
    public View getView() {
        return view;
    }

    private View view;
    private InetAddress ipAddress;
    private String name;
    private int ID;

    public Consumer<InetAddress> onButtonPress;

    public AvailableServer(String name, InetAddress ipAddress, int ID){
        this(name, ipAddress);
        this.ID=ID;
        System.out.println("Created new control: "+ID);
    }

    public AvailableServer(String name, InetAddress ipAddress){
        this.ipAddress = ipAddress;
        this.name = name;
        view = MainActivity.inflater.inflate(R.layout.custom_control_available_server, new LinearLayout(MainActivity.context));

        ((TextView)view.findViewById(R.id.textView_serverName)).setText(name);
        ((TextView)view.findViewById(R.id.textView_ipAddress)).setText(getIpAddressToString());
        view.findViewById(R.id.button_serverSelected).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickHandler(v);
            }
        });
    }

    public String getIpAddressToString(){
        if(ipAddress==null) return "null";
        return ipAddress.toString().substring(1);
    }

    public InetAddress getIpAddress(){
        return ipAddress;
    }

    public String getName(){
        return name;
    }

    private void onClickHandler(View v){
        if(onButtonPress!=null){
            onButtonPress.accept(getIpAddress());
        }
        else System.out.println("No action set to onButtonPress.");
    }
}
