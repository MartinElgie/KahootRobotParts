package kahoot.schema.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "partcompatibility")
public class CompatiblePartsDto {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "compatibilityid")
    private Integer compatibilityId;

    @Column(name = "firstpart")
    private Integer firstPart;

    @Column(name = "secondpart")
    private Integer secondPart;

    public CompatiblePartsDto() {
    }

    public Integer getFirstPart() {
        return firstPart;
    }

    public void setFirstPart(Integer firstPart) {
        this.firstPart = firstPart;
    }

    public Integer getSecondPart() {
        return secondPart;
    }

    public void setSecondPart(Integer secondPart) {
        this.secondPart = secondPart;
    }
}
