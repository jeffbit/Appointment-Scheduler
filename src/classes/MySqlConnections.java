/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import static classes.Customer.customers;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.ObservableList;
import javax.swing.JOptionPane;

/**
 *
 * @author jeffr
 */
public class MySqlConnections {

    private Connection conn = null;
    private static MySqlConnections mySql;
    

    public MySqlConnections() {

    }

    public static Connection ConnectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://52.206.157.109:3306/U04fyV", "U04fyV", "53688229620");

            return conn;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            return null;

        }

    }

    public static ObservableList<Customer> selectCustomerRecord() {
        mySql = new MySqlConnections();
        String sql = "SELECT customer.customerName, address.address, address.phone, address.postalCode"
                + " FROM address"
                + " INNER JOIN customer"
                + " ON address.addressId = customer.addressId";
        //creates connection to database and selects all information from customer table in database,
        //if connection is unsucessful, goes to catch sqlexception

        try {
            Connection conn = MySqlConnections.ConnectDB();
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                Customer customer = new Customer(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
                customers.add(customer);

            }
            conn.close();

        } catch (SQLException e) {
            System.out.println("sql error " + e);
        }

        return customers;

    }

}
