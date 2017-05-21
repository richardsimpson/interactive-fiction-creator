package uk.co.rjsoftware.adventure.model;

/**
 * Created by richardsimpson on 14/05/2017.
 */
public enum Direction {
    NORTH("North"),
    EAST("East"),
    SOUTH("South"),
    WEST("West");

    private String description;

    Direction(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
