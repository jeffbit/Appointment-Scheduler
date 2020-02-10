/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BrownJeffSoftware2;

import static BrownJeffSoftware2.CustomerRecordController.customerIndex;
import classes.AlertBox;
import classes.MySqlConnections;
import classes.Customer;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jeffr
 */
public class UpdateCustomerController implements Initializable {

    @FXML
    private TextField nameTextField;
    @FXML
    private TextField addressTextField;
    @FXML
    private Button saveBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Text nameText;
    @FXML
    private Text addressText;
    @FXML
    private Text phoneText;
    @FXML
    private TextField phoneTextField;
    @FXML
    private TextField postalCodeTextField;
    @FXML
    private Text postalCode;
    @FXML
    private ComboBox cityComboBox;
    @FXML
    private ComboBox<String> countryComboBox;

    private int customerIndex = customerIndex();
    private MySqlConnections mySql;
    private Customer customer;
    private String customerName;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //gets customer from currently selected customer in tableview
        customer = Customer.customers.get(customerIndex);
        // populates textfields with currently selected customer information
        this.nameTextField.setText(customer.getCustomerName());
        this.addressTextField.setText(customer.getAddress());
        this.phoneTextField.setText(customer.getPhone());
        this.postalCodeTextField.setText(customer.getPostalCode());

    }

    @FXML
    private void handleSaveBtn(MouseEvent event) throws IOException {
        // validates customer, if false alertbox error is thrown , else customer is updated
        if (validateUpdateCustomer() == false) {

        } else {

            //sets connection to database
            this.mySql = new MySqlConnections();
            String customerSql = "UPDATE customer SET customerName = ?, lastUpdate = now(), lastUpdateBy = ? WHERE CustomerName = ?";
            String addressSql = "UPDATE address SET address = ?, postalCode = ?, phone = ?, lastUpdate = now(), lastUpdateBy = ? WHERE address = ?";

            try {
                Connection conn = MySqlConnections.ConnectDB();
                PreparedStatement customerPs = conn.prepareStatement(customerSql);
                PreparedStatement addressPs = conn.prepareStatement(addressSql);

                customerPs.setString(1, this.nameTextField.getText());
                customerPs.setString(2, LoginScreenController.user.getUserName());
                customerPs.setString(3, CustomerRecordController.currentCustomerName);
                customerPs.executeUpdate();

                addressPs.setString(1, this.addressTextField.getText());
                addressPs.setString(2, this.postalCodeTextField.getText());
                addressPs.setString(3, this.phoneTextField.getText());
                addressPs.setString(4, LoginScreenController.user.getUserName());
                addressPs.setString(5, CustomerRecordController.currentCustomerAddress);
                addressPs.executeUpdate();

            } catch (SQLException e) {
                System.out.println(" SQL error " + e);
            }
            //updates currently selected customer information in the tableview
            CustomerRecordController.currentCustomer.setCustomerName(this.nameTextField.getText());
            CustomerRecordController.currentCustomer.setAddress(this.addressTextField.getText());
            CustomerRecordController.currentCustomer.setPhone(this.phoneTextField.getText());

            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("CustomerRecord.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    @FXML
    private void handleCancelBtn(MouseEvent event) throws IOException {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("CustomerRecord.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // validates textfield data before updating customer info
    private boolean validateUpdateCustomer() {
        String name = this.nameTextField.getText();
        String address = this.addressTextField.getText();
        String phone = this.phoneTextField.getText();
        String postalCode = this.postalCodeTextField.getText();

        if (name == null || name.length() == 0) {
            AlertBox.display("Error", "Name must be entered correctly");
            return false;
        }
        if (address == null || address.length() == 0) {
            AlertBox.display("Error", "Address must be entered correctly");
            return false;
        }
        if (phone == null || phone.length() == 0) {
            AlertBox.display("Error", "Phone must be entered correctly");
            return false;
        }
        if (postalCode == null || postalCode.length() == 0) {
            AlertBox.display("Error", "Postal Code must be entered properly");
            return false;
        }

        return true;
    }

}
