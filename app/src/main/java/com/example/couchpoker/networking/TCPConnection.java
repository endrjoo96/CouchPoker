package com.example.couchpoker.networking;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.function.Function;

public class TCPConnection {

    public static Socket getConnection(InetAddress serverAddress) {
        Socket returnValue = null;
        String uuID = com.example.couchpoker.files.Settings.getID();
        String clientName = com.example.couchpoker.files.Settings.getUsername();

        RunnableFuture<Socket> runnableFuture = new FutureTask<Socket>(()->{
            Socket internalSocket = null;
            try {
                internalSocket = new Socket(serverAddress, 25051);
                DataOutputStream outputStream = new DataOutputStream(internalSocket.getOutputStream());
                BufferedReader streamFromServer = new BufferedReader(new InputStreamReader(internalSocket.getInputStream()));

                String received = streamFromServer.readLine();
                System.out.println("received: "+received);

                byte[] message = uuID.getBytes(StandardCharsets.UTF_8);
                outputStream.write(message, 0, message.length);

                received = streamFromServer.readLine();
                System.out.println("received: "+received);

                if("SEND_NICKNAME".equals(received)){
                    byte[] nickname = clientName.getBytes(StandardCharsets.UTF_8);
                    outputStream.write(nickname, 0, nickname.length);
                }
                else if(("HI_"+clientName).equals(received)) return internalSocket;
                else internalSocket = null;

                System.out.println("received: "+received);

            } catch (IOException ioex) {
                ioex.printStackTrace();
                internalSocket = null;
            }
            return internalSocket;
        });

        try {
            new Thread(runnableFuture).start();
            returnValue = runnableFuture.get();
        } catch (InterruptedException intex){
            intex.printStackTrace();
        } catch (ExecutionException exex){
            exex.printStackTrace();
        }

        return returnValue;
    }
}
