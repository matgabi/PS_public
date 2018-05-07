package model.interfaces;

import java.math.BigDecimal;

public interface TableItem {
    public String getName();
    public BigDecimal getPrice();
    public String getUm();
    public int getQuantity();
    public BigDecimal getValue();
    public BigDecimal getTvaValue();
    public BigDecimal getDiscount();
    public BigDecimal getDiscountValue();
    public BigDecimal getDiscountTvaValue();

    public void setValue(BigDecimal value);
    public void setTvaValue(BigDecimal tvaValue);

}
