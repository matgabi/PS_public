package model;

import model.interfaces.TableItem;

import java.io.Serializable;
import java.math.BigDecimal;

public class ProductItem implements Serializable,Comparable,TableItem{
    private Product product;
    private int quantity;
    private String um;
    private BigDecimal discount;
    private TVA tva;

    public ProductItem(Product product, int quantity, String um, BigDecimal discount, TVA tva) {
        this.product = product;
        this.quantity = quantity;
        this.um = um;
        this.discount = discount;
        this.tva = tva;
    }

    public String getName()
    {
        return product.getProductName();
    }

    public BigDecimal getPrice()
    {
        return product.getPrice();
    }

    public String getUm() {
        return um;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getValue()
    {
        BigDecimal value = product.getPrice().multiply(new BigDecimal(quantity));
        return value;
    }

    public BigDecimal getTvaValue()
    {
        BigDecimal value = product.getPrice().multiply(new BigDecimal(quantity));
        BigDecimal tvaValue = value.multiply(tva.getValue()).divide(new BigDecimal(100));
        return tvaValue;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public BigDecimal getDiscountValue()
    {
        BigDecimal value = product.getPrice().multiply(new BigDecimal(quantity));
        BigDecimal discountValue = value.multiply(discount).divide(new BigDecimal(100));
        return discountValue;
    }

    public BigDecimal getDiscountTvaValue()
    {
        BigDecimal value = product.getPrice().multiply(new BigDecimal(quantity));
        BigDecimal discountValue = value.multiply(discount).divide(new BigDecimal(100));
        BigDecimal tvaDiscountValue = discountValue.multiply(tva.getValue()).divide(new BigDecimal(100));
        return  tvaDiscountValue;
    }

    @Override
    public void setValue(BigDecimal value) {

    }

    @Override
    public void setTvaValue(BigDecimal tvaValue) {

    }

    @Override
    public int compareTo(Object o) {
        return product.compareTo(((ProductItem)o).product);
    }

    @Override
    public String toString() {
        return "ProductItem{" +
                "product=" + product +
                ", quantity=" + quantity +
                ", um='" + um + '\'' +
                ", discount=" + discount +
                ", tva=" + tva +
                '}';
    }
}
