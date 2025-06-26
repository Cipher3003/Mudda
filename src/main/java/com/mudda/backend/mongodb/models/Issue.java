//package com.mudda.backend.models;
//
//
//import java.sql.Timestamp;
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.Table;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//@NoArgsConstructor
//@AllArgsConstructor
//@Getter
//@Setter
//@Entity
//@Table(name = "issues")
//public class Issue {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long issueId;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
//
//    @ManyToOne
//    @JoinColumn(name = "category_id", nullable = false)
//    private Category category;
//
//    @ManyToOne
//    @JoinColumn(name = "location_id", nullable = false)
//    private Location location;
//
//    @Column(nullable = false)
//    private String title;
//
//    @Column(columnDefinition = "TEXT")
//    private String description;
//
//    private String status = "OPEN";
//
//    private String imageUrl;
//
//    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
//    private Timestamp createdAt;
//
//    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
//    private Timestamp updatedAt;
//}
