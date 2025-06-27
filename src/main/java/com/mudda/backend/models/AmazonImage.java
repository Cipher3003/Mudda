package com.mudda.backend.models;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AmazonImage {

    @Id
    private String amazonUserIdString;

    @NotNull
    private String imageUrl;
}
