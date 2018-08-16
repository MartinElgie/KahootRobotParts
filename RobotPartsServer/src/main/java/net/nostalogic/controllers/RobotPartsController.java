package net.nostalogic.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.nostalogic.entities.RobotPart;
import net.nostalogic.services.RobotPartService;

@RestController
@RequestMapping(value = "robots/parts")
public class RobotPartsController {

    private RobotPartService robotPartService;

    @Autowired
    public RobotPartsController(RobotPartService robotPartService) {
        this.robotPartService = robotPartService;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/read/{serialNumber}")
    public ResponseEntity<RobotPart> getRobotPart(@PathVariable String serialNumber) {
        Optional<RobotPart> retrievedPart = robotPartService.getPart(serialNumber);
        return retrievedPart
                .map(robotPart -> new ResponseEntity<>(robotPart, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/add")
    public ResponseEntity<RobotPart> getRobotPart(@RequestBody RobotPart robotPart) {
        Optional<RobotPart> addedPart = robotPartService.addPart(robotPart);
        return addedPart
                .map(part -> new ResponseEntity<>(part, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/update/{serialNumber}")
    public ResponseEntity<RobotPart> updateRobotPart(@RequestBody RobotPart robotPart, @PathVariable String serialNumber) {
        robotPart.setSerialNumber(serialNumber);
        Optional<RobotPart> updatedPart = robotPartService.updatePart(robotPart);
        return updatedPart
                .map(part -> new ResponseEntity<>(part, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/delete/{serialNumber}")
    public ResponseEntity deleteRobotPart(@PathVariable String serialNumber) {
        boolean deleted = robotPartService.deletePart(serialNumber);
        return new ResponseEntity(deleted ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/all")
    public ResponseEntity<List<RobotPart>> getAllRobotParts() {
        List<RobotPart> allParts = robotPartService.getAllParts();
        return new ResponseEntity<>(allParts, HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/compatible/{serialNumber}")
    public ResponseEntity<List<RobotPart>> getAllCompatibleParts(@PathVariable String serialNumber) {
        List<RobotPart> compatibleParts = robotPartService.getCompatibleParts(serialNumber);
        return new ResponseEntity<>(compatibleParts, HttpStatus.OK);
    }
}
