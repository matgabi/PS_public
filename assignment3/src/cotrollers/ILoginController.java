package cotrollers;

import javafx.collections.ObservableList;
import model.InputParser;
import model.InvoiceSystem;
import model.interfaces.SellerInterface;

import java.util.Optional;

public class ILoginController {

    private InvoiceSystem invoiceSystem;

    public ILoginController(InvoiceSystem invoiceSystem)
    {
        this.invoiceSystem = invoiceSystem;
    }

    public boolean handleLogin(String userTextFieldInput,SellerInterface seller)
    {
        Optional<Integer> year = InputParser.tryParseInteger(userTextFieldInput);
        if(!year.isPresent())
        {
            return false;
        }

        if(year.get() < 2000 || year.get() > 2030)
        {
            return false;
        }
        invoiceSystem.setSelectedSeller(seller);
        return true;
    }

    public ObservableList<SellerInterface> getAllSellers()
    {
        return invoiceSystem.getAllSellers();
    }

    public void generateInvoice()
    {
        invoiceSystem.generateInvoice();
    }

    public void reset()
    {
        invoiceSystem.reset();
    }
}
