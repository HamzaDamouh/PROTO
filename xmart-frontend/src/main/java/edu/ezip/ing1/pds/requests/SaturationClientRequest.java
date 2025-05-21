package edu.ezip.ing1.pds.requests;

import edu.ezip.ing1.pds.client.commons.ClientRequest;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.commons.Request;
import edu.ezip.ing1.pds.commons.Response;

import java.io.IOException;

public class SaturationClientRequest extends ClientRequest<String, String> {

    public SaturationClientRequest(NetworkConfig networkConfig, int myBirthDate, Request request, String info, byte[] bytes)
            throws IOException {
        super(networkConfig, myBirthDate, request, info, bytes);
    }

    @Override
    public String readResult(String body) throws IOException {
        return body;
    }
}
