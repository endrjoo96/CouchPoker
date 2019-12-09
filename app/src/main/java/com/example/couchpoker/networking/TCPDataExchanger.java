package com.example.couchpoker.networking;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class TCPDataExchanger {

    public class ReceivedMessageEventArgs {
        public String msg;

        public ReceivedMessageEventArgs(String msg) {
            this.msg = msg;
        }

    }

    private Socket connectedClient;
    public Consumer<ReceivedMessageEventArgs> ReceivedMessage = null;
    private Thread receiver;
    private Thread sender;
    public boolean stopThread=false;

    private byte[] messageToSend;

    public TCPDataExchanger(Socket connectedClient) {
        this.connectedClient = connectedClient;
    }

    public void runReceiver(){
        if(connectedClient!=null) {
            stopThread = false;

            receiver = new Thread(() -> {
                while(!stopThread) {
                    try {
                        BufferedReader streamFromServer = new BufferedReader(new InputStreamReader(connectedClient.getInputStream()));
                        while (!stopThread) {
                            String message = streamFromServer.readLine();
                            if (ReceivedMessage != null) {
                                ReceivedMessage.accept(new ReceivedMessageEventArgs(message));
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            receiver.start();
        }
    }

    public void stopReceiver(){
        stopThread=true;
    }

    public void sendMessage(String msg){
        if(connectedClient!=null) {
            messageToSend = msg.getBytes(StandardCharsets.UTF_8);

            sender = new Thread(()->{
                try {
                    DataOutputStream outputStream = new DataOutputStream(connectedClient.getOutputStream());
                    outputStream.write(messageToSend, 0, messageToSend.length);
                } catch (Exception ex){
                    ex.printStackTrace();
                }
                messageToSend = null;
            });

            sender.start();
        }
    }

}