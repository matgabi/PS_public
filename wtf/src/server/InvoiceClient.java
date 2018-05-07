package server;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class InvoiceClient  {

    public final static int DEFAULT_PORT = 9000;

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public InvoiceClient() throws IOException {
        socket = new Socket(InetAddress.getLocalHost(),DEFAULT_PORT);
        out =  new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

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
        if(command.equals("1")) {
            out.writeObject(ServerCommands.COMMAND.CLOSE);
        }
        else
        {
            out.writeObject(ServerCommands.COMMAND.INVALID_COMMAND);
        }
        try {
            System.out.println(in.readObject().toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        InvoiceClient client = new InvoiceClient();
        String command = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while(true)
        {
            System.out.println("Introdu comanda");
            command = br.readLine();
            if(command.equals("1"))
            {
                client.sendToServer(command);
                client.close();
                break;
            }
            client.sendToServer(command);
        }
        br.close();
    }
}
