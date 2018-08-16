package net.nostalogic.schema.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import net.nostalogic.schema.dto.CompatiblePartsDto;

public interface CompatibilityRepository extends CrudRepository<CompatiblePartsDto, Integer> {

    List<CompatiblePartsDto> findAllByFirstPartOrSecondPart(int firstPart, int secondPart);
    Optional<CompatiblePartsDto> findTopByFirstPartAndSecondPart(int firstPart, int secondPart);

}
