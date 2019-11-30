package com.example.couchpoker.networking;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.function.Function;

public class TCPConnection {

    public static Socket getConnection(InetAddress serverAddress) {
        Socket returnValue = null;
        String uuID = "mojeuuidheheh";
        String clientName = "jerneXD";

        RunnableFuture<Socket> runnableFuture = new FutureTask<Socket>(()->{
            Socket internalSocket = null;
            try {
                internalSocket = new Socket(serverAddress, 25051);
                DataOutputStream outputStream = new DataOutputStream(internalSocket.getOutputStream());
                BufferedReader streamFromServer = new BufferedReader(new InputStreamReader(internalSocket.getInputStream()));

                outputStream.writeBytes(uuID);
                String received = streamFromServer.readLine();

                internalSocket.close();

            } catch (IOException ioex) {
                ioex.printStackTrace();
            }
            return internalSocket;
        });

        try {
            runnableFuture.run();
            while (!runnableFuture.isDone()) Thread.sleep(200);
            returnValue = runnableFuture.get();
        } catch (InterruptedException intex){
            intex.printStackTrace();
        } catch (ExecutionException exex){
            exex.printStackTrace();
        }

        if (returnValue != null) return returnValue;
        throw new NullPointerException("Function did not returned any socket connected to server");
    }
}
