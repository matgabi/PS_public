package test;

import javafx.collections.FXCollections;
import model.*;
import org.junit.Test;
import repository.Repository;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InvoiceSystemTest {

    @Test
    public void addNewProduct_test()
    {
        Product p = mock(Product.class);
        ReportFactory reportFactory = mock(ReportFactory.class);
        Repository repository = mock(Repository.class);
        InvoiceSystem invoiceSystem = new InvoiceSystem(repository,reportFactory);

        when(repository.addNewProduct(p)).thenReturn(true);

        assertEquals(invoiceSystem.addNewProduct(p),true);
        verify(repository).addNewProduct(p);
    }

    @Test
    public void getsetClient_test()
    {
        Client c = mock(Client.class);
        ReportFactory reportFactory = mock(ReportFactory.class);
        Repository repository = mock(Repository.class);
        InvoiceSystem invoiceSystem = new InvoiceSystem(repository,reportFactory);

        invoiceSystem.setSelectedClient(c);

        assertEquals(invoiceSystem.getSelectedClient(),c);
    }

    @Test
    public void getDiscount_test()
    {
        Product p = new Product("A",BigDecimal.valueOf(100));
        ProductItem productItem = new ProductItem(p,1,"l",BigDecimal.valueOf(19),new TVA(TVA.TVA_TYPE.NORMAL9_TVA));

        ReportFactory reportFactory = mock(ReportFactory.class);
        Repository repository = mock(Repository.class);
        InvoiceSystem invoiceSystem = new InvoiceSystem(repository,reportFactory);

        invoiceSystem.insertItem(productItem);
        assertEquals(invoiceSystem.getTotalValue(),BigDecimal.valueOf(81));
        assertEquals(invoiceSystem.getTotalTvaValue(),BigDecimal.valueOf(7.29));
        assertEquals(invoiceSystem.getCurrentDiscount().getValue(),BigDecimal.valueOf(-19));
    }

}
