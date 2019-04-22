package uk.co.rjsoftware.adventure.view.editor.components

import javafx.scene.shape.LineTo
import javafx.scene.shape.MoveTo
import javafx.scene.shape.Path

class PathComponent extends Path {

    private final LineTo line
    private final RoomComponent sourceRoom
    private RoomComponent targetRoom

    PathComponent(RoomComponent sourceRoom) {
        super()
        this.sourceRoom = sourceRoom
        final MoveTo moveTo = new MoveTo(0, 0)
        this.line = new LineTo(0, 0)

        getElements().addAll(moveTo, line)
    }

    void setEndpoint(double x, double y) {
        line.setX(x)
        line.setY(y)
    }

    void setTargetRoom(RoomComponent targetRoom) {
        this.targetRoom = targetRoom
    }
}
