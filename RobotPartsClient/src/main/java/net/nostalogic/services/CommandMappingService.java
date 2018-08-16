package net.nostalogic.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import net.nostalogic.entities.RobotPart;
import net.nostalogic.inputs.Command;

@Service
public class CommandMappingService {

    private Map<Command, String> endpoints;
    private Map<Command, HttpMethod> methods;
    private Map<String, Command> commandNames;
    private Map<Command, List<String>> commandArguments;
    private List<String> postArguments;

    static final String ARG_SERIAL_NUMBER = "serial_number";
    private static final String ARG_NAME = "name";
    private static final String ARG_MANUFACTURER = "manufacturer";
    private static final String ARG_WEIGHT = "weight_grams";
    private static final String ARG_COMPATIBLE_PARTS = "compatible_parts";

    @Autowired
    public CommandMappingService() {
        populateEndpoints();
        populateMethods();
        populateCommandNames();
        populateCommandArguments();
        populatePostArguments();
    }

    private void populateEndpoints() {
        endpoints = new HashMap<>();
        endpoints.put(Command.ADD, "/add");
        endpoints.put(Command.READ, "/read");
        endpoints.put(Command.UPDATE, "/update");
        endpoints.put(Command.DELETE, "/delete");
        endpoints.put(Command.LIST_ALL, "/all");
        endpoints.put(Command.LIST_COMPATIBLE, "/compatible");
    }

    private void populateMethods() {
        methods = new HashMap<>();
        methods.put(Command.ADD, HttpMethod.POST);
        methods.put(Command.READ, HttpMethod.GET);
        methods.put(Command.UPDATE, HttpMethod.POST);
        methods.put(Command.DELETE, HttpMethod.GET);
        methods.put(Command.LIST_ALL, HttpMethod.GET);
        methods.put(Command.LIST_COMPATIBLE, HttpMethod.GET);
    }

    private void populateCommandNames() {
        commandNames = new HashMap<>();
        commandNames.put("add", Command.ADD);
        commandNames.put("read", Command.READ);
        commandNames.put("delete", Command.DELETE);
        commandNames.put("update", Command.UPDATE);
        commandNames.put("list", Command.LIST_ALL);
        commandNames.put("compatible", Command.LIST_COMPATIBLE);
        commandNames.put("exit", Command.EXIT);
        commandNames.put("help", Command.HELP);
    }

    private void populateCommandArguments() {
        commandArguments = new HashMap<>();
        commandArguments.put(Command.ADD, new ArrayList<>());
        commandArguments.get(Command.ADD).add(ARG_NAME);
        commandArguments.get(Command.ADD).add(ARG_MANUFACTURER);
        commandArguments.get(Command.ADD).add(ARG_WEIGHT);
        commandArguments.put(Command.READ, new ArrayList<>());
        commandArguments.get(Command.READ).add(ARG_SERIAL_NUMBER);
        commandArguments.put(Command.DELETE, new ArrayList<>());
        commandArguments.get(Command.DELETE).add(ARG_SERIAL_NUMBER);
        commandArguments.put(Command.UPDATE, new ArrayList<>());
        commandArguments.get(Command.UPDATE).add(ARG_SERIAL_NUMBER);
        commandArguments.put(Command.LIST_ALL, new ArrayList<>());
        commandArguments.put(Command.LIST_COMPATIBLE, new ArrayList<>());
        commandArguments.get(Command.LIST_COMPATIBLE).add(ARG_SERIAL_NUMBER);
        commandArguments.put(Command.EXIT, new ArrayList<>());
        commandArguments.put(Command.HELP, new ArrayList<>());
    }

    private void populatePostArguments() {
        postArguments = new ArrayList<>();
        postArguments.add(ARG_NAME);
        postArguments.add(ARG_MANUFACTURER);
        postArguments.add(ARG_WEIGHT);
        postArguments.add(ARG_COMPATIBLE_PARTS);
    }

    void displayAllCommands() {
        for (String commandName : commandNames.keySet()) {
            Command command = commandNames.get(commandName);
            System.out.print(commandName);
            System.out.print(" " + getCommandArgumentsString(command) + "\r\n");
        }
    }

    Map<String, Command> getCommandNames() {
        return commandNames;
    }

    List<String> getCommandRequiredArguments(Command command) {
        return commandArguments.get(command);
    }

    HttpMethod getCommandMethod(Command command) {
        return methods.get(command);
    }

    List<String> getPostArguments() {
        return postArguments;
    }

    String getEndpoint(Command command) {
        return endpoints.get(command);
    }

    RobotPart createEntityFromParameters(Map<String, String> parameters) {
        RobotPart robotPart = new RobotPart();
        if (parameters.containsKey(ARG_NAME)) {
            robotPart.setName(parameters.get(ARG_NAME));
        }
        if (parameters.containsKey(ARG_MANUFACTURER)) {
            robotPart.setManufacturer(parameters.get(ARG_MANUFACTURER));
        }
        if (parameters.containsKey(ARG_WEIGHT)) {
            robotPart.setWeightGrams(Float.parseFloat(parameters.get(ARG_WEIGHT)));
        }
        if (parameters.containsKey(ARG_COMPATIBLE_PARTS)) {
            robotPart.getCompatibleParts().add(parameters.get(ARG_COMPATIBLE_PARTS));
        }
        return robotPart;
    }

    private String getCommandArgumentsString(Command command) {
        String arguments = "";
        List<String> commandOptions = commandArguments.get(command);
        if (commandOptions.size() > 0) {
            StringJoiner joiner = new StringJoiner(", ");
            for (String argument : commandArguments.get(command)) {
                joiner.add("--" + argument);
            }
            arguments = "[ " + joiner.toString() + " ]";
        }

        return arguments;
    }

}
