package uk.co.rjsoftware.adventure.view.editor.components

import groovy.transform.TypeChecked
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color

@TypeChecked
class MoveComponent extends AnchorPane {

    private Region componentToMove
    private double currentX
    private double currentY
    private double offsetX
    private double offsetY

    MoveComponent() {
        this.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.DASHED, null, null)))

    }

    void setComponentToMove(ResizeComponent resizeComponent, Region node, double offsetX, double offsetY) {
        this.componentToMove = node
        this.offsetX = offsetX
        this.offsetY = offsetY

        currentX = node.getLayoutX()
        currentY = node.getLayoutY()

        setLayoutX(currentX)
        setLayoutY(currentY)
        setMinSize(node.getLayoutBounds().getWidth(), node.getLayoutBounds().getHeight())
        setMaxSize(node.getLayoutBounds().getWidth(), node.getLayoutBounds().getHeight())
        setPrefSize(node.getLayoutBounds().getWidth(), node.getLayoutBounds().getHeight())

        componentToMove.setOnMouseDragged(this.&onMouseDraggedComponent)
        resizeComponent.setOnMouseDragged(this.&onMouseDraggedComponent)
    }

    private void onMouseDraggedComponent(MouseEvent event) {
        println("component dragged")

        final double newX = currentX + event.getX() - this.offsetX
        final double newY = currentY + event.getY() - this.offsetY

        setLayoutX(newX)
        setLayoutY(newY)
    }

    void mouseReleased() {
        componentToMove.setLayoutX(getLayoutX())
        componentToMove.setLayoutY(getLayoutY())
    }

}
