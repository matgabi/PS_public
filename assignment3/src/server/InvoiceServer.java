package server;

import model.PdfCreator;
import repository.Repository;

import java.io.IOError;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class InvoiceServer implements Runnable {

    public static final int DEFAULT_PORT = 9000;
    private ServerSocket serverSocket;
    private ServerSocket notificationSocket;

    private boolean isStopped = false;

    private ArrayList<ClientHandler> connectedClients;

    private Repository repository;
    private PdfCreator pdfCreator;

    public InvoiceServer(Repository repository,PdfCreator pdfCreator) throws IOException {
       serverSocket = new ServerSocket(DEFAULT_PORT);
       notificationSocket = new ServerSocket(1234);
       connectedClients = new ArrayList<ClientHandler>();

        this.repository = repository;
        this.pdfCreator = pdfCreator;
    }

    public void close()
    {
        isStopped = true;
        try
        {
            serverSocket.close();
            repository.close();
        }
        catch (IOException e)
        {
            System.out.println("Could not close socket server");
        } catch (Exception e) {
            e.printStackTrace();
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

    public void notifyAllClients(String message,ClientHandler client)
    {
        for(ClientHandler c : connectedClients)
        {
            if(c != client)
            {
                c.sendNotification(message);
            }
        }
    }

    @Override
    public void run() {
        while (!isStopped)
        {
            Socket clientSocket = null;
            Socket notifications = null;
            try
            {
                clientSocket = serverSocket.accept();
                notifications = notificationSocket.accept();
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
                clientHandler = new ClientHandler(clientSocket,notifications,"thread" + connectedClients.size(),this,repository,pdfCreator);
            } catch (IOException e) {
                e.printStackTrace();
            }
            connectedClients.add(clientHandler);
            Thread clientThread = new Thread(clientHandler);
            clientThread.start();

        }
    }

    public static void main(String[] args) throws IOException {
        Repository repository = new Repository();
        PdfCreator pdfCreator = new PdfCreator();
        InvoiceServer server = new InvoiceServer(repository,pdfCreator);

        server.start();

    }
}
