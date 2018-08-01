package kahoot.services;

import static kahoot.services.CommandMappingService.ARG_SERIAL_NUMBER;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import kahoot.entities.RobotPart;
import kahoot.inputs.Command;
import kahoot.web.ServerRequest;

@Service
public class RequestAssemblyService {

    private static final String BASE_URL = "http://localhost:3700/robots/parts";

    private CommandMappingService mappingService;
    private WebClient webClient;
    private ObjectMapper mapper;

    @Autowired
    public RequestAssemblyService(CommandMappingService mappingService, ObjectMapper mapper) {
        this.mappingService = mappingService;
        this.mapper = mapper;
        createWebClient();
    }

    private void createWebClient() {
        this.webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
    }

    ServerRequest createServerRequest(Map<String, String> parameters) {
        Command command = Command.valueOf(parameters.get("command"));
        RobotPart robotPart = mappingService.createEntityFromParameters(parameters);
        ServerRequest serverRequest = new ServerRequest(webClient);
        serverRequest.setCommand(command);
        serverRequest.setSerialiser(mapper);
        serverRequest.setMethod(mappingService.getCommandMethod(command));
        serverRequest.setPartEntity(robotPart);
        serverRequest.setUri(getUri(command, parameters));
        return serverRequest;
    }

    private String getUri(Command command, Map<String, String> parameters) {
        String uri = mappingService.getEndpoint(command);
        if (command.equals(Command.READ)
                || command.equals(Command.UPDATE)
                || command.equals(Command.DELETE)
                || command.equals(Command.LIST_COMPATIBLE)) {
            uri += "/" + parameters.get(ARG_SERIAL_NUMBER);
        }
        return uri;
    }

}
