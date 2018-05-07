package test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import model.Factura;
import model.Product;
import model.ProductItem;
import model.TVA;
import model.interfaces.TableItem;
import org.junit.Test;

import java.math.BigDecimal;
import static org.junit.Assert.assertEquals;

public class FacturaTest {

    @Test
    public void autoCompute_test()
    {
        ObservableList<TableItem> products = FXCollections.observableArrayList();
        Product product = new Product("p", BigDecimal.TEN);
        ProductItem productItem = new ProductItem(product,10,"l",BigDecimal.TEN,new TVA(TVA.TVA_TYPE.NORMAL19_TVA));

        products.add(productItem);

        Factura factura = new Factura();

        assertEquals(factura.getTotalValue(),BigDecimal.valueOf(0));
        assertEquals(factura.getTotalTvaValue(),BigDecimal.valueOf(0));
        assertEquals(factura.getTotalDiscountValue(),BigDecimal.valueOf(0));
        assertEquals(factura.getTotalDiscountTvaValue(),BigDecimal.valueOf(0));

        factura.setProducts(products);

        assertEquals(factura.getTotalValue(),BigDecimal.valueOf(100));
        assertEquals(factura.getTotalTvaValue(),BigDecimal.valueOf(19));
        assertEquals(factura.getTotalDiscountValue(),BigDecimal.valueOf(-10));
        assertEquals(factura.getTotalDiscountTvaValue(),BigDecimal.valueOf(-1.9));
    }

}
