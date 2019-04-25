package uk.co.rjsoftware.adventure.model

import uk.co.rjsoftware.adventure.utils.StringUtils

class Exit implements Entrance {
    private Room origin
    private Direction direction
    private Room destination
    private Direction entranceDirection
    private boolean scenery
    private String prefix
    private String suffix

    Exit(Direction direction, Room destination, Direction entranceDirection) {
        this.direction = direction
        this.destination = destination
        this.entranceDirection = entranceDirection
    }

    Exit(Direction direction, Room destination) {
        this(direction, destination, direction.oppositeDirection)
    }

    Exit(Direction direction) {
        this(direction, null)
    }

    Exit copy() {
        final Exit exitCopy = new Exit(this.direction, null, this.entranceDirection)
        exitCopy.scenery = this.scenery
        exitCopy.prefix = this.prefix
        exitCopy.suffix = this.suffix
        // NOTE: Don't assign the destination here - need to do that in Adventure.copy, after all the room copies have been created
        // NOTE: Don't assign the origin here - need to do that in Room.copy, after all the exits have been created.
        exitCopy
    }

    Room getOrigin() {
        this.origin
    }

    void setOrigin(Room origin) {
        this.origin = origin
    }

    Direction getDirection() {
        direction
    }

    void setDirection(Direction direction) {
        this.direction = direction
    }

    Direction getOriginDirection() {
        this.direction
    }

    void setOriginDirection(Direction direction) {
        this.direction = direction
    }

    Room getDestination() {
        destination
    }

    void setDestination(Room destination) {
        this.destination = destination
    }

    Direction getEntranceDirection() {
        entranceDirection
    }

    void setEntranceDirection(Direction entranceDirection) {
        this.entranceDirection = entranceDirection
    }

    Boolean isScenery() {
        this.scenery
    }

    void setScenery(Boolean scenery) {
        this.scenery = scenery
    }

    String getDescription() {
        String result = ""

        if (StringUtils.hasValue(this.prefix)) {
            result =  this.prefix + " "
        }

        result = result + this.direction.getDescription()

        if (StringUtils.hasValue(this.suffix)) {
            result = result + " " + this.suffix
        }

        result
    }

    String getPrefix() {
        return prefix
    }

    void setPrefix(String prefix) {
        this.prefix = prefix
    }

    String getSuffix() {
        return suffix
    }

    void setSuffix(String suffix) {
        this.suffix = suffix
    }
}
