package com.vaadin.tests.data.bean;

public enum Sex {
    MALE("Male"), FEMALE("Female"), UNKNOWN("WTF?");

    private String stringRepresentation;

    private Sex(String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    public String getStringRepresentation() {
        return stringRepresentation;
    }
}
