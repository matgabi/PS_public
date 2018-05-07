package test;

import cotrollers.IDbController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.*;
import model.interfaces.ClientInterface;
import org.junit.Test;
import org.mockito.Matchers;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IDbControllerTest {

    @Test
    public void getClients_test(){
        InvoiceSystem invoiceSystem = mock(InvoiceSystem.class);
        IDbController dbController = new IDbController(invoiceSystem);
        when(invoiceSystem.getAllClients()).thenReturn(FXCollections.observableArrayList(new Client()));

        ObservableList<ClientInterface> clients = dbController.getAllClients();
        verify(invoiceSystem).getAllClients();
        assertEquals(clients.size(),1);

    }

    @Test
    public void addNewProduct_test()
    {
        Product p = new Product();
        InvoiceSystem invoiceSystem = mock(InvoiceSystem.class);
        IDbController dbController = new IDbController(invoiceSystem);
        when(invoiceSystem.addNewProduct(p)).thenReturn(true);

        assertEquals(dbController.addNewProduct(p),true);
        verify(invoiceSystem).addNewProduct(p);
    }

    @Test
    public void updateSeller_test()
    {
        Seller s = new Seller();
        InvoiceSystem invoiceSystem = mock(InvoiceSystem.class);
        IDbController dbController = new IDbController(invoiceSystem);
        when(invoiceSystem.updateSeller(s)).thenReturn(true);

        assertEquals(dbController.updateSeller(s),true);
        verify(invoiceSystem).updateSeller(s);
    }

    @Test
    public void deleteEmitor_test()
    {
        Emitor e = new Emitor();
        InvoiceSystem invoiceSystem = mock(InvoiceSystem.class);
        IDbController dbController = new IDbController(invoiceSystem);
        when(invoiceSystem.deleteEmitor(e)).thenReturn(true);

        assertEquals(dbController.deleteEmitor(e),true);
        verify(invoiceSystem).deleteEmitor(e);
    }
}
