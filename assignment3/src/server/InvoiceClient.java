package server;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class InvoiceClient  {

    public final static int DEFAULT_PORT = 9000;

    private Socket socket;
    private BufferedReader in;
    private OutputStream out;

    public InvoiceClient() throws IOException {
        socket = new Socket(InetAddress.getLocalHost(),DEFAULT_PORT);
        in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        out =  socket.getOutputStream();
    }

    public void close()
    {
        try {
            socket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendToServer(String command) throws IOException {

        out.write(command.getBytes());
        System.out.println("Comanda a fost scrisa");
        System.out.println(in.readLine());
    }

    public static void main(String[] args) throws IOException {
        InvoiceClient client = new InvoiceClient();
        String command = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while(true)
        {
            System.out.println("Introdu comanda");
            command = br.readLine();
            if(command.equals("close"))
            {
                client.sendToServer(command+"\n");
                client.close();
                break;
            }
            client.sendToServer(command+"\n");
        }
        br.close();
    }
}
