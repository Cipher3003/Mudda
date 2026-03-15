/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : ValidEnum
 * Author  : Vikas Kumar
 * Created : 15-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = {ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValidator.class)
public @interface ValidEnum {

    Class<? extends Enum<?>> enumClass();

    String message() default "must be a valid enum value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
