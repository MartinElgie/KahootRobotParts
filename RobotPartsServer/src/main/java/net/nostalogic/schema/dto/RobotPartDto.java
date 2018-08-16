package net.nostalogic.schema.dto;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity(name = "robotpart")
public class RobotPartDto {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "partid")
    private Integer partid;

    @Column(name = "partname")
    private String name;

    @Column(name = "serialnumber")
    private String serialnumber;

    @Column(name = "weightgrams")
    private float weightgrams;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "manufacturerid")
    private ManufacturerDto manufacturer;

    public RobotPartDto() {
    }

    public Integer getPartid() {
        return partid;
    }

    public void setPartid(Integer partid) {
        this.partid = partid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerialnumber() {
        return serialnumber;
    }

    public void setSerialnumber(String serialnumber) {
        this.serialnumber = serialnumber;
    }

    public float getWeightgrams() {
        return weightgrams;
    }

    public void setWeightgrams(float weightgrams) {
        this.weightgrams = weightgrams;
    }

    public ManufacturerDto getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(ManufacturerDto manufacturer) {
        this.manufacturer = manufacturer;
    }
}
