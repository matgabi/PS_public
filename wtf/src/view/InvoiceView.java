package view;


import cotrollers.InvoiceController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import model.*;
import model.interfaces.ClientInterface;
import model.interfaces.TableItem;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

public class InvoiceView extends BorderPane {

    private InvoiceSystem invoiceSystem;
    private InvoiceController invoiceController;

    @FXML
    TableView<TableItem> productsItemsTable;
    @FXML
    TableView<Factura> invoicesTable;
    @FXML
    ComboBox<Product> productsComboBox;
    @FXML
    TextField priceTextField;
    @FXML
    TextField quantityTextField;
    @FXML
    ComboBox<String> umComboBox;
    @FXML
    TextField discountTextField;

    @FXML
    ComboBox<ClientInterface> clientsComboBox;
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
    TextField clientBancaText;

    @FXML
    TextField totalValueText;
    @FXML
    TextField totalTVAText;
    @FXML
    TextField totalText;

    @FXML
    RadioButton tva19RadioButton;
    @FXML
    RadioButton tva9RadioButton;

    @FXML
    Label errorLogLabel;

    public InvoiceView (InvoiceSystem invoiceSystem) throws IOException {
        this.invoiceSystem = invoiceSystem;
        this.invoiceController = new InvoiceController(invoiceSystem);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("invoiceview.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
    }



    @FXML
    private void initialize()
    {
        ToggleGroup tvaTogleGroup = tva9RadioButton.getToggleGroup();
        tvaTogleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1) {

                RadioButton chk = (RadioButton)t1.getToggleGroup().getSelectedToggle(); // Cast object to radio button
                invoiceController.setCurrentTVA(chk.getText());
                System.out.println("Selected Radio Button - "+chk.getText());

            }
        });

        clientsComboBox.setItems(invoiceController.getAllClients());
        clientsComboBox.getSelectionModel().selectFirst();
        clientsComboBox.setConverter(new StringConverter() {
            @Override
            public String toString(Object object) {
                return ((ClientInterface)object).getName();
            }

            @Override
            public Object fromString(String string) {
                return null;
            }
        });
        autocompleteClientInfo();

        productsComboBox.setItems(invoiceController.getAllProducts());
        productsComboBox.getSelectionModel().selectFirst();
        productsComboBox.setConverter(new StringConverter() {
            @Override
            public String toString(Object object) {
                return ((Product)object).getProductName();
            }

            @Override
            public Object fromString(String string) {
                return null;
            }
        });
        autoCompleteProductInfo();

        ObservableList<String> um = FXCollections.observableArrayList("l","sac","buc");
        umComboBox.setItems(um);
        umComboBox.getSelectionModel().selectFirst();

        initializeItemsTableView();
        initializeInvoicesTable();
    }

    private void initializeInvoicesTable() {
        TableColumn name = invoicesTable.getColumns().get(0);
        TableColumn date = invoicesTable.getColumns().get(1);

        name.setCellValueFactory(new PropertyValueFactory<Factura,String>("name"));
        date.setCellValueFactory(new PropertyValueFactory<Factura,Date>("date"));

        ContextMenu rightClickMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Sterge factura");
        MenuItem loadInvoice = new MenuItem("Incarca factura");

        deleteItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Factura factura = invoicesTable.getSelectionModel().getSelectedItem();

                invoiceController.deleteFactura(factura);

            }
        });
        loadInvoice.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Factura factura = invoicesTable.getSelectionModel().getSelectedItem();

                invoiceController.setSelectedClient(factura.getClient());
                invoiceController.setCurrentTVA(factura.getTva().getValue().toString());
                invoiceController.setSelectedSeller(factura.getSeller());
                invoiceController.setSelectedCar(factura.getCar());
                invoiceController.setSelectedEmitor(factura.getEmitor());
                invoiceController.setSelectedProducts(FXCollections.observableArrayList(factura.getProducts()));
                autoUpdateView();
            }
        });

        rightClickMenu.getItems().add(deleteItem);
        rightClickMenu.getItems().add(loadInvoice);

        invoicesTable.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                if(event.getButton() == MouseButton.SECONDARY)
                {
                    rightClickMenu.show(invoicesTable,event.getScreenX(),event.getScreenY());
                }
                else
                {
                    rightClickMenu.hide();
                }
            }
        });

        invoicesTable.setItems(invoiceController.getFacturi());
    }

    private void initializeItemsTableView() {
        TableColumn name = productsItemsTable.getColumns().get(0);
        TableColumn um = productsItemsTable.getColumns().get(1);
        TableColumn quantity = productsItemsTable.getColumns().get(2);
        TableColumn pretUnitar = productsItemsTable.getColumns().get(3);
        TableColumn value = productsItemsTable.getColumns().get(4);
        TableColumn tva = productsItemsTable.getColumns().get(5);
        TableColumn discount = productsItemsTable.getColumns().get(6);
        TableColumn discountValue = productsItemsTable.getColumns().get(7);
        TableColumn discountTvaValue = productsItemsTable.getColumns().get(8);

        name.setCellValueFactory(new PropertyValueFactory<TableItem,String>("name"));
        um.setCellValueFactory(new PropertyValueFactory<TableItem,String>("um"));
        quantity.setCellValueFactory(new PropertyValueFactory<TableItem,Integer>("quantity"));
        pretUnitar.setCellValueFactory(new PropertyValueFactory<TableItem,BigDecimal>("price"));
        value.setCellValueFactory(new PropertyValueFactory<TableItem,BigDecimal>("value"));
        tva.setCellValueFactory(new PropertyValueFactory<TableItem,BigDecimal>("tvaValue"));
        discount.setCellValueFactory(new PropertyValueFactory<TableItem,BigDecimal>("discount"));
        discountValue.setCellValueFactory(new PropertyValueFactory<TableItem,BigDecimal>("discountValue"));
        discountTvaValue.setCellValueFactory(new PropertyValueFactory<TableItem,BigDecimal>("discountTvaValue"));

        ContextMenu rightClickMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Sterge produs");

        deleteItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TableItem product = productsItemsTable.getSelectionModel().getSelectedItem();
                invoiceController.deleteItem(product);
                showProductItemsTable();
            }
        });
        rightClickMenu.getItems().add(deleteItem);

        productsItemsTable.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                if(event.getButton() == MouseButton.SECONDARY)
                {
                    rightClickMenu.show(productsItemsTable,event.getScreenX(),event.getScreenY());
                }
                else
                {
                    rightClickMenu.hide();
                }
            }
        });

    }

    @FXML
    private void autocompleteClientInfo()
    {
        ClientInterface client = clientsComboBox.getSelectionModel().getSelectedItem();
        invoiceController.setSelectedClient(client);
        if(client!= null)
        {
            clientNameText.setText(invoiceController.getSelectedClient().getName());
            clientCuiText.setText(invoiceController.getSelectedClient().getCui());
            clientNrText.setText(invoiceController.getSelectedClient().getNr());
            clientSediuText.setText(invoiceController.getSelectedClient().getSediu());
            clientJudetText.setText(invoiceController.getSelectedClient().getJudet());
            clientContText.setText(invoiceController.getSelectedClient().getCont());
            clientBancaText.setText(invoiceController.getSelectedClient().getBanca());
        }
    }

    @FXML
    private void autoCompleteProductInfo()
    {
        Product product = (Product)productsComboBox.getSelectionModel().getSelectedItem();
        if(product != null)
        {
            priceTextField.setText(product.getPrice().toString());
        }
    }

    @FXML
    private void handleAddItemButton()
    {

        Product product = productsComboBox.getSelectionModel().getSelectedItem();
        String quantity = quantityTextField.getText();
        String um = umComboBox.getSelectionModel().getSelectedItem();
        String discount = discountTextField.getText();
        TVA tva = new TVA(TVA.TVA_TYPE.NORMAL19_TVA);

        Optional<TableItem> productItem = invoiceController.insertItem(product,quantity,discount,um,invoiceController.getCurrentTVA());
        if(!productItem.isPresent())
        {
            System.out.println("EEEEEEEROR MA FREND");
            errorLogLabel.setText("Introduceti totate datele necesare");
        }
        else
        {
            showProductItemsTable();
            errorLogLabel.setText("");
        }

    }

    private void showProductItemsTable() {
        ObservableList<TableItem> tableItems = FXCollections.observableArrayList(invoiceController.getSelectedProducts());
        TableItem discountItem = invoiceController.getCurrentDiscount();
        tableItems.add(discountItem);
        productsItemsTable.setItems(tableItems);
        totalValueText.setText(invoiceController.getTotalValue().toString());
        totalTVAText.setText(invoiceController.getTotalTvaValue().toString());
        totalText.setText(invoiceController.getTotal().toString());
    }

    private void autoUpdateView()
    {
        ObservableList<TableItem> tableItems = FXCollections.observableArrayList(invoiceController.getSelectedProducts());
        TableItem discountItem = invoiceController.getCurrentDiscount();
        tableItems.add(discountItem);
        productsItemsTable.setItems(tableItems);
        totalValueText.setText(invoiceController.getTotalValue().toString());
        totalTVAText.setText(invoiceController.getTotalTvaValue().toString());
        totalText.setText(invoiceController.getTotal().toString());

        clientsComboBox.getSelectionModel().select(invoiceController.getSelectedClient());
        autocompleteClientInfo();

        ToggleGroup tvaTogleGroup = tva9RadioButton.getToggleGroup();
        if(invoiceController.getCurrentTVA().getValue().toString().equals("19"))
        {
            tvaTogleGroup.selectToggle(tva19RadioButton);
        }
        else
        {
            tvaTogleGroup.selectToggle(tva9RadioButton);
        }
    }

    public void refreshInfo()
    {
        Product product = productsComboBox.getSelectionModel().getSelectedItem();
        ClientInterface client = clientsComboBox.getSelectionModel().getSelectedItem();

        clientsComboBox.setItems(invoiceController.getAllClients());
//        clientsComboBox.getSelectionModel().selectFirst();
        productsComboBox.setItems(invoiceController.getAllProducts());
//        productsComboBox.getSelectionModel().selectFirst();
        productsComboBox.getSelectionModel().select(product);
        clientsComboBox.getSelectionModel().select(client);
    }

    public void reset()
    {
        showProductItemsTable();
        //productsItemsTable.setItems(null);
        for(TableItem t : invoiceController.getSelectedProducts())
        {
            System.out.println(t);
        }
    }
}
