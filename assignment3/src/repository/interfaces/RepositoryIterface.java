package repository.interfaces;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.*;
import model.interfaces.ClientInterface;
import model.interfaces.SellerInterface;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public interface RepositoryIterface {
    public ObservableList<Product> getAllProducts();
    public ObservableList<ClientInterface> getAllClients();
    public ObservableList<SellerInterface> getAllSellers();
    public ObservableList<Emitor> getAllEmitors();
    public ObservableList<FacturaInfo> getAllActivities();

    public boolean addNewProduct(Product product);
    public boolean addNewClient(Client client);
    public boolean addNewSeller(Seller seller);
    public boolean addNewEmitor(Emitor emitor);
    public boolean addNewActivity(FacturaInfo facturaInfo);

    public boolean updateProduct(Product product);
    public boolean updateClient(Client client);
    public boolean updateSeller(Seller seller);
    public boolean updateEmitor(Emitor emitor);
    public boolean updateActivity(FacturaInfo facturaInfo);

    public boolean deleteProduct(Product product);
    public boolean deleteClient(Client client);
    public boolean deleteSeller(Seller seller);
    public boolean deleteEmitor(Emitor emitor);
    public boolean deleteActivity(FacturaInfo facturaInfo);

    public boolean addNewBatch(InvoicesAvailable invoicesAvailable);
    public boolean updateBatch(InvoicesAvailable invoicesAvailable);
    public ObservableList<InvoicesAvailable> getAvailableInvoices(Seller seller);
}
