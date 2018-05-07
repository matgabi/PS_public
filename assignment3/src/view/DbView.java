package view;

import cotrollers.IDbController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.converter.BigDecimalStringConverter;

import model.*;
import model.interfaces.ClientInterface;
import model.interfaces.Report;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Optional;


public class DbView extends BorderPane {

    private InvoiceSystem invoiceSystem;
    private IDbController dbController;

    @FXML
    TableView<Product> productsTable;
    @FXML
    TextField productNameText;
    @FXML
    TextField productPriceText;
    @FXML
    Label productLogLabel;


    @FXML
    TableView<ClientInterface> clientsTable;
    @FXML
    TextField clientNameText;
    @FXML
    TextField clientCuiText;
    @FXML
    TextField clientNrText;
    @FXML
    TextField clientSediuText;
    @FXML
    TextField clientJudetText;
    @FXML
    TextField clientContText;
    @FXML
    TextField clientBankText;
    @FXML
    Label clientLogLabel;

    @FXML
    ComboBox<model.ReportFactory.REPORT_TYPE> raportsComboBox;
    @FXML
    TextArea raportContentText;


    public DbView(InvoiceSystem invoiceSystem) throws IOException {
        this.invoiceSystem = invoiceSystem;

        this.dbController = new IDbController(invoiceSystem);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("dbview.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
    }

    @FXML
    private void initialize()
    {

        ObservableList<ReportFactory.REPORT_TYPE> raports = FXCollections.observableArrayList(EnumSet.allOf(model.ReportFactory.REPORT_TYPE.class));

        raportsComboBox.setItems(raports);
        raportsComboBox.getSelectionModel().selectFirst();

        initializeProductTable();
        initializeClientTable();
        showProductTable();
        showClientTable();
    }

    private void showProductTable() {
        productsTable.setItems(dbController.getAllProducts());
    }

    private void showClientTable()
    {
        clientsTable.setItems(dbController.getAllClients());
    }

    private void initializeClientTable()
    {
        try {
            TableColumn id = clientsTable.getColumns().get(0);
            TableColumn name = clientsTable.getColumns().get(1);
            TableColumn cui = clientsTable.getColumns().get(2);
            TableColumn nr = clientsTable.getColumns().get(3);
            TableColumn sediu = clientsTable.getColumns().get(4);
            TableColumn judet = clientsTable.getColumns().get(5);
            TableColumn cont = clientsTable.getColumns().get(6);
            TableColumn banca = clientsTable.getColumns().get(7);

            id.setCellValueFactory(new PropertyValueFactory<Client,Integer>("id"));
            name.setCellValueFactory(new PropertyValueFactory<Client,String>("name"));
            cui.setCellValueFactory(new PropertyValueFactory<Client,String>("cui"));
            nr.setCellValueFactory(new PropertyValueFactory<Client,String>("nr"));
            sediu.setCellValueFactory(new PropertyValueFactory<Client,String>("sediu"));
            judet.setCellValueFactory(new PropertyValueFactory<Client,String>("judet"));
            cont.setCellValueFactory(new PropertyValueFactory<Client,String>("cont"));
            banca.setCellValueFactory(new PropertyValueFactory<Client,String>("banca"));


            name.setCellFactory(TextFieldTableCell.forTableColumn());
            name.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Client,String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<Client,String> event) {
                    ClientInterface client= (Client)event.getTableView().getItems().get(event.getTablePosition().getRow());
                    client.setName(event.getNewValue());
                    dbController.updateClient(client);
                    System.out.println(client);
                }
            });

            cui.setCellFactory(TextFieldTableCell.forTableColumn());
            cui.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Client,String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<Client,String> event) {
                    ClientInterface client= (Client)event.getTableView().getItems().get(event.getTablePosition().getRow());
                    client.setCui(event.getNewValue());
                    dbController.updateClient(client);
                    System.out.println(client);
                }
            });

            nr.setCellFactory(TextFieldTableCell.forTableColumn());
            nr.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Client,String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<Client,String> event) {
                    ClientInterface client= (Client)event.getTableView().getItems().get(event.getTablePosition().getRow());
                    client.setNr(event.getNewValue());
                    dbController.updateClient(client);
                    System.out.println(client);
                }
            });


            ContextMenu rightClickMenu = new ContextMenu();
            MenuItem deleteClient = new MenuItem("Sterge client");

            deleteClient.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    ClientInterface client = clientsTable.getSelectionModel().getSelectedItem();
                    dbController.deleteClient(client);
                    showClientTable();
                }
            });
            rightClickMenu.getItems().add(deleteClient);

            clientsTable.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(javafx.scene.input.MouseEvent event) {
                    if(event.getButton() == MouseButton.SECONDARY)
                    {
                        rightClickMenu.show(clientsTable,event.getScreenX(),event.getScreenY());
                    }
                    else
                    {
                        rightClickMenu.hide();
                    }
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void initializeProductTable()
    {
        try {
            TableColumn id = productsTable.getColumns().get(0);
            TableColumn name = productsTable.getColumns().get(1);
            TableColumn price = productsTable.getColumns().get(2);

            id.setCellValueFactory(new PropertyValueFactory<Product,Integer>("id"));
            name.setCellValueFactory(new PropertyValueFactory<Product,String>("productName"));
            price.setCellValueFactory(new PropertyValueFactory<Product,BigDecimal>("price"));

            price.setCellFactory(c -> new TableCell<Product,BigDecimal>(){
                @Override
                protected void updateItem(BigDecimal item,boolean empty)
                {

                    setText(empty ? null : item.setScale(2,BigDecimal.ROUND_HALF_UP).toString());
                }
            });

            name.setCellFactory(TextFieldTableCell.forTableColumn());
            name.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Product,String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<Product,String> event) {
                    Product product = (Product)event.getTableView().getItems().get(event.getTablePosition().getRow());
                    product.setProductName(event.getNewValue());
                    dbController.updateProduct(product);
                    System.out.println(product);
                }
            });

            //price.setCellFactory(TextFieldTableCell.forTableColumn());
            price.setCellFactory(TextFieldTableCell.<Product,BigDecimal>forTableColumn(new BigDecimalStringConverter()));
            price.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Product,BigDecimal>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<Product,BigDecimal> event) {
                    Product product = (Product)event.getTableView().getItems().get(event.getTablePosition().getRow());
                    product.setPrice(event.getNewValue());
                    dbController.updateProduct(product);
                    System.out.println(product);
                }
            });

            ContextMenu rightClickMenu = new ContextMenu();
            MenuItem deleteProduct = new MenuItem("Sterge produs");

            deleteProduct.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Product product = productsTable.getSelectionModel().getSelectedItem();
                    dbController.deleteProduct(product);
                    showProductTable();
                }
            });
            rightClickMenu.getItems().add(deleteProduct);

            productsTable.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(javafx.scene.input.MouseEvent event) {
                    if(event.getButton() == MouseButton.SECONDARY)
                    {
                        rightClickMenu.show(productsTable,event.getScreenX(),event.getScreenY());
                    }
                    else
                    {
                        rightClickMenu.hide();
                    }
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @FXML
    private void addNewProduct()
    {
            Optional<String> productName = InputParser.tryParseString(productNameText.getText());
            if(!productName.isPresent())
            {
                productLogLabel.setText("invalid name");
                return;
            }
            Optional<Double> price = InputParser.tryParseDouble(productPriceText.getText());
            if (!price.isPresent()){
                productLogLabel.setText("invalid price");
                return;
            }
            Product product = new Product(productName.get(),new BigDecimal(price.get()));
            dbController.addNewProduct(product);
            showProductTable();
            productNameText.setText("");
            productPriceText.setText("");
            System.out.println(product);
            productLogLabel.setText("");


    }

    @FXML
    private void addNewClient()
    {
        Optional<String> name = InputParser.tryParseString(clientNameText.getText());
        if(!name.isPresent())
        {
            clientLogLabel.setText("Invalid name");
            return;
        }
        Optional<String> cui = InputParser.tryParseString(clientCuiText.getText());
        if(!cui.isPresent())
        {
            clientLogLabel.setText("Invalid cui");
            return;
        }
        Optional<String> nr = InputParser.tryParseString(clientNrText.getText());
        if(!nr.isPresent())
        {
            clientLogLabel.setText("Invalid nr");
            return;
        }
        Optional<String> sediu = InputParser.tryParseString(clientSediuText.getText());
        if(!sediu.isPresent())
        {
            clientLogLabel.setText("Invalid sediu");
            return;
        }
        Optional<String> judet = InputParser.tryParseString(clientJudetText.getText());
        if(!judet.isPresent())
        {
            clientLogLabel.setText("Invalid judet");
            return;
        }
        Optional<String> cont = InputParser.tryParseString(clientContText.getText());
        if(!cont.isPresent())
        {
            clientLogLabel.setText("Invalid cont");
            return;
        }
        Optional<String> banca = InputParser.tryParseString(clientBankText.getText());
        if(!banca.isPresent())
        {
            clientLogLabel.setText("Invalid bank");
            return;
        }
        ClientInterface client = new Client();
        client.setName(name.get());
        client.setCui(cui.get());
        client.setNr(nr.get());
        client.setSediu(sediu.get());
        client.setJudet(judet.get());
        client.setCont(cont.get());
        client.setBanca(banca.get());

        dbController.addNewClient(client);
        clientLogLabel.setText("");

        clientNameText.setText("");
        clientCuiText.setText("");
        clientNrText.setText("");
        clientSediuText.setText("");
        clientJudetText.setText("");
        clientContText.setText("");
        clientBankText.setText("");

        showClientTable();

    }

    @FXML
    public void generateReport()
    {
        Report report = dbController.getReport(raportsComboBox.getValue());
        raportContentText.setText(report.getReport());
    }
}
