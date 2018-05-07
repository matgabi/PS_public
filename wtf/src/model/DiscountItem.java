package model;

import model.interfaces.TableItem;

import java.io.Serializable;
import java.math.BigDecimal;

public class DiscountItem implements TableItem,Serializable {

    private BigDecimal value;
    private BigDecimal tvaValue;

    public DiscountItem(BigDecimal value, BigDecimal tvaValue) {
        this.value = value;
        this.tvaValue = tvaValue;
    }

    @Override
    public String getName() {
        return "Discount";
    }

    @Override
    public BigDecimal getPrice() {
        return null;
    }

    @Override
    public String getUm() {
        return "buc";
    }

    @Override
    public int getQuantity() {
        return 1;
    }

    @Override
    public BigDecimal getValue() {
        return value;
    }

    @Override
    public BigDecimal getTvaValue() {
        return tvaValue;
    }

    @Override
    public BigDecimal getDiscount() {
        return null;
    }

    @Override
    public BigDecimal getDiscountValue() {
        return null;
    }

    @Override
    public BigDecimal getDiscountTvaValue() {
        return null;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public void setTvaValue(BigDecimal tvaValue) {
        this.tvaValue = tvaValue;
    }
}
