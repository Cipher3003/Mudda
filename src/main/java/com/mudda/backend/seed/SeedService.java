package com.mudda.backend.seed;

import com.github.javafaker.Faker;
import com.mudda.backend.category.CategoryResponse;
import com.mudda.backend.category.CategoryService;
import com.mudda.backend.category.CreateCategoryRequest;
import com.mudda.backend.comment.Comment;
import com.mudda.backend.comment.CommentMapper;
import com.mudda.backend.comment.CommentService;
import com.mudda.backend.comment.CreateCommentRequest;
import com.mudda.backend.issue.CreateIssueRequest;
import com.mudda.backend.issue.IssueResponse;
import com.mudda.backend.issue.IssueService;
import com.mudda.backend.location.CoordinateDTO;
import com.mudda.backend.location.CreateLocationRequest;
import com.mudda.backend.location.LocationResponse;
import com.mudda.backend.location.LocationService;
import com.mudda.backend.role.CreateRoleRequest;
import com.mudda.backend.role.RoleResponse;
import com.mudda.backend.role.RoleService;
import com.mudda.backend.user.CreateUserRequest;
import com.mudda.backend.user.User;
import com.mudda.backend.user.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.IntStream;

@Service
public class SeedService {

    @PersistenceContext
    private EntityManager entityManager;
    private final Faker faker = new Faker();
    private final Random random = new Random();

    // In a real application, these services would be injected.
    // For this example, we are instantiating them directly.
    private final RoleService roleService;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final UserService userService;
    private final IssueService issueService;
    private final CommentService commentService;

    public SeedService(RoleService roleService, CategoryService categoryService,
                       LocationService locationService, UserService userService,
                       IssueService issueService, CommentService commentService) {
        this.roleService = roleService;
        this.categoryService = categoryService;
        this.locationService = locationService;
        this.userService = userService;
        this.issueService = issueService;
        this.commentService = commentService;
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
        feedback.add("Starting database cleanup...");

        try {
            // A PostgresSQL-specific way to temporarily disable foreign key checks
            entityManager.createNativeQuery("SET session_replication_role = 'replica'").executeUpdate();
            feedback.add("Step 1: Foreign key constraints disabled.");

            // Fetch all table names in the 'public' schema
            @SuppressWarnings("unchecked")
            List<String> tableNames = entityManager.createNativeQuery(
                    "SELECT tablename FROM pg_tables WHERE schemaname = 'public'"
            ).getResultList();
            feedback.add("Step 2: Found " + tableNames.size() + " tables to clear.");

            // Truncate all tables and reset their auto-incrementing keys
            for (String tableName : tableNames) {
                entityManager.createNativeQuery("TRUNCATE TABLE \"" + tableName + "\" RESTART IDENTITY CASCADE")
                        .executeUpdate();
            }
            feedback.add("Step 3: All tables truncated successfully.");

            // Re-enable foreign key checks
            entityManager.createNativeQuery("SET session_replication_role = 'origin'").executeUpdate();
            feedback.add("Step 4: Foreign key constraints re-enabled.");
            feedback.add("Database cleared successfully.");

        } catch (Exception e) {
            feedback.add("ERROR: An unexpected error occurred while clearing the database.");
            feedback.add("Error Details: " + e.getMessage());
            // The @Transactional annotation will automatically handle the rollback.
            // We re-enable constraints in a finally block to be safe.
        } finally {
            try {
                // Ensure constraints are always re-enabled, even on failure.
                entityManager.createNativeQuery("SET session_replication_role = 'origin'").executeUpdate();
            } catch (Exception e) {
                feedback.add("CRITICAL ERROR: Failed to re-enable foreign key constraints. " +
                        "Manual intervention may be required.");
            }
        }
        return feedback;
    }

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
        request.seedDTOList().forEach(dto -> generationMap.put(dto.entity(), dto.count()));

        // --- Process entities in a fixed order that respects dependencies ---
        if (generationMap.containsKey(Entity.Role)) {
            generateRoles(generationMap.get(Entity.Role), roleIds, feedback);
        }
        if (generationMap.containsKey(Entity.Location)) {
            generateLocations(generationMap.get(Entity.Location), locationIds, feedback);
        }
        if (generationMap.containsKey(Entity.Category)) {
            generateCategories(generationMap.get(Entity.Category), categoryIds, feedback);
        }
        if (generationMap.containsKey(Entity.User)) {
            generateUsers(generationMap.get(Entity.User), userIds, roleIds, feedback);
        }
        if (generationMap.containsKey(Entity.Issue)) {
            generateIssues(generationMap.get(Entity.Issue), issueIds, userIds, locationIds, categoryIds, feedback);
        }
        if (generationMap.containsKey(Entity.Comment)) {
            generateComments(generationMap.get(Entity.Comment), topLevelCommentIds, issueIds, userIds, feedback);
        }
        if (generationMap.containsKey(Entity.Reply)) {
            generateReplies(generationMap.get(Entity.Reply), topLevelCommentIds, issueIds, userIds, feedback);
        }


        if (feedback.isEmpty()) {
            feedback.add("No entities requested for generation.");
        } else if (feedback.stream().noneMatch(msg -> msg.startsWith("ERROR:"))) {
            feedback.add("All requested entities generated successfully.");
        }
        return feedback;
    }

    // --- Generation Logic for each Entity ---

    private void generateRoles(int count, List<Long> roleIds, List<String> feedback) {
        feedback.add("Generating " + count + " roles...");
        for (int i = 0; i < count; i++) {
            CreateRoleRequest request = new CreateRoleRequest(faker.job().title());
            // In a real app: roleService.createRole(request);
            RoleResponse role = roleService.createRole(request);
            // Simulating DB save by adding a generated ID to our list.
            roleIds.add(role.roleId());
        }
    }

    private void generateLocations(int count, List<Long> locationIds, List<String> feedback) {
        feedback.add("Generating " + count + " locations...");
        for (int i = 0; i < count; i++) {
            CoordinateDTO coordinate = new CoordinateDTO(
                    Double.parseDouble(faker.address().latitude().replace(',', '.')),
                    Double.parseDouble(faker.address().longitude().replace(',', '.'))
            );
            CreateLocationRequest request = new CreateLocationRequest(
                    faker.address().streetAddress(),
                    faker.address().zipCode(),
                    faker.address().city(),
                    faker.address().state(),
                    coordinate
            );
            // In a real app: locationService.createLocation(request);
            LocationResponse location = locationService.createLocation(request);
            locationIds.add(location.locationId());
        }
    }

    private void generateCategories(int count, List<Long> categoryIds, List<String> feedback) {
        feedback.add("Generating " + count + " categories...");
        for (int i = 0; i < count; i++) {
            CreateCategoryRequest request = new CreateCategoryRequest(faker.commerce().department());
            // In a real app: categoryService.createCategory(request);
            CategoryResponse category = categoryService.createCategory(request);
            categoryIds.add(category.id());
        }
    }

    private void generateUsers(int count, List<Long> userIds, List<Long> roleIds, List<String> feedback) {
        if (roleIds.isEmpty()) {
            feedback.add("Cannot generate users: No roles found. Add roles in request.");
            return;
        }
        feedback.add("Generating " + count + " users...");
        for (int i = 0; i < count; i++) {
            CreateUserRequest request = new CreateUserRequest(
                    faker.name().username(),
                    faker.name().fullName(),
                    faker.internet().emailAddress(),
                    faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                    faker.numerify("##########"),
                    faker.internet().password(),
                    getRandomId(roleIds), // Get a random existing role ID
                    faker.avatar().image()
            );
            // In a real app: userService.createUser(request);
            User user = userService.createUser(request);
            userIds.add(user.getUserId());
        }
    }

    private void generateIssues(int count, List<Long> issueIds, List<Long> userIds,
                                List<Long> locationIds, List<Long> categoryIds, List<String> feedback) {
        if (userIds.isEmpty() || locationIds.isEmpty() || categoryIds.isEmpty()) {
            feedback.add("Cannot generate issues: Missing Users, Locations, or Categories. " +
                    "Add users, locations, categories in request.");
            return;
        }
        feedback.add("Generating " + count + " issues...");
        for (int i = 0; i < count; i++) {
            List<String> media = IntStream.range(0, faker.number().numberBetween(1, 5))
                    .mapToObj(n -> faker.internet().url())
                    .toList();

            CreateIssueRequest request = new CreateIssueRequest(
                    faker.lorem().sentence(),
                    faker.lorem().paragraph(),
                    getRandomId(userIds),
                    getRandomId(locationIds),
                    getRandomId(categoryIds),
                    media
            );
            // In a real app: issueService.createIssue(request);
            IssueResponse issue = issueService.createIssue(request);
            issueIds.add(issue.id());
        }
    }

    private void generateComments(int count, List<Long> topLevelCommentIds, List<Long> issueIds,
                                  List<Long> userIds, List<String> feedback) {
        if (issueIds.isEmpty() || userIds.isEmpty()) {
            feedback.add("Cannot generate comments: Missing Issues or Users. Add issues, users in request");
            return;
        }
        feedback.add("Generating " + count + " top-level comments...");
        for (int i = 0; i < count; i++) {
            CreateCommentRequest request = new CreateCommentRequest(
                    faker.lorem().paragraph(),
                    getRandomId(issueIds),
                    getRandomId(userIds),
                    null // This is a top-level comment, so parentId is null
            );
            // In a real app: commentService.createComment(request);
            Comment comment = commentService.createComment(CommentMapper.toComment(request));
            // Simulating DB save and adding new comment's ID to the list for potential replies.
            topLevelCommentIds.add(comment.getCommentId());
        }
    }

    private void generateReplies(int count, List<Long> topLevelCommentIds, List<Long> issueIds,
                                 List<Long> userIds, List<String> feedback) {
        if (topLevelCommentIds.isEmpty() || userIds.isEmpty() || issueIds.isEmpty()) {
            feedback.add("Cannot generate replies: Missing parent Comments, Users, or Issues. " +
                    "Add comments, users, issues in request");
            return;
        }
        feedback.add("Generating " + count + " replies...");
        for (int i = 0; i < count; i++) {
            Long parentId = getRandomId(topLevelCommentIds);
            CreateCommentRequest request = new CreateCommentRequest(
                    faker.lorem().paragraph(),
                    getRandomId(issueIds), // A reply still belongs to an issue
                    getRandomId(userIds),
                    parentId // This is a reply, so parentId is set
            );
            // In a real app: commentService.createComment(request);
            commentService.createComment(CommentMapper.toComment(request));
        }
    }

    /**
     * Utility method to pick a random ID from a list of existing IDs.
     */
    private Long getRandomId(List<Long> idList) {
        if (idList == null || idList.isEmpty()) {
            return null;
        }
        return idList.get(random.nextInt(idList.size()));
    }
}
