package com.abik.nowme.module.shared.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SingleEmojiValidator.class)
public @interface SingleEmoji {

    String message() default "must contain exactly one emoji";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
