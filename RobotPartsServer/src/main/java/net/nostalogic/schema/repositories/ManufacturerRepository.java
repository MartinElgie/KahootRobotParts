package net.nostalogic.schema.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import net.nostalogic.schema.dto.ManufacturerDto;

@Repository
public interface ManufacturerRepository extends CrudRepository<ManufacturerDto, Integer> {

    Optional<ManufacturerDto> findTopByManufacturerName(String manufacturerName);

}
