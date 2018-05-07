package cotrollers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Emitor;
import model.InputParser;
import model.InvoiceSystem;

import javax.swing.*;
import java.util.Optional;

public class DeliveryController {

    private InvoiceSystem invoiceSystem;

    public DeliveryController(InvoiceSystem invoiceSystem)
    {
        this.invoiceSystem = invoiceSystem;
    }

    public ObservableList<Emitor> getAllEmitors()
    {
        return invoiceSystem.getAllEmitors();
    }

    public ObservableList<String> getAllCars()
    {
        ObservableList<String> cars = FXCollections.observableArrayList("NT-66-MAT","NT-11-WST");
        return cars;
    }

    public boolean setSelectedCar(String car)
    {
        Optional<String> carString = InputParser.tryParseString(car);
        if(!carString.isPresent())
        {
            return false;
        }
        else
        {
            invoiceSystem.setSelectedCar(carString.get());
            return true;
        }
    }

    public boolean setSelectedEmitor(Emitor emitor)
    {
        if(emitor == null)
        {
            return false;
        }
        else
        {
            invoiceSystem.setSelectedEmitor(emitor);
            return true;
        }
    }

    public void generateInvoice() {
        if (invoiceSystem.getInvoicesAvailable().getNr() == 0) {
            System.out.println("NU MAI AI FACTURI");

            JOptionPane.showMessageDialog(null,"Nu mai ai facturi disponibile.Cumpara un nou batch!");

            return;
        } else {
            invoiceSystem.generateInvoice();

        }
    }
    public void setPdfPath(String path)
    {
        Optional<String> pdfPath = InputParser.tryParseString(path);
        if(!pdfPath.isPresent())
        {
            return;
        }
        invoiceSystem.setPath(pdfPath.get());
    }

    public int getInvoicesAvailable()
    {
        return invoiceSystem.getInvoicesAvailable().getNr();
    }

    public void buyMoreInvoices()
    {
            invoiceSystem.buyMoreInvoices();
    }

}
