package edu.ezip.ing1.pds.business.server;

import edu.ezip.ing1.pds.business.dto.Meal;
import edu.ezip.ing1.pds.business.dto.User;
import edu.ezip.ing1.pds.business.enums.MealTypeEnum;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class MealPlanner {

    private final Map<MealTypeEnum, Double> ratios;
    private final double margin; //  ±15%
    private final int diversityWindowDays;
    private final Random random = new Random();

    public MealPlanner(Map<MealTypeEnum, Double> ratios, double margin, int diversityWindowDays) {
        this.ratios = ratios;
        this.margin = margin;
        this.diversityWindowDays = diversityWindowDays;
    }

    public Map<LocalDate, List<Meal>> planMeals(User user,
                                                List<Meal> allMeals,
                                                LocalDate from,
                                                int days) {
        double tdee = user.calculateDailyCalories();
        Map<LocalDate, List<Meal>> week = new LinkedHashMap<>();


        Deque<Set<String>> recentIngredients = new ArrayDeque<>();

        for (int d = 0; d < days; d++) {
            LocalDate date = from.plusDays(d);
            List<Meal> dailyPlan = new ArrayList<>();


            Set<String> windowSeen = recentIngredients.stream()
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());

            for (MealTypeEnum type : ratios.keySet()) {
                int targetCal = (int) (tdee * ratios.get(type));
                int minCal = (int) (targetCal * (1 - margin));
                int maxCal = (int) (targetCal * (1 + margin));


                List<Meal> candidates = allMeals.stream()
                        .filter(m -> type.equals(m.getType()))
                        .filter(m -> m.getCalories() >= minCal && m.getCalories() <= maxCal)
                        .collect(Collectors.toList());

                if (candidates.isEmpty()) {

                    candidates = allMeals.stream()
                            .filter(m -> type.equals(m.getType()))
                            .filter(m -> m.getCalories() >= targetCal * 0.5 && m.getCalories() <= targetCal * 1.5)
                            .collect(Collectors.toList());
                }

                Meal selected = selectMeal(candidates, targetCal, windowSeen);
                if (selected != null) {
                    dailyPlan.add(selected);
                }
            }


            Set<String> dayIngs = dailyPlan.stream()
                    .flatMap(m -> m.getIngredients().stream())
                    .collect(Collectors.toSet());
            recentIngredients.addLast(dayIngs);
            if (recentIngredients.size() > diversityWindowDays) {
                recentIngredients.removeFirst();
            }

            week.put(date, dailyPlan);
        }
        return week;
    }


    private Meal selectMeal(List<Meal> candidates, int targetCal, Set<String> windowSeen) {
        if (candidates.isEmpty()) return null;


        List<Scored> scored = new ArrayList<>();
        for (Meal m : candidates) {
            double macroScore = 1.0 - Math.abs(m.getCalories() - targetCal) / (double) targetCal;
            long newCount = m.getIngredients().stream()
                    .filter(ing -> !windowSeen.contains(ing))
                    .count();
            double diversityScore = newCount / (double) m.getIngredients().size();
            double totalScore = 0.7 * macroScore + 0.3 * diversityScore;
            scored.add(new Scored(m, totalScore));
        }


        scored.sort(Comparator.comparingDouble((Scored s) -> s.score).reversed());


        int topN = Math.min(3, scored.size());
        List<Scored> topList = scored.subList(0, topN);
        double sum = topList.stream().mapToDouble(s -> s.score).sum();
        double r = random.nextDouble() * sum;
        double cumulative = 0;

        for (Scored s : topList) {
            cumulative += s.score;
            if (r <= cumulative) {
                return s.meal;
            }
        }
        return topList.get(0).meal;
    }

    private static class Scored {
        final Meal meal;
        final double score;
        Scored(Meal meal, double score) {
            this.meal = meal;
            this.score = score;
        }
    }
}
