package com.mudda.backend.seed;

import com.google.gson.Gson;
import com.mudda.backend.comment.CommentLike;
import com.mudda.backend.comment.CommentLikeService;
import com.mudda.backend.comment.CommentService;
import com.mudda.backend.comment.dto.CommentLikeSeed;
import com.mudda.backend.comment.dto.CommentSeed;
import com.mudda.backend.comment.dto.CreateCommentRequest;
import com.mudda.backend.issue.IssueCategory;
import com.mudda.backend.issue.IssueService;
import com.mudda.backend.issue.dto.CreateIssueRequest;
import com.mudda.backend.issue.dto.IssueSeed;
import com.mudda.backend.issue.dto.CoordinateDTO;
import com.mudda.backend.user.MuddaUserRole;
import com.mudda.backend.user.UserService;
import com.mudda.backend.user.dto.CreateUserRequest;
import com.mudda.backend.user.dto.UserSeed;
import com.mudda.backend.vote.Vote;
import com.mudda.backend.vote.VoteSeed;
import com.mudda.backend.vote.VoteService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import net.datafaker.providers.base.Address;
import net.datafaker.providers.base.Text;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static net.datafaker.providers.base.Text.DIGITS;
import static net.datafaker.providers.base.Text.EN_UPPERCASE;

@Slf4j
@Service
public class SeedService {

    // In a real application, these services would be injected.
    // For this example, we are instantiating them directly.
    @PersistenceContext
    private EntityManager entityManager;
    private final Random random = new Random();
    private final Faker faker = new Faker(random);

    private final UserService userService;
    private final IssueService issueService;
    private final VoteService voteService;
    private final CommentService commentService;
    private final CommentLikeService commentLikeService;

    public SeedService(
            UserService userService,
            VoteService voteService,
            IssueService issueService,
            CommentService commentService,
            CommentLikeService commentLikeService
    ) {
        this.userService = userService;
        this.voteService = voteService;
        this.issueService = issueService;
        this.commentService = commentService;
        this.commentLikeService = commentLikeService;
    }

    /**
     * Clears all data from all tables in the public schema.
     * This is a destructive operation and should be used with caution.
     *
     * @return A list of strings detailing the operations performed.
     */
    @Transactional
    public List<String> clearDatabase() {
        log.info("Clearing database");
        List<String> feedback = new ArrayList<>();
        feedback.add("Starting database cleanup using manual deletion...");

        // The order of deletion is crucial to avoid foreign key constraint violations.
        // We delete from entities that have dependencies first, moving towards root
        // entities.
        // For example, Comments depend on Issues and Users, so comment must be deleted
        // first.
        List<String> entityNamesInDeletionOrder = List.of(
                "CommentLike",
                "Comment",
                "Vote",
                "Issue",
                "VerificationToken",
                "RefreshToken",
                "MuddaUser");

        try {
            for (String entityName : entityNamesInDeletionOrder) {
                String jpql = "DELETE FROM " + entityName;
                int deletedCount = entityManager.createQuery(jpql).executeUpdate();
                feedback.add("Deleted " + deletedCount + " records from " + entityName + ".");
            }
            feedback.add("All records deleted successfully.");

            // --- Next Step: Reset all primary key sequences ---
            feedback.add("Resetting all table sequences...");
            // Note: Use custom sequence names that are based on Hibernate/JPA naming
            // conventions.
            List<String> sequenceNames = List.of(
                    "comment_likes_id_seq",
                    "comments_id_seq",
                    "votes_id_seq",
                    "issues_id_seq",
                    "action_tokens_id_seq",
                    "refresh_token_id_seq",
                    "users_id_seq");

            for (String sequenceName : sequenceNames) {
                try {
                    entityManager.createNativeQuery("ALTER SEQUENCE " + sequenceName + " RESTART WITH 1;")
                            .executeUpdate();
                    feedback.add("Sequence '" + sequenceName + "' reset to 1.");
                } catch (Exception e) {
                    // This might fail if a sequence doesn't exist or name is different, which is
                    // okay.
                    // We log it as a warning instead of a fatal error.
                    feedback.add("WARN: Could not reset sequence '" + sequenceName
                            + "'. It might not exist or name is incorrect. Skipping.");
                }
            }
            feedback.add("Database cleared successfully.");

        } catch (Exception e) {
            feedback.add("ERROR: An unexpected error occurred while clearing the database.");
            feedback.add("Error Details: " + e.getMessage());
            log.error("Unexpected error while clearing the database.", e);
        }
        return feedback;
    }

    @Transactional
    public List<String> seedDatabase(CreateSeedRequest request) {
        log.info("Seeding database with request");
        // --- Data stores to simulate database primary keys for relationships ---
        List<String> feedback = new ArrayList<>();
        List<Long> userIds = new ArrayList<>();
        List<Long> issueIds = new ArrayList<>();
        List<Long> topLevelCommentIds = new ArrayList<>(); // For flat (YouTube-style) replies

        // --- Improvement 1: More efficient processing using a Map ---
        // This avoids iterating over the request list multiple times.
        Map<Entity, Integer> generationMap = new EnumMap<>(Entity.class);
        request.seedDTOList()
                .forEach(dto -> generationMap.put(dto.entity(), dto.count()));

        // --- Process entities in a fixed order that respects dependencies ---
        // TODO: better seeding for relationship
        // if (generationMap.containsKey(Entity.User))
        // generateUsers(generationMap.get(Entity.User), userIds, feedback);
        if (generationMap.containsKey(Entity.User))
            userIds.addAll(LongStream.rangeClosed(0, generationMap.get(Entity.User)).boxed().toList());

        if (generationMap.containsKey(Entity.Issue))
            generateIssues(generationMap.get(Entity.Issue), issueIds, userIds, feedback);

        if (generationMap.containsKey(Entity.Comment))
            generateComments(generationMap.get(Entity.Comment), topLevelCommentIds, issueIds, userIds, feedback);

        if (generationMap.containsKey(Entity.Reply))
            generateReplies(generationMap.get(Entity.Reply), topLevelCommentIds, issueIds, userIds, feedback);

        if (generationMap.containsKey(Entity.Vote))
            generateVotes(generationMap.get(Entity.Vote), issueIds, userIds, feedback);

        if (feedback.isEmpty())
            feedback.add("No entities requested for generation.");
        else if (feedback.stream().noneMatch(msg -> msg.startsWith("ERROR:")))
            feedback.add("All requested entities generated successfully.");

        return feedback;
    }

    @Transactional
    public List<String> seedDatabaseFromJson() {
        log.info("Seeding database from JSON");
        Gson gson = new Gson();
        List<String> feedback = new ArrayList<>();

        try (InputStream stream = SeedService.class.getClassLoader().getResourceAsStream("seed.json")) {
            if (stream == null) {
                feedback.add("Error: seed.json not found in classpath");
                return feedback;
            }

            try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                SeedData seedData = gson.fromJson(reader, SeedData.class);

                userService.saveUsers(seedData.users().stream().map(UserSeed::toUser).toList());
                feedback.add("Users seeded: " + seedData.users().size());

                issueService.saveIssues(seedData.issues().stream().map(IssueSeed::toIssue).toList());
                feedback.add("Issues seeded: " + seedData.issues().size());

                commentService.saveComments(seedData.comments().stream().map(CommentSeed::toComment).toList());
                feedback.add("Comments seeded: " + seedData.comments().size());

                int userCount = seedData.users().size();

                List<Vote> votesList = new ArrayList<>();

                for (int issueId = 1; issueId <= seedData.issues().size(); issueId++) {
                    int voteCount = random.nextInt(1, userCount);

                    for (int voteId = 0; voteId < voteCount; voteId++) {
                        votesList.add(VoteSeed.toVote(new VoteSeed(
                                issueId,
                                random.nextInt(1, userCount + 1))));
                    }
                }

                voteService.saveVotes(votesList);
                feedback.add("Votes generated: " + votesList.size());

                List<CommentLike> commentLikes = new ArrayList<>();

                for (int commentId = 1; commentId <= seedData.comments().size(); commentId++) {
                    int likes = random.nextInt(1, userCount);

                    for (int likeId = 0; likeId < likes; likeId++) {
                        commentLikes.add(CommentLikeSeed
                                .toCommentLike(new CommentLikeSeed(
                                        commentId,
                                        random.nextInt(1, userCount + 1))));
                    }
                }

                commentLikeService.saveCommentLikes(commentLikes);
                feedback.add("Comment Likes generated: " + commentLikes.size());

                feedback.add("Json seeding completed successfully");

            }
        } catch (IOException e) {
            feedback.add("Error during JSON seeding: " + e.getMessage());
            log.error("Unexcepted error while seeding from json", e);
        }

        return feedback;
    }

    // --- Generation Logic for each Entity ---

    private void generateUsers(int count, List<Long> userIds, List<String> feedback) {
        feedback.add("Generating " + count + " users...");

        List<CreateUserRequest> userRequests = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            // Generate unique username, email, and phone number to avoid constraint
            // violations
            String uniqueSuffix = String.valueOf(random.nextInt(100000));
            String baseUsername = faker.credentials().username().replaceAll("[^a-zA-Z0-9]", ""); // Sanitize username
            String uniqueUsername = baseUsername + uniqueSuffix;
            String uniqueEmail = uniqueUsername + "@example.com";
            String uniquePhoneNumber = "+919" + faker.number().digits(9); // Create a unique 12-digit number
            String password = faker.text().text(
                    Text.TextSymbolsBuilder
                            .builder()
                            .len(8)
                            .with(EN_UPPERCASE, 2)
                            .with(DIGITS, 3)
                            .build());

            userRequests.add(new CreateUserRequest(
                    uniqueUsername,
                    faker.name().fullName(),
                    uniqueEmail,
                    faker.timeAndDate().birthday(),
                    uniquePhoneNumber,
                    password,
                    getRandomMuddaUserRole(), // Get a random existing role
                    faker.avatar().image(),
                    null));
        }
        userIds.addAll(userService.createUsers(userRequests));
    }

    private void generateIssues(int count, List<Long> issueIds, List<Long> userIds, List<String> feedback) {
        if (userIds.isEmpty()) {
            feedback.add("Cannot generate issues: Missing Users, Locations. " +
                    "Add users, locations, categories in request.");
            return;
        }
        feedback.add("Generating " + count + " issues...");

        List<CreateIssueRequest> issueRequests = new ArrayList<>();
        List<Long> users = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            List<String> media = IntStream
                    .range(0, faker.number().numberBetween(1, 5))
                    .mapToObj(n -> faker.internet().url())
                    .toList();

            String title = faker.lorem().fixedString(random.nextInt(50, 140)).trim();
            Address address = faker.address();
            Double x = Double.parseDouble(faker.address().latitude().replace(',', '.'));
            Double y = Double.parseDouble(faker.address().longitude().replace(',', '.'));

            issueRequests.add(new CreateIssueRequest(
                    title,
                    faker.lorem().paragraph(random.nextInt(5, 15)),
                    getRandomIssueCategory().getCode(),
                    media,
                    address.zipCode(),
                    address.city(),
                    address.state(),
                    new CoordinateDTO(x, y)
            ));

            users.add(getRandomId(userIds));
        }
        issueIds.addAll(issueService.createIssues(users, issueRequests));
    }

    private void generateComments(int count, List<Long> parentCommentIds, List<Long> issueIds,
                                  List<Long> userIds, List<String> feedback) {
        if (issueIds.isEmpty() || userIds.isEmpty()) {
            feedback.add("Cannot generate comments: Missing Issues or Users. Add issues, users in request");
            return;
        }
        feedback.add("Generating " + count + " top-level comments...");

        List<CreateCommentRequest> commentRequests = new ArrayList<>();
        List<Long> issues = new ArrayList<>();
        List<Long> users = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            commentRequests.add(new CreateCommentRequest(
                    faker.lorem().paragraph(random.nextInt(5, 15))));

            issues.add(getRandomId(issueIds));
            users.add(getRandomId(userIds));
        }

        parentCommentIds.addAll(commentService.createComments(issues, users, commentRequests));
    }

    private void generateReplies(int count, List<Long> parentCommentIds, List<Long> issueIds,
                                 List<Long> userIds, List<String> feedback) {
        if (parentCommentIds.isEmpty() || userIds.isEmpty() || issueIds.isEmpty()) {
            feedback.add("Cannot generate replies: Missing parent Comments, Users, or Issues. " +
                    "Add comments, users, issues in request");
            return;
        }
        feedback.add("Generating " + count + " replies...");

        List<CreateCommentRequest> replyRequests = new ArrayList<>();
        List<Long> parents = new ArrayList<>();
        List<Long> issues = new ArrayList<>();
        List<Long> users = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            replyRequests.add(new CreateCommentRequest(
                    faker.lorem().paragraph(random.nextInt(5, 15))));
            parents.add(getRandomId(parentCommentIds));
            issues.add(getRandomId(issueIds));
            users.add(getRandomId(userIds));
        }

        commentService.createReplies(parents, users, issues, replyRequests);
    }

    private void generateVotes(int count, List<Long> issueIds, List<Long> userIds, List<String> feedback) {
        if (issueIds.isEmpty() || userIds.isEmpty()) {
            feedback.add("Cannot generate votes: Missing Issues or Users. Add issues, users in request");
            return;
        }
        feedback.add("Generating " + count + " votes...");

        List<Vote> votes = new ArrayList<>();
        Set<String> uniqueVotes = new HashSet<>();

        // Ensure we don't loop forever if count is larger than possible combinations
        long maxPossible = (long) issueIds.size() * userIds.size();
        int toGenerate = (int) Math.min(count, maxPossible);

        int attempts = 0;
        int maxAttempts = toGenerate * 3; // To avoid infinite loop if distribution is bad

        while (votes.size() < toGenerate && attempts < maxAttempts) {
            Long issueId = getRandomId(issueIds);
            Long userId = getRandomId(userIds);
            String key = issueId + "-" + userId;

            if (uniqueVotes.add(key)) {
                votes.add(Vote.castVote(issueId, userId));
            }
            attempts++;
        }

        voteService.saveVotes(votes);
    }

    /**
     * Utility method to pick a random ID from a list of existing IDs.
     */
    private Long getRandomId(List<Long> idList) {
        if (idList == null || idList.isEmpty())
            return null;

        return idList.get(random.nextInt(idList.size()));
    }

    private MuddaUserRole getRandomMuddaUserRole() {
        MuddaUserRole[] muddaUserRoles = MuddaUserRole.values();
        return muddaUserRoles[random.nextInt(muddaUserRoles.length)];
    }

    private IssueCategory getRandomIssueCategory() {
        IssueCategory[] issueCategories = IssueCategory.values();
        return issueCategories[random.nextInt(issueCategories.length)];
    }
}
