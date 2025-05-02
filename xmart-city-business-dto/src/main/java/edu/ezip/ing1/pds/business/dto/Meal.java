package edu.ezip.ing1.pds.business.dto;

import edu.ezip.ing1.pds.business.enums.MealTypeEnum;
import java.util.Set;

public class Meal {
    private int id;
    private String name;
    private MealTypeEnum type;
    private int calories;
    private Set<String> ingredients;  // from JSONB


    public int getId() {return id;}
    public String getName() {return name;}
    public MealTypeEnum getType() {return type;}
    public int getCalories() {return calories;}
    public Set<String> getIngredients() {return ingredients;}

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(MealTypeEnum type) {
        this.type = type;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void setIngredients(Set<String> ingredients) {
        this.ingredients = ingredients;
    }



}
