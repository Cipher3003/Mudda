package com.mudda.backend.amazon.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
public class AmazonImage {

    @Id
    private String amazonUserIdString;

    @NotNull
    private String imageUrl;
}
