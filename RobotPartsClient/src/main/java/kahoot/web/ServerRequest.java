package kahoot.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import kahoot.entities.RobotPart;
import kahoot.inputs.Command;

public class ServerRequest {

    private WebClient webClient;
    private String uri;
    private HttpMethod method;
    private RobotPart partEntity;
    private Command command;
    private ObjectMapper mapper;
    private static Gson prettifier = new GsonBuilder().setPrettyPrinting().create();
    private static JsonParser jp = new JsonParser();

    public ServerRequest(WebClient webClient) {
        this.webClient = webClient;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public void setPartEntity(RobotPart partEntity) {
        this.partEntity = partEntity;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public void setSerialiser(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public void sendRequest() {
        ClientResponse response = webClient
                .method(method)
                .uri(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(partEntity))
                .exchange()
                .block();

        processResponse(response);
    }

    private void processResponse(ClientResponse response) {
        switch (response.statusCode().value()) {
            case 200:
                outputSuccessfulRespone(response);
                break;
            case 404:
                System.out.println("The specified serial number was not found in the inventory");
                break;
            default:
                System.out.println("The inventory encountered an error while trying to process this request");
        }
    }

    private void outputSuccessfulRespone(ClientResponse response) {
        switch (command) {
            case DELETE:
                System.out.println("Part successfully deleted from the inventory");
                break;
            case READ:
            case ADD:
            case UPDATE:
                prettyPrintResponse(response, false);
                break;
            case LIST_COMPATIBLE:
            case LIST_ALL:
                prettyPrintResponse(response, true);
                break;
        }
    }

    private void prettyPrintResponse(ClientResponse response, boolean isArray) {
        try {
            String serialised = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(response
                            .bodyToMono(isArray ? RobotPart[].class : RobotPart.class)
                            .block());
            System.out.println(serialised);
        } catch (JsonProcessingException e) {
            System.out.println("Command was successful but inventory response could not be read");
        }
    }


}