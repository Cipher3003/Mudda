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
                "Locality",
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
                    "comment_likes_seq",
                    "comments_comment_id_seq",
                    "votes_vote_id_seq",
                    "issues_issue_id_seq",
                    "users_user_id_seq",
                    "locations_location_id_seq",
                    "localities_locality_id_seq",
                    "categories_category_id_seq",
                    "roles_role_id_seq");

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
            // Appending a random number to ensure the role name is unique
            String uniqueRoleName = faker.job().title() + " " + random.nextInt(100000);
            CreateRoleRequest request = new CreateRoleRequest(uniqueRoleName);
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
                    Double.parseDouble(faker.address().longitude().replace(',', '.')));
            CreateLocationRequest request = new CreateLocationRequest(
                    faker.address().streetAddress(),
                    faker.address().zipCode(),
                    faker.address().city(),
                    faker.address().state(),
                    coordinate);
            // In a real app: locationService.createLocation(request);
            LocationResponse location = locationService.createLocation(request);
            locationIds.add(location.locationId());
        }
    }

    private void generateCategories(int count, List<Long> categoryIds, List<String> feedback) {
        feedback.add("Generating " + count + " categories...");
        for (int i = 0; i < count; i++) {
            // Appending a random number to ensure the category name is unique
            String uniqueCategoryName = faker.commerce().department() + " " + random.nextInt(100000);
            CreateCategoryRequest request = new CreateCategoryRequest(uniqueCategoryName);
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
            // Generate unique username, email, and phone number to avoid constraint
            // violations
            String uniqueSuffix = String.valueOf(random.nextInt(100000));
            String baseUsername = faker.name().username().replaceAll("[^a-zA-Z0-9]", ""); // Sanitize username
            String uniqueUsername = baseUsername + uniqueSuffix;
            String uniqueEmail = uniqueUsername + "@example.com";
            String uniquePhoneNumber = "+919" + faker.number().digits(9); // Create a unique 12-digit number
            CreateUserRequest request = new CreateUserRequest(
                    uniqueUsername,
                    faker.name().fullName(),
                    uniqueEmail,
                    faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                    uniquePhoneNumber,
                    faker.internet().password(),
                    getRandomId(roleIds), // Get a random existing role ID
                    faker.avatar().image());
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
                    media);
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
            // Simulating DB save and adding new comment's ID to the list for potential
            // replies.
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
