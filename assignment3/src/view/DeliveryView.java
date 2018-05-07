package view;

import cotrollers.DeliveryController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Emitor;
import model.InvoiceSystem;

import java.io.File;
import java.io.IOException;

public class DeliveryView extends BorderPane {

    private InvoiceSystem invoiceSystem;
    private DeliveryController deliveryController;
    @FXML
    ComboBox<Emitor> emitorsComboBox;
    @FXML
    TextField emitorNameText;
    @FXML
    TextField emitorSerieText;
    @FXML
    TextField emitorNrText;
    @FXML
    TextField emitorEmText;

    @FXML
    ComboBox<String> carsComboBox;

    @FXML
    TextField saveLocationText;

    public DeliveryView(InvoiceSystem invoiceSystem) throws IOException {

        this.invoiceSystem = invoiceSystem;
        this.deliveryController = new DeliveryController(invoiceSystem);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("deliveryinfo.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
    }

    @FXML
    private void initialize()
    {
        emitorsComboBox.setItems(deliveryController.getAllEmitors());

        emitorsComboBox.setConverter(new StringConverter<Emitor>() {
            @Override
            public String toString(Emitor object) {
                return ((Emitor)object).getFirstName() + " " + ((Emitor)object).getLastName();
            }

            @Override
            public Emitor fromString(String string) {
                return null;
            }
        });
        emitorsComboBox.getSelectionModel().selectFirst();
        autoCompleteEmitorInfo();

        carsComboBox.setItems(deliveryController.getAllCars());
        carsComboBox.getSelectionModel().selectFirst();
        autoCompleteCarInfo();
    }

    @FXML
    private void autoCompleteEmitorInfo()
    {
        Emitor emitor = emitorsComboBox.getSelectionModel().getSelectedItem();
        if(deliveryController.setSelectedEmitor(emitor))
        {
            emitorNameText.setText(emitor.getFirstName() + " " + emitor.getLastName());
            emitorSerieText.setText(emitor.getSeria());
            emitorNrText.setText(emitor.getNr());
            emitorEmText.setText(emitor.getCiEmitor());
        }
    }

    @FXML
    private void autoCompleteCarInfo()
    {
        String car = carsComboBox.getSelectionModel().getSelectedItem();
        if(deliveryController.setSelectedCar(car))
        {

        }
    }

    @FXML
    private void generateInvoice()
    {
        deliveryController.generateInvoice();
        Stage stage = (Stage)this.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void changeDirectory()
    {
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setInitialDirectory(new File("C:\\Users\\MGA4CLJ\\Desktop\\Fac\\ps\\30238-matgabi\\assignment2\\facturiPdf"));
        fileChooser.setTitle("A");
        File f = fileChooser.showDialog(this.getScene().getWindow());
        System.out.println(f);

        saveLocationText.setText(f.toString());
        deliveryController.setPdfPath(saveLocationText.getText());

    }

}
