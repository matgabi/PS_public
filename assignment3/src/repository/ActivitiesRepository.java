package repository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Factura;
import model.FacturaInfo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import serialize.SerializeBill;

import java.util.List;

public class ActivitiesRepository implements AutoCloseable {

    private Configuration configuration;
    private SessionFactory factory;

    public ActivitiesRepository(Configuration configuration)
    {
        this.configuration = configuration;
        factory = configuration.buildSessionFactory();
    }

    public boolean addNewActivity(FacturaInfo facturaInfo) {
        try (Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(facturaInfo);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean updateActivity(FacturaInfo facturaInfo) {
        try (Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(facturaInfo);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteActivity(FacturaInfo facturaInfo) {
        try (Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.delete(facturaInfo);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public ObservableList<FacturaInfo> getAllActivities() {
        List<FacturaInfo> activities = null;
        Session session = factory.openSession();
        activities = session.createQuery("from FacturaInfo ", FacturaInfo.class).list();
        return FXCollections.observableArrayList(activities);
    }

    @Override
    public void close() throws Exception {
        factory.close();
    }

    public static void main(String[] args)
    {
        SerializeBill.deserialize();
        ObservableList<Factura> facturas = SerializeBill.getFacturi();
        ObservableList<FacturaInfo> facturaInfos = FXCollections.observableArrayList();

        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");

        ActivitiesRepository activitiesRepository = new ActivitiesRepository(configuration);

        for(Factura f : facturas)
        {
            FacturaInfo facturaInfo = new FacturaInfo();
            facturaInfo.setName(f.getName());
            facturaInfo.setClient(f.getClient().getName());
            facturaInfo.setSeller(f.getSeller().getName());
            facturaInfo.setEmitor(f.getEmitor().getFirstName());
            facturaInfo.setCar(f.getCar());
            facturaInfo.setDate(f.getDate());
            facturaInfo.setProducts(f.getProducts().size());
            facturaInfo.setTotal(f.getTotalValue());
            facturaInfo.setTotalTva(f.getTotalTvaValue());
            facturaInfo.setTotalDiscount(f.getTotalDiscountValue());
            facturaInfo.setTotalDiscountTva(f.getTotalDiscountTvaValue());

            //activitiesRepository.addNewActivity(facturaInfo);
        }

    }
}
