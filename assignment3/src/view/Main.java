package view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.InvoiceSystem;
import model.ReportFactory;
import repository.Repository;
import serialize.SerializeBill;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        SerializeBill.deserialize();

        ReportFactory reportFactory = new ReportFactory();
        Repository repository = new Repository();
        InvoiceSystem invoiceSystem = new InvoiceSystem(repository,reportFactory);
        BorderPane loginView = new LoginView(invoiceSystem);


        Scene loginScene = new Scene(loginView);

        primaryStage.setTitle("Invoice System");
        primaryStage.setScene(loginScene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            try {
                SerializeBill.serialize();
                repository.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
