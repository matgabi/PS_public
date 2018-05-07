package test;

import cotrollers.ILoginController;
import model.InvoiceSystem;
import model.Seller;
import org.junit.Test;

import javax.persistence.criteria.CriteriaBuilder;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ILoginControllerTest {

    @Test
    public void handleLogin_valid_input()
    {
        Seller s = new Seller();
        String input ="2018";

        InvoiceSystem invoiceSystem = mock(InvoiceSystem.class);
        ILoginController loginController = new ILoginController(invoiceSystem);

        assertEquals(loginController.handleLogin(input,s),true);
        verify(invoiceSystem).setSelectedSeller(s);

    }

    @Test
    public void handleLogin_invalid_input()
    {
        Seller s = new Seller();
        String input ="201";

        InvoiceSystem invoiceSystem = mock(InvoiceSystem.class);
        ILoginController loginController = new ILoginController(invoiceSystem);

        assertEquals(loginController.handleLogin(input,s),false);
    }

    @Test
    public void generateInvoice_test()
    {
        InvoiceSystem invoiceSystem = mock(InvoiceSystem.class);
        ILoginController loginController = new ILoginController(invoiceSystem);

        loginController.generateInvoice();
        verify(invoiceSystem).generateInvoice();
    }

}
