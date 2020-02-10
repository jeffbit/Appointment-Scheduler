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
public class City {

    private int cityId;
    private String cityName;
    private int countryId;
    public static ObservableList<City> usList = FXCollections.observableArrayList();
    public static ObservableList<City> japaneseList = FXCollections.observableArrayList();
    
    
    //US CITIES
    public final static City washington = new City("Washington", 1);
    public final static City newYork = new City("New York", 2);
    public final static City losAngeles = new City("Los Angeles", 3);
    public final static City chicago = new City("Chicago", 4);
    public final static City houston = new City("Houston", 5);
    public final static City phoenix = new City("Phoenix", 6);
    //JAPANESE CITIES
    public final static City tokyo = new City("Tokyo", 7);
    public final static City tovohasi = new City("Tovohashi", 8);
    public final static City okazaki = new City("Okazaki", 9);
    public final static City toyota = new City("Toyota", 10);
    public final static City akita = new City("Akita", 11);

    public City(String cityName, int cityId) {
        this.cityName = cityName;
        this.cityId = cityId;
    }

    //GETTER
    public int getCityId() {
        return cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public int getCountryId() {
        return countryId;
    }

    public static void addCity(City city) {
        usList.add(city);
    }

}
