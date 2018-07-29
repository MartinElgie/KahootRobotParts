package kahoot.schema.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import kahoot.schema.dto.RobotPartDto;

@Repository
public interface RobotPartRepository extends CrudRepository<RobotPartDto, Integer> {

    Optional<RobotPartDto> getByPartid(int partId);
    Optional<RobotPartDto> getBySerialnumber(String serialNumber);
    List<RobotPartDto> getAllBySerialnumberIn(List<String> serialNumbers);
    RobotPartDto findTopByPartid(int partid);
    List<RobotPartDto> findAllByPartidIn(List<Integer> partIds);

}
