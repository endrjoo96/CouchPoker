package com.example.couchpoker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.couchpoker.custom_controls.AvailableServer;
import com.example.couchpoker.networking.TCPConnection;
import com.example.couchpoker.networking.UDPReceiver;

import java.net.InetAddress;
import java.net.Socket;

import static com.example.couchpoker.files.Settings.isFileCorrect;
import static com.example.couchpoker.files.Settings.saveUsername;

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

        if(!isFileCorrect()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            View v = inflater.inflate(R.layout.layout_nickname_popup, null);

            Dialog dialog = builder.setView(v).create();
            //Dialog dialog = builder.setView(new View(this)).create();
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.copyFrom(dialog.getWindow().getAttributes());
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
            dialog.show();
            dialog.getWindow().setAttributes(params);

            Button acceptButton = v.findViewById(R.id.button_fromDialog_acceptUsername);
            EditText usernameEditText = v.findViewById(R.id.editText_fromDialog_username);

            acceptButton.setEnabled(false);

            usernameEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(usernameEditText.getText().toString().length()<3) acceptButton.setEnabled(false);
                    else acceptButton.setEnabled(true);
                }

                @Override
                public void afterTextChanged(Editable editable) { }
            });
            acceptButton.setOnClickListener((view)->{
                String username = (usernameEditText.getText().toString());
                saveUsername(username);
                dialog.dismiss();
            });
            dialog.setOnKeyListener(new Dialog.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface arg0, int keyCode,
                                     KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) { }
                    return true;
                }
            });
        }

        receiver = new UDPReceiver();
        receiver.dataReceived = this::onServerBroadcastReceive;
        receiver.runReceiver();

        Button b = findViewById(R.id.button_config);
        b.setOnClickListener((View v)->{
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            lp.setMargins(20,0,20,0);
            LinearLayout layout = new LinearLayout(this);
            EditText editText = new EditText(layout.getContext());
            editText.setLayoutParams(lp);
            layout.addView(editText);


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            Dialog dialog = builder.setView(layout).setTitle("Zmień swoją nazwę").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String username = (editText.getText().toString());
                    if(username.length()<3){
                        Toast.makeText(context, "Nazwa musi zawierać minimum 3 znaki", Toast.LENGTH_LONG).show();
                        return;
                    }
                    saveUsername(username);
                    Toast.makeText(context, "Nazwa zmieniona na: "+username, Toast.LENGTH_LONG).show();
                    dialogInterface.dismiss();
                }
            }).setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create();
            dialog.show();
        });
    }

    private void onConnectingToServer(InetAddress ipAddress){
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
            LinearLayout linearLayout = findViewById(R.id.serverList_linearLayout);
            runOnUiThread(()->{linearLayout.removeAllViews();});
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
