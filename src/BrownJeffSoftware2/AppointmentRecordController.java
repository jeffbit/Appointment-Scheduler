/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BrownJeffSoftware2;

import classes.AlertBox;
import classes.Appointment;
import static classes.Appointment.appointments;
import classes.MySqlConnections;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jeffr
 */
public class AppointmentRecordController implements Initializable {

    @FXML
    private Button addAppointmentBtn;
    @FXML
    private Button updateAppointment;
    @FXML
    private Button deleteAppointmentBtn;
    @FXML
    private Button backBtn;
    @FXML
    private Button exitBtn;
    @FXML
    private RadioButton thisWeekRadioBtn;
    @FXML
    private RadioButton thisMonthRadioBtn;
    @FXML
    private TableView<Appointment> appointmentTable;
    @FXML
    private TableColumn<Appointment, String> titleColumn;
    @FXML
    private TableColumn<Appointment, Integer> customerIdColumn;
    @FXML
    private TableColumn<Appointment, Integer> appointmentIdColumn;
    @FXML
    private TableColumn<Appointment, String> descriptionColumn;
    @FXML
    private TableColumn<Appointment, String> locationColumn;
    @FXML
    private TableColumn<Appointment, String> contactColumn;
    @FXML
    private TableColumn<Appointment, String> typeColumn;
    @FXML
    private TableColumn<Appointment, ZonedDateTime> startTimeColumn;
    @FXML
    private TableColumn<Appointment, LocalDateTime> endTimeColumn;
    @FXML
    private TableColumn<Appointment, String> urlTimeColumn;
    @FXML
    private Button consultantReportButton;

    private static MySqlConnections mySql;
    private static int appointmentIndex;
    private static Appointment appointment;
    private static LocalDateTime startDate;
    private static LocalDateTime endDate;
    private static ZoneId zone = ZoneId.systemDefault();

    public static Appointment currentAppointment;

    public static int getAppointmentIndex() {
        return appointmentIndex;
    }
    @FXML
    private Text curentDateText;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        this.curentDateText.setText(LocalDate.now().toString());

        //loads table column with information from database
        this.appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        this.customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        this.titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        this.descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        this.locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        this.contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        this.typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        this.urlTimeColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        this.startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        this.endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
        this.appointmentTable.setItems(appointments);
        this.appointmentTable.refresh();

    }

    @FXML
    private void handleAddAppointmentAction(ActionEvent event) throws IOException {
        // switches to addAppointment screen
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("AddAppointment.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleUpdateAppointmentAction(ActionEvent event) throws IOException {
        //switches to update appointment Screen and loads fields with currently selected item if item is selected in table
        if (appointmentTable.getSelectionModel().getSelectedItem() == null) {
            AlertBox.display("Error", "No Appointment Selected");

        } else {
            //gets currently selected appointment from table and loads data from currently selected appointment into updateappointment page
            currentAppointment = appointmentTable.getSelectionModel().getSelectedItem();
            appointmentIndex = Appointment.appointments.indexOf(currentAppointment);

            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("UpdateAppointment.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }

    }

    @FXML
    private void handleDeleteAppointmentAction(ActionEvent event) {
        if (appointmentTable.getSelectionModel().getSelectedItem() == null) {
            AlertBox.display("Error", "No Appointment Selected");

        } else {
            //Stores currently selected appoint title  in String for alertbox display
            String selectedAppointment = this.appointmentTable.getSelectionModel().getSelectedItem().getTitle();
            int selectedAppointmentId = this.appointmentTable.getSelectionModel().getSelectedItem().getAppointmentId();
            //calls sql statement to remove customer from dataabase and list
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Appointment will be Deleted");
            alert.setContentText("Delete appointment " + selectedAppointmentId + " with title " + selectedAppointment);
            //alertbox waits for user response to delete customer, if user clicks ok, customer is deleted from database and tableview
            //if cancel is clicked alert closes and goes back to customerrecord screen
            alert.showAndWait().ifPresent((response -> {
                if (response == ButtonType.OK) {
                    try {
                        //call to delete appointment if ok button is clicked on alert pop up
                        deleteAppointment();
                        Node node = (Node) event.getSource();
                        Stage stage = (Stage) node.getScene().getWindow();
                        Parent root = FXMLLoader.load(getClass().getResource("AppointmentRecord.fxml"));
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();
                    } catch (IOException ex) {
                        System.out.println("error during deletion");
                    }
                } else {
                    try {
                        //switches to AppointmentRecord screen if cancel is clicked during alert pop up
                        Node node = (Node) event.getSource();
                        Stage stage = (Stage) node.getScene().getWindow();
                        Parent root = FXMLLoader.load(getClass().getResource("AppointmentRecord.fxml"));
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
    private void handleBackBtnAction(ActionEvent event) throws IOException {
        //switches to customerRecord screen
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("CustomerRecord.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleExitBtnAction(ActionEvent event) {
        //exits and closes application
        System.exit(0);

    }

    // selects all appointments from database and loads them into the appointmenttable
    public static ObservableList<Appointment> selectAppointmentRecord() {
        AppointmentRecordController.mySql = new MySqlConnections();
        String sql = "SELECT  appointment.appointmentId, customer.customerId, appointment.userId, "
                + "appointment.title, appointment.description, appointment.location, appointment.contact,"
                + " appointment.type, appointment.url, appointment.start, appointment.end"
                + " FROM appointment"
                + " INNER JOIN customer"
                + " ON customer.customerId = appointment.customerId";
        //creates connection to database and selects all information from customer table in database,
        //if connection is unsucessful, goes to catch sqlexception

        try {
            Connection conn = MySqlConnections.ConnectDB();
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                // pulls timestamp from db
                Timestamp startTimestamp = rs.getTimestamp(10);
                //UTC time for db storage
                ZonedDateTime zonedStartUtc = startTimestamp.toLocalDateTime().atZone(zone).withZoneSameInstant(ZoneId.of("UTC"));
                // zonedtime for current user timezone
                ZonedDateTime newLocalStart = zonedStartUtc.withZoneSameInstant(zone);
                // pulls timestamp from db
                Timestamp endTimestamp = rs.getTimestamp(11);
                //UTC time for db storage
                ZonedDateTime zonedEndUtc = endTimestamp.toLocalDateTime().atZone(zone).withZoneSameInstant(ZoneId.of("UTC"));
                // zonedtime for current user timezone
                ZonedDateTime newLocalEnd = zonedEndUtc.withZoneSameInstant(zone);

                appointment = new Appointment(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getString(4), rs.getString(5), rs.getString(6),
                        rs.getString(7), rs.getString(8), rs.getString(9), newLocalStart.toLocalDateTime(), newLocalEnd.toLocalDateTime());

                appointments.add(appointment);

            }
            conn.close();

        } catch (SQLException e) {
            System.out.println("sql error " + e);
        }

        return appointments;

    }

    //deletes appointment from fxml tableview and database
    private void deleteAppointment() {
        AppointmentRecordController.mySql = new MySqlConnections();
        String sql = "DELETE FROM appointment where appointmentID = ?";

        try {
            Connection conn = MySqlConnections.ConnectDB();
            PreparedStatement ps = conn.prepareStatement(sql);
            Appointment deletedAppointments = (Appointment) this.appointmentTable.getSelectionModel().getSelectedItem();

            ps.setInt(1, deletedAppointments.getAppointmentId());
            ps.executeUpdate();
            this.appointmentTable.getItems().remove(deletedAppointments);
            this.appointmentTable.refresh();

        } catch (SQLException e) {
            System.out.println("error in sql " + e);

        }
    }

    @FXML
    private void handleSortByThisWeek(MouseEvent event) {

        if (this.thisMonthRadioBtn.isSelected()) {
            // does not execute is thismonthradiobutton is selected

        } else {
            // if radio button is clicked appointments in current week will be displayed, when unclicked all appointments will be displayed
            if (this.thisWeekRadioBtn.isSelected()) {
                selectByWeek();

            } else {
                this.appointmentTable.setItems(appointments);
                this.appointmentTable.refresh();

            }
        }
    }

    @FXML
    private void handleAppointmentByMonth(MouseEvent event) {
        if (this.thisWeekRadioBtn.isSelected()) {
            // does not execute if thisweekradiobutton is selected
        } else {
            // if radio button is clicked appointments in current month will be displayed, when unclicked all appointments will be displayed
            if (this.thisMonthRadioBtn.isSelected()) {
                selectByMonth();

            } else {
                this.appointmentTable.setItems(appointments);
                this.appointmentTable.refresh();

            }
        }
    }

    @FXML
    private void handleGenerateReports(ActionEvent event) throws IOException {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("GenerateReport.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // select data by week to be displayed in current table view
    private void selectByWeek() {
        LocalDateTime now = LocalDateTime.now().minusDays(1);
        LocalDateTime nowPlus7 = now.plusDays(7);
        FilteredList<Appointment> filteredByWeek = new FilteredList<>(appointments);
        // Lambda is used here to setpredicate on all items in the filtered list by looping through each row and getting 
        //each row in the filteredList LocalDateTime
        // instead of implementing an interface to define this I used a simple method that is reusable
        filteredByWeek.setPredicate(row -> {

            LocalDateTime rowDate = row.getStart();

            return rowDate.isAfter(now) && rowDate.isBefore(nowPlus7);
        });
        this.appointmentTable.setItems(filteredByWeek);
    }

    //select data by month to be displayed in current table view
    private void selectByMonth() {
        LocalDateTime now = LocalDateTime.now().minusDays(1);
        LocalDateTime nowPlus1Month = now.plusMonths(1);
        FilteredList<Appointment> filteredByMonth = new FilteredList<>(appointments);
        // Lambda is used here to setpredicate on all items in the filtered list by looping through each row and getting 
        //each row in the filteredList LocalDateTime
        // instead of implementing an interface to define this I used a simple method that is reusable
        filteredByMonth.setPredicate(row -> {

            LocalDateTime rowDate = row.getStart();

            return rowDate.isAfter(now) && rowDate.isBefore(nowPlus1Month);
        });
        this.appointmentTable.setItems(filteredByMonth);
    }

    //gives a reminder to user on login if user has appointment within 15 minutes of login
    public static void appointmentReminder() {

        LocalDateTime now = LocalDateTime.now().minusMinutes(1);
        LocalDateTime nowPlus15Minutes = LocalDateTime.now().plusMinutes(15);

        FilteredList<Appointment> filteredByMin = new FilteredList<>(appointments);
        // Lambda is used here to setpredicate on all items in the filtered list by looping through each row and getting 
        //each row in the filteredList LocalDateTime
        // instead of implementing an interface to define this I used a simple method that is reusable
        filteredByMin.setPredicate(row -> {

            LocalDateTime rowDate = row.getStart();
            return rowDate.isAfter(now) && rowDate.isBefore(nowPlus15Minutes);

        });
        if (filteredByMin.isEmpty()) {

        } else {
            String title = filteredByMin.get(0).getTitle();
            String start = filteredByMin.get(0).getStart().toString();
            AlertBox.display("Appointment Reminder", "You have an appointment named " + title + " at " + start);

        }

    }

}
