package edu.ezip.ing1.pds.business.server;

import edu.ezip.ing1.pds.commons.Request;
import edu.ezip.ing1.pds.commons.Response;

import java.sql.Connection;

public class Dispatcher {

    private final UserService userService = new UserService();
    private final MealPlanService mealPlanService = new MealPlanService();

    public Response dispatch(Request request, Connection connection) throws Exception {
        return switch (request.getRequestOrder()) {
            case "CREATE_USER" -> userService.createUser(request, connection);
            case "LOGIN_USER" -> userService.loginUser(request, connection);
            case "GENERATE_MEAL_PLAN" -> mealPlanService.generateMealPlan(request, connection);
            case "GET_MEAL_PLAN"     -> mealPlanService.getMealPlan(request, connection);
            default -> throw new IllegalArgumentException("Unknown request: " + request.getRequestOrder());
        };
    }
}
