/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : SeedDataGenerator
 * Author  : Vikas Kumar
 * Created : 09-04-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.seed;

import com.mudda.backend.comment.Comment;
import com.mudda.backend.issue.Issue;
import com.mudda.backend.issue.IssueCategory;
import com.mudda.backend.issue.PointFactory;
import com.mudda.backend.media.Media;
import com.mudda.backend.media.MediaOwner;
import com.mudda.backend.media.UploadStatus;
import com.mudda.backend.user.MuddaUser;
import com.mudda.backend.user.MuddaUserRole;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.util.*;

public class SeedDataGenerator {

    public static final float MEDIA_OVERFLOW = 1.5f;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Random random = new Random(42);

    // region Samples

    private final List<String> USER_IMAGE_SAMPLE = List.of(
            "https://plus.unsplash.com/premium_photo-1689568126014-06fea9d5d341?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "https://media.istockphoto.com/id/499728904/photo/unknown-person-silhouette.jpg?s=1024x1024&w=is&k=20&c=cvcV9WvqFt691KAQCXPzFexJ5VVSIYx4lBlhPXwydaE=",
            "https://plus.unsplash.com/premium_photo-1689568126014-06fea9d5d341?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "https://media.istockphoto.com/id/499728904/photo/unknown-person-silhouette.jpg?s=1024x1024&w=is&k=20&c=cvcV9WvqFt691KAQCXPzFexJ5VVSIYx4lBlhPXwydaE="
    );

    private final List<String> ISSUE_IMAGE_SAMPLE = List.of(
            "https://images.unsplash.com/photo-1560782205-4dd83ceb0270?q=80&w=387&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "https://images.unsplash.com/photo-1631174721289-6656e7f3353c?q=80&w=870&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "https://images.unsplash.com/photo-1495556650867-99590cea3657?q=80&w=870&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "https://plus.unsplash.com/premium_photo-1663036928694-d218b69cc123?q=80&w=892&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "https://plus.unsplash.com/premium_photo-1661963600977-5fc312f6a60e?q=80&w=1300&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "https://images.unsplash.com/photo-1600336153113-d66c79de3e91?q=80&w=435&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "https://images.unsplash.com/photo-1601026909629-bad5e1122bc6?q=80&w=774&auto=format&fit=crop&ixlib=rb-4.1. 0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "https://images.unsplash.com/photo-1587763483696-6d41d2de6084?q=80&w=387&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "https://plus.unsplash.com/premium_photo-1693000474956-1a5d8a85befc?q=80&w=387&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "https://images.unsplash.com/photo-1607432750402-48f85c94f63a?q=80&w=870&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "https://images.unsplash.com/photo-1624662153875-0f303ddd5456?q=80&w=1258&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
    );

    // endregion

    public List<MuddaUser> generateUsers(List<Long> userIds) {
        int count = userIds.size();
        List<MuddaUser> users = new ArrayList<>(count);

//        feedback.add("Generating %d users...".formatted(count));

        LocalDate dob = LocalDate.of(1990, 1, 1);
        String password = "password123";
        String hashedPassword = passwordEncoder.encode(password);
        MuddaUserRole role = MuddaUserRole.CITIZEN;

        for (long id : userIds) {
            String username = "User_%s".formatted(id);
            String name = "Tester_%s".formatted(id);
            String phoneNumber = "555 %07d".formatted(id);
            String email = "%s@fake.com".formatted(username);

            MuddaUser user = new MuddaUser(
                    username,
                    name,
                    phoneNumber,
                    dob,
                    email,
                    hashedPassword,
                    null,
                    role
            );
            user.setUserId(id);
            user.setEnabled(true);

            users.add(user);
        }

        return users;
    }

    public List<Issue> generateIssues(List<Long> issueIds, List<Long> userIds) {
        int count = issueIds.size();
        List<Issue> issues = new ArrayList<>(count);

//        feedback.add("Generating %d issues".formatted(count));

        List<String> cities = List.of("Berlin", "Liverpool", "Mumbai", "Gurgaon", "Tokyo", "New York", "Beijing");
        List<String> states = List.of("Haryana", "Bavaria", "Washington DC", "Jalisco", "Tokyo", "Guizhou", "Hubei");

        for (long id : issueIds) {
            String title = "Issue #%d".formatted(count);
            String description = "Issue description for %d. Some useless text here.".formatted(count);

            long userId = randomChoice(userIds);
            IssueCategory category = randomEnum(IssueCategory.class);
            String pinCode = "122%03d".formatted(userId);
            String city = randomChoice(cities);
            String state = randomChoice(states);
            double x = random.nextDouble(-90, 90 + 1);
            double y = random.nextDouble(-180, 180 + 1);
            Point coordinate = PointFactory.createPoint(new Coordinate(x, y));

            Issue issue = new Issue(
                    title,
                    description,
                    userId,
                    category,
                    pinCode,
                    city,
                    state,
                    coordinate
            );
            issue.setId(id);

            issues.add(issue);
        }

        return issues;
    }

    public List<Comment> generateComments(List<Long> commentIds, List<Long> issueIds, List<Long> userIds) {
        int count = commentIds.size();
        List<Comment> comments = new ArrayList<>(count);

//        feedback.add("Generating %d comments".formatted(count));

        for (long id : commentIds) {

            String text = "Comment text";
            Long issueId = randomChoice(issueIds);
            Long userId = randomChoice(userIds);

            Comment comment = new Comment(
                    text,
                    issueId,
                    userId
            );
            comment.setId(id);

            comments.add(comment);
        }

        return comments;
    }

    public List<Media> generateMedias(List<Long> mediaIds, List<Long> mediaIssueIds, List<Long> mediaUserIds) {
        if (mediaIds == null || mediaIds.isEmpty()) return null;

        int splitIndex = (int) (mediaIssueIds.size() * MEDIA_OVERFLOW);

        List<Media> issueMediaList = generateMediaList(
                mediaIds.subList(0, splitIndex), mediaIssueIds,
                ISSUE_IMAGE_SAMPLE, MediaOwner.ISSUE
        );
        List<Media> userMediaList = generateMediaList(
                mediaIds.subList(splitIndex, mediaIds.size()), mediaUserIds,
                USER_IMAGE_SAMPLE, MediaOwner.USER
        );

        issueMediaList.addAll(userMediaList);
        return issueMediaList;
    }

    private List<Media> generateMediaList(
            List<Long> mediaIds, List<Long> ownerIds, List<String> imageSamples, MediaOwner mediaOwner
    ) {
        List<Media> mediaList = new ArrayList<>(mediaIds.size());

        UploadStatus status = UploadStatus.SUCCESS;
        int position = 0;
        int mediaIndex = 0;

        for (long ownerId : ownerIds) {
            String publicId = generatePublicId();
            String mediaKey = randomChoice(imageSamples);

            Media media = new Media(
                    publicId,
                    mediaKey,
                    status,
                    position
            );
            media.setId(mediaIds.get(mediaIndex++));
            media.setOwnerId(ownerId);
            media.setOwnerType(mediaOwner);

            mediaList.add(media);
        }

        for (; mediaIndex < mediaIds.size(); mediaIndex++) {
            long id = mediaIds.get(mediaIndex);
            long ownerId = randomChoice(ownerIds);
            String publicId = generatePublicId();
            String mediaKey = randomChoice(imageSamples);

            Media media = new Media(
                    publicId,
                    mediaKey,
                    status,
                    position
            );
            media.setId(id);
            media.setOwnerId(ownerId);
            media.setOwnerType(mediaOwner);

            mediaList.add(media);
        }

        return mediaList;
    }

    private <T extends Enum<T>> T randomEnum(Class<T> enumClass) {
        T[] values = enumClass.getEnumConstants();
        return values[random.nextInt(values.length)];
    }

    private <T> T randomChoice(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

    private String generatePublicId() {
        UUID uuid = UUID.randomUUID();
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buffer.array());
    }
}
