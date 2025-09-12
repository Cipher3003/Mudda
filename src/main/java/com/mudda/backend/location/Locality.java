package com.mudda.backend.location;


import jakarta.persistence.*;

import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "Locality")
@Table(name = "localities")
public class Locality {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long localityId;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public Locality(String city, String state) {
        this.city = city;
        this.state = state;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

}