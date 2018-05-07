package model;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "activities")
public class FacturaInfo implements Serializable {

    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    private int id;

    @Column
    private String name;

    @Column
    private String client;

    @Column
    private String seller;

    @Column
    private String emitor;

    @Column
    private String car;

    @Column
    private Date date;

    @Column
    private int products;

    @Column
    private BigDecimal total;

    @Column
    private BigDecimal totalTva;

    @Column
    private BigDecimal totalDiscount;

    @Column
    private BigDecimal totalDiscountTva;

    public FacturaInfo(){}

    public FacturaInfo(Factura factura)
    {
        this.name = factura.getName();
        this.client = factura.getClient().getName();
        this.seller = factura.getSeller().getName();
        this.emitor = factura.getEmitor().getFirstName();
        this.car = factura.getCar();
        this.date = factura.getDate();
        this.products = factura.getProducts().size();
        this.total = factura.getTotalValue();
        this.totalTva = factura.getTotalTvaValue();
        this.totalDiscount = factura.getTotalDiscountValue();
        this.totalDiscountTva = factura.getTotalDiscountTvaValue();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getEmitor() {
        return emitor;
    }

    public void setEmitor(String emitor) {
        this.emitor = emitor;
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

    public int getProducts() {
        return products;
    }

    public void setProducts(int products) {
        this.products = products;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getTotalTva() {
        return totalTva;
    }

    public void setTotalTva(BigDecimal totalTva) {
        this.totalTva = totalTva;
    }

    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public BigDecimal getTotalDiscountTva() {
        return totalDiscountTva;
    }

    public void setTotalDiscountTva(BigDecimal totalDiscountTva) {
        this.totalDiscountTva = totalDiscountTva;
    }

    @Override
    public String toString() {
        return "FacturaInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", client='" + client + '\'' +
                ", seller='" + seller + '\'' +
                ", emitor='" + emitor + '\'' +
                ", car='" + car + '\'' +
                ", date=" + date +
                ", products=" + products +
                ", total=" + total +
                ", totalTva=" + totalTva +
                ", totalDiscount=" + totalDiscount +
                ", totalDiscountTva=" + totalDiscountTva +
                '}';
    }
}
