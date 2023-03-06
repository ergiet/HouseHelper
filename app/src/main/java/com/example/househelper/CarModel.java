package com.example.househelper;

import com.google.firebase.Timestamp;

public class CarModel {
    String make;
    String model;
    String vin;
    String plate;
    Timestamp firstRegistration;
    Timestamp mot;
    Timestamp insurance;
    boolean is_private;
    String user;

    public CarModel(String make, String model, String vin, String plate, Timestamp firstRegistration,
                    Timestamp mot, Timestamp insurance, boolean is_private, String user) {
        this.make = make;
        this.model = model;
        this.vin = vin;
        this.plate = plate;
        this.firstRegistration = firstRegistration;
        this.mot = mot;
        this.insurance = insurance;
        this.is_private = is_private;
        this.user = user;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getVin() {
        return vin;
    }

    public String getPlate() {
        return plate;
    }

    public Timestamp getFirstRegistration() {
        return firstRegistration;
    }

    public Timestamp getMot() {
        return mot;
    }

    public Timestamp getInsurance() {
        return insurance;
    }

    public Boolean getIs_private() {
        return is_private;
    }

    public String getUser() {
        return user;
    }
}