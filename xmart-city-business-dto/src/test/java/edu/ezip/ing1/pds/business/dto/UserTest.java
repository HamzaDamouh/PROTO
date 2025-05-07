package edu.ezip.ing1.pds.business.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.ezip.ing1.pds.business.enums.ActivityLevelEnum;
import edu.ezip.ing1.pds.business.enums.GoalEnum;
import edu.ezip.ing1.pds.business.enums.SexEnum;

class UserTest {
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setAge(30);
        user.setHeightCm(175);
        user.setWeightKg(70);
    }

    @Test
    void testCalculateDailyCalories_Maintain_Low_Male() {
        user.setSex(SexEnum.male);
        user.setActivityLevel(ActivityLevelEnum.low);
        user.setGoal(GoalEnum.maintain);

        // BMR = 10*70 + 6.25*175 - 5*30 + 5 = 1648.75
        double expectedBmr = 10 * 70 + 6.25 * 175 - 5 * 30 + 5;
        double expected = expectedBmr * 1.2;
        assertEquals(expected, user.calculateDailyCalories(), 0.01);
    }

    @Test
    void testCalculateDailyCalories_Lose_Moderate_Female() {
        user.setSex(SexEnum.female);
        user.setActivityLevel(ActivityLevelEnum.moderate);
        user.setGoal(GoalEnum.lose);

        // BMR = 10*70 + 6.25*175 - 5*30 - 161 = 1482.75
        double expectedBmr = 10 * 70 + 6.25 * 175 - 5 * 30 - 161;
        double maintenance = expectedBmr * 1.55; // moderate
        double expected = maintenance - 500;     // lose goal
        assertEquals(expected, user.calculateDailyCalories(), 0.01);
    }

    @Test
    void testCalculateDailyCalories_Gain_High_Male() {
        user.setSex(SexEnum.male);
        user.setActivityLevel(ActivityLevelEnum.high);
        user.setGoal(GoalEnum.gain);

        double expectedBmr = 10 * 70 + 6.25 * 175 - 5 * 30 + 5;
        double maintenance = expectedBmr * 1.9;  // high
        double expected = maintenance + 500;     // gain
        assertEquals(expected, user.calculateDailyCalories(), 0.01);
    }
}
