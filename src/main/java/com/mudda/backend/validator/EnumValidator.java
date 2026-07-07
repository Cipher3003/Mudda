/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : EnumValidator
 * Author  : Vikas Kumar
 * Created : 15-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {

    private Set<String> allowedValues;

    @Override
    public void initialize(ValidEnum targetEnum) {
        allowedValues = Arrays.stream(targetEnum.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
//        NOTE: use @NotNull to check for null values
        if (value == null) return true;
        return allowedValues.contains(value);
    }
}
