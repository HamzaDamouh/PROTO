package edu.ezip.ing1.pds.business.server;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.ezip.ing1.pds.business.dto.User;
import edu.ezip.ing1.pds.business.enums.ActivityLevelEnum;
import edu.ezip.ing1.pds.business.enums.GoalEnum;
import edu.ezip.ing1.pds.business.enums.SexEnum;
import edu.ezip.ing1.pds.commons.Request;
import edu.ezip.ing1.pds.commons.Response;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {

    private static final String INSERT_USER_SQL =
            "INSERT INTO users (email, password_hash, age, sex, height_cm, weight_kg, activity_level, goal, created_at) " +
                    "VALUES (?, ?, ?, ?::sex_enum, ?, ?, ?::activity_level_enum, ?::goal_enum, now()) RETURNING id";

    private static final String SELECT_USER_SQL =
            "SELECT * FROM users WHERE email = ? AND password_hash = ?";

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public Response createUser(Request request, Connection connection) throws SQLException, IOException {
        User user = mapper.readValue(request.getRequestBody(), User.class);
        PreparedStatement stmt = connection.prepareStatement(INSERT_USER_SQL);
        stmt.setString(1, user.getEmail());
        stmt.setString(2, user.getPasswordHash());
        stmt.setInt(3, user.getAge());
        stmt.setString(4, user.getSex().name());
        stmt.setInt(5, user.getHeightCm());
        stmt.setInt(6, user.getWeightKg());
        stmt.setString(7, user.getActivityLevel().name());
        stmt.setString(8, user.getGoal().name());

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            user.setId(rs.getInt(1));
        }

        return new Response(request.getRequestId(), user);
    }

    public Response loginUser(Request request, Connection connection) throws SQLException, IOException {
        User input = mapper.readValue(request.getRequestBody(), User.class);
        PreparedStatement stmt = connection.prepareStatement(SELECT_USER_SQL);
        stmt.setString(1, input.getEmail());
        stmt.setString(2, input.getPasswordHash());

        ResultSet rs = stmt.executeQuery();
        if (!rs.next()) {
            throw new SQLException("Invalid credentials");
        }

        User user = new User();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setAge(rs.getInt("age"));
        user.setSex(SexEnum.valueOf(rs.getString("sex")));
        user.setHeightCm(rs.getInt("height_cm"));
        user.setWeightKg(rs.getInt("weight_kg"));
        user.setActivityLevel(ActivityLevelEnum.valueOf(rs.getString("activity_level")));
        user.setGoal(GoalEnum.valueOf(rs.getString("goal")));

        return new Response(request.getRequestId(), user);
    }
}
