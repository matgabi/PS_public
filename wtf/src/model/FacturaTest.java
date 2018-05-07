package model;

import javafx.collections.FXCollections;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FacturaTest {

    @Test
    public void setProductsTest_test()
    {
        Factura f = mock(Factura.class);
        Product p = mock(Product.class);
        when(p.getPrice()).thenReturn(BigDecimal.valueOf(10));
        ProductItem productItem = new ProductItem(p,10,"l",BigDecimal.valueOf(10),mock(TVA.class));

        when(f.getTotalValue()).thenReturn(BigDecimal.valueOf(100));

        f.setProducts(FXCollections.observableArrayList(productItem));
        assertEquals(f.getTotalValue(),productItem.getValue());

    }

}
