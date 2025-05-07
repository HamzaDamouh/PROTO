package edu.ezip.ing1.pds.business.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.ezip.ing1.pds.business.dto.Meal;
import edu.ezip.ing1.pds.business.dto.MealPlan;
import edu.ezip.ing1.pds.business.dto.MealPlanItem;
import edu.ezip.ing1.pds.business.dto.User;
import edu.ezip.ing1.pds.business.enums.MealTypeEnum;
import edu.ezip.ing1.pds.commons.Request;
import edu.ezip.ing1.pds.commons.Response;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class MealPlanService {
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());


    private static final Map<MealTypeEnum, Double> RATIOS = new EnumMap<>(MealTypeEnum.class);
    static {
        RATIOS.put(MealTypeEnum.breakfast, 0.25);
        RATIOS.put(MealTypeEnum.lunch_dinner,     0.40);
        RATIOS.put(MealTypeEnum.snack,     0.10);
    }
    private static final double CAL_MARGIN = 0.15;         // ±15%
    private static final int DIVERSITY_WINDOW = 3;         // 3-day

    private static final String UPSERT_SQL =
            """
            INSERT INTO meal_plans (user_id, date, meal_type, meal_id)
            VALUES (?, ?, ?::meal_type_enum, ?)
            ON CONFLICT (user_id, date, meal_type)
            DO UPDATE SET meal_id = EXCLUDED.meal_id
            """;

    private static final String SELECT_PLAN_SQL =
            """
            SELECT mp.date, mp.meal_type, m.name, m.calories
            FROM meal_plans mp
            JOIN meals m ON mp.meal_id = m.id
            WHERE mp.user_id = ?
            ORDER BY mp.date, mp.meal_type
            """;


    public Response generateMealPlan(Request request, Connection conn) throws Exception {
        User user = mapper.readValue(request.getRequestBody(), User.class);

        MealLoader loader = new MealLoader();
        List<Meal> allMeals = loader.findAllMeals(conn);


        MealPlanner planner = new MealPlanner(RATIOS, CAL_MARGIN, DIVERSITY_WINDOW);
        Map<LocalDate, List<Meal>> plan = planner.planMeals(
                user, allMeals, LocalDate.now(), 7);


        try (PreparedStatement ps = conn.prepareStatement(UPSERT_SQL)) {
            for (var entry : plan.entrySet()) {
                LocalDate date = entry.getKey();
                for (Meal meal : entry.getValue()) {
                    ps.setInt(1, user.getId());
                    ps.setDate(2, Date.valueOf(date));
                    ps.setString(3, meal.getType().name());
                    ps.setInt(4, meal.getId());
                    ps.addBatch();
                }
            }
            ps.executeBatch();
        }


        MealPlan dto = new MealPlan();
        for (var entry : plan.entrySet()) {
            LocalDate date = entry.getKey();
            for (Meal meal : entry.getValue()) {
                MealPlanItem item = new MealPlanItem();
                item.setMealType(meal.getType());
                item.setMealName(meal.getName());
                item.setCalories(meal.getCalories());
                dto.addItem(item);
            }
        }

        return new Response(request.getRequestId(), dto);
    }


    public Response getMealPlan(Request request, Connection conn) throws SQLException, IOException {
        User user = mapper.readValue(request.getRequestBody(), User.class);
        MealPlan plan = new MealPlan();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_PLAN_SQL)) {
            stmt.setInt(1, user.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MealPlanItem item = new MealPlanItem();
                    item.setMealType(MealTypeEnum.valueOf(rs.getString("meal_type")));
                    item.setMealName(rs.getString("name"));
                    item.setCalories(rs.getInt("calories"));
                    plan.addItem(item);
                }
            }
        }
        return new Response(request.getRequestId(), plan);
    }
}