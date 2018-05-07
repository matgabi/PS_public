package view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.InvoiceSystem;
import model.ReportFactory;
import repository.Repository;
import serialize.SerializeBill;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;

public class Main extends Application implements Runnable {
    LoginView loginView;

    private Socket socket;
    private Socket notifications;


    private ObjectInputStream nin;
    private ObjectOutputStream nout;

    private boolean isConnected = true;

    @Override
    public void start(Stage primaryStage) throws Exception{
        SerializeBill.deserialize();

        ReportFactory reportFactory = new ReportFactory();
        try
        {
            socket = new Socket(InetAddress.getLocalHost(),9000);
            notifications = new Socket(InetAddress.getLocalHost(),1234);

        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null,"Serverul nu este pornit!");
            this.stop();
            return;
        }



        InvoiceSystem invoiceSystem;
        try
        {
            invoiceSystem = new InvoiceSystem(reportFactory,socket);
        }
        catch(IOException e)
        {
            JOptionPane.showMessageDialog(null,"Server might be stopped");
            this.stop();
            primaryStage.close();
            return;
        }
        nout =  new ObjectOutputStream(notifications.getOutputStream());
        nin =  new ObjectInputStream(notifications.getInputStream());

        this.start();
        loginView = new LoginView(invoiceSystem);


        Scene loginScene = new Scene(loginView);

        primaryStage.setTitle("Invoice System");
        primaryStage.setScene(loginScene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            isConnected = false;
            try {
                SerializeBill.serialize();
                invoiceSystem.closeConnectionToServer();
                socket.close();
                notifications.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void start()
    {
        new Thread(this).start();
    }

    @Override
    public void run() {
        System.out.println("Listening for notifications");
        while (isConnected)
        {
            try {
                String message = nin.readObject().toString();
                System.out.println(message);
                loginView.refreshInfo();
                try {
                    displayTray(message);
                } catch (AWTException e) {
                    // e.printStackTrace();
                }
            } catch (IOException e) {
                System.out.println("Connection might be closed");
                //e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void displayTray(String message) throws AWTException, MalformedURLException {
        //Obtain only one instance of the SystemTray object
        SystemTray tray = SystemTray.getSystemTray();

        //If the icon is a file
        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
        //Alternative (if the icon is on the classpath):
        //Image image = Toolkit.getToolkit().createImage(getClass().getResource("icon.png"));

        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
        //Let the system resize the image if needed
        trayIcon.setImageAutoSize(true);
        //Set tooltip text for the tray icon
        trayIcon.setToolTip("System tray icon demo");
        tray.add(trayIcon);

        trayIcon.displayMessage("Invoice system", message, TrayIcon.MessageType.INFO);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
