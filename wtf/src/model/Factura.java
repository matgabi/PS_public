package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import model.interfaces.ClientInterface;
import model.interfaces.SellerInterface;
import model.interfaces.TableItem;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class Factura implements Serializable{

    private String name;
    private String pdfPath = "C:\\Users\\MGA4CLJ\\Desktop\\Fac\\ps\\30238-matgabi\\assignment3\\facturiPdf";

    private SellerInterface seller;
    private ClientInterface client;
    private Emitor emitor;
    private ArrayList<TableItem> products;
    private String car;

    private Date date;

    private TVA tva;

    private BigDecimal totalValue;
    private BigDecimal totalTvaValue;
    private BigDecimal totalDiscountValue;
    private BigDecimal totalDiscountTvaValue;

    public Factura() {
        totalDiscountTvaValue = new BigDecimal(0);
        totalDiscountValue = new BigDecimal(0);
        totalTvaValue = new BigDecimal(0);
        totalValue = new BigDecimal(0);
    }

    public SellerInterface getSeller() {
        return seller;
    }

    public void setSeller(SellerInterface seller) {
        this.seller = seller;
    }

    public ClientInterface getClient() {
        return client;
    }

    public void setClient(ClientInterface client) {
        this.client = client;
    }

    public Emitor getEmitor() {
        return emitor;
    }

    public void setEmitor(Emitor emitor) {
        this.emitor = emitor;
    }

    public ArrayList<TableItem> getProducts() {
        return products;
    }

    public void setProducts(ObservableList<TableItem> products) {
        this.products = new ArrayList<TableItem>(products);
        autoCompute();
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    private void autoCompute()
    {
        for(TableItem p : products)
        {
            totalValue = totalValue.add(p.getValue());
            totalTvaValue = totalTvaValue.add(p.getTvaValue());
            totalDiscountValue = totalDiscountValue.subtract(p.getDiscountValue());
            totalDiscountTvaValue = totalDiscountTvaValue.subtract(p.getDiscountTvaValue());
        }
        System.out.println(totalDiscountTvaValue.toString());
        System.out.println(totalDiscountValue.toString());
        TableItem discountItem = new DiscountItem(totalDiscountValue,totalDiscountTvaValue);
        products.add(discountItem);
    }

    public BigDecimal getTotalDiscountTvaValue() {
        return totalDiscountTvaValue;
    }

    public BigDecimal getTotalDiscountValue() {
        return totalDiscountValue;
    }

    public BigDecimal getTotalTvaValue() {
        return totalTvaValue;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TVA getTva() {
        return tva;
    }

    public void setTva(TVA tva) {
        this.tva = tva;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }
}
