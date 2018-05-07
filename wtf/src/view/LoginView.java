package view;

import cotrollers.ILoginController;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.InvoiceSystem;
import model.interfaces.SellerInterface;

import java.io.IOException;

public class LoginView extends BorderPane{

    private InvoiceSystem invoiceSystem;
    private ILoginController loginController;

    private InvoiceView invoiceView;

    @FXML
    private BorderPane mainLayout;
    @FXML
    private ComboBox sellerComboBox;
    @FXML
    private TextField userTextField;
    @FXML
    private Label logLabel;

    @FXML
    private MenuBar menuBar;


    public LoginView(InvoiceSystem invoiceSystem) throws IOException {
        this.invoiceSystem = invoiceSystem;
        loginController = new ILoginController(invoiceSystem);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("login.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
    }

    @FXML
    private void initialize()
    {
        sellerComboBox.setItems(loginController.getAllSellers());
        sellerComboBox.setConverter(new StringConverter() {
            @Override
            public String toString(Object object) {
                return ((SellerInterface)object).getName();
            }

            @Override
            public Object fromString(String string) {
                return null;
            }
        });
        sellerComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void logInControl() throws IOException {
        if(loginController.handleLogin(userTextField.getText(),(SellerInterface)sellerComboBox.getSelectionModel().getSelectedItem()))
        {
            System.out.println(invoiceSystem.getSelectedSeller());
            invoiceView = new InvoiceView(invoiceSystem);
            mainLayout.setCenter(invoiceView);
            menuBar.setDisable(false);
        }
        else
        {
            logLabel.setText("Invalid year");
            return;
        }
    }

    @FXML
    private void showDatabaseInfo() throws IOException {
        BorderPane databaseView = new DbView(invoiceSystem);
        Stage addDbStage = new Stage();
        addDbStage.setTitle("Baza de date");
        addDbStage.initModality(Modality.WINDOW_MODAL);

        addDbStage.initOwner(this.getScene().getWindow());

        Scene dbScene = new Scene(databaseView);
        addDbStage.setScene(dbScene);
        addDbStage.setOnCloseRequest(event -> {
            invoiceView.refreshInfo();
        });
        addDbStage.showAndWait();
    }

    @FXML
    private void generateInvoice() throws IOException {
        BorderPane deliveryView = new DeliveryView(invoiceSystem);
        Stage addDeliveryStage = new Stage();
        addDeliveryStage.setTitle("Informatii livrare");
        addDeliveryStage.initModality(Modality.WINDOW_MODAL);

        addDeliveryStage.initOwner(this.getScene().getWindow());

        Scene deliveryScene = new Scene(deliveryView);
        addDeliveryStage.setScene(deliveryScene);
        addDeliveryStage.showAndWait();
    }

    @FXML
    public void reset()
    {
        loginController.reset();
        invoiceView.reset();
    }

    public void refreshInfo()
    {
        invoiceView.refreshInfo();
    }
}
