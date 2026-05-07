package com.whitetower.meridia.enumeration;


public enum ServiceResponseType {
    OK ("There is no business validation error."),
    ENTITY_ALREADY_EXISTS ("The entity already exists."),
    UNKNOWN_DB_ERROR ("Some unknown error occurred while accessing the Database"),
    ENTITY_IS_INVALID("The entity is invalid."),
    ENTITY_NOT_FOUND("Entity not found.");

    public final String message;
    ServiceResponseType(String passedMessage) {
        message = passedMessage;
    }
}
