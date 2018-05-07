package server;

import javafx.collections.ObservableList;
import model.*;
import model.Client;
import model.interfaces.ClientInterface;
import model.interfaces.SellerInterface;
import repository.Repository;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Optional;

public class ClientHandler implements Runnable {

    private Socket socket;
    private Socket notifications;
    private String clientName;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    private ObjectInputStream nin;
    private ObjectOutputStream nout;

    private boolean isStopped = false;

    private InvoiceServer server;

    private Repository repository;
    private PdfCreator pdfCreator;

    public ClientHandler(Socket socket,Socket notificatinos,String clientName,InvoiceServer server,Repository repository,PdfCreator pdfCreator) throws IOException {
        this.socket = socket;
        this.notifications = notificatinos;

        this.clientName = clientName;

        out =  new ObjectOutputStream(socket.getOutputStream());
        in =  new ObjectInputStream(socket.getInputStream());

        nout =  new ObjectOutputStream(notificatinos.getOutputStream());
        nin =  new ObjectInputStream(notificatinos.getInputStream());


        this.server = server;
        this.repository = repository;
        this.pdfCreator = pdfCreator;
    }

    @Override
    public void run() {
        System.out.println("Client thread started");
        while (!isStopped)
        {
            try
            {
                handleComands();
            }
            catch (Exception e)
            {
                System.out.println("Error in handeling commands");
            }
        }
    }

    private void handleComands() throws IOException {
        ServerCommands.COMMAND command = null;
        try {
            command = (ServerCommands.COMMAND)in.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if(command == ServerCommands.COMMAND.SET_CLIENT_NAME)
        {
            try {
                this.clientName = in.readObject() + "-" + clientName;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println("ClientHandler created " + this.clientName);
        }
        else if(command == ServerCommands.COMMAND.GET_AVAILABLE_INVOICES)
        {
            System.out.println("Received command: " + command);
            try {
                Seller seller = (Seller)in.readObject();
                System.out.println(seller);
                ObservableList<InvoicesAvailable> invoicesAvailable = repository.getAvailableInvoices(seller);
                out.writeObject(invoicesAvailable.get(0));
                out.flush();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        else if(command == ServerCommands.COMMAND.BUY_INVOICES)
        {
            System.out.println("Received command: " + command);
            try {
                InvoicesAvailable invoicesAvailable= (InvoicesAvailable) in.readObject();
                System.out.println(invoicesAvailable);
                boolean success = repository.updateBatch(invoicesAvailable);
                out.writeObject(true);
                out.flush();

                server.notifyAllClients(Notifications.BUY_INVOICES + clientName,this);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                out.writeObject(false);
                out.flush();
            } catch (Exception e)
            {
                out.writeObject(false);
                out.flush();
            }
        }
        else if(command == ServerCommands.COMMAND.CLOSE)
        {
            System.out.println("Received command: " + command);
            close();
        }
        else if(command == ServerCommands.COMMAND.GET_ALL_CLIENTS)
        {
            System.out.println("Received command: " + command);
            ArrayList<ClientInterface> clients =new ArrayList<ClientInterface>(repository.getAllClients()) ;

            out.writeObject(clients);
            out.flush();
        }
        else if(command == ServerCommands.COMMAND.GET_ALL_PRODUCTS)
        {
            System.out.println("Received command: " + command);
            ArrayList<Product> products =new ArrayList<Product>(repository.getAllProducts()) ;

            out.writeObject(products);
            out.flush();
        }
        else if(command == ServerCommands.COMMAND.GET_ALL_EMITORS)
        {
            System.out.println("Received command: " + command);
            ArrayList<Emitor> emitors =new ArrayList<Emitor>(repository.getAllEmitors()) ;

            out.writeObject(emitors);
            out.flush();
        }
        else if(command == ServerCommands.COMMAND.GET_ALL_SELLERS)
        {
            System.out.println("Received command: " + command);
            ArrayList<SellerInterface> sellers =new ArrayList<SellerInterface>(repository.getAllSellers()) ;

            out.writeObject(sellers);
            out.flush();
        }
        else if(command == ServerCommands.COMMAND.GET_ALL_ACTIVITIES)
        {
            System.out.println("Received command: " + command);
            ArrayList<FacturaInfo> facturaInfos =new ArrayList<FacturaInfo>(repository.getAllActivities()) ;

            out.writeObject(facturaInfos);
            out.flush();
        }
        else if(command == ServerCommands.COMMAND.ADD_NEW_PRODUCT)
        {
            System.out.println("Received command: " + command);
            try {
                Product product = (Product)in.readObject();
                System.out.println(product);
                boolean success = repository.addNewProduct(product);
                out.writeObject(true);
                out.flush();

                server.notifyAllClients(Notifications.NEW_PRODUCT_ADDED + " by " + clientName,this);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                out.writeObject(false);
                out.flush();
            } catch (Exception e)
            {
                out.writeObject(false);
                out.flush();
            }
        }
        else if(command == ServerCommands.COMMAND.ADD_NEW_CLIENT)
        {
            System.out.println("Received command: " + command);
            try {
                Client client = (Client) in.readObject();
                System.out.println(client);
                boolean success = repository.addNewClient(client);
                out.writeObject(true);
                out.flush();

                server.notifyAllClients(Notifications.NEW_CLIENT_ADDED + " by " + clientName,this);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                out.writeObject(false);
                out.flush();
            } catch (Exception e)
            {
                out.writeObject(false);
                out.flush();
            }
        }
        else if(command == ServerCommands.COMMAND.ADD_NEW_SELLER)
        {
            System.out.println("Received command: " + command);
            try {
                Seller seller = (Seller) in.readObject();
                System.out.println(seller);
                boolean success = repository.addNewSeller(seller);
                out.writeObject(true);
                out.flush();

                server.notifyAllClients(Notifications.NEW_SELLER_ADDED + " by " + clientName,this);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                out.writeObject(false);
                out.flush();
            } catch (Exception e)
            {
                out.writeObject(false);
                out.flush();
            }
        }
        else if(command == ServerCommands.COMMAND.ADD_NEW_EMITOR)
        {
            System.out.println("Received command: " + command);
            try {
                Emitor emitor = (Emitor) in.readObject();
                System.out.println(emitor);
                boolean success = repository.addNewEmitor(emitor);
                out.writeObject(true);
                out.flush();

                server.notifyAllClients(Notifications.NEW_EMITOR_ADDED + " by " + clientName,this);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                out.writeObject(false);
                out.flush();
            } catch (Exception e)
            {
                out.writeObject(false);
                out.flush();
            }
        }
        else if(command == ServerCommands.COMMAND.ADD_NEW_ACTIVITY)
        {
            System.out.println("Received command: " + command);
            try {
                FacturaInfo facturaInfo = (FacturaInfo) in.readObject();
                System.out.println(facturaInfo);
                boolean success = repository.addNewActivity(facturaInfo);
                out.writeObject(true);
                out.flush();

                server.notifyAllClients(Notifications.NEW_ACTIVITY_ADDED + " by " + clientName,this);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                out.writeObject(false);
                out.flush();
            } catch (Exception e)
            {
                out.writeObject(false);
                out.flush();
            }
        }
        else if(command == ServerCommands.COMMAND.UPDATE_PRODUCT)
        {
            System.out.println("Received command: " + command);
            try {
                Product product = (Product)in.readObject();
                System.out.println(product);
                boolean success = repository.updateProduct(product);
                out.writeObject(true);
                out.flush();

                server.notifyAllClients(Notifications.PRODUCT_UPDATED + " by " + clientName,this);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                out.writeObject(false);
                out.flush();
            } catch (Exception e)
            {
                out.writeObject(false);
                out.flush();
            }
        }
        else if(command == ServerCommands.COMMAND.UPDATE_CLIENT)
        {
            System.out.println("Received command: " + command);
            try {
                Client client = (Client) in.readObject();
                System.out.println(client);
                boolean success = repository.updateClient(client);
                out.writeObject(true);
                out.flush();

                server.notifyAllClients(Notifications.CLIENT_UPDATED + " by " + clientName,this);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                out.writeObject(false);
                out.flush();
            } catch (Exception e)
            {
                out.writeObject(false);
                out.flush();
            }
        }
        else if(command == ServerCommands.COMMAND.UPDATE_SELLER)
        {
            System.out.println("Received command: " + command);
            try {
                Seller seller = (Seller) in.readObject();
                System.out.println(seller);
                boolean success = repository.updateSeller(seller);
                out.writeObject(true);
                out.flush();

                server.notifyAllClients(Notifications.SELLER_UPDATED + " by " + clientName,this);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                out.writeObject(false);
                out.flush();
            } catch (Exception e)
            {
                out.writeObject(false);
                out.flush();
            }
        }
        else if(command == ServerCommands.COMMAND.UPDATE_EMITOR)
        {
            System.out.println("Received command: " + command);
            try {
                Emitor emitor = (Emitor) in.readObject();
                System.out.println(emitor);
                boolean success = repository.updateEmitor(emitor);
                out.writeObject(true);
                out.flush();
                server.notifyAllClients(Notifications.EMITOR_UPDATED + " by " + clientName,this);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                out.writeObject(false);
                out.flush();
            } catch (Exception e)
            {
                out.writeObject(false);
                out.flush();
            }
        }
        else if(command == ServerCommands.COMMAND.UPDATE_ACTIVITY)
        {
            System.out.println("Received command: " + command);
            try {
                FacturaInfo facturaInfo = (FacturaInfo) in.readObject();
                System.out.println(facturaInfo);
                boolean success = repository.updateActivity(facturaInfo);
                out.writeObject(true);
                out.flush();

                server.notifyAllClients(Notifications.ACTIVITY_UPDATED + " by " + clientName,this);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                out.writeObject(false);
                out.flush();
            } catch (Exception e)
            {
                out.writeObject(false);
                out.flush();
            }
        }
        else if(command == ServerCommands.COMMAND.DELETE_PRODUCT)
        {
            System.out.println("Received command: " + command);
            try {
                Product product = (Product)in.readObject();
                System.out.println(product);
                boolean success = repository.deleteProduct(product);
                out.writeObject(true);
                out.flush();

                server.notifyAllClients(Notifications.PRODUCT_DELETED + " by " + clientName,this);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                out.writeObject(false);
                out.flush();
            } catch (Exception e)
            {
                out.writeObject(false);
                out.flush();
            }
        }
        else if(command == ServerCommands.COMMAND.DELETE_CLIENT)
        {
            System.out.println("Received command: " + command);
            try {
                Client client = (Client) in.readObject();
                System.out.println(client);
                boolean success = repository.deleteClient(client);
                out.writeObject(true);
                out.flush();

                server.notifyAllClients(Notifications.CLIENT_DElETED + " by " + clientName,this);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                out.writeObject(false);
                out.flush();
            } catch (Exception e)
            {
                out.writeObject(false);
                out.flush();
            }
        }
        else if(command == ServerCommands.COMMAND.DELETE_SELLER)
        {
            System.out.println("Received command: " + command);
            try {
                Seller seller = (Seller) in.readObject();
                System.out.println(seller);
                boolean success = repository.deleteSeller(seller);
                out.writeObject(true);
                out.flush();

                server.notifyAllClients(Notifications.SELLER_DELETED + " by " + clientName,this);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                out.writeObject(false);
                out.flush();
            } catch (Exception e)
            {
                out.writeObject(false);
                out.flush();
            }
        }
        else if(command == ServerCommands.COMMAND.DELETE_EMITOR)
        {
            System.out.println("Received command: " + command);
            try {
                Emitor emitor = (Emitor) in.readObject();
                System.out.println(emitor);
                boolean success = repository.deleteEmitor(emitor);
                out.writeObject(true);
                out.flush();

                server.notifyAllClients(Notifications.EMITOR_DELETED + " by " + clientName,this);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                out.writeObject(false);
                out.flush();
            } catch (Exception e)
            {
                out.writeObject(false);
                out.flush();
            }
        }
        else if(command == ServerCommands.COMMAND.DELETE_ACTIVITY)
        {
            System.out.println("Received command: " + command);
            try {
                FacturaInfo facturaInfo = (FacturaInfo) in.readObject();
                System.out.println(facturaInfo);
                boolean success = repository.deleteActivity(facturaInfo);
                out.writeObject(true);
                out.flush();

                server.notifyAllClients(Notifications.ACTIVITY_DELETED + " by " + clientName,this);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                out.writeObject(false);
                out.flush();
            } catch (Exception e)
            {
                out.writeObject(false);
                out.flush();
            }
        }
        else if(command == ServerCommands.COMMAND.GENERATE_INVOICE)
        {
            System.out.println("Received command: " + command);
            try {
                Factura factura = (Factura)in.readObject();
                pdfCreator.createPdf("a4",factura);

                InvoicesAvailable invoicesAvailable = (InvoicesAvailable)in.readObject();
                repository.updateBatch(invoicesAvailable);

                server.notifyAllClients(Notifications.NEW_INVOICE_CREATED + " by " + clientName,this);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        else if (command == ServerCommands.COMMAND.INVALID_COMMAND)
        {
            System.out.println("Received command: " + command);
        }
    }

    public void sendNotification(String message)
    {
        try {
            nout.writeObject(message);
            nout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeAll() {
        isStopped = true;
        server.removeClient(this);
        server.close();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        isStopped = true;
        server.removeClient(this);
        try {
            in.close();
            out.close();
            nin.close();
            nout.close();

            socket.close();
            notifications.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
