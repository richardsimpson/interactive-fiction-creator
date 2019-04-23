package uk.co.rjsoftware.adventure.view.editor.components

import javafx.scene.shape.LineTo
import javafx.scene.shape.MoveTo
import javafx.scene.shape.Path
import uk.co.rjsoftware.adventure.model.Direction

class PathComponent extends Path {

    private final LineTo line
    private final RoomComponent sourceRoom
    private final Direction sourceDirection
    private RoomComponent targetRoom
    private Direction targetDirection

    PathComponent(RoomComponent sourceRoom, Direction sourceDirection) {
        super()
        this.sourceRoom = sourceRoom
        this.sourceDirection = sourceDirection
        final MoveTo moveTo = new MoveTo(0, 0)
        this.line = new LineTo(0, 0)

        getElements().addAll(moveTo, line)

        updatePathToSourceRoom()
        setEndpoint(0, 0)
    }

    void setEndpoint(double x, double y) {
        line.setX(x)
        line.setY(y)
    }

    void setTarget(RoomComponent targetRoom, Direction targetDirection) {
        this.targetRoom = targetRoom
        this.targetDirection = targetDirection

        updatePathToTargetRoom()
    }

    void updatePathTo(RoomComponent roomComponent) {
        if (roomComponent == this.sourceRoom) {
            updatePathToSourceRoom()
            updatePathToTargetRoom()
        }
        else if (roomComponent == this.targetRoom) {
            updatePathToTargetRoom()
        }
        else {
            throw new RuntimeException("Cannot determine with end of the path to update")
        }
    }

    private updatePathToSourceRoom() {
        setLayoutX(getXFor(this.sourceRoom, this.sourceDirection))
        setLayoutY(getYFor(this.sourceRoom, this.sourceDirection))
    }

    private updatePathToTargetRoom() {
        final double nodeX = getXFor(this.targetRoom, this.targetDirection)
        final double nodeY = getYFor(this.targetRoom, this.targetDirection)

        setEndpoint(nodeX - getLayoutX(), nodeY - getLayoutY())
    }

    private double getXFor(RoomComponent roomComponent, Direction direction) {
        switch (direction) {
            case Direction.NORTH:
            case Direction.SOUTH:
                return roomComponent.getLayoutX() + roomComponent.getWidth()/2
            case Direction.NORTHWEST:
            case Direction.SOUTHWEST:
            case Direction.WEST:
                return roomComponent.getLayoutX()
            case Direction.NORTHEAST:
            case Direction.SOUTHEAST:
            case Direction.EAST:
                return roomComponent.getLayoutX() + roomComponent.getWidth()
            default:
                throw new RuntimeException("Unexpected Direction: " + direction.name())
        }
    }

    private double getYFor(RoomComponent roomComponent, Direction direction) {
        switch (direction) {
            case Direction.NORTHWEST:
            case Direction.NORTHEAST:
            case Direction.NORTH:
                return roomComponent.getLayoutY()
            case Direction.EAST:
            case Direction.WEST:
                return roomComponent.getLayoutY() + roomComponent.getHeight()/2
            case Direction.SOUTH:
            case Direction.SOUTHWEST:
            case Direction.SOUTHEAST:
                return roomComponent.getLayoutY() + roomComponent.getHeight()
            default:
                throw new RuntimeException("Unexpected Direction: " + direction.name())

        }
    }
}
