package edu.ezip.ing1.pds.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import edu.ezip.ing1.pds.business.dto.User;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.commons.Request;
import edu.ezip.ing1.pds.commons.Response;
import edu.ezip.ing1.pds.requests.CreateUserClientRequest;
import edu.ezip.ing1.pds.requests.LoginUserClientRequest;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class UserClientService {

    private final NetworkConfig networkConfig;

    public UserClientService(NetworkConfig networkConfig) {
        this.networkConfig = networkConfig;
    }

    public User createUser(User user) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
                .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);

        Request request = new Request();
        request.setRequestId(UUID.randomUUID().toString());
        request.setRequestOrder("CREATE_USER");
        request.setRequestContent(json);

        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        byte[] bytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(request);

        CreateUserClientRequest clientRequest = new CreateUserClientRequest(
                networkConfig, 0, request, user, bytes);
        clientRequest.join();
        if (clientRequest.getResult() == null) {
            throw new Exception("Le serveur est indisponible.");
        }
        return clientRequest.getResult();


    }

    public User loginUser(String email, String passwordHash) throws Exception {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordHash);

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
                .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);

        Request request = new Request();
        request.setRequestId(UUID.randomUUID().toString());
        request.setRequestOrder("LOGIN_USER");
        request.setRequestContent(json);

        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        byte[] bytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(request);

        LoginUserClientRequest clientRequest = new LoginUserClientRequest(
                networkConfig, 0, request, user, bytes);
        clientRequest.join();

        User result = clientRequest.getResult();


        if (result == null) {
            throw new Exception("Échec de la connexion : le serveur est indisponible ou les identifiants sont incorrects.");
        }



        return clientRequest.getResult();
    }
}
