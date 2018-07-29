package kahoot.schema.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import kahoot.schema.dto.ManufacturerDto;

@Repository
public interface ManufacturerRepository extends CrudRepository<ManufacturerDto, Integer> {

    Optional<ManufacturerDto> findByManufacturerid(String manufacturerId);
    Optional<ManufacturerDto> findTopByManufacturerName(String manufacturerName);

}
