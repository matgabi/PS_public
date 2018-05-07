package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import model.interfaces.ClientInterface;
import model.interfaces.Report;
import model.interfaces.SellerInterface;
import model.interfaces.TableItem;
import repository.Repository;
import repository.interfaces.RepositoryIterface;
import serialize.SerializeBill;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class InvoiceSystem {

    private String path = "C:\\Users\\MGA4CLJ\\Desktop\\Fac\\ps\\30238-matgabi\\assignment2\\facturiPdf";

    private PdfCreator pdfCreator;
    private RepositoryIterface repository;
    private ObservableList<Factura> facturi;


    private SellerInterface selectedSeller;
    private ClientInterface selectedClient;
    private Emitor selectedEmitor;
    private ObservableList<TableItem> selectedProducts;
    private TableItem currentDiscount;
    private String selectedCar;


    private ReportFactory reportFactory;

    private TVA currentTVA;

    public InvoiceSystem(Repository repository, ReportFactory reportFactory)
    {
        this.reportFactory = reportFactory;
        this.repository = repository;
        facturi = SerializeBill.getFacturi();
        pdfCreator = new PdfCreator();

        selectedCar = null;
        selectedClient = null;
        selectedEmitor = null;
        selectedProducts = FXCollections.observableArrayList();
        selectedSeller = null;
        currentTVA = new TVA(TVA.TVA_TYPE.NORMAL19_TVA);
        currentDiscount = new DiscountItem(new BigDecimal(0),new BigDecimal(0));
    }

    public ObservableList<Factura> getFacturi() {
        return facturi;
    }

    public void setFacturi(ObservableList<Factura> facturi) {
        this.facturi = facturi;
    }

    public SellerInterface getSelectedSeller() {
        return selectedSeller;
    }

    public void setSelectedSeller(SellerInterface selectedSeller) {
        this.selectedSeller = selectedSeller;
    }

    public ClientInterface getSelectedClient() {
        return selectedClient;
    }

    public void setSelectedClient(ClientInterface selectedClient) {
        this.selectedClient = selectedClient;
    }

    public Emitor getSelectedEmitor() {
        return selectedEmitor;
    }

    public void setSelectedEmitor(Emitor selectedEmitor) {
        this.selectedEmitor = selectedEmitor;
    }

    public ObservableList<TableItem> getSelectedProducts() {
        return selectedProducts;
    }

    public void setSelectedProducts(ObservableList<TableItem> selectedProducts) {
        this.selectedProducts = selectedProducts;
        recomputeDiscount();
    }

    public String getSelectedCar() {
        return selectedCar;
    }

    public void setSelectedCar(String selectedCar) {
        this.selectedCar = selectedCar;
    }

    public TableItem getCurrentDiscount() {
        return selectedProducts.size() > 0 ? currentDiscount : null;
    }

    public TVA getCurrentTVA() {
        return currentTVA;
    }

    public void setCurrentTVA(TVA currentTVA) {
        this.currentTVA = currentTVA;
    }

    public ObservableList<Product> getAllProducts()
    {
        return repository.getAllProducts();
    }
    public ObservableList<ClientInterface> getAllClients()
    {
        return  repository.getAllClients();
    }
    public ObservableList<SellerInterface> getAllSellers()
    {
        return repository.getAllSellers();
    }
    public ObservableList<Emitor> getAllEmitors()
    {
        return repository.getAllEmitors();
    }
    public ObservableList<FacturaInfo> getAllActivities(){ return repository.getAllActivities(); }

    public boolean addNewProduct(Product product)
    {
        return repository.addNewProduct(product);
    }
    public boolean addNewClient(Client client)
    {
        return repository.addNewClient(client);
    }
    public boolean addNewSeller(Seller seller)
    {
        return repository.addNewSeller(seller);
    }
    public boolean addNewEmitor(Emitor emitor)
    {
        return repository.addNewEmitor(emitor);
    }
    public boolean addNewActivity(FacturaInfo facturaInfo) { return repository.addNewActivity(facturaInfo); }

    public boolean updateProduct(Product product)
    {
        return repository.updateProduct(product);
    }
    public boolean updateClient(Client client)
    {
        return repository.updateClient(client);
    }
    public boolean updateSeller(Seller seller)
    {
        return repository.updateSeller(seller);
    }
    public boolean updateEmitor(Emitor emitor)
    {
        return repository.updateEmitor(emitor);
    }
    public boolean updateActivity(FacturaInfo facturaInfo) {return repository.updateActivity(facturaInfo); }

    public boolean deleteProduct(Product product)
    {
        return repository.deleteProduct(product);
    }
    public boolean deleteClient(Client client)
    {
        return repository.deleteClient(client);
    }
    public boolean deleteSeller(Seller seller)
    {
        return repository.deleteSeller(seller);
    }
    public boolean deleteEmitor(Emitor emitor)
    {
        return repository.deleteEmitor(emitor);
    }
    public boolean deleteActivity(FacturaInfo facturaInfo) { return repository.deleteActivity(facturaInfo); }

    public void insertItem(TableItem product)
    {
        selectedProducts.add(product);
        recomputeDiscount();
    }

    private void recomputeDiscount()
    {

        BigDecimal totalDiscountValue = BigDecimal.valueOf(0);
        BigDecimal totalDiscountTvaValue = BigDecimal.valueOf(0);
        for(TableItem p : selectedProducts)
        {
            totalDiscountValue = totalDiscountValue.add(p.getDiscountValue());
            totalDiscountTvaValue = totalDiscountTvaValue.add(p.getDiscountTvaValue());
        }
        currentDiscount.setValue(totalDiscountValue.multiply(new BigDecimal(-1)));
        currentDiscount.setTvaValue(totalDiscountTvaValue.multiply(new BigDecimal(-1)));
    }

    public boolean deleteItem(TableItem product)
    {
        selectedProducts.remove(product);
        recomputeDiscount();
        return true;
    }

    public BigDecimal getTotalValue()
    {
        BigDecimal totalValue = BigDecimal.valueOf(0);
        for(TableItem p : selectedProducts)
        {
           totalValue = totalValue.add(p.getValue());
        }
        totalValue = totalValue.add(currentDiscount.getValue());
        return totalValue;
    }

    public BigDecimal getTotalTvaValue()
    {
        BigDecimal totalTvaValue = BigDecimal.valueOf(0);
        for(TableItem p : selectedProducts)
        {
            totalTvaValue = totalTvaValue.add(p.getTvaValue());
        }
        totalTvaValue = totalTvaValue.add(currentDiscount.getTvaValue());
        return totalTvaValue;
    }

    public BigDecimal getTotal()
    {
        BigDecimal totalValue = BigDecimal.valueOf(0);
        BigDecimal totalTvaValue = BigDecimal.valueOf(0);
        for(TableItem p : selectedProducts)
        {
            totalValue = totalValue.add(p.getValue());
            totalTvaValue = totalTvaValue.add(p.getTvaValue());
        }
        totalValue = totalValue.add(currentDiscount.getValue());
        totalTvaValue = totalTvaValue.add(currentDiscount.getTvaValue());
        return totalTvaValue.add(totalValue);
    }

    public boolean deleteFactura(Factura factura)
    {
        return facturi.remove(factura);
    }

    public void generateInvoice()
    {
        Factura factura = new Factura();
        factura.setCar(selectedCar);
        factura.setClient(selectedClient);
        factura.setEmitor(selectedEmitor);
        factura.setSeller(selectedSeller);
        factura.setProducts(selectedProducts);
        factura.setDate(new Date());


        factura.setName(selectedClient.getName().replace(" ","_") + "_" + SerializeBill.getCount());
        factura.setTva(currentTVA);
        factura.setPdfPath(path);

        System.out.println(factura.getSeller());
        System.out.println(factura.getEmitor());
        System.out.println(factura.getClient());
        System.out.println(factura.getCar());
        for(TableItem p : factura.getProducts())
        {
            System.out.println(p);
        }
        System.out.println(factura.getTva().getValue().toString());
        pdfCreator.createPdf("a4",factura);
        facturi.add(factura);

        FacturaInfo facturaInfo = new FacturaInfo(factura);
        addNewActivity(facturaInfo);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Report getReport(ReportFactory.REPORT_TYPE report_type)
    {
        return reportFactory.getReport(report_type,getAllActivities());
    }

    public void reset()
    {
        selectedProducts = FXCollections.observableArrayList();
        currentDiscount.setValue(BigDecimal.valueOf(0));
        currentDiscount.setTvaValue(BigDecimal.valueOf(0));
    }
}
