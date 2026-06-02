/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : SeedServiceV2
 * Author  : Vikas Kumar
 * Created : 08-04-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.seed;

import com.mudda.backend.comment.Comment;
import com.mudda.backend.issue.Issue;
import com.mudda.backend.media.Media;
import com.mudda.backend.user.MuddaUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.mudda.backend.seed.SeedDataGenerator.MEDIA_OVERFLOW;

@Slf4j
@Service
public class SeedServiceV2 {
    // could introduce small batch inserts to reduce failure side effects
    // reduce allocation size -> 20

    private final JdbcTemplate jdbcTemplate;
    private final SeedDataGenerator generator = new SeedDataGenerator();

    private List<String> feedback;
    private List<Long> userIds;
    private List<Long> issueIds;
    private List<Long> commentIds;
    private List<Long> mediaIds;
    private int mediaCoverage = 0;

    // region SQL queries

    private static final String INSERT_USERS = """
             INSERT INTO users (
                user_id, username, name, phone_number, date_of_birth, email, hashed_password, role, enabled, failed_login_attempts, created_at)
             VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
    private static final String INSERT_ISSUES = """
             INSERT INTO issues (
                issue_id, title, description, pin_code, city, state, coordinate, issue_status, issue_category, severity_score, vote_count, comment_count, created_at, user_id)
             VALUES (?, ?, ?, ?, ?, ?, ST_SetSRID(ST_MakePoint(?, ?), 4326), ?, ?, ?, ?, ?, ?, ?)
            """;
    private static final String INSERT_COMMENTS = """
             INSERT INTO comments (
                id, created_at, issue_id, like_count, reply_count, text, user_id)
             VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
    private static final String INSERT_MEDIA = """
             INSERT INTO media (
                id, created_at, media_key, owner_id, owner_type, position, public_id, status)
             VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
    private static final String FETCH_SEQUENCE = """
            SELECT nextVal('%s_id_seq') FROM generate_series(1, %d)
            """;
    private static final String TRUNCATE_ALL = """
            TRUNCATE comment_likes, comments, votes, media, issues, action_tokens, refresh_tokens, users
            RESTART IDENTITY CASCADE;
            """;

    // endregion

    private static final int BATCH_SIZE = 100;

    public SeedServiceV2(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /*
     * Better way for ID
     * Define IdBlock - same size as sequence increment
     * works similarly to hibernate
     * fetch id blocks while prefetching
     * randomly get blocks for better randomness in media coverage
     * assign special relationships or behaviors to some blocks
     * generator will handle how to assign id based on blocks
     * need a way to also keep the original size or track the last block with less size capacity
     */

    /*
     * New way for id fetching (Maybe better)
     * Determine the sequence blocks needed for the entities based on entity count and sequence increment
     * Then store the range of ID instead list, consume all IDs in between
     * inspired by hibernate batching
     */

    @Transactional
    public List<String> clearDatabase() {
        log.info("Clearing database");

        List<String> feedback = new ArrayList<>();
        feedback.add("Starting database cleanup using truncate...");

        try {
            jdbcTemplate.execute(TRUNCATE_ALL);
            feedback.add("Deleted all records from all tables.");
            feedback.add("Database cleanup complete.");
        } catch (Exception e) {
            feedback.add("ERROR: " + e.getMessage());
            log.error("Unexpected error while clearing the database.", e);
        }

        return feedback;
    }

    @Transactional
    public List<String> seedDatabase(CreateSeedRequest request) {
        feedback = new ArrayList<>();

        for (CreateSeedRequestItem item : request.seedDTOList())
            switch (item.entity()) {
                case User -> userIds = fetchIds("users", item.count());
                case Issue -> issueIds = fetchIds("issues", item.count());
                case Comment -> commentIds = fetchIds("comments", item.count());
                case Media -> {
                    int coverage = item.count();
                    if (coverage < 0 || coverage > 100) {
                        feedback.add("Error: Wrong Media coverage value. Media coverage must be between 1 and 100. Defaulting to 0% coverage.");
                        mediaIds = new ArrayList<>(0);
                    } else {
                        int totalEntities = issueIds.size() + userIds.size();
                        int totalMediaCount = (int) (totalEntities * coverage * MEDIA_OVERFLOW) / 100;
                        mediaIds = fetchIds("media", totalMediaCount);
                        mediaCoverage = coverage;
                    }
                }
            }

        for (CreateSeedRequestItem item : request.seedDTOList())
            switch (item.entity()) {
                case User -> seedUsers();
                case Issue -> seedIssues();
                case Comment -> seedComments();
                case Media -> seedMedia();
            }

        return feedback;
    }

    private void seedUsers() {
        List<MuddaUser> users = generator.generateUsers(userIds);
        int rowsCount = insertUsers(users);
        feedback.add("Inserted %d users...".formatted(rowsCount));

        dumpUsers(users);
    }

    private void dumpUsers(List<MuddaUser> users) {
        String userCsvPath = "seed-dumps/users.csv";
        feedback.add("Writing user credentials to file at: %s".formatted(userCsvPath));

        String password = "password123";
        List<String> usersCSV = users.stream()
                .map(user -> String.join(",",
                        user.getUserId().toString(),
                        user.getUsername(),
                        user.getEmail(),
                        password
                ))
                .toList();

        try {
            Files.write(Paths.get(userCsvPath), usersCSV, StandardCharsets.UTF_8);
        } catch (IOException e) {
            feedback.add("Error writing users.csv");
            System.err.printf("Error writing users.csv %s%n", e.getMessage());
        }
    }

    private int insertUsers(List<MuddaUser> users) {
        return batchInsert(INSERT_USERS, users, (ps, user) -> {
            ps.setLong(1, user.getUserId());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getName());
            ps.setString(4, user.getPhoneNumber());
            ps.setDate(5, Date.valueOf(user.getDateOfBirth()));
            ps.setString(6, user.getEmail());
            ps.setString(7, user.getHashedPassword());
            ps.setString(8, user.getRole().name());
            ps.setBoolean(9, user.isEnabled());
            ps.setInt(10, user.getFailedLoginAttempts());
            ps.setTimestamp(11, Timestamp.from(Instant.now()));
        });
    }

    private void seedIssues() {
        List<Issue> issues = generator.generateIssues(issueIds, userIds);
        int rowCount = insertIssues(issues);
        feedback.add("Finished seeding %d issues".formatted(rowCount));
    }

    private int insertIssues(List<Issue> issues) {
        return batchInsert(INSERT_ISSUES, issues, (ps, issue) -> {
            ps.setLong(1, issue.getId());
            ps.setString(2, issue.getTitle());
            ps.setString(3, issue.getDescription());
            ps.setString(4, issue.getPinCode());
            ps.setString(5, issue.getCity());
            ps.setString(6, issue.getState());
            ps.setDouble(7, issue.getCoordinate().getX());
            ps.setDouble(8, issue.getCoordinate().getY());
            ps.setString(9, issue.getStatus().name());
            ps.setString(10, issue.getCategory().name());
            ps.setDouble(11, issue.getSeverityScore());
            ps.setLong(12, issue.getVoteCount());
            ps.setLong(13, issue.getCommentCount());
            ps.setTimestamp(14, Timestamp.from(Instant.now()));
            ps.setLong(15, issue.getUserId());
        });
    }

    private void seedComments() {
        List<Comment> comments = generator.generateComments(commentIds, issueIds, userIds);
        int rowsCount = insertComments(comments);
        feedback.add("Inserted %d comments".formatted(rowsCount));
    }

    private int insertComments(List<Comment> comments) {
        return batchInsert(INSERT_COMMENTS, comments, (ps, comment) -> {
            ps.setLong(1, comment.getId());
            ps.setTimestamp(2, Timestamp.from(Instant.now()));
            ps.setLong(3, comment.getIssueId());
            ps.setLong(4, comment.getLikeCount());
            ps.setLong(5, comment.getReplyCount());
            ps.setString(6, comment.getText());
            ps.setLong(7, comment.getUserId());
        });
    }

    private void seedMedia() {
        List<Long> mediaIssueIds = subListPercent(issueIds, mediaCoverage);
        List<Long> mediaUserIds = subListPercent(userIds, mediaCoverage);
        List<Media> medias = generator.generateMedias(mediaIds, mediaIssueIds, mediaUserIds);
        int rowsCount = insertMedias(medias);
        feedback.add("Inserted %d medias".formatted(rowsCount));
    }

    private int insertMedias(List<Media> medias) {
        return batchInsert(INSERT_MEDIA, medias, (ps, media) -> {
            ps.setLong(1, media.getId());
            ps.setTimestamp(2, Timestamp.from(Instant.now()));
            ps.setString(3, media.getMediaKey());
            ps.setLong(4, media.getOwnerId());
            ps.setString(5, media.getOwnerType().name());
            ps.setLong(6, media.getPosition());
            ps.setString(7, media.getPublicId());
            ps.setString(8, media.getStatus().name());
        });
    }

    private <T> int batchInsert(String sql, List<T> items, ParameterizedPreparedStatementSetter<T> setter) {
        if (items == null || items.isEmpty()) return 0;
        jdbcTemplate.batchUpdate(sql, items, BATCH_SIZE, setter);
        return items.size();
    }

    private List<Long> fetchIds(String table, int count) {
        String sql = FETCH_SEQUENCE.formatted(table, count);
        return jdbcTemplate.queryForList(sql, Long.class);
    }

    private <T> List<T> subListPercent(List<T> list, int percent) {
        return list.subList(0, (list.size() * percent) / 100);
    }

}
