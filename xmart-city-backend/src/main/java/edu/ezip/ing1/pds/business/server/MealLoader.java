package edu.ezip.ing1.pds.business.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.ezip.ing1.pds.business.dto.Meal;
import edu.ezip.ing1.pds.business.enums.MealTypeEnum;

import java.io.IOException;
import java.sql.*;
import java.util.*;

public class MealLoader {
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public List<Meal> findAllMeals(Connection conn) throws SQLException {
        String sql = "SELECT id, name, type, calories, ingredients FROM meals";
        List<Meal> meals = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                try {
                    Meal m = new Meal();
                    m.setId(rs.getInt("id"));
                    m.setName(rs.getString("name"));
                    m.setCalories(rs.getInt("calories"));

                    String rawType = rs.getString("type");
                    System.out.println("Loading meal type: '" + rawType + "'");

                    try {
                        switch (rawType.trim().toLowerCase()) {
                            case "breakfast" -> m.setType(MealTypeEnum.breakfast);
                            case "lunch_dinner" -> m.setType(MealTypeEnum.lunch_dinner);
                            case "snack" -> m.setType(MealTypeEnum.snack);
                            default -> {
                                System.err.println("Unknown meal type: '" + rawType + "' — skipping meal ID " + m.getId());
                                continue;
                            }
                        }

                    } catch (IllegalArgumentException ex) {
                        System.err.println("Skipping invalid type: " + rawType + " for meal ID: " + m.getId());
                        continue; // skip this meal
                    }

                    String json = rs.getString("ingredients");
                    Set<String> ings = new HashSet<>(
                            mapper.readValue(json, mapper.getTypeFactory()
                                    .constructCollectionType(List.class, String.class))
                    );
                    m.setIngredients(ings);

                    meals.add(m);

                } catch (IOException e) {
                    System.err.println("Failed to parse ingredients JSON for a meal. Skipping.");
                }
            }
        }

        return meals;
    }
}
