package uk.co.rjsoftware.adventure.model

import uk.co.rjsoftware.adventure.utils.StringUtils

class Exit {
    private Direction direction
    private Room destination
    private boolean scenery
    private String prefix
    private String suffix

    Exit(Direction direction, Room destination) {
        this.destination = destination
        this.direction = direction
    }

    Exit(Direction direction) {
        this(direction, null)
    }

    Exit copy() {
        final Exit exitCopy = new Exit(this.direction)
        exitCopy.scenery = this.scenery
        exitCopy.prefix = this.prefix
        exitCopy.suffix = this.suffix
        // NOTE: Don't assign the destination here - need to do that in Room.copy, after all the rooms copies have been created
        exitCopy
    }

    Direction getDirection() {
        direction
    }

    void setDirection(Direction direction) {
        this.direction = direction
    }

    Room getDestination() {
        destination
    }

    void setDestination(Room destination) {
        this.destination = destination
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
