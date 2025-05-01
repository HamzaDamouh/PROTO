package edu.ezip.ing1.pds.business.dto;

import edu.ezip.ing1.pds.business.enums.*;

import java.time.LocalDateTime;

public class User {
    private int id;
    private String email;
    private String passwordHash;
    private Integer age;
    private SexEnum sex;
    private Integer heightCm;
    private Integer weightKg;
    private ActivityLevelEnum activityLevel;
    private GoalEnum goal;


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public SexEnum getSex() { return sex; }
    public void setSex(SexEnum sex) { this.sex = sex; }

    public Integer getHeightCm() { return heightCm; }
    public void setHeightCm(Integer heightCm) { this.heightCm = heightCm; }

    public Integer getWeightKg() { return weightKg; }
    public void setWeightKg(Integer weightKg) { this.weightKg = weightKg; }

    public ActivityLevelEnum getActivityLevel() { return activityLevel; }
    public void setActivityLevel(ActivityLevelEnum activityLevel) { this.activityLevel = activityLevel; }

    public GoalEnum getGoal() { return goal; }
    public void setGoal(GoalEnum goal) { this.goal = goal; }



    public double calculateBMR() {
        if (sex == SexEnum.male) {
            return 10 * weightKg + 6.25 * heightCm - 5 * age + 5;
        } else {
            return 10 * weightKg + 6.25 * heightCm - 5 * age - 161;
        }
    }

    public double calculateDailyCalories() {
        double bmr = calculateBMR();
        double multiplier;
        switch (activityLevel) {
            case low:
                multiplier = 1.2;
                break;
            case moderate:
                multiplier = 1.55;
                break;
            case high:
                multiplier = 1.9;
                break;
            default:
                multiplier = 1.2;
        }

        double maintenance = bmr * multiplier;

        switch (goal) {
            case lose:
                return maintenance - 500;
            case gain:
                return maintenance + 500;
            case maintain:
            default:
                return maintenance;
        }
    }

}
