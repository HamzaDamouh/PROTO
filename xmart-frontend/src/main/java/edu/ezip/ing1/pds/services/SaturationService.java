package edu.ezip.ing1.pds.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.commons.Request;
import edu.ezip.ing1.pds.requests.SaturationClientRequest;

import java.util.UUID;

public class SaturationService {

    private final NetworkConfig networkConfig;

    public SaturationService(NetworkConfig networkConfig) {
        this.networkConfig = networkConfig;
    }

    public String startSaturation() throws Exception {
        // 1) Configure ObjectMapper *exactly* like your other client services:
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


        mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);


        Request request = new Request();
        request.setRequestId(UUID.randomUUID().toString());
        request.setRequestOrder("saturation");
        ObjectNode emptyBody = mapper.createObjectNode();
        request.setRequestBody(emptyBody);


        byte[] requestBytes = mapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsBytes(request);


        SaturationClientRequest clientReq = new SaturationClientRequest(
                networkConfig,
                0,
                request,
                "",
                requestBytes
        );
        clientReq.join();




        return clientReq.getResult();
    }
}
