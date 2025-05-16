package com.anvil_shield.main_service.repository;

import com.anvil_shield.main_service.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<UserEntity> userRowMapper = (rs, rowNum) -> {
        UserEntity user = new UserEntity();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));
        return user;
    };

    public UserEntity save(UserEntity user) {
        String sql = """
                    INSERT INTO users (email, password, first_name, last_name, created_at, updated_at)
                    VALUES (?, ?, ?, ?, ?, ?)
                """;

        Timestamp now = new Timestamp(System.currentTimeMillis());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFirstName());
            ps.setString(4, user.getLastName());
            ps.setTimestamp(5, now);
            ps.setTimestamp(6, now);
            return ps;
        }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        if (keys != null && keys.containsKey("id")) {
            user.setId(((Number) keys.get("id")).longValue());
        }
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        return user;
    }

    public Optional<UserEntity> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        List<UserEntity> users = jdbcTemplate.query(sql, userRowMapper, email);
        return users.stream().findFirst();
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    public Optional<UserEntity> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<UserEntity> users = jdbcTemplate.query(sql, userRowMapper, id);
        return users.stream().findFirst();
    }

}