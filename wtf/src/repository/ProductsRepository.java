package repository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Product;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.math.BigDecimal;
import java.util.List;

public class ProductsRepository implements AutoCloseable{

    private Configuration configuration;
    private SessionFactory factory;

    public ProductsRepository(Configuration configuration) {
        this.configuration = configuration;
        factory = configuration.buildSessionFactory();
    }

    @Override
    public void finalize() {
        System.out.println("Book instance is getting destroyed");
        factory.close();
    }

    public boolean addNewProduct(Product product)
    {
        try(Session session = factory.openSession())
        {
            Transaction transaction = session.beginTransaction();
            session.persist(product);
            transaction.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public boolean updateProduct(Product product)
    {
        try(Session session = factory.openSession())
        {
            Transaction transaction = session.beginTransaction();
            session.update(product);
            transaction.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteProduct(Product product)
    {
        try(Session session = factory.openSession())
        {
            Transaction transaction = session.beginTransaction();
            session.delete(product);
            transaction.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public ObservableList<Product> getAllProducts()
    {
        List<Product> products = null;
        Session session = factory.openSession();
        products = session.createQuery("from Product",Product.class).list();
        return FXCollections.observableArrayList(products);
    }

    public static void main(String[] args)
    {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        ProductsRepository repository = new ProductsRepository(configuration);
        Product product = new Product("Articol 1",new BigDecimal(24.152));

        product.setId(3);
        product.setProductName("Articol 2");
        product.setPrice(new BigDecimal(100.25));

        //repository.updateProduct(product);
        //repository.addNewProduct(product);
        repository.deleteProduct(product);
        for(Product p : repository.getAllProducts())
        {
            System.out.println(p);
        }
        repository.finalize();
    }

    @Override
    public void close() throws Exception {
        factory.close();
    }
}
