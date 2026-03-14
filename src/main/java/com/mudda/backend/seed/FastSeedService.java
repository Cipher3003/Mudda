/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : FastSeedService
 * Author  : Vikas Kumar
 * Created : 10-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.seed;

import com.mudda.backend.user.MuddaUserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FastSeedService {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public FastSeedService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    // TODO: generate users in batch with consecutive IDs, currently it skips 50 for batching
    public List<String[]> seedUsers(int count) {
        String query = """
                 INSERT INTO users (
                    user_id, username, name, phone_number, date_of_birth, email, hashed_password, role, enabled, failed_login_attempts, created_at)
                 VALUES (nextval('users_id_seq'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        List<String[]> csvData = new ArrayList<>();

        long timestamp = System.currentTimeMillis();
        Timestamp now = Timestamp.from(Instant.now());
        LocalDate dob = LocalDate.of(1990, 1, 1);

        String password = "password123";
        String role = MuddaUserRole.CITIZEN.name();
        String hashedPassword = passwordEncoder.encode(password);

        for (int i = 0; i < count; i++) {
            String uniqueId = "%d_%d".formatted(timestamp, i);
            csvData.add(new String[]{
                    "user_%s".formatted(uniqueId),  // 0
                    "Name %d".formatted(i), // 1
                    "555 %07d".formatted(i),    // 2
                    "user%s@test.com".formatted(uniqueId),  // 3
                    password  // 4
            });
        }

        log.debug("Finished generating unique data for seeding to database");

        jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                String[] row = csvData.get(i);
                ps.setString(1, row[0]);
                ps.setString(2, row[1]);
                ps.setString(3, row[2]);
                ps.setObject(4, Date.valueOf(dob));
                ps.setString(5, row[3]);
                ps.setString(6, hashedPassword);
                ps.setString(7, role);
                ps.setBoolean(8, true);
                ps.setInt(9, 0);
                ps.setObject(10, now);
            }

            @Override
            public int getBatchSize() {
                return count;
            }
        });

        log.info("Seeded Users: {} successfully to database with one query", count);

        return csvData;
    }
}
