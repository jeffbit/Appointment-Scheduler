/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BrownJeffSoftware2;

import classes.AlertBox;
import classes.Appointment;
import classes.Customer;
import static classes.Customer.customers;
import classes.MySqlConnections;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jeffr
 */
public class AddAppointmentController implements Initializable {

    @FXML
    private Button saveBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Text titleText;
    @FXML
    private Text DescriptionText;
    @FXML
    private Text locationText;
    @FXML
    private Text contactText;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField locationTextField;
    @FXML
    private Text typeText;
    @FXML
    private Text urlText;
    @FXML
    private TextField urlTextField;
    @FXML
    private TextField titleTextField;
    @FXML
    private TableView<Customer> AppointmentCustomerTable;
    @FXML
    private TableColumn<Customer, Integer> customerIdTableView;
    @FXML
    private TableColumn<Customer, Integer> customerNameTableView;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private ComboBox<String> startHrComboBox;
    @FXML
    private ComboBox<String> startMinComboBox;
    @FXML
    private ComboBox<String> contactComboBox;
    @FXML
    private ComboBox<String> typeComboBox;

    private MySqlConnections mySql;
    private static LocalDateTime startLdt;
    //observablelist for typecombobox and contactcombobox items
    private ObservableList<String> typeComboBoxList = FXCollections.observableArrayList("Status Updates", "Imformation Sharing", "Decision Making",
            "Problem Solving", "Team Building", "Workshop", "Issue Resolution", "Training", "Status Update Review");
    private ObservableList<String> contactComboBoxList = FXCollections.observableArrayList("John S", "Timothy P", "Ryan W", "Tina A", "Marco Z");
    ObservableList<String> hours = FXCollections.observableArrayList();
    ObservableList<String> minutes = FXCollections.observableArrayList();
    private ZoneId zone = ZoneId.systemDefault();

    // localdatetime to get start time of appointment
    public static LocalDateTime getStartLdt() {
        return startLdt;
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
        //sets type combobox to typecomboboxlist
        this.typeComboBox.setItems(typeComboBoxList);
        // sets contact combobox to contactcomboboxlist
        this.contactComboBox.setItems(contactComboBoxList);
        //loads table with current customers in database
        this.customerIdTableView.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        this.customerNameTableView.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        this.AppointmentCustomerTable.setItems(customers);

        // For Start and End TIme
        //user cannot choose appointment outside of business hours
        hours.addAll("08", "09", "10", "11",
                "12", "13", "14", "15");
        minutes.addAll("00", "15", "30", "45");

        startHrComboBox.setItems(hours);
        startMinComboBox.setItems(minutes);

    }

    //validates the appointment being added to appointmenttable
    private boolean validateAppointment() {
        Customer customer = this.AppointmentCustomerTable.getSelectionModel().getSelectedItem();
        String title = this.titleTextField.getText();
        String description = this.descriptionTextField.getText();
        String location = this.locationTextField.getText();
        String contact = this.contactComboBox.getSelectionModel().getSelectedItem();
        String type = this.typeComboBox.getValue();
        String url = this.urlTextField.getText();
        LocalDate startDate = this.startDatePicker.getValue();
        String startHr = this.startHrComboBox.getValue();
        String startMin = this.startMinComboBox.getValue();
        int intStartHr = Integer.parseInt(startHr);
        int intStartMin = Integer.parseInt(startMin);
        LocalTime startTime = LocalTime.of(intStartHr, intStartMin);
        LocalDateTime localStartDate = LocalDateTime.of(startDate, startTime);
        ZonedDateTime zonedStartTime = localStartDate.atZone(zone);
        //converts startLdt time to zonedDateTime at UTC time
        ZonedDateTime utcStartTime = ZonedDateTime.of(localStartDate, ZoneId.of("UTC"));
        LocalTime localUtcTime = utcStartTime.toLocalTime();

        ZonedDateTime endZonedDateTime = zonedStartTime.plusMinutes(30);

        LocalTime appointmentStart = LocalTime.of(8, 0);
        LocalTime appointmentEnd = LocalTime.of(16, 0);
        
        

        if (localUtcTime.isBefore(appointmentStart) || localUtcTime.isAfter(appointmentEnd)) {
            AlertBox.display("Error", "Appointment cannot take place outside of business hours");
            return false;

        }

        // if statements to make sure that added appointment is properly validated, if if statement is false AlertBox is displayed to user
        if (customer == null) {
            AlertBox.display("Error", "Customer must be selected");

        }
        if (title == null || title.length() == 0) {
            AlertBox.display("Error", "Title must be entered");

        }
        if (description == null || description.length() == 0) {
            AlertBox.display("Error", "Description must be entered");

        }
        if (location == null || location.length() == 0) {
            AlertBox.display("Error", "Location must be entered");

        }
        if (contact == null || contact.length() == 0) {
            AlertBox.display("Error", "Contact must be entered");

        }
        if (type == null || type.length() == 0) {
            AlertBox.display("Error", "Type must be selected");

        }
        if (url == null || url.length() == 0) {
            AlertBox.display("Error", "Url must be entered");

        }
        if (zonedStartTime == null) {
            AlertBox.display("Error", "Start time must be entered");
        } else {
            try {
                // tests to make sure appointment date and time are not overlapping each other
                if (overLappingAppointments(zonedStartTime, endZonedDateTime)) {
                    AlertBox.display("Error", "Error in overlap method");
                    return false;
                }

            } catch (SQLException e) {
                System.out.println("Error " + e);
                AlertBox.display("Error", "Appointment Times cannot overlap");

            }
        }

        outSideBussinessHours();
        return true;
    }
    //checks to make sure appointment date and time is during bussiness hours

    @FXML
    private void handleSaveBtnAction(ActionEvent event) throws IOException {
        //gets start date and time values to store 
        LocalDate startDate = startDatePicker.getValue();
        String startHour = startHrComboBox.getValue();
        String startMinute = startMinComboBox.getValue();
        // creates localdatetime of addedvalues
        startLdt = LocalDateTime.of(startDate.getYear(), startDate.getMonthValue(),
                startDate.getDayOfMonth(), Integer.parseInt(startHour), Integer.parseInt(startMinute));

        //UTC time for db storage
        ZonedDateTime zonedStartUtc = startLdt.atZone(zone).withZoneSameInstant(ZoneId.of("UTC"));
        // zonedtime for current user timezone
        ZonedDateTime newLocalStart = zonedStartUtc.withZoneSameInstant(zone);

        //UTC time for db storage
        ZonedDateTime zonedEndUtc = zonedStartUtc.plusMinutes(30);
        // zonedtime for current user timezone
        ZonedDateTime newLocalEnd = zonedEndUtc.withZoneSameInstant(zone);

        //validates appointment information, if false prints error, else continues to execute statement
        if (validateAppointment() == false) {
            System.out.println("Appointment cannot be added");

        } else {
            this.mySql = new MySqlConnections();
            String appointmentSql = "INSERT INTO appointment ( customerId, userId, title, description, location, contact,"
                    + " type, url,start, end, createDate, createdBy, lastUpdate, lastUpdateBy) "
                    + "VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now(), ?, now(), ?)";
            String appointmentIdCall = "SELECT LAST_INSERT_ID()";

            try {
                Connection conn = MySqlConnections.ConnectDB();
                PreparedStatement ps = conn.prepareStatement(appointmentSql);

                ps.setInt(1, this.AppointmentCustomerTable.getSelectionModel().getSelectedItem().getCustomerId());
                ps.setInt(2, LoginScreenController.user.getUserId());
                ps.setString(3, this.titleTextField.getText());
                ps.setString(4, this.descriptionTextField.getText());
                ps.setString(5, this.locationTextField.getText());
                ps.setString(6, this.contactComboBox.getSelectionModel().getSelectedItem());
                ps.setString(7, this.typeComboBox.getSelectionModel().getSelectedItem());
                ps.setString(8, this.urlTextField.getText());
                ps.setString(9, zonedStartUtc.toLocalDateTime().toString());
                ps.setString(10, zonedEndUtc.toLocalDateTime().toString());
                ps.setString(11, LoginScreenController.user.getUserName());
                ps.setString(12, LoginScreenController.user.getUserName());
                ps.executeUpdate();

                ResultSet rs = conn.createStatement().executeQuery(appointmentIdCall);
                while (rs.next()) {

                    //adds newly created appointment to appointment list to be added to tableview on appointmentrecord tableview
                    Appointment.appointments.add(new Appointment(rs.getInt(1), this.AppointmentCustomerTable.getSelectionModel().getSelectedItem().getCustomerId(), LoginScreenController.user.getUserId(), this.titleTextField.getText(), this.descriptionTextField.getText(), this.locationTextField.getText(),
                            this.contactComboBox.getSelectionModel().getSelectedItem(), this.typeComboBox.getSelectionModel().getSelectedItem(),
                            this.urlTextField.getText(), newLocalStart.toLocalDateTime(), newLocalEnd.toLocalDateTime()));
                }
                Node node = (Node) event.getSource();
                Stage stage = (Stage) node.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("AppointmentRecord.fxml"));
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

            } catch (SQLException e) {
                System.out.println("ERROR " + e);
            }

        }
    }

    @FXML
    private void handleCancelBtnAction(ActionEvent event) throws IOException {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("AppointmentRecord.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // checks to make sure appointment being added does not overlap with currently scheduled appointment in Database
    private boolean overLappingAppointments(ZonedDateTime start, ZonedDateTime end) throws SQLException {
        this.mySql = new MySqlConnections();
        // checks to see if start and end time overlap any existing times in database
        String sql = "SELECT * FROM appointment "
                + "WHERE ? BETWEEN start AND end OR ? BETWEEN start AND end OR ? < start AND ? > end "
                + "AND createdBy = ?";
        try {

            Connection conn = MySqlConnections.ConnectDB();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setTimestamp(1, Timestamp.valueOf(start.toLocalDateTime()));
            ps.setTimestamp(2, Timestamp.valueOf(end.toLocalDateTime()));
            ps.setTimestamp(3, Timestamp.valueOf(start.toLocalDateTime()));
            ps.setTimestamp(4, Timestamp.valueOf(end.toLocalDateTime()));
            ps.setString(5, LoginScreenController.user.getUserName());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return true;
            }

        } catch (SQLException e) {
            System.out.println("SQL Error " + e);

        } catch (Exception e) {
            System.out.println("Exception Error " + e);

        }
        return false;
    }

    // Exception control to prevent user from scheduling appointments outside of business hours including outside the hours of 8am-5pm,
    //and on Saturday and Sunday 
    private void outSideBussinessHours() {
        LocalDate datePicker = this.startDatePicker.getValue();
        int hour = Integer.parseInt(this.startHrComboBox.getValue());
        int min = Integer.parseInt(this.startMinComboBox.getValue());
        LocalTime time = LocalTime.of(hour, min);
        try {
            if (datePicker.getDayOfWeek() == DayOfWeek.SATURDAY || datePicker.getDayOfWeek() == DayOfWeek.SUNDAY) {
                AlertBox.display("Error", "Appointments cannot be scheduled on Saturday or Sunday");
            }
            // if appointment is outside business hours. combobox is set up so users cannot choose appointment outside hours
            //if (time.getHour() < 8 || time.getHour() > 5) {
            // AlertBox.display("Error", "Appointment cannot be scheduled before 8am or after 5pm");
            //}

        } catch (Exception e) {
            System.out.println("Exception Error " + e);
        }

    }

}
