package kahoot.schema.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity(name = "manufacturer")
public class ManufacturerDto {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @PrimaryKeyJoinColumn(name = "manufacturerid")
    private Integer manufacturerid;

    @Column(name = "manufacturername", unique = true)
    private String manufacturerName;

    public ManufacturerDto() {
    }

    public Integer getManufacturerid() {
        return manufacturerid;
    }

    public void setManufacturerid(Integer manufacturerid) {
        this.manufacturerid = manufacturerid;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }
}
