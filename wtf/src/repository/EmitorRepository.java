package repository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Emitor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class EmitorRepository implements AutoCloseable{

    private Configuration configuration;
    private SessionFactory factory;

    public EmitorRepository(Configuration configuration) {
        this.configuration = configuration;
        factory = configuration.buildSessionFactory();
    }

    public boolean addNewEmitor(Emitor emitor) {
        try (Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(emitor);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean updateEmitor(Emitor emitor) {
        try (Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(emitor);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteEmitor(Emitor emitor) {
        try (Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.delete(emitor);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public ObservableList<Emitor> getAllEmitors() {
        List<Emitor> emitors = null;
        Session session = factory.openSession();
        emitors = session.createQuery("from Emitor", Emitor.class).list();
        return FXCollections.observableArrayList(emitors);
    }

    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        EmitorRepository repository = new EmitorRepository(configuration);

        Emitor emitor = new Emitor();
        emitor.setFirstName("Gabriel");
        emitor.setLastName("Matasariu");
        emitor.setSeria("NT");
        emitor.setNr("763050");
        emitor.setCiEmitor("POLITIA NEAMT");

        repository.addNewEmitor(emitor);
        for(Emitor e : repository.getAllEmitors())
        {
            System.out.println(e);
        }
    }

    @Override
    public void close() throws Exception {
        this.factory.close();
    }
}
