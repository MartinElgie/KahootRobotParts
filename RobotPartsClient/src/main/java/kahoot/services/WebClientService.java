package kahoot.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebClientService {

    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    private CommandMappingService commandService;
    private RequestAssemblyService requestService;
    private InputParsingService parsingService;

    @Autowired
    public WebClientService(RequestAssemblyService requestService, CommandMappingService commandService, InputParsingService parsingService) {
        this.commandService = commandService;
        this.requestService = requestService;
        this.parsingService = parsingService;
    }

    public void beginClient() {
        displayPossibleCommands();
        listenForCommand();
    }

    private void displayPossibleCommands() {
        System.out.println("\r\nPossible commands [parameters]:");
        commandService.displayAllCommands();
    }

    private void listenForCommand() {
        try {
            System.out.print("Please enter a command: ");
            String input = reader.readLine();
            Map<String, String> parameters = parsingService.parseInput(input);
            requestService.createServerRequest(parameters).sendRequest();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage() + " (type \"help\" to see commands)");
        }
        listenForCommand();
    }

}
