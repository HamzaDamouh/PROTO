package edu.ezip.ing1.pds.business.server;

import edu.ezip.commons.connectionpool.config.impl.ConnectionPoolImpl;
import edu.ezip.ing1.pds.commons.Request;
import edu.ezip.ing1.pds.commons.Response;

import java.sql.Connection;

// ─────────────────────────────────────────────────────────────────────────────

public class Dispatcher {
    private final ConnectionPoolImpl pool;
    private final UserService userService = new UserService();
    private final MealPlanService mealPlanService = new MealPlanService();

    public Dispatcher(ConnectionPoolImpl pool) {
        this.pool = pool;
    }

    public Response dispatch(Request request, Connection connection) throws Exception {
        return switch (request.getRequestOrder()) {
            case "CREATE_USER"        -> userService.createUser(request, connection);
            case "LOGIN_USER"         -> userService.loginUser(request, connection);
            case "GENERATE_MEAL_PLAN" -> mealPlanService.generateMealPlan(request, connection);
            case "GET_MEAL_PLAN"      -> mealPlanService.getMealPlan(request, connection);

            case "saturation" -> {
                if (Saturation.isLocked()) {
                    yield new Response(
                            request.getRequestId(),
                            "Pool est déjà saturé ; impossibilité de faire des requêtes"
                    );
                }


                boolean ok = Saturation.lockForSaturation(pool, connection, 1);
                String msg = ok
                        ? "Saturation du pool pour 1 minutes"
                        : "Impossible de saturer le pool (connexion déjà prise)";
                yield new Response(request.getRequestId(), msg);
            }


            default -> throw new IllegalArgumentException(
                    "Unknown request: " + request.getRequestOrder()
            );
        };
    }
}
