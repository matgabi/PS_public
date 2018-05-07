package test;

import model.Product;
import model.ProductItem;
import model.TVA;
import org.junit.Test;
import static org.junit.Assert.assertEquals;


import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProductItemTest {

    @Test
    public void getValue_test()
    {
        Product p = mock(Product.class);
        ProductItem productItem = new ProductItem(p,10,"l", BigDecimal.valueOf(0),mock(TVA.class));
        when(p.getPrice()).thenReturn(BigDecimal.valueOf(10));

        assertEquals(BigDecimal.valueOf(100),productItem.getValue());
        verify(p).getPrice();

    }

    @Test
    public void getTvaValue_test()
    {
        TVA tva = mock(TVA.class);
        when(tva.getValue()).thenReturn(BigDecimal.valueOf(9));
        Product p = mock(Product.class);
        ProductItem productItem = new ProductItem(p,10,"l", BigDecimal.valueOf(0),tva);
        when(p.getPrice()).thenReturn(BigDecimal.valueOf(10));

        assertEquals(BigDecimal.valueOf(9),productItem.getTvaValue());
        verify(p).getPrice();

    }
}
