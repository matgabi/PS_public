package server;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client implements Runnable {

    final public static int DEFAULT_PORT = 9000;
    final public static String host = "localhost";

    @Override
    public void run() {

    }

    public static void main(String[] args)
    {
        try{
            Socket socket = new Socket(InetAddress.getLocalHost(),DEFAULT_PORT);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            OutputStream out = socket.getOutputStream();
            out.write(("mere\n" +
                    " - " +
                    "").getBytes());
            System.out.println(in.readLine());
            in.close();
            out.close();
            socket.close();

        }
        catch (IOException e)
        {

        }

    }
}
