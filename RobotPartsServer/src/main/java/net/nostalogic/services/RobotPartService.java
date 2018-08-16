package net.nostalogic.services;

import java.util.List;
import java.util.Optional;

import net.nostalogic.entities.RobotPart;


public interface RobotPartService {

    Optional<RobotPart> addPart(RobotPart robotPart);
    Optional<RobotPart> getPart(String serialNumber);
    Optional<RobotPart> updatePart(RobotPart robotPart);
    boolean deletePart(String serialNumber);
    List<RobotPart> getAllParts();
    List<RobotPart> getCompatibleParts(String serialNumber);

}
