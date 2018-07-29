package kahoot.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import kahoot.entities.RobotPart;
import kahoot.schema.dto.CompatiblePartsDto;
import kahoot.schema.dto.ManufacturerDto;
import kahoot.schema.dto.RobotPartDto;
import kahoot.schema.repositories.CompatibilityRepository;
import kahoot.schema.repositories.ManufacturerRepository;
import kahoot.schema.repositories.RobotPartRepository;

public class RobotPartServiceImpl implements RobotPartService {

    @Autowired
    private RobotPartRepository partRepository;

    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @Autowired
    private CompatibilityRepository compatibilityRepository;

    @Override
    public Optional<RobotPart> addPart(RobotPart robotPart) {
        RobotPartDto dto = new RobotPartDto();
        robotPart.setSerialNumber(UUID.randomUUID().toString());
        entityToDto(robotPart, dto);
        Optional<RobotPartDto> savedPart = saveRobotPart(dto);
        savedPart.ifPresent(newPartDto -> updateCompatiblities(newPartDto.getPartid(), robotPart.getCompatibleParts()));
        Optional<RobotPart> newPartEntity = savedPart.map(this::dtoToEntity);
        newPartEntity.ifPresent(this::addCompatibleSerialNumbers);
        return newPartEntity;
    }

    @Override
    public Optional<RobotPart> getPart(String serialNumber) {
        Optional<RobotPartDto> retrievedPart = partRepository.getBySerialnumber(serialNumber);
        Optional<RobotPart> part = retrievedPart.map(this::dtoToEntity);
        part.ifPresent(this::addCompatibleSerialNumbers);
        return part;
    }

    @Override
    public Optional<RobotPart> updatePart(RobotPart robotPart) {
        Optional<RobotPartDto> dto = partRepository.getBySerialnumber(robotPart.getSerialNumber());
        Optional<RobotPart> updatedPart;
        if (dto.isPresent()) {
            RobotPartDto updatePart = dto.get();
            entityToDto(robotPart, updatePart);
            updatePart = partRepository.save(updatePart);
            updateCompatiblities(updatePart.getPartid(), robotPart.getCompatibleParts());
            Optional<RobotPartDto> savedDto = saveRobotPart(updatePart);
            updatedPart = savedDto.map(this::dtoToEntity);
            updatedPart.ifPresent(this::addCompatibleSerialNumbers);
        } else {
            updatedPart = Optional.empty();
        }
        return updatedPart;
    }

    private void updateCompatiblities(int partId, List<String> compatibleParts) {
        List<RobotPartDto> relatedPartDtos = partRepository.getAllBySerialnumberIn(compatibleParts);
        List<Integer> newCompatibilites = new ArrayList<>();
        for (RobotPartDto relatedPart : relatedPartDtos) {
            // Enforced by database constraint, the lowest part ID is always the 'firstPart'
            int firstPart = partId > relatedPart.getPartid() ? relatedPart.getPartid() : partId;
            int secondPart = partId > relatedPart.getPartid() ? partId : relatedPart.getPartid();
            newCompatibilites.add(relatedPart.getPartid());
            if (!compatibilityRepository.findTopByFirstPartAndSecondPart(firstPart, secondPart).isPresent()) {
                CompatiblePartsDto compatibility = new CompatiblePartsDto();
                compatibility.setFirstPart(firstPart);
                compatibility.setSecondPart(secondPart);
                compatibilityRepository.save(compatibility);
            }
        }

        List<CompatiblePartsDto> allCompatible = compatibilityRepository.findAllByFirstPartOrSecondPart(partId, partId);
        for (CompatiblePartsDto compatibility : allCompatible) {
            if (!newCompatibilites.contains(compatibility.getFirstPart()) &&
                    !newCompatibilites.contains(compatibility.getSecondPart())) {
                compatibilityRepository.delete(compatibility);
            }
        }

    }

    private Optional<RobotPartDto> saveRobotPart(RobotPartDto originalDto) {
        Optional<RobotPartDto> savedDto;
        try {
            savedDto = Optional.of(partRepository.save(originalDto));
        } catch (Exception e) {
            savedDto = Optional.empty();
        }
        return savedDto;
    }

    @Override
    public boolean deletePart(String serialNumber) {
        Optional<RobotPartDto> dto = partRepository.getBySerialnumber(serialNumber);
        boolean deletable = dto.isPresent();
        if (deletable) {
            // Delete compatibility entry
            List<CompatiblePartsDto> comaptibilities = compatibilityRepository
                    .findAllByFirstPartOrSecondPart(dto.get().getPartid(), dto.get().getPartid());
            for (CompatiblePartsDto compatibility : comaptibilities) {
                compatibilityRepository.delete(compatibility);
            }
            partRepository.delete(dto.get());
        }
        return deletable;
    }

    @Override
    public List<RobotPart> getAllParts() {
        Iterable<RobotPartDto> allPartDtos = partRepository.findAll();
        List<RobotPart> entities = new ArrayList<>();
        allPartDtos.forEach(dto -> entities.add(dtoToEntity(dto)));
        entities.forEach(this::addCompatibleSerialNumbers);
        return entities;
    }

    @Override
    public List<RobotPart> getCompatibleParts(String serialNumber) {
        Optional<RobotPartDto> optDto = partRepository.getBySerialnumber(serialNumber);
        return optDto
                .map(dto -> getCompatibleParts(dto.getPartid()))
                .orElse(Collections.emptyList());
    }

    private List<RobotPart> getCompatibleParts(int partId) {
        List<Integer> compatiblePartIds = new ArrayList<>();
        List<CompatiblePartsDto> compatibilities = compatibilityRepository
                .findAllByFirstPartOrSecondPart(partId, partId);
        compatibilities.forEach(compatibility ->
                compatiblePartIds.add(getCompatibleId(partId, compatibility)));
        List<RobotPartDto> compatibleDtos = partRepository.findAllByPartidIn(compatiblePartIds);
        final List<RobotPart> compatibleParts = new ArrayList<>();
        compatibleDtos.forEach(compatible -> compatibleParts.add(dtoToEntity(compatible)));

        return compatibleParts;
    }

    private ManufacturerDto getOrCreateManufacturer(String manufacturerName) {
        Optional<ManufacturerDto> dto = manufacturerRepository.findTopByManufacturerName(manufacturerName);
        return dto.orElse(createManufacturer(manufacturerName));
    }

    private ManufacturerDto createManufacturer(String manufacturerName) {
        ManufacturerDto dto = new ManufacturerDto();
        dto.setManufacturerName(manufacturerName);
        dto = manufacturerRepository.save(dto);
        return dto;
    }

    private Integer getCompatibleId(int originalId, CompatiblePartsDto compatiblePartDto) {
        return compatiblePartDto.getFirstPart() == originalId
                ? compatiblePartDto.getSecondPart()
                : compatiblePartDto.getFirstPart();
    }

    private RobotPart dtoToEntity(RobotPartDto dto) {
        RobotPart entity = new RobotPart();
        entity.setSerialNumber(dto.getSerialnumber());
        entity.setName(dto.getName());
        entity.setManufacturer(dto.getManufacturer().getManufacturerName());
        entity.setWeightGrams(dto.getWeightgrams());
        return entity;
    }

    /**
     * Add compatible part serial numbers to an part entity. To avoid recursion only add related items
     * to the final entities being returned to the client.
     * @param robotPart Part to add related part serial numbers to
     */
    private void addCompatibleSerialNumbers(RobotPart robotPart) {
        getCompatibleParts(robotPart.getSerialNumber())
                .forEach(compatible -> robotPart.getCompatibleParts().add(compatible.getSerialNumber()));
    }

    private RobotPartDto entityToDto(RobotPart entity, RobotPartDto dto) {
        dto.setName(entity.getName());
        dto.setSerialnumber(entity.getSerialNumber());
        dto.setManufacturer(getOrCreateManufacturer(entity.getManufacturer()));
        dto.setWeightgrams(entity.getWeightGrams());
        return dto;
    }
}
