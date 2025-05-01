package edu.ezip.ing1.pds.backend;

import edu.ezip.ing1.pds.business.server.UserService;
import edu.ezip.ing1.pds.commons.Request;
import edu.ezip.ing1.pds.commons.Response;

import java.sql.Connection;

public class Dispatcher {

    private final UserService userService = new UserService();

    public Response dispatch(Request request, Connection connection) throws Exception {
        return switch (request.getRequestOrder()) {
            case "CREATE_USER" -> userService.createUser(request, connection);
            case "LOGIN_USER" -> userService.loginUser(request, connection);

            default -> throw new IllegalArgumentException("Unknown request: " + request.getRequestOrder());
        };
    }
}
