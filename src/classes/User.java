/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author jeffr
 */
public class User {

    private int userId;
    private String userName;
    private String password;
    private static ObservableList<User> users = FXCollections.observableArrayList();

    public User(String userName, String password, int userId) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
    }

    public User(String userName, String password) {

        this.userName = userName;
        this.password = password;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static ObservableList<User> getUsers() {
        return users;
    }

}
