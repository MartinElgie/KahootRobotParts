package net.nostalogic.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import net.nostalogic.inputs.Command;

@Service
public class InputParsingService {

    private CommandMappingService mappingService;

    @Autowired
    public InputParsingService(CommandMappingService mappingService) {
        this.mappingService = mappingService;
    }

    Map<String, String> parseInput(String userInput) {
        Map<String, String> parameters = new HashMap<>();
        matchCommand(userInput, parameters);
        return parameters;
    }

    private void matchCommand(String input, Map<String, String> parameters) {
        String[] splitInput = input.split(" ");
        Command command = mappingService.getCommandNames().get(splitInput[0]);
        if (command != null) {
            switch (command) {
                case READ:
                case ADD:
                case UPDATE:
                case DELETE:
                case LIST_ALL:
                case LIST_COMPATIBLE:
                    parameters.put("command", command.toString());
                    parseServerCommandArguments(command, splitInput, parameters);
                    break;
                case EXIT:
                    System.exit(1);
                    break;
                case HELP:
                default:
                    mappingService.displayAllCommands();
                    break;
            }
        } else {
            throw new RuntimeException("\"" + input + "\" is not a valid command");
        }
    }

    private void parseServerCommandArguments(Command command, String[] splitInput, Map<String, String> parameters) {
        requiredArgLoop:
        for (String requiredArg : mappingService.getCommandRequiredArguments(command)) {
            for (int i = 0; i < splitInput.length - 1; i++) {
                // Element at [i] is the name of the argument, value is at [i + 1]
                if (splitInput[i].equalsIgnoreCase(requiredArg)) {
                    parameters.put(requiredArg, splitInput[i + 1]);
                    continue requiredArgLoop;
                }
            }
            // Request this input if it wasn't present
            parameters.put(requiredArg, listenForArgument(requiredArg, true));
        }

        // If this is a post request ask for all entity fields as possible optional parameters
        if (mappingService.getCommandMethod(command).equals(RequestMethod.POST)) {
            for (String optionalArg : mappingService.getPostArguments()) {
                if (!parameters.containsKey(optionalArg)) {
                    listenForArgument(optionalArg, false);
                }
            }
        }
    }

    private String listenForArgument(String argumentName, boolean required) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String requestedInput = argumentName + (required ? "" : " (optional - can be blank)");
            System.out.println("Please enter a value for " + requestedInput + ":");
            String input = reader.readLine();
            if (input.length() == 0 && required) {
                throw new RuntimeException("Input \"" + argumentName + "\" is required but was not provided.");
            }
            return input;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
