/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalTime;

/**
 *
 * @author jeffrey
 */
public class Logger {

    private static final String LOGGERFILENAME = "log.txt";

    public Logger() {
    }
        //creates logger to log user and timestamp when succesfully logged onto application
    public static void log(String userName) {
        try (FileWriter fw = new FileWriter(LOGGERFILENAME, true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter pw = new PrintWriter(bw)) {
            pw.println("Time at Login: " + LocalTime.now() + ".  User at Login: " + userName + ".");
        } catch (IOException ex) {
            System.out.println("Error at login: " + ex.getMessage());
        }

    }
}
