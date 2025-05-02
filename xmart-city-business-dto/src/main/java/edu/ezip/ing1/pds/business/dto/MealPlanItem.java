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





}
