package uk.co.rjsoftware.adventure.model

class Exit {
    private Direction direction
    private Room destination
    private boolean scenery

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
        this.direction.getDescription()
    }
}
