package com.whitetower.meridia.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {

    final Integer MIN_PASSWORD_SIZE = 12;
    final Integer MAX_PASSWORD_SIZE = 100;


    @Override
    public boolean isValid(String passwordField, ConstraintValidatorContext context) {
        if (passwordField == null || passwordField.length() < MIN_PASSWORD_SIZE || passwordField.length() > MAX_PASSWORD_SIZE
            || !hasAtLeastOneDigit(passwordField) || !hasAtLeastOneUpperChar(passwordField) || !hasAtLeastOneSpecialChar(passwordField)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean hasAtLeastOneUpperChar(String passwordField) {
        return passwordField.chars().anyMatch(ch -> Character.isUpperCase(ch));
    }

    private boolean hasAtLeastOneDigit(String passwordField) {
        return passwordField.chars().anyMatch(ch -> Character.isDigit(ch));
    }

    private boolean hasAtLeastOneSpecialChar(String passwordField) {
        return passwordField.chars().anyMatch(ch -> !Character.isAlphabetic(ch) && !Character.isDigit(ch));
    }


}
