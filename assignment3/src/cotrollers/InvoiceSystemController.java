package cotrollers;

import javafx.collections.ObservableList;
import model.*;
import model.interfaces.ClientInterface;
import model.interfaces.SellerInterface;
import model.interfaces.TableItem;

public class InvoiceSystemController {
    private InvoiceSystem invoiceSystem;

    public InvoiceSystemController(InvoiceSystem invoiceSystem) {
        this.invoiceSystem = invoiceSystem;
    }

    public ObservableList<Factura> getFacturi() {
        return invoiceSystem.getFacturi();
    }

    public void setFacturi(ObservableList<Factura> facturi) {
        invoiceSystem.setFacturi(facturi);
    }

    public SellerInterface getSelectedSeller() {
        return invoiceSystem.getSelectedSeller();
    }

    public void setSelectedSeller(SellerInterface selectedSeller) {
       invoiceSystem.setSelectedSeller(selectedSeller);
    }

    public ClientInterface getSelectedClient() {
        return invoiceSystem.getSelectedClient();
    }

    public void setSelectedClient(ClientInterface selectedClient) {
       invoiceSystem.setSelectedClient(selectedClient);
    }

    public Emitor getSelectedEmitor() {
        return invoiceSystem.getSelectedEmitor();
    }

    public void setSelectedEmitor(Emitor selectedEmitor) {
        invoiceSystem.setSelectedEmitor(selectedEmitor);
    }

    public ObservableList<TableItem> getSelectedProducts() {
        return invoiceSystem.getSelectedProducts();
    }

    public void setSelectedProducts(ObservableList<TableItem> selectedProducts) {
       invoiceSystem.setSelectedProducts(selectedProducts);
    }

    public String getSelectedCar() {
        return invoiceSystem.getSelectedCar();
    }

    public void setSelectedCar(String selectedCar) {
        invoiceSystem.setSelectedCar(selectedCar);
    }

    public TableItem getCurrentDiscount() {
        return invoiceSystem.getCurrentDiscount();
    }

    public TVA getCurrentTVA() {
        return invoiceSystem.getCurrentTVA();
    }

    public void setCurrentTVA(TVA currentTVA) {
       invoiceSystem.setCurrentTVA(currentTVA);
    }



    public void insertItem(TableItem product)
    {
        invoiceSystem.insertItem(product);
    }

    public void generateInvoice()
    {
        invoiceSystem.generateInvoice();
    }
}
