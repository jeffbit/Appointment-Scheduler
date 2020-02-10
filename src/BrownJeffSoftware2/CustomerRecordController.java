package BrownJeffSoftware2;

import classes.AlertBox;
import classes.Customer;
import static classes.Customer.customers;
import classes.MySqlConnections;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jeffr
 */
public class CustomerRecordController implements Initializable {

    @FXML
    private Button addCustomerBtn;
    @FXML
    private Button updateCustomerBtn;
    @FXML
    private Button deleteCustomerBtn;
    @FXML
    private Button exitBtn;
    @FXML
    private Button appointmentBtn;
    @FXML
    private TableColumn<Customer, String> nameColumn;
    @FXML
    private TableColumn<Customer, String> addressColumn;
    @FXML
    private TableColumn<Customer, String> phoneNumberColumn;
    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private Button logFileButton;

    // for Database Connection 
    private static MySqlConnections mySql;
    private static int customerIndex;
    public static Customer currentCustomer;
    //HOLDS CURRENTLY SELECTED CUSTOMERS NAME AND ADDRESS FOR UPDATING
    public static String currentCustomerName;
    public static String currentCustomerAddress;
    public static int customerIndex() {
        return customerIndex;
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        //sets column in table to propertyvalue associated with mysql database
        this.nameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        this.addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        this.phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        //Sets the mysql data to the customerTable record 
        customerTable.setItems(customers);
        customerTable.refresh();

    }

    @FXML
    private void handleExitBtn(MouseEvent event) {
        // exits out of application on exit button click
        System.exit(0);

    }

    @FXML
    private void handleAddCustomerBtn(MouseEvent event) throws IOException {
        //switches to AddCustomer screen on button click
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("AddCustomer.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleUpdateCustomerBtn(MouseEvent event) throws IOException {
        //switches to UpdateCustomer controller if customer is selected, if not alertbox is displayed

        if (customerTable.getSelectionModel().getSelectedItem() == null) {
            AlertBox.display("Error", "No customer Selected");

        } else {
            // GETS THE CURRENTLY SELECTED CUSTOMER FROM TABLE AND INSERTS CUSTOMER INFO INTO UPDATE PAGE
            currentCustomer = customerTable.getSelectionModel().getSelectedItem();
            customerIndex = Customer.customers.indexOf(currentCustomer);
            currentCustomerName = currentCustomer.getCustomerName();
            currentCustomerAddress = currentCustomer.getAddress();
            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("UpdateCustomer.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }

    }

    @FXML
    private void handleDeleteCustomerBtn(MouseEvent event) {
        //if customer is not selected alertbox displays, if customer is selected, customer is deleted
        if (customerTable.getSelectionModel().getSelectedItem() == null) {
            AlertBox.display("Error", "No customer Selected");

        } else {
            //Stores currently selected customer name in String for alertbox display
            String selectedCustomerName = this.customerTable.getSelectionModel().getSelectedItem().getCustomerName();
            //calls sql statement to remove customer from dataabase and list

            //Comfirmation alertbox display to confirm removal of selectedcustomer from database
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Customer will be Deleted");
            alert.setContentText("Delete customer " + selectedCustomerName + " ?");
            //alertbox waits for user response to delete customer, if user clicks ok, customer is deleted from database and tableview
            //if cancel is clicked alert closes and goes back to customerrecord screen

            alert.showAndWait().ifPresent((response -> {
                if (response == ButtonType.OK) {
                    try {
                        deleteCustomer();
                        Node node = (Node) event.getSource();
                        Stage stage = (Stage) node.getScene().getWindow();
                        Parent root = FXMLLoader.load(getClass().getResource("CustomerRecord.fxml"));
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();
                    } catch (IOException ex) {
                        System.out.println("error during deletion");
                    }
                } else {
                    try {
                        Node node = (Node) event.getSource();
                        Stage stage = (Stage) node.getScene().getWindow();
                        Parent root = FXMLLoader.load(getClass().getResource("CustomerRecord.fxml"));
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();
                    } catch (IOException ex) {
                        System.out.println("cancel customer deletion");
                    }

                }
            }));

        }
    }

    @FXML
    private void handleAppointmentBtn(MouseEvent event) throws IOException {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("AppointmentRecord.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // allows user to open logFile saved on desktop to view timstamp of user Logins
    @FXML
    private void handleOpenLogFile(ActionEvent event) {
        File file = new File("log.txt");
        if (file.exists()) {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException e) {
                    System.out.println("Error Opening Log File: " + e.getMessage());
                }
            }
        }
    }

    public void deleteCustomer() {
        mySql = new MySqlConnections();
        String sql = "DELETE customer.*, address.* from customer, address WHERE customer.customerName = ? AND customer.addressId = address.addressId";

        try {
            Connection conn = MySqlConnections.ConnectDB();
            PreparedStatement ps = conn.prepareStatement(sql);
            //selects currently highlighted row in tableview and assigns it to customerinfo
            Customer deleteCustomer = (Customer) this.customerTable.getSelectionModel().getSelectedItem();

            //removes values from Database  and updates Database 
            ps.setString(1, deleteCustomer.getCustomerName());
            ps.executeUpdate();
            //removes values and refreshes updated table views 
            this.customerTable.getItems().remove(deleteCustomer);
            this.customerTable.refresh();

        } catch (SQLException e) {
            System.out.println("error during during deletion " + e);

        }
    }

    public static ObservableList<Customer> selectCustomerRecord() {
        mySql = new MySqlConnections();
        String sql = "SELECT customer.customerId, customer.customerName, address.address, address.phone, address.postalCode"
                + " FROM address"
                + " INNER JOIN customer"
                + " ON address.addressId = customer.addressId";
        //creates connection to database and selects all information from customer table in database,
        //if connection is unsucessful, goes to catch sqlexception

        try {
            Connection conn = MySqlConnections.ConnectDB();
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                Customer customer = new Customer(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
                customers.add(customer);

            }
            conn.close();

        } catch (SQLException e) {
            System.out.println("sql error " + e);
        }

        return customers;

    }

}
