package net.nostalogic.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


public class RobotPart implements Serializable {

    private String serialNumber;
    private String name;
    private String manufacturer;
    private float weightGrams;
    private List<String> compatibleParts = new ArrayList<>();


    @JsonProperty(value = "serial_number")
    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    @JsonProperty(value = "weight_grams")
    public float getWeightGrams() {
        return weightGrams;
    }

    public void setWeightGrams(float weightGrams) {
        this.weightGrams = weightGrams;
    }

    @JsonProperty(value = "compatible_parts")
    public List<String> getCompatibleParts() {
        return compatibleParts;
    }

}
