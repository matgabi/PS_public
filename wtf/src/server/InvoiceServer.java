package server;

import java.io.IOError;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class InvoiceServer implements Runnable {

    public static final int DEFAULT_PORT = 9000;
    private ServerSocket serverSocket;

    private boolean isStopped = false;

    private ArrayList<ClientHandler> connectedClients;

    public InvoiceServer() throws IOException {
       serverSocket = new ServerSocket(DEFAULT_PORT);
       connectedClients = new ArrayList<ClientHandler>();
    }

    public void close()
    {
        isStopped = true;
        try
        {
            serverSocket.close();
        }
        catch (IOException e)
        {
            System.out.println("Could not close socket server");
        }
    }

    public void removeClient(ClientHandler clientHandler)
    {
        connectedClients.remove(clientHandler);
    }

    public void start()
    {
        Thread listenClients = new Thread(this);
        listenClients.start();
    }

    @Override
    public void run() {
        while (!isStopped)
        {
            Socket clientSocket = null;
            try
            {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("Error in listening for clients");
                if(isStopped) {
                    System.out.println("Server Stopped.") ;
                    break;
                }
                //e.printStackTrace();
            }
            ClientHandler clientHandler = null;
            try {
                clientHandler = new ClientHandler(clientSocket,"thread" + connectedClients.size(),this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            connectedClients.add(clientHandler);
            Thread clientThread = new Thread(clientHandler);
            clientThread.start();

        }
    }

    public static void main(String[] args) throws IOException {
        InvoiceServer server = new InvoiceServer();

        server.start();

    }
}
