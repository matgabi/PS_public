package cotrollers;

import javafx.collections.ObservableList;
import model.*;
import model.interfaces.ClientInterface;
import model.interfaces.SellerInterface;
import model.interfaces.TableItem;

import java.math.BigDecimal;
import java.util.Optional;

public class InvoiceController {

    private InvoiceSystem invoiceSystem;

    public InvoiceController(InvoiceSystem invoiceSystem)
    {
        this.invoiceSystem = invoiceSystem;
    }

    public ObservableList<ClientInterface> getAllClients()
    {
        return invoiceSystem.getAllClients();
    }

    public ObservableList<Product> getAllProducts()
    {
        return invoiceSystem.getAllProducts();
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

    public void setCurrentTVA(String currentTVA) {
        Optional<String> tva = InputParser.tryParseString(currentTVA);
        if(!tva.isPresent())
        {
            return;
        }
        if(tva.get().equals("19"))
        {
            invoiceSystem.setCurrentTVA(new TVA(TVA.TVA_TYPE.NORMAL19_TVA));
        }
        else if(tva.get().equals("9"))
        {
            invoiceSystem.setCurrentTVA(new TVA(TVA.TVA_TYPE.NORMAL9_TVA));
        }

    }



    public Optional<TableItem> insertItem(Product product, String quantity, String discount, String um, TVA tva)
    {
        if(product == null)
        {
            return Optional.empty();
        }
        if(tva == null)
        {
            return Optional.empty();
        }

        Optional<Integer> cantitate = InputParser.tryParseInteger(quantity);
        if(!cantitate.isPresent())
        {
            return Optional.empty();
        }

        Optional<Double> discountValue = InputParser.tryParseDouble(discount);
        if(!discountValue.isPresent())
        {
            return Optional.empty();
        }

        Optional<String> umValue = InputParser.tryParseString(um);
        if(!umValue.isPresent())
        {
            return Optional.empty();
        }

        TableItem productItem = new ProductItem(product,cantitate.get(),umValue.get(),new BigDecimal(discountValue.get()),tva);
        invoiceSystem.insertItem(productItem);
        return Optional.of(productItem);
    }

    public boolean deleteFactura(Factura factura)
    {
        return invoiceSystem.deleteFactura(factura);
    }

    public boolean deleteItem(TableItem product)
    {
        return invoiceSystem.deleteItem(product);
    }

    public BigDecimal getTotalValue()
    {
        return invoiceSystem.getTotalValue();
    }

    public BigDecimal getTotalTvaValue()
    {
        return invoiceSystem.getTotalTvaValue();
    }

    public BigDecimal getTotal()
    {
        return invoiceSystem.getTotal();
    }

    public void generateInvoice()
    {
        invoiceSystem.generateInvoice();
    }


}
