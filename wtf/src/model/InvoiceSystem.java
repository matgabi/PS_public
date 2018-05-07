package model;

import com.sun.deploy.util.SessionState;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import model.interfaces.ClientInterface;
import model.interfaces.Report;
import model.interfaces.SellerInterface;
import model.interfaces.TableItem;

import serialize.SerializeBill;
import server.ServerCommands;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.math.BigDecimal;

import java.net.Socket;

import java.util.ArrayList;
import java.util.Date;

public class InvoiceSystem  {

    private String path = "C:\\Users\\MGA4CLJ\\Desktop\\Fac\\ps\\30238-matgabi\\assignment3\\facturiPdf";



    private ObservableList<Factura> facturi;


    private SellerInterface selectedSeller;
    private ClientInterface selectedClient;
    private Emitor selectedEmitor;
    private ObservableList<TableItem> selectedProducts;
    private TableItem currentDiscount;
    private String selectedCar;


    private ReportFactory reportFactory;

    private TVA currentTVA;

    private Socket socket;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    private boolean isConnected = true;

    private InvoicesAvailable invoicesAvailable;

    public InvoiceSystem(ReportFactory reportFactory,Socket socket) throws IOException {
        this.reportFactory = reportFactory;

        facturi = SerializeBill.getFacturi();

        selectedCar = null;
        selectedClient = null;
        selectedEmitor = null;
        selectedProducts = FXCollections.observableArrayList();
        selectedSeller = null;
        currentTVA = new TVA(TVA.TVA_TYPE.NORMAL19_TVA);
        currentDiscount = new DiscountItem(new BigDecimal(0),new BigDecimal(0));


        this.socket = socket;

        out =  new ObjectOutputStream(socket.getOutputStream());
        in =  new ObjectInputStream(socket.getInputStream());



    }

    public void sendNameToServer()
    {
        try {
            out.writeObject(ServerCommands.COMMAND.SET_CLIENT_NAME);
            out.flush();
            out.writeObject(this.selectedSeller.getName());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.writeObject(ServerCommands.COMMAND.GET_AVAILABLE_INVOICES);
            out.flush();
            out.writeObject(selectedSeller);
            out.flush();
            invoicesAvailable = (InvoicesAvailable)in.readObject();
            System.out.println(invoicesAvailable);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public InvoicesAvailable getInvoicesAvailable()
    {
        try {
            out.writeObject(ServerCommands.COMMAND.GET_AVAILABLE_INVOICES);
            out.flush();
            out.writeObject(selectedSeller);
            out.flush();
            invoicesAvailable = (InvoicesAvailable)in.readObject();
            System.out.println(invoicesAvailable);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return invoicesAvailable;
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
        if(selectedProducts.size() > 0)
        {
            selectedProducts.remove(selectedProducts.size() - 1);
        }
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

    public boolean buyMoreInvoices()
    {
        invoicesAvailable.setNr(invoicesAvailable.getNr() + 5);
        boolean success = false;
        try {
            out.writeObject(ServerCommands.COMMAND.BUY_INVOICES);
            out.flush();
            out.writeObject(invoicesAvailable);
            out.flush();
            success =  (boolean)in.readObject();
            out.writeObject(ServerCommands.COMMAND.GET_AVAILABLE_INVOICES);
            out.flush();
            out.writeObject(selectedSeller);
            out.flush();
            invoicesAvailable = (InvoicesAvailable)in.readObject();
            System.out.println(invoicesAvailable);
            return success;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return success;
    }

    public ObservableList<Product> getAllProducts()
    {
        try {
            out.writeObject(ServerCommands.COMMAND.GET_ALL_PRODUCTS);
            out.flush();
            ArrayList<Product> products = (ArrayList<Product>)in.readObject();
            return FXCollections.observableArrayList(products);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public ObservableList<ClientInterface> getAllClients()
    {
        try {
            out.writeObject(ServerCommands.COMMAND.GET_ALL_CLIENTS);
            out.flush();
            ArrayList<ClientInterface> clients = (ArrayList<ClientInterface>)in.readObject();
            return FXCollections.observableArrayList(clients);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public ObservableList<SellerInterface> getAllSellers()
    {
        try {
            out.writeObject(ServerCommands.COMMAND.GET_ALL_SELLERS);
            out.flush();
            ArrayList<SellerInterface> sellers = (ArrayList<SellerInterface>)in.readObject();
            return FXCollections.observableArrayList(sellers);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public ObservableList<Emitor> getAllEmitors()
    {
        try {
            out.writeObject(ServerCommands.COMMAND.GET_ALL_EMITORS);
            out.flush();
            ArrayList<Emitor> emitors = (ArrayList<Emitor>)in.readObject();
            return FXCollections.observableArrayList(emitors);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public ObservableList<FacturaInfo> getAllActivities(){    try {
        out.writeObject(ServerCommands.COMMAND.GET_ALL_ACTIVITIES);
        out.flush();
        ArrayList<FacturaInfo> facturaInfos = (ArrayList<FacturaInfo>)in.readObject();
        return FXCollections.observableArrayList(facturaInfos);
    } catch (IOException e) {
        e.printStackTrace();
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    }
        return null; }

    public boolean addNewProduct(Product product)
    {
        boolean success = false;
        try {
            out.writeObject(ServerCommands.COMMAND.ADD_NEW_PRODUCT);
            out.flush();
            out.writeObject(product);
            out.flush();
            success =  (boolean)in.readObject();
            return success;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return success;
    }
    public boolean addNewClient(Client client)
    {
        boolean success = false;
        try {
            out.writeObject(ServerCommands.COMMAND.ADD_NEW_CLIENT);
            out.flush();
            out.writeObject(client);
            out.flush();
            success =  (boolean)in.readObject();
            return success;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return success;
    }
    public boolean addNewSeller(Seller seller)
    {
        boolean success = false;
        try {
            out.writeObject(ServerCommands.COMMAND.ADD_NEW_SELLER);
            out.flush();
            out.writeObject(seller);
            out.flush();
            success =  (boolean)in.readObject();
            return success;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return success;
    }
    public boolean addNewEmitor(Emitor emitor)
    {
        boolean success = false;
        try {
            out.writeObject(ServerCommands.COMMAND.ADD_NEW_EMITOR);
            out.flush();
            out.writeObject(emitor);
            out.flush();
            success =  (boolean)in.readObject();
            return success;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return success;
    }
    public boolean addNewActivity(FacturaInfo facturaInfo) {
        boolean success = false;
        try {
            out.writeObject(ServerCommands.COMMAND.ADD_NEW_ACTIVITY);
            out.flush();
            out.writeObject(facturaInfo);
            out.flush();
            success =  (boolean)in.readObject();
            return success;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return success;
    }

    public boolean updateProduct(Product product)
    {
        boolean success = false;
        try {
            out.writeObject(ServerCommands.COMMAND.UPDATE_PRODUCT);
            out.flush();
            out.writeObject(product);
            out.flush();
            success =  (boolean)in.readObject();
            return success;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return success;
    }
    public boolean updateClient(Client client)
    {
        boolean success = false;
        try {
            out.writeObject(ServerCommands.COMMAND.UPDATE_CLIENT);
            out.flush();
            out.writeObject(client);
            out.flush();
            success =  (boolean)in.readObject();
            return success;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return success;
    }
    public boolean updateSeller(Seller seller)
    {
        boolean success = false;
        try {
            out.writeObject(ServerCommands.COMMAND.UPDATE_SELLER);
            out.flush();
            out.writeObject(seller);
            out.flush();
            success =  (boolean)in.readObject();
            return success;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return success;
    }
    public boolean updateEmitor(Emitor emitor)
    {
        boolean success = false;
        try {
            out.writeObject(ServerCommands.COMMAND.UPDATE_EMITOR);
            out.flush();
            out.writeObject(emitor);
            out.flush();
            success =  (boolean)in.readObject();
            return success;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return success;
    }
    public boolean updateActivity(FacturaInfo facturaInfo) {
        boolean success = false;
        try {
            out.writeObject(ServerCommands.COMMAND.UPDATE_ACTIVITY);
            out.flush();
            out.writeObject(facturaInfo);
            out.flush();
            success =  (boolean)in.readObject();
            return success;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return success;
    }

    public boolean deleteProduct(Product product)
    {
        boolean success = false;
        try {
            out.writeObject(ServerCommands.COMMAND.DELETE_PRODUCT);
            out.flush();
            out.writeObject(product);
            out.flush();
            success =  (boolean)in.readObject();
            return success;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return success;
    }
    public boolean deleteClient(Client client)
    {
        boolean success = false;
        try {
            out.writeObject(ServerCommands.COMMAND.DELETE_CLIENT);
            out.flush();
            out.writeObject(client);
            out.flush();
            success =  (boolean)in.readObject();
            return success;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return success;
    }
    public boolean deleteSeller(Seller seller)
    {
        boolean success = false;
        try {
            out.writeObject(ServerCommands.COMMAND.DELETE_SELLER);
            out.flush();
            out.writeObject(seller);
            out.flush();
            success =  (boolean)in.readObject();
            return success;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return success;
    }
    public boolean deleteEmitor(Emitor emitor)
    {
        boolean success = false;
        try {
            out.writeObject(ServerCommands.COMMAND.DELETE_EMITOR);
            out.flush();
            out.writeObject(emitor);
            out.flush();
            success =  (boolean)in.readObject();
            return success;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return success;
    }
    public boolean deleteActivity(FacturaInfo facturaInfo) { boolean success = false;
        try {
            out.writeObject(ServerCommands.COMMAND.DELETE_ACTIVITY);
            out.flush();
            out.writeObject(facturaInfo);
            out.flush();
            success =  (boolean)in.readObject();
            return success;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return success; }

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
        //pdfCreator.createPdf("a4",factura);
        try {
            out.writeObject(ServerCommands.COMMAND.GENERATE_INVOICE);
            out.flush();
            out.writeObject(factura);
            out.flush();

            invoicesAvailable.setNr(invoicesAvailable.getNr() - 1);
            out.writeObject(invoicesAvailable);
            out.flush();

            out.writeObject(ServerCommands.COMMAND.GET_AVAILABLE_INVOICES);
            out.flush();
            out.writeObject(selectedSeller);
            out.flush();
            invoicesAvailable = (InvoicesAvailable)in.readObject();
            System.out.println(invoicesAvailable);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


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

    public synchronized void closeConnectionToServer()
    {
        isConnected = false;
        try {
            out.writeObject(ServerCommands.COMMAND.CLOSE);
            out.flush();


            in.close();
            out.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
