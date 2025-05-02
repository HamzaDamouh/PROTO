package edu.ezip.ing1.pds.business.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import edu.ezip.ing1.pds.business.enums.MealTypeEnum;

import java.io.IOException;
import java.time.LocalDate;

public class MealPlanItem {

    private MealTypeEnum mealType;
    private String mealName;
    private int calories;



    public MealTypeEnum getMealType() { return mealType; }
    public void setMealType(MealTypeEnum mealType) { this.mealType = mealType; }

    public String getMealName() { return mealName; }
    public void setMealName(String mealName) { this.mealName = mealName; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }





    // CHATGPT
    public static class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
        @Override
        public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.getCodec().readTree(p);

            try {
                // Try to parse as a standard ISO date string first
                if (node.isTextual()) {
                    return LocalDate.parse(node.asText());
                }

                // If it's an object with year/month/day fields, extract them
                if (node.isObject()) {
                    int year = node.has("year") ? node.get("year").asInt() : 0;
                    int month = node.has("monthValue") ? node.get("monthValue").asInt() : 1;
                    int day = node.has("dayOfMonth") ? node.get("dayOfMonth").asInt() : 1;

                    if (year > 0 && month > 0 && day > 0) {
                        return LocalDate.of(year, month, day);
                    }
                }

                // Fallback to current date
                return LocalDate.now();
            } catch (Exception e) {
                // Log error and fallback to current date
                System.err.println("Error deserializing date: " + e.getMessage());
                return LocalDate.now();
            }
        }
    }

}
