package classes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author jeffr
 */
public class Customer {

    private int customerId;
    private String customerName;
    private int addressId;
    private String cityName;
    private String countryName;
    private String phone;
    private String address;
    private String postalCode;
    public static ObservableList<Customer> customers = FXCollections.observableArrayList();

    //CONSTRUCTORS
  

    public Customer(int customerId, String customerName, String address, String phone, String postalCode) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.address = address;
        this.phone = phone;
        this.postalCode = postalCode;

    }

    public Customer(String customerName, String address, String phone, String postalCode) {
        this.customerName = customerName;
        this.address = address;
        this.phone = phone;
        this.postalCode = postalCode;
    }

    public Customer(String customerName) {
        this.customerName = customerName;
    }

    //GETTERS
    public int getAddressId() {
        return addressId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getCityName() {
        return cityName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    //SETTERS
    public void setCustomerName(String name) {
        this.customerName = name;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setAddress(int addressId) {
        this.addressId = addressId;

    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

}
