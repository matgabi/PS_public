package repository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.InvoicesAvailable;
import model.Seller;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class SellerRepository implements AutoCloseable{

    private Configuration configuration;
    private SessionFactory factory;

    public SellerRepository(Configuration configuration) {
        this.configuration = configuration;
        factory = configuration.buildSessionFactory();
    }

    public boolean addNewSeller(Seller seller) {
        try (Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(seller);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean updateSeller(Seller seller) {
        try (Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(seller);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteSeller(Seller seller) {
        try (Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.delete(seller);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public ObservableList<Seller> getAllSellers() {
        List<Seller> sellers = null;
        Session session = factory.openSession();
        sellers = session.createQuery("from Seller", Seller.class).list();
        return FXCollections.observableArrayList(sellers);
    }

    public boolean addNewBatch(InvoicesAvailable invoicesAvailable) {
        try (Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(invoicesAvailable);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean updateBatch(InvoicesAvailable invoicesAvailable) {
        try (Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(invoicesAvailable);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public ObservableList<InvoicesAvailable> getAvailableInvoices(Seller seller) {
        List<InvoicesAvailable> invoicesAvailables = null;
        Session session = factory.openSession();
        invoicesAvailables = session.createQuery("from InvoicesAvailable  where clientId = :clientId", InvoicesAvailable.class).setParameter("clientId",seller.getId()).list();
        return FXCollections.observableArrayList(invoicesAvailables);
    }

    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        SellerRepository repository = new SellerRepository(configuration);

//        Seller seller = new Seller();
//        seller.setName("SC Mat SRL");
//        seller.setCui("J22/111/2004");
//        seller.setNr("RO13272111");
//        seller.setSediu("Iasi");
//        seller.setCapital("200 RON");
//        seller.setJudet("Iasi");
//        seller.setCont("RO89BRDL52112565851");
//        seller.setBanca("Banca BRD");
//
//        repository.addNewSeller(seller);
//
//        for(Seller s : repository.getAllSellers())
//        {
//            System.out.println(s);
//        }

        InvoicesAvailable invoicesAvailable = new InvoicesAvailable();
        invoicesAvailable.setClientId(1);
        invoicesAvailable.setClientName("SC Mat SRL");

        //repository.addNewBatch(invoicesAvailable);
        invoicesAvailable.setNr(10);
        invoicesAvailable.setId(1);
        repository.updateBatch(invoicesAvailable);

        Seller seller = new Seller();
        seller.setId(2);

        for(InvoicesAvailable i : repository.getAvailableInvoices(seller))
        {
            System.out.println(i);
        }
    }

    @Override
    public void close() throws Exception {
        factory.close();
    }
}