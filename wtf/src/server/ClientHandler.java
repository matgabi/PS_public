package server;

import model.InputParser;

import java.io.*;
import java.net.Socket;
import java.util.Optional;

public class ClientHandler implements Runnable {

    private Socket socket;
    private String clientName;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    private boolean isStopped = false;

    private InvoiceServer server;

    public ClientHandler(Socket socket,String clientName,InvoiceServer server) throws IOException {
        this.socket = socket;
        this.clientName = clientName;
        System.out.println("ClientHandler created " + clientName);

        out =  new ObjectOutputStream(socket.getOutputStream());
        in =  new ObjectInputStream(socket.getInputStream());

        this.server = server;
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
        String command = null;
        try {
            command = (String)in.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Optional<ServerCommands.COMMAND> task = InputParser.tryParseCommand(command);
        if(task.get() == ServerCommands.COMMAND.CLOSE)
        {
            System.out.println("Received command: " + command);
            out.writeObject("done");
            close();
        }
        else if(task.get() == ServerCommands.COMMAND.GET_ALL_CLIENTS)
        {
            System.out.println("Received command: " + command);
            out.writeObject("done");
            closeAll();
        }
        else if (task.get() == ServerCommands.COMMAND.INVALID_COMMAND)
        {
            System.out.println("Received command: " + command);
            out.writeObject("done");
            out.flush();
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
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
