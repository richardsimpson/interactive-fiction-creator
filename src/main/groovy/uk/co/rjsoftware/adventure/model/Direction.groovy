package uk.co.rjsoftware.adventure.model

import groovy.transform.TypeChecked

@TypeChecked
enum Direction {
    NORTH("North", SOUTH),
    SOUTH("South", NORTH),
    EAST("East", WEST),
    WEST("West", EAST),
    NORTHEAST("Northeast", SOUTHWEST),
    SOUTHEAST("Southeast", NORTHWEST),
    SOUTHWEST("Southwest", NORTHEAST),
    NORTHWEST("Northwest", SOUTHEAST),
    UP("Up", DOWN),
    DOWN("Down", UP);

    private String description
    private Direction oppositeDirection

    Direction(String description, Direction oppositeDirection) {
        this.description = description
        this.oppositeDirection = oppositeDirection
    }

    String getDescription() {
        return description;
    }

    Direction getOppositeDirection() {
        switch (this) {
            case NORTH:     SOUTH; break
            case SOUTH:     NORTH; break
            case EAST:      WEST; break
            case WEST:      EAST; break
            case NORTHEAST: SOUTHWEST; break
            case SOUTHEAST: NORTHWEST; break
            case SOUTHWEST: NORTHEAST; break
            case NORTHWEST: SOUTHEAST; break
            case UP:        DOWN; break
            case DOWN:      UP; break
            default:
                throw new RuntimeException("Unexpected Direction: " + this.name())
        }
    }
}
