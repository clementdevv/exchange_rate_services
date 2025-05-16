package com.anvil_shield.main_service.repository;

import com.anvil_shield.main_service.entity.ConversionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ConversionRepository {

    private final JdbcTemplate jdbcTemplate;

    public ConversionEntity saveConversion(ConversionEntity entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO conversions (from_currency, to_currency, original_amount, " +
                            "converted_amount, rate, timestamp) VALUES (?, ?, ?, ?, ?, ?)",
                    new String[]{"id"}
            );
            ps.setString(1, entity.getFromCurrency());
            ps.setString(2, entity.getToCurrency());
            ps.setBigDecimal(3, entity.getOriginalAmount());
            ps.setBigDecimal(4, entity.getConvertedAmount());
            ps.setBigDecimal(5, entity.getRate());
            ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            return ps;
        }, keyHolder);

        // Safely extract the generated 'id'
        Map<String, Object> keys = keyHolder.getKeys();
        if (keys != null && keys.containsKey("id")) {
            entity.setId(((Number) keys.get("id")).longValue());
        }

        entity.setTimestamp(new Timestamp(System.currentTimeMillis()));
        return entity;
    }

    @Transactional
    public void logConversion(Long conversionId, String userEmail, String message) {
        String sql = """
                INSERT INTO conversion_logs (conversion_id, message, performed_by, timestamp)
                VALUES (?, ?, ?, ?)
                """;
        jdbcTemplate.update(sql, conversionId, message, userEmail, new Timestamp(System.currentTimeMillis()));
    }
}