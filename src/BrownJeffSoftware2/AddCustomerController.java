/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BrownJeffSoftware2;

import classes.Address;
import classes.AlertBox;
import classes.Customer;
import classes.MySqlConnections;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
public class AddCustomerController implements Initializable {

    private TextField customerIdTextField;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField addressTextField;
    @FXML
    private TextField postalCodeTextField;
    @FXML
    private TextField phoneTextField;
    @FXML
    private TextField address2TextField;
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
    private Text postalCode;
    @FXML
    private Text cityText;
    @FXML
    private ComboBox cityComboBox;
    @FXML
    private Text countryText;
    @FXML
    private ComboBox<String> countryComboBox;

    private Address address;
    private MySqlConnections mySql;
    private static final ObservableList<String> countryComboBoxList = FXCollections.observableArrayList("United States", "Japan");
    private final ObservableList<String> usStringList = FXCollections.observableArrayList("Washington", "New York", "Los Angeles", "Chicago", "Houston", "Phoenix");
    private static final ObservableList<String> japaneseStringList = FXCollections.observableArrayList("Tokyo", "Tovohasi", "Okazaki", "Toyota", "Akita");
    private String address2 = "";

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        this.countryComboBox.setItems(countryComboBoxList);

    }

    @FXML
    private void handleSaveBtn(MouseEvent event) throws IOException, SQLException {
        //validates customer info to make sure added information meets requirements
        if (validateCustomer() == false) {

        } else {
            // calls db connection
            this.mySql = new MySqlConnections();
            String customerSql = "INSERT INTO customer(customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy)VALUES(?,?,1, now(), ?, now(), ?)";
            String addressSql = "INSERT INTO address(address,address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy )VALUES(?,?,?,?,?,now(), ?, now(), ?)";

            try {
                //creates db connection
                Connection conn = MySqlConnections.ConnectDB();
                //customer db call
                PreparedStatement customerPs = conn.prepareStatement(customerSql);
                customerPs.setString(1, this.nameTextField.getText());
                customerPs.setInt(2, returnAddressId());
                customerPs.setString(3, LoginScreenController.user.getUserName());
                customerPs.setString(4, LoginScreenController.user.getUserName());
                customerPs.executeUpdate();
                // address database call
                PreparedStatement addressPs = conn.prepareStatement(addressSql);
                addressPs.setString(1, this.addressTextField.getText());
                addressPs.setString(2, address2);
                addressPs.setInt(3, cityChoice());
                addressPs.setString(4, this.postalCodeTextField.getText());
                addressPs.setString(5, this.phoneTextField.getText());
                addressPs.setString(6, LoginScreenController.user.getUserName());
                addressPs.setString(7, LoginScreenController.user.getUserName());
                addressPs.executeUpdate();

                Customer.customers.add(new Customer(this.nameTextField.getText(), this.addressTextField.getText(), this.phoneTextField.getText(), this.postalCode.getText()));

            } catch (SQLException e) {
                System.out.println("sql error " + e);

            }

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

    // method to validate customer data enetered into fields
    public boolean validateCustomer() {
        String customerName = this.nameTextField.getText();
        String address = this.addressTextField.getText();
        String phoneNumber = this.phoneTextField.getText();
        String postalCode = this.postalCodeTextField.getText();
        String cityBox = this.cityComboBox.getValue().toString();

        if (customerName == null || customerName.length() == 0) {
            AlertBox.display("Error", "Name must entered");
            return false;
        }
        if (address == null || address.length() == 0) {
            AlertBox.display("Error", "Address must be entered");
            return false;
        }
        if (phoneNumber == null || phoneNumber.length() == 0) {
            AlertBox.display("Error", "Phone Number must entered");
            return false;
        }
        if (postalCode == null || postalCode.length() == 0) {
            AlertBox.display("Error", "Postal Code must be entered");
            return false;
        }
        if (cityBox == null || cityBox.length() == 0) {
            AlertBox.display("Error", "City must be chosen from drop down menu");
            return false;
        }
        return true;

    }

    // sets city box based on users choice in country box
    public void countryChoice() {
        if (this.countryComboBox.getValue().equals("United States")) {
            this.cityComboBox.setItems(usStringList);
        } else {
            this.cityComboBox.setItems(japaneseStringList);
        }
    }

    // call to return addressId from address and increment by 1
    public int returnAddressId() {
        this.mySql = new MySqlConnections();
        String sql = "SELECT addressId from address";
        try {
            Connection conn = MySqlConnections.ConnectDB();
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                address = new Address(rs.getInt(1) + 1);

            }

        } catch (SQLException e) {
            System.out.println("Error " + e);

        }

        return address.getAddressId();

    }

    // method to return city choice from combo box
    public int cityChoice() {
        if (this.cityComboBox.getValue().equals("Washington")) {
            return 1;
        }
        if (this.cityComboBox.getValue().equals("New York")) {
            return 2;
        }
        if (this.cityComboBox.getValue().equals("Los Angeles")) {
            return 3;
        }
        if (this.cityComboBox.getValue().equals("Chicago")) {
            return 4;
        }
        if (this.cityComboBox.getValue().equals("Houston")) {
            return 5;
        }
        if (this.cityComboBox.getValue().equals("Phoenix")) {
            return 6;
        }
        if (this.cityComboBox.getValue().equals("Tokyo")) {
            return 7;
        }
        if (this.cityComboBox.getValue().equals("Tovohasi")) {
            return 8;
        }
        if (this.cityComboBox.getValue().equals("Okazaki")) {
            return 9;
        }
        if (this.cityComboBox.getValue().equals("Toyota")) {
            return 10;
        }
        if (this.cityComboBox.getValue().equals("Akita")) {
            return 11;
        } else {
            return 0;
        }
    }

}
