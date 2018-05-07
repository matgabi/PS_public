package cotrollers;

import javafx.collections.ObservableList;
import model.*;
import model.interfaces.ClientInterface;
import model.interfaces.Report;
import model.interfaces.SellerInterface;

public class IDbController {

    private InvoiceSystem invoiceSystem;

    public IDbController(InvoiceSystem invoiceSystem)
    {
        this.invoiceSystem = invoiceSystem;
    }

    public ObservableList<Product> getAllProducts()
    {
        return invoiceSystem.getAllProducts();
    }
    public ObservableList<ClientInterface> getAllClients()
    {
        return  invoiceSystem.getAllClients();
    }
    public ObservableList<SellerInterface> getAllSellers()
    {
        return invoiceSystem.getAllSellers();
    }
    public ObservableList<Emitor> getAllEmitors()
    {
        return invoiceSystem.getAllEmitors();
    }

    public boolean addNewProduct(Product product)
    {
        return invoiceSystem.addNewProduct(product);
    }
    public boolean addNewClient(ClientInterface client)
    {
        return invoiceSystem.addNewClient((Client)client);
    }
    public boolean addNewSeller(Seller seller)
    {
        return invoiceSystem.addNewSeller(seller);
    }
    public boolean addNewEmitor(Emitor emitor)
    {
        return invoiceSystem.addNewEmitor(emitor);
    }

    public boolean updateProduct(Product product)
    {
        return invoiceSystem.updateProduct(product);
    }
    public boolean updateClient(ClientInterface client)
    {
        return invoiceSystem.updateClient((Client)client);
    }
    public boolean updateSeller(Seller seller)
    {
        return invoiceSystem.updateSeller(seller);
    }
    public boolean updateEmitor(Emitor emitor)
    {
        return invoiceSystem.updateEmitor(emitor);
    }

    public boolean deleteProduct(Product product)
    {
        return invoiceSystem.deleteProduct(product);
    }
    public boolean deleteClient(ClientInterface client)
    {
        return invoiceSystem.deleteClient((Client)client);
    }
    public boolean deleteSeller(Seller seller)
    {
        return invoiceSystem.deleteSeller(seller);
    }
    public boolean deleteEmitor(Emitor emitor)
    {
        return invoiceSystem.deleteEmitor(emitor);
    }

    public Report getReport(ReportFactory.REPORT_TYPE report_type)
    {
        return invoiceSystem.getReport(report_type);
    }

}
