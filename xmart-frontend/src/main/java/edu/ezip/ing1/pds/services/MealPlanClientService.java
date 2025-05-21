package edu.ezip.ing1.pds.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.ezip.ing1.pds.business.dto.MealPlan;
import edu.ezip.ing1.pds.business.dto.User;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.commons.Request;
import edu.ezip.ing1.pds.requests.GenerateMealPlanClientRequest;
import edu.ezip.ing1.pds.requests.GetMealPlanClientRequest;
import java.util.UUID;

public class MealPlanClientService {

    private final NetworkConfig networkConfig;

    public MealPlanClientService(NetworkConfig networkConfig) {
        this.networkConfig = networkConfig;
    }

    public String generateMealPlan(User user) throws Exception {

        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
                .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        String userJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);


        Request request = new Request();
        request.setRequestId(UUID.randomUUID().toString());
        request.setRequestOrder("GENERATE_MEAL_PLAN");
        request.setRequestContent(userJson);


        mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        byte[] bytes = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(request);


        GenerateMealPlanClientRequest clientReq =
                new GenerateMealPlanClientRequest(networkConfig, 0, request, user, bytes);
        clientReq.join();

        if (clientReq.getErrorMessage() != null) {
            throw new Exception(clientReq.getErrorMessage());
        }

        String result = clientReq.getResult();
        if (result == null) {
            throw new Exception("Le serveur est indisponible.");
        }
        return clientReq.getResult();
    }

    public MealPlan getMealPlan(User user) throws Exception {

        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
                .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        String userJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);


        Request request = new Request();
        request.setRequestId(UUID.randomUUID().toString());
        request.setRequestOrder("GET_MEAL_PLAN");
        request.setRequestContent(userJson);


        mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        byte[] bytes = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(request);


        GetMealPlanClientRequest clientReq =
                new GetMealPlanClientRequest(networkConfig, 0, request, user, bytes);
        clientReq.join();


        if (clientReq.getErrorMessage() != null) {
            throw new Exception(clientReq.getErrorMessage());
        }

        String result = clientReq.getResult().toString();
        if (result == null) {
            throw new Exception("Le serveur est indisponible.");
        }
        return clientReq.getResult();
    }
}
