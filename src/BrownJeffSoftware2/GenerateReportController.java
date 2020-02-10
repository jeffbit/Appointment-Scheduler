/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BrownJeffSoftware2;

import classes.MySqlConnections;
import classes.Report;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jeffr
 */
public class GenerateReportController implements Initializable {

    @FXML
    private Text generateReportText;
    @FXML
    private Button backButton;
    @FXML
    private TableView<Report> byTypeTableView;
    @FXML
    private TableColumn<Report, Integer> byTypeCountColumn;
    @FXML
    private TableColumn<Report, String> byTypeTypeColumn;
    @FXML
    private TableView<Report> byMonthTableView;
    @FXML
    private TableColumn<Report, Integer> byMonthCountColumn;
    @FXML
    private TableColumn<Report, String> byMonthMonthColumn;
    @FXML
    private Text byMonthText;
    @FXML
    private Text byTypeText;
    @FXML
    private Text byConsultantText;
    @FXML
    private TableColumn<Report, Integer> byConsultantCustomerIdColumn;
    @FXML
    private TableColumn<Report, String> byConsultantConsultantColumn;
    @FXML
    private TableView<Report> byConsultantTableView;
    @FXML
    private TableColumn<Report, String> byConsultantTitleColumn;
    @FXML
    private TableColumn<Report, String> byConsultantDescriptionColumn;
    @FXML
    private TableColumn<Report, String> byConsultantLocationColumn;
    @FXML
    private TableColumn<Report, String> byConsultantStartColumn;
    @FXML
    private TableColumn<Report, Integer> byConsultantAppointmentIdColumn;
    @FXML
    private ComboBox<String> consultantComboBox;

    private MySqlConnections mySql;
    private final ObservableList<Report> reportByConsultant = FXCollections.observableArrayList();
    private final ObservableList<Report> reportByMonth = FXCollections.observableArrayList();
    private final ObservableList<Report> reportByType = FXCollections.observableArrayList();
    private final ObservableList<String> consultants = FXCollections.observableArrayList("user1", "user2", "test", "wflick");

    public GenerateReportController() {
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.consultantComboBox.setItems(consultants);

        //report by type
        this.byTypeCountColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        this.byTypeTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        this.byTypeTableView.setItems(reportByType);
        generateReportByType();

        // report by Month
        this.byMonthCountColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        this.byMonthMonthColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        this.byMonthTableView.setItems(reportByMonth);
        generateReportByMonth();

        // report by consultant schedule
        this.byConsultantAppointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        this.byConsultantCustomerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        this.byConsultantTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        this.byConsultantDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        this.byConsultantLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        this.byConsultantStartColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        this.byConsultantTableView.setItems(reportByConsultant);

        // TODO
    }

    @FXML
    private void handleBackButton(ActionEvent event) throws IOException {
        //switches back to AppointmentRecord Screen
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("AppointmentRecord.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void generateReportByMonth() {
        this.mySql = new MySqlConnections();

        // select all appointments by month(start) that appointment is created in
        String sql = "SELECT count(customerId), DATE_FORMAT(start, \"%Y-%m\") AS month FROM appointment GROUP BY month;";

        try {
            Connection conn = MySqlConnections.ConnectDB();
            ResultSet rs = conn.createStatement().executeQuery(sql);

            while (rs.next()) {
                Report byMonth = new Report(rs.getInt(1), rs.getString(2), null);
                this.reportByMonth.add(byMonth);

            }

        } catch (SQLException e) {
            System.out.println("Month error " + e);

        }

    }

    private void generateReportByType() {
        this.mySql = new MySqlConnections();

        // selects count of all appointments by type of appointment 
        String sql = "SELECT COUNT(customerId), type FROM appointment GROUP BY type";

        try {
            Connection conn = MySqlConnections.ConnectDB();
            ResultSet rs = conn.createStatement().executeQuery(sql);

            while (rs.next()) {
                Report byType = new Report(rs.getInt(1), null, rs.getString(2));
                this.reportByType.add(byType);

            }

        } catch (SQLException e) {
            System.out.println("Type error " + e);

        }
    }

    private void generateReportByConsultant(int userId) {
        this.mySql = new MySqlConnections();

        // groups appointments by consultant (user) and displays in tableview
        String sql = "SELECT appointmentId, customerId, title, description, location, start FROM appointment WHERE userId = '" + userId + "'";

        try {
            Connection conn = MySqlConnections.ConnectDB();
            ResultSet rs = conn.createStatement().executeQuery(sql);

            while (rs.next()) {
                Report byConsultant = new Report(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6));

                this.reportByConsultant.add(byConsultant);

            }

        } catch (SQLException e) {
            System.out.println(" Consultant error " + e);

        }

    }

    @FXML
    private void handleConsultantComboBox(ActionEvent event) {
        if (this.consultantComboBox.getSelectionModel().getSelectedItem().equals("wflick")) {
            this.reportByConsultant.clear();
            generateReportByConsultant(1);
        } else if (this.consultantComboBox.getSelectionModel().getSelectedItem().equals("user1")) {
            this.reportByConsultant.clear();
            generateReportByConsultant(2);
        } else if (this.consultantComboBox.getSelectionModel().getSelectedItem().equals("user2")) {
            this.reportByConsultant.clear();
            generateReportByConsultant(3);
        } else if (this.consultantComboBox.getSelectionModel().getSelectedItem().equals("test")) {
            this.reportByConsultant.clear();
            generateReportByConsultant(4);
        }
    }

}
