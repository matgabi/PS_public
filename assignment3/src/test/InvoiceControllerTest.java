package test;

import cotrollers.InvoiceController;
import model.*;
import org.junit.Test;
import org.mockito.Mockito;
import repository.Repository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class InvoiceControllerTest {

    @Test
    public void deleteFactura_test()
    {
        Factura f = mock(Factura.class);
        InvoiceSystem invoiceSystem = mock(InvoiceSystem.class);
        InvoiceController invoiceController = new InvoiceController(invoiceSystem);
        when(invoiceSystem.deleteFactura(f)).thenReturn(true);

        assertEquals(invoiceController.deleteFactura(f),true);
        verify(invoiceSystem).deleteFactura(f);
    }

    @Test
    public void generateInvoice_test()
    {
        InvoiceSystem invoiceSystem = mock(InvoiceSystem.class);
        InvoiceController invoiceController = new InvoiceController(invoiceSystem);

        invoiceController.generateInvoice();
        verify(invoiceSystem).generateInvoice();
    }
}
