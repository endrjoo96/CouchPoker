package com.example.couchpoker.custom_controls;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.couchpoker.MainActivity;
import com.example.couchpoker.R;

public class AvailableServer {
    public View getView() {
        return view;
    }

    private View view;

    public AvailableServer(String name, int freeSlotsCount){
        view = MainActivity.inflater.inflate(R.layout.custom_control_available_server, new LinearLayout(MainActivity.context));
        ((TextView)view.findViewById(R.id.server_name_text)).setText(name);
        ((TextView)view.findViewById(R.id.available_slots_text)).setText(Integer.toString(freeSlotsCount));
    }



}
