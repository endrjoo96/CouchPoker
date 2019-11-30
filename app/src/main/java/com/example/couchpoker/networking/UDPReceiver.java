package com.example.couchpoker.networking;

import android.app.Activity;

import com.example.couchpoker.MainActivity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.Consumer;

public class UDPReceiver {

    public class DataReceivedArgs{
        public String serverName;
        public String ipAddress;
        public InetAddress _ipAddress;

        DataReceivedArgs(String msg, InetAddress ipAddress){
            this.serverName=msg.substring(key.length());
            this.ipAddress = ipAddress.toString().substring(1);
            _ipAddress = ipAddress;
        }
    }

    private String key = "CouchPoker_Server|";
    public Consumer<DataReceivedArgs> dataReceived;
    private Thread t;
    private DatagramSocket socket;
    private int bufferLength=1024;
    private boolean stopReceiver=false;

    public UDPReceiver(){
        try {
            socket = new DatagramSocket(25051);
        } catch (java.net.SocketException socketex){
            socketex.printStackTrace();
            return;
        }

        t = new Thread(()->{
            byte[] tmpData = new byte[bufferLength];
            DatagramPacket packet = new DatagramPacket(tmpData, bufferLength);
            while (!stopReceiver) {
                try {
                    socket.receive(packet);
                    byte[] data = new byte[packet.getLength()];
                    System.arraycopy(tmpData, packet.getOffset(), data, 0, packet.getLength());
                    String message = new String(data, StandardCharsets.UTF_8);
                    if (dataReceived != null && isFromThisSystem(message) ) {
                        dataReceived.accept(new DataReceivedArgs(message, packet.getAddress()));
                    }
                } catch (IOException ioex) {
                    ioex.printStackTrace();
                }
            }
            System.out.println("Receiver has been stopped by user");

        });
    }

    public Thread runReceiver(){
        if(t!=null) {
            stopReceiver=false;
            t.start();
            return t;
        }
        else throw new NullPointerException("Receiver thread has not been initiated (probably could not set socket?)");
    }

    private boolean isFromThisSystem(String message){
        if(message.length()>=key.length()) {
            if (message.substring(0, key.length()).equals(key)) {
                return true;
            }
        }

        return false;
    }

    public void stopReceiver(){
        stopReceiver=true;
    }
}
