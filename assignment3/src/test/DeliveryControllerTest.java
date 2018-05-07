package test;


import cotrollers.DeliveryController;
import javafx.collections.FXCollections;
import model.InvoiceSystem;
import org.junit.Test;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeliveryControllerTest {

    @Test
    public void getAllCars()
    {
        DeliveryController deliveryController = mock(DeliveryController.class);
        when(deliveryController.getAllCars()).thenReturn(FXCollections.observableArrayList("car1,car2"));
        assertEquals(deliveryController.getAllCars(),FXCollections.observableArrayList("car1,car2"));
    }

    @Test
    public void setSelectedCar_validCarString()
    {
        InvoiceSystem invoiceSystem = mock(InvoiceSystem.class);

        DeliveryController deliveryController = new DeliveryController(invoiceSystem);
        deliveryController.setSelectedCar("car1");
        verify(invoiceSystem).setSelectedCar("car1");
        assertEquals(true,deliveryController.setSelectedCar("car1"));

    }

    @Test
    public void setSelectedCar_invalidCarString()
    {
        InvoiceSystem invoiceSystem = mock(InvoiceSystem.class);
        DeliveryController deliveryController = new DeliveryController(invoiceSystem);

        assertEquals(false,deliveryController.setSelectedCar(null));
        assertEquals(false,deliveryController.setSelectedCar(""));
    }

    @Test
    public void setPdfPath_validPath()
    {
        InvoiceSystem invoiceSystem = mock(InvoiceSystem.class);
        DeliveryController deliveryController = new DeliveryController(invoiceSystem);

        deliveryController.setPdfPath("C:\\Desktop");
        verify(invoiceSystem).setPath("C:\\Desktop");

    }
}
