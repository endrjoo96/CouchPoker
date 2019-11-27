package com.example.couchpoker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.example.couchpoker.custom_controls.AvailableServer;

public class MainActivity extends AppCompatActivity {
    public static LayoutInflater inflater;
    public static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inflater = (LayoutInflater) this.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = this.getApplicationContext();

        LinearLayout linearLayout = findViewById(R.id.serverList_linearLayout);

        for(int i=0; i<20; i++){
            linearLayout.addView(new AvailableServer("Server no. "+(i+1), 20-i).getView());
        }


    }
}
