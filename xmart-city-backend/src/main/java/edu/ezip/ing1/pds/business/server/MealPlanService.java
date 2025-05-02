package edu.ezip.ing1.pds.business.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.ezip.ing1.pds.business.dto.MealPlan;
import edu.ezip.ing1.pds.business.dto.MealPlanItem;
import edu.ezip.ing1.pds.business.dto.User;
import edu.ezip.ing1.pds.business.enums.MealTypeEnum;
import edu.ezip.ing1.pds.commons.Request;
import edu.ezip.ing1.pds.commons.Response;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.EnumMap;

public class MealPlanService {
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    // Ratios for distributing daily calories
    private static final double BREAKFAST_RATIO    = 0.20;
    private static final double LUNCH_RATIO        = 0.40;
    private static final double SNACK_RATIO        = 0.40;

    // Pick one random meal of the given type within the calorie range
    private static final String PICK_SQL = """
        SELECT id
        FROM meals
        WHERE type = ?::meal_type_enum
          AND calories BETWEEN ? AND ?
        ORDER BY random()
        LIMIT 1
        """;

    // Upsert into meal_plans, casting the third parameter as the enum
    private static final String UPSERT_SQL = """
        INSERT INTO meal_plans (user_id, date, meal_type, meal_id)
        VALUES (?, ?, ?::meal_type_enum, ?)
        ON CONFLICT (user_id, date, meal_type)
        DO UPDATE SET meal_id = EXCLUDED.meal_id
        """;

    /**
     * Generates a 7-day plan by picking one random meal per MealTypeEnum each day.
     * Returns a simple status message.
     */
    public Response generateMealPlan(Request request, Connection conn) throws Exception {
        User user = mapper.readValue(request.getRequestBody(), User.class);
        int uid    = user.getId();
        int target = (int) user.calculateDailyCalories();
        LocalDate today = LocalDate.now();

        // Prepare statements once
        try (
                PreparedStatement pickStmt   = conn.prepareStatement(PICK_SQL);
                PreparedStatement upsertStmt = conn.prepareStatement(UPSERT_SQL)
        ) {
            // For each of the next 7 days…
            for (int d = 0; d < 7; d++) {
                LocalDate date = today.plusDays(d);

                // For each meal type (breakfast, lunch_dinner, snack)…
                for (MealTypeEnum type : MealTypeEnum.values()) {
                    // Choose the ratio dynamically
                    double ratio = switch (type) {
                        case breakfast    -> BREAKFAST_RATIO;
                        case lunch_dinner -> LUNCH_RATIO;
                        case snack        -> SNACK_RATIO;
                    };
                    int minCal = (int) (target * ratio * 0.8);
                    int maxCal = (int) (target * ratio * 1.2);

                    // Pick one random meal
                    pickStmt.setString(1, type.name());
                    pickStmt.setInt(2, minCal);
                    pickStmt.setInt(3, maxCal);
                    try (ResultSet rs = pickStmt.executeQuery()) {
                        if (rs.next()) {
                            int mealId = rs.getInt(1);

                            // Upsert into the meal_plans table
                            upsertStmt.setInt   (1, uid);
                            upsertStmt.setDate  (2, Date.valueOf(date));
                            upsertStmt.setString(3, type.name());
                            upsertStmt.setInt   (4, mealId);
                            upsertStmt.executeUpdate();
                        }
                    }
                }
            }
        }

        return new Response(request.getRequestId(), "Meal plan generated");
    }

    /**
     * Fetches all MealPlanItems for the user, ordered by date & meal_type.
     */
    public Response getMealPlan(Request request, Connection conn) throws SQLException, IOException {
        User user = mapper.readValue(request.getRequestBody(), User.class);
        int uid   = user.getId();

        String sql = """
            SELECT mp.date, mp.meal_type, m.name, m.calories
            FROM meal_plans mp
            JOIN meals m ON mp.meal_id = m.id
            WHERE mp.user_id = ?
            ORDER BY mp.date, mp.meal_type
            """;

        MealPlan plan = new MealPlan();
        try (
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, uid);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MealPlanItem item = new MealPlanItem();
                    item.setDate     ( rs.getDate("date").toLocalDate() );
                    item.setMealType ( MealTypeEnum.valueOf(rs.getString("meal_type")) );
                    item.setMealName ( rs.getString("name") );
                    item.setCalories ( rs.getInt("calories") );
                    plan.addItem(item);
                }
            }
        }

        return new Response(request.getRequestId(), plan);
    }
}
