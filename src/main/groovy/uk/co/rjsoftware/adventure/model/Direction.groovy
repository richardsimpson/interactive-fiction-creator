package uk.co.rjsoftware.adventure.model

import groovy.transform.TypeChecked

@TypeChecked
enum Direction {
    NORTH("North"),
    SOUTH("South"),
    EAST("East"),
    WEST("West"),
    NORTHEAST("Northeast"),
    SOUTHEAST("Southeast"),
    SOUTHWEST("Southwest"),
    NORTHWEST("Northwest"),
    UP("Up"),
    DOWN("Down");

    private String description;

    Direction(String description) {
        this.description = description;
    }

    String getDescription() {
        return description;
    }
}
