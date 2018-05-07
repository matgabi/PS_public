package repository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.*;
import model.interfaces.ClientInterface;
import model.interfaces.SellerInterface;
import org.hibernate.cfg.Configuration;
import repository.interfaces.RepositoryIterface;

public class Repository implements RepositoryIterface, AutoCloseable {

    private ProductsRepository productsRepository;
    private ClientRepository clientRepository;
    private SellerRepository sellerRepository;
    private EmitorRepository emitorRepository;
    private ActivitiesRepository activitiesRepository;

    public Repository()
    {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        productsRepository = new ProductsRepository(configuration);
        clientRepository = new ClientRepository(configuration);
        sellerRepository = new SellerRepository(configuration);
        emitorRepository = new EmitorRepository(configuration);
        activitiesRepository = new ActivitiesRepository(configuration);
    }

    @Override
    public ObservableList<Product> getAllProducts() {
        return productsRepository.getAllProducts();
    }

    @Override
    public ObservableList<ClientInterface> getAllClients() {
        ObservableList<ClientInterface>  clients = FXCollections.observableArrayList(clientRepository.getAllClients());
        return clients;
    }

    @Override
    public ObservableList<SellerInterface> getAllSellers() {
        ObservableList<SellerInterface> sellers = FXCollections.observableArrayList(sellerRepository.getAllSellers());
        return sellers;
    }

    @Override
    public ObservableList<Emitor> getAllEmitors() {
        return emitorRepository.getAllEmitors();
    }
    @Override
    public ObservableList<FacturaInfo> getAllActivities(){ return activitiesRepository.getAllActivities(); }
    @Override
    public boolean addNewProduct(Product product) {
        return productsRepository.addNewProduct(product);
    }

    @Override
    public boolean addNewClient(Client client) {
        return clientRepository.addNewClient(client);
    }

    @Override
    public boolean addNewSeller(Seller seller) {
        return sellerRepository.addNewSeller(seller);
    }

    @Override
    public boolean addNewEmitor(Emitor emitor) {
        return emitorRepository.addNewEmitor(emitor);
    }
    @Override
    public boolean addNewActivity(FacturaInfo facturaInfo) { return activitiesRepository.addNewActivity(facturaInfo); }

    @Override
    public boolean updateProduct(Product product) {
        return productsRepository.updateProduct(product);
    }

    @Override
    public boolean updateClient(Client client) {
        return clientRepository.updateClient(client);
    }

    @Override
    public boolean updateSeller(Seller seller) {
        return sellerRepository.updateSeller(seller);
    }

    @Override
    public boolean updateEmitor(Emitor emitor) {
        return emitorRepository.updateEmitor(emitor);
    }

    @Override
    public boolean updateActivity(FacturaInfo facturaInfo) {return activitiesRepository.updateActivity(facturaInfo); }

    @Override
    public boolean deleteProduct(Product product) {
        return productsRepository.deleteProduct(product);
    }

    @Override
    public boolean deleteClient(Client client) {
        return clientRepository.deleteClient(client);
    }

    @Override
    public boolean deleteSeller(Seller seller) {
        return sellerRepository.deleteSeller(seller);
    }

    @Override
    public boolean deleteEmitor(Emitor emitor) {
        return emitorRepository.deleteEmitor(emitor);
    }

    @Override
    public boolean deleteActivity(FacturaInfo facturaInfo) { return activitiesRepository.deleteActivity(facturaInfo); }

    @Override
    public boolean addNewBatch(InvoicesAvailable invoicesAvailable) {
        return sellerRepository.addNewBatch(invoicesAvailable);
    }

    @Override
    public boolean updateBatch(InvoicesAvailable invoicesAvailable) {
        return sellerRepository.updateBatch(invoicesAvailable);
    }

    @Override
    public ObservableList<InvoicesAvailable> getAvailableInvoices(Seller seller) {
        return sellerRepository.getAvailableInvoices(seller);
    }

    @Override
    public void close() throws Exception {
        clientRepository.close();
        emitorRepository.close();
        productsRepository.close();
        sellerRepository.close();
        activitiesRepository.close();
    }
}
