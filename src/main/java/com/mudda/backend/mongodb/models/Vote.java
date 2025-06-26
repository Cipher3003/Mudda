//package com.mudda.backend.models;
//
//import java.sql.Timestamp;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.EnumType;
//import jakarta.persistence.Enumerated;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.Table;
//import jakarta.persistence.UniqueConstraint;
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
//@Table(name = "votes", uniqueConstraints = {
//    @UniqueConstraint(columnNames = {"issue_id", "user_id"})
//})
//public class Vote {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long voteId;
//
//    @ManyToOne
//    @JoinColumn(name = "issue_id", nullable = false)
//    private Issue issue;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private VoteType voteType;
//
//    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
//    private Timestamp createdAt;
//}
