package com.mudda.backend.seed;

import com.google.gson.Gson;
import com.mudda.backend.category.CategorySeed;
import com.mudda.backend.category.CategoryService;
import com.mudda.backend.category.CreateCategoryRequest;
import com.mudda.backend.comment.CommentLike;
import com.mudda.backend.comment.CommentLikeSeed;
import com.mudda.backend.comment.CommentLikeService;
import com.mudda.backend.comment.CommentSeed;
import com.mudda.backend.comment.CommentService;
import com.mudda.backend.comment.CreateCommentRequest;
import com.mudda.backend.issue.CreateIssueRequest;
import com.mudda.backend.issue.IssueSeed;
import com.mudda.backend.issue.IssueService;
import com.mudda.backend.location.CoordinateDTO;
import com.mudda.backend.location.CreateLocationRequest;
import com.mudda.backend.location.LocationSeed;
import com.mudda.backend.location.LocationService;
import com.mudda.backend.role.CreateRoleRequest;
import com.mudda.backend.role.RoleSeed;
import com.mudda.backend.role.RoleService;
import com.mudda.backend.user.CreateUserRequest;
import com.mudda.backend.user.UserSeed;
import com.mudda.backend.user.UserService;
import com.mudda.backend.vote.Vote;
import com.mudda.backend.vote.VoteSeed;
import com.mudda.backend.vote.VoteService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import net.datafaker.Faker;
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

import static net.datafaker.providers.base.Text.DIGITS;
import static net.datafaker.providers.base.Text.EN_UPPERCASE;

@Service
public class SeedService {

    @PersistenceContext
    private EntityManager entityManager;
    private final Random random = new Random();
    private final Faker faker = new Faker(random);

    // In a real application, these services would be injected.
    // For this example, we are instantiating them directly.
    private final RoleService roleService;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final UserService userService;
    private final IssueService issueService;
    private final VoteService voteService;
    private final CommentService commentService;
    private final CommentLikeService commentLikeService;

    public SeedService(RoleService roleService,
            CategoryService categoryService,
            LocationService locationService,
            UserService userService,
            VoteService voteService,
            IssueService issueService,
            CommentService commentService,
            CommentLikeService commentLikeService) {
        this.roleService = roleService;
        this.categoryService = categoryService;
        this.locationService = locationService;
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
        List<String> feedback = new ArrayList<>();
        feedback.add("Starting database cleanup using manual deletion...");

        // The order of deletion is crucial to avoid foreign key constraint violations.
        // We delete from entities that have dependencies first, moving towards root
        // entities.
        // For example, Comments depend on Issues and Users, so they must be deleted
        // first.
        List<String> entityNamesInDeletionOrder = List.of(
                "CommentLike",
                "Comment",
                "Vote",
                "Issue",
                "User",
                "Location",
                "Category",
                "Role");

        try {
            for (String entityName : entityNamesInDeletionOrder) {
                String jpql = "DELETE FROM " + entityName;
                int deletedCount = entityManager.createQuery(jpql).executeUpdate();
                feedback.add("Deleted " + deletedCount + " records from " + entityName + ".");
            }
            feedback.add("All records deleted successfully.");

            // --- New Step: Reset all primary key sequences ---
            feedback.add("Resetting all table sequences...");
            // Note: These sequence names are based on default Hibernate/JPA naming
            // conventions.
            // You may need to adjust them if your entity definitions specify custom
            // sequence names.
            List<String> sequenceNames = List.of(
                    "comment_likes_id_seq",
                    "comments_id_seq",
                    "votes_id_seq",
                    "issues_id_seq",
                    "users_id_seq",
                    "locations_id_seq",
                    "categories_id_seq",
                    "roles_id_seq");

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
            // The @Transactional annotation will automatically handle rolling back the
            // transaction
            // in case of an error, preventing a partially cleared database.
        }
        return feedback;
    }

    @Transactional
    public List<String> seedDatabase(CreateSeedRequest request) {
        // --- Data stores to simulate database primary keys for relationships ---
        List<String> feedback = new ArrayList<>();
        List<Long> roleIds = new ArrayList<>();
        List<Long> userIds = new ArrayList<>();
        List<Long> locationIds = new ArrayList<>();
        List<Long> categoryIds = new ArrayList<>();
        List<Long> issueIds = new ArrayList<>();
        List<Long> topLevelCommentIds = new ArrayList<>(); // For flat (YouTube-style) replies

        // --- Improvement 1: More efficient processing using a Map ---
        // This avoids iterating over the request list multiple times.
        Map<Entity, Integer> generationMap = new EnumMap<>(Entity.class);
        request.seedDTOList()
                .forEach(dto -> generationMap.put(dto.entity(), dto.count()));

        // --- Process entities in a fixed order that respects dependencies ---
        if (generationMap.containsKey(Entity.Role))
            generateRoles(generationMap.get(Entity.Role), roleIds, feedback);

        if (generationMap.containsKey(Entity.User))
            generateUsers(generationMap.get(Entity.User), userIds, roleIds, feedback);

        if (generationMap.containsKey(Entity.Location))
            generateLocations(generationMap.get(Entity.Location), locationIds, feedback);

        if (generationMap.containsKey(Entity.Category))
            generateCategories(generationMap.get(Entity.Category), categoryIds, feedback);

        if (generationMap.containsKey(Entity.Issue))
            generateIssues(generationMap.get(Entity.Issue), issueIds, userIds, locationIds, categoryIds, feedback);

        if (generationMap.containsKey(Entity.Comment))
            generateComments(generationMap.get(Entity.Comment), topLevelCommentIds, issueIds, userIds, feedback);

        if (generationMap.containsKey(Entity.Reply))
            generateReplies(generationMap.get(Entity.Reply), topLevelCommentIds, issueIds, userIds, feedback);

        if (feedback.isEmpty())
            feedback.add("No entities requested for generation.");
        else if (feedback.stream().noneMatch(msg -> msg.startsWith("ERROR:")))
            feedback.add("All requested entities generated successfully.");

        return feedback;
    }

    @Transactional
    public List<String> seedDatabaseFromJson() {
        Gson gson = new Gson();
        List<String> feedback = new ArrayList<>();

        try (InputStream stream = SeedService.class.getClassLoader().getResourceAsStream("seed.json")) {
            if (stream == null) {
                feedback.add("Error: seed.json not found in classpath");
                return feedback;
            }

            try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                SeedData seedData = gson.fromJson(reader, SeedData.class);

                roleService.saveRoles(seedData.roles().stream().map(RoleSeed::toRole).toList());
                feedback.add("Roles seeded: " + seedData.roles().size());

                userService.saveUsers(seedData.users().stream().map(UserSeed::toUser).toList());
                feedback.add("Users seeded: " + seedData.users().size());

                locationService.saveLocations(seedData.locations().stream().map(LocationSeed::toLocation).toList());
                feedback.add("Locations seeded: " + seedData.locations().size());

                categoryService.saveCategories(seedData.categories().stream().map(CategorySeed::toCategory).toList());
                feedback.add("Categories seeded: " + seedData.categories().size());

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
                                random.nextInt(1, userCount + 1)
                        )));
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
                                        random.nextInt(1, userCount + 1)
                                )));
                    }
                }

                commentLikeService.saveCommentLikes(commentLikes);
                feedback.add("Comment Likes generated: " + commentLikes.size());

                feedback.add("Json seeding completed successfully");

            }
        } catch (IOException e) {
            feedback.add("Error during JSON seeding: "+e.getMessage());
            e.printStackTrace();
        }

        return feedback;
    }

    // --- Generation Logic for each Entity ---

    private void generateRoles(int count, List<Long> roleIds, List<String> feedback) {
        feedback.add("Generating " + count + " roles...");
        List<CreateRoleRequest> roleRequests = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            // Appending a random number to ensure the role name is unique
            String uniqueRoleName = faker.job().title() + " " + random.nextInt(100000);
            roleRequests.add(new CreateRoleRequest(uniqueRoleName));
        }
        roleIds.addAll(roleService.createRoles(roleRequests));
    }

    private void generateUsers(int count, List<Long> userIds, List<Long> roleIds, List<String> feedback) {
        if (roleIds.isEmpty()) {
            feedback.add("Cannot generate users: No roles found. Add roles in request.");
            return;
        }
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
                    getRandomId(roleIds), // Get a random existing role ID
                    faker.avatar().image()));
        }
        userIds.addAll(userService.createUsers(userRequests));
    }

    private void generateLocations(int count, List<Long> locationIds, List<String> feedback) {
        feedback.add("Generating " + count + " locations...");
        List<CreateLocationRequest> locationRequests = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            CoordinateDTO coordinate = new CoordinateDTO(
                    Double.parseDouble(faker.address().latitude().replace(',', '.')),
                    Double.parseDouble(faker.address().longitude().replace(',', '.')));

            CreateLocationRequest request = new CreateLocationRequest(
                    faker.address().streetAddress(),
                    faker.address().zipCode(),
                    faker.address().city(),
                    faker.address().state(),
                    coordinate);

            locationRequests.add(request);
        }
        locationIds.addAll(locationService.createLocations(locationRequests));
    }

    private void generateCategories(int count, List<Long> categoryIds, List<String> feedback) {
        feedback.add("Generating " + count + " categories...");
        List<CreateCategoryRequest> categoryRequests = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            // Appending a random number to ensure the category name is unique
            String uniqueCategoryName = faker.commerce().department() + " " + random.nextInt(100000);
            categoryRequests.add(new CreateCategoryRequest(uniqueCategoryName));
        }

        categoryIds.addAll(categoryService.createCategories(categoryRequests));
    }

    private void generateIssues(int count, List<Long> issueIds, List<Long> userIds,
            List<Long> locationIds, List<Long> categoryIds, List<String> feedback) {
        if (userIds.isEmpty() || locationIds.isEmpty() || categoryIds.isEmpty()) {
            feedback.add("Cannot generate issues: Missing Users, Locations, or Categories. " +
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

            String title = faker.lorem().sentence(random.nextInt(5, 20)).trim();
            if (title.length() > 150)
                title = title.substring(0, 140);

            issueRequests.add(new CreateIssueRequest(
                    title,
                    faker.lorem().paragraph(random.nextInt(5, 15)),
                    getRandomId(locationIds),
                    getRandomId(categoryIds),
                    media));

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

    /**
     * Utility method to pick a random ID from a list of existing IDs.
     */
    private Long getRandomId(List<Long> idList) {
        if (idList == null || idList.isEmpty())
            return null;

        return idList.get(random.nextInt(idList.size()));
    }
}
