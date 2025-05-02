package edu.ezip.ing1.pds.business.dto;

import java.util.ArrayList;
import java.util.List;

public class MealPlan {
    private final List<MealPlanItem> items = new ArrayList<>();
    public void addItem(MealPlanItem item) { items.add(item); }
    public List<MealPlanItem> getItems() { return items; }
}
