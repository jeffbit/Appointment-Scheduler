/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BrownJeffSoftware2;

import static BrownJeffSoftware2.AppointmentRecordController.getAppointmentIndex;
import classes.AlertBox;
import classes.Appointment;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jeffr
 */
public class UpdateAppointmentController implements Initializable {

    @FXML
    private Text titleText;
    @FXML
    private Text DescriptionText;
    @FXML
    private Text locationText;
    @FXML
    private Text contactText;
    @FXML
    private Text typeText;
    @FXML
    private Text urlText;
    @FXML
    private Button saveBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private TextField urlTextField;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private ComboBox<String> contactComboBox;
    private TextField contactTextField;
    @FXML
    private TextField locationTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField titleTextField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private ComboBox<String> startMinComboBox;
    @FXML
    private ComboBox<String> startHrComboBox;

    private TextField startDateTimeTextField;
    private TextField endDateTimeTextField;
    private Appointment appointment;
    private int appointmentIndex = getAppointmentIndex();
    private MySqlConnections mySql;
    private ObservableList<String> hours = FXCollections.observableArrayList();
    private ObservableList<String> minutes = FXCollections.observableArrayList();
    private ZoneId zone = ZoneId.systemDefault();

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        hours.addAll("08", "09", "10", "11",
                "12", "13", "14", "15");
        minutes.addAll("00", "15", "30", "45");
        //Sets combobox to lists and loads each table column with appropriate data to be updated
        appointment = Appointment.appointments.get(appointmentIndex);
        this.titleTextField.setText(appointment.getTitle());
        this.descriptionTextField.setText(appointment.getDescription());
        this.contactComboBox.setValue(appointment.getContact());
        this.typeComboBox.setValue(appointment.getType());
        this.urlTextField.setText(appointment.getUrl());
        this.locationTextField.setText(appointment.getLocation());
        LocalDate date = appointment.getStart().toLocalDate();
        int hour = appointment.getStart().getHour();
        int min = appointment.getStart().getMinute();

        this.startDatePicker.setValue(date);
        this.startHrComboBox.setItems(hours);
        this.startMinComboBox.setItems(minutes);

        //Sets textfields to uneditable
        this.titleTextField.setEditable(false);
        this.descriptionTextField.setEditable(false);
        this.contactComboBox.setEditable(false);
        this.typeComboBox.setEditable(false);
        this.urlTextField.setEditable(false);
        this.locationTextField.setEditable(false);

    }

    private void handleCancelBtnAction(MouseEvent event) throws IOException {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("AppointmentRecord.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // validates startdatetime and localdate to be updated
    private boolean validateUpdateAppointment() {

        LocalDate selectedDate = this.startDatePicker.getValue();
        String startHr = this.startHrComboBox.getValue();
        String startMin = this.startMinComboBox.getValue();
        int intStartHr = Integer.parseInt(startHr);
        int intStartMin = Integer.parseInt(startMin);
        LocalTime startTime = LocalTime.of(intStartHr, intStartMin);
        LocalDateTime localStartDate = LocalDateTime.of(selectedDate, startTime);
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

        if (selectedDate == null) {
            AlertBox.display("Error", "Date must be selected ");

        }

        if (startHr == null || startMin == null) {
            AlertBox.display("Error", "Appointment Time must be entered");

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

    @FXML
    private void handleSaveBtnAction(ActionEvent event) throws IOException {
        // checks to see if appointment can be validated, if false no execution of update, else appointment will be updated
        if (validateUpdateAppointment() == false) {

        } else {
            this.mySql = new MySqlConnections();

            String sql = "UPDATE appointment SET start = ?, end = ?, lastUpdate = now(), lastUpdateBy = ? WHERE appointmentId = ?";
            LocalDate startDate = this.startDatePicker.getValue();
            String startHr = this.startHrComboBox.getValue();
            String startMin = this.startMinComboBox.getValue();
            LocalDateTime startLdt = LocalDateTime.of(startDate.getYear(), startDate.getMonthValue(),
                    startDate.getDayOfMonth(), Integer.parseInt(startHr), Integer.parseInt(startMin));
           
            //UTC time for db storage
            ZonedDateTime zonedStartUtc = startLdt.atZone(zone).withZoneSameInstant(ZoneId.of("UTC"));
            // zonedtime for current user timezone
            ZonedDateTime newLocalStart = zonedStartUtc.withZoneSameInstant(zone);

            //UTC time for db storage
            ZonedDateTime zonedEndUtc = zonedStartUtc.plusMinutes(30);
            // zonedtime for current user timezone
            ZonedDateTime newLocalEnd = zonedEndUtc.withZoneSameInstant(zone);

            try {
                Connection conn = MySqlConnections.ConnectDB();
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, zonedStartUtc.toLocalDateTime().toString());
                ps.setString(2, zonedEndUtc.toLocalDateTime().toString());
                ps.setString(3, LoginScreenController.user.getUserName());
                ps.setInt(4, AppointmentRecordController.currentAppointment.getAppointmentId());
                ps.executeUpdate();

            } catch (SQLException e) {
                System.out.println("Error in sql value " + e);

            }
            // updates currently selected customers tableview start and end time appointments
            AppointmentRecordController.currentAppointment.setStart(newLocalStart.toLocalDateTime());
            AppointmentRecordController.currentAppointment.setEnd(newLocalEnd.toLocalDateTime());

            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("AppointmentRecord.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
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
            //AlertBox.display("Error", "Appointment cannot be scheduled before 8am or after 5pm");
            //}

        } catch (Exception e) {
            System.out.println("Exception Error " + e);
        }

    }

}
