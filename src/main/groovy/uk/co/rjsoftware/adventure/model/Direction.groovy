package uk.co.rjsoftware.adventure.model

enum Direction {
    NORTH("North"),
    SOUTH("South"),
    EAST("East"),
    WEST("West"),
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
