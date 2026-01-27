/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : EntityValidator
 * Author  : Vikas Kumar
 * Created : 12-11-2025
 * ---------------------------------------------------------------
 */
package com.mudda.backend.utils;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.repository.CrudRepository;

public class EntityValidator {

    public static <T, ID> void validateExists(CrudRepository<T, ID> repo, ID id, String entityName) {
        if (!repo.existsById(id))
            throw new EntityNotFoundException("%s not found with id: %s".formatted(entityName, id));
    }
}
