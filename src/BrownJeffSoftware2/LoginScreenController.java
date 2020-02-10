/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BrownJeffSoftware2;

import classes.MySqlConnections;
import classes.AlertBox;
import classes.Logger;
import classes.User;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

/**
 *
 * @author jeffr
 */
public class LoginScreenController implements Initializable {

    @FXML
    private Label label;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private Button LoginButton;
    @FXML
    private Button ClearButton;
    @FXML
    private Button exitButton;
    @FXML
    private Text timeText;

    private Connection conn = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;

    public static String userName;
    public static User user;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // sets locale to  EN to display alert in english instead of spanish
        //Locale.setDefault(new Locale("en", "EN"));
        //Changes Locale to ES to to display alert in spanish instead of english
        //Locale.setDefault(new Locale("es", "ES"));
        //sets timeText and dateText to display current LocalTime and Date for current Zone
        ZoneId zone = ZoneId.systemDefault();
       LocalDateTime localTime = LocalDateTime.now(zone);
     
       
       
       
        ZonedDateTime zonedDateTime = localTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime newZonedDateTime = zonedDateTime.withZoneSameInstant(zone);
        LocalDateTime fixedTime = newZonedDateTime.toLocalDateTime();
        
        this.timeText.setText(fixedTime.now().toString());
       

    }

    @FXML
    private void handleLoginAction(MouseEvent event) throws IOException, SQLException {
        // Checks to see if username and password are correct and displays alertbox according to information entered
        this.conn = MySqlConnections.ConnectDB();
        String sql = "Select * from user where userName = ? and password = ?";

        try {
            this.pst = this.conn.prepareStatement(sql);
            this.pst.setString(1, this.usernameTextField.getText());
            this.pst.setString(2, this.passwordTextField.getText());

            this.rs = this.pst.executeQuery();
            if (this.rs.next()) {

                //adds new user if correct credentials
                user = new User(rs.getString("userName"), rs.getString("password"), rs.getInt("userId"));

                Node node = (Node) event.getSource();
                Stage stage = (Stage) node.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("CustomerRecord.fxml"));
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
                //Alerts user if there are any appointments within 15 minutes of loginf
                AppointmentRecordController.appointmentReminder();

                // if correct credentials are accepted, Timestamp and username are logged to logger
                Logger.log(rs.getString("userName"));

            } else {
                //retrives locale and sets message to dislay in english or spanish depending on locale
                Locale.getDefault();
                ResourceBundle rb = ResourceBundle.getBundle("Language.files/rb");
                AlertBox.display(rb.getString("error"), rb.getString("errormessage"));

            }

        } catch (IOException | SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    @FXML
    private void handleClearAction(MouseEvent event) {
        // on button click clears username and password textfield
        this.passwordTextField.setText("");
        this.usernameTextField.setText("");

    }

    @FXML
    private void handleExitAction(MouseEvent event) {
        // on button click exits out of login screen / closes application 
        System.exit(0);

    }

    
}
