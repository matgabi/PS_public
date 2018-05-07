package repository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Client;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class ClientRepository implements AutoCloseable{

    private Configuration configuration;
    private SessionFactory factory;

    public ClientRepository(Configuration configuration) {
        this.configuration = configuration;
        factory = configuration.buildSessionFactory();
    }

    public boolean addNewClient(Client client)
    {
        try(Session session = factory.openSession())
        {
            Transaction transaction = session.beginTransaction();
            session.persist(client);
            transaction.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean updateClient(Client client)
    {
        try(Session session = factory.openSession())
        {
            Transaction transaction = session.beginTransaction();
            session.update(client);
            transaction.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteClient(Client client)
    {
        try(Session session = factory.openSession())
        {
            Transaction transaction = session.beginTransaction();
            session.delete(client);
            transaction.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public ObservableList<Client> getAllClients()
    {
        List<Client> clients = null;
        Session session = factory.openSession();
        clients = session.createQuery("from Client",Client.class).list();
        return FXCollections.observableArrayList(clients);
    }

    public static void main(String[] args)
    {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        ClientRepository repository = new ClientRepository(configuration);

        Client client = new Client();
        client.setName("SC FMM SRL");
        client.setCui("J22/234/2007");
        client.setNr("RO10872342");
        client.setSediu("Cluj-Napoca");
        client.setJudet("Cluj");
        client.setCont("RO89BRDL25136136251");
        client.setBanca("Banca BRD");

        client.setId(3);
        client.setName("SC WTF SRL");

        //repository.addNewClient(client);
        //repository.updateClient(client);
        //repository.deleteClient(client);
        for(Client c  : repository.getAllClients())
        {
            System.out.println(c);
        }

    }

    @Override
    public void close() throws Exception {
        this.factory.close();
    }
}
