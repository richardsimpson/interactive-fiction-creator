package uk.co.rjsoftware.adventure.view.editor.components

import groovy.transform.TypeChecked
import javafx.scene.Parent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Circle

@TypeChecked
class ConnectorsComponent extends AnchorPane {

    private static final int DRAG_NODE_SIZE = 4
    private static final int DRAG_NODE_RADIUS = DRAG_NODE_SIZE

    private final Circle connectorNodeN = new Circle(DRAG_NODE_SIZE, Color.BLACK)
    private final Circle connectorNodeS = new Circle(DRAG_NODE_SIZE, Color.BLACK)
    private final Circle connectorNodeE = new Circle(DRAG_NODE_SIZE, Color.BLACK)
    private final Circle connectorNodeW = new Circle(DRAG_NODE_SIZE, Color.BLACK)
    private final Circle connectorNodeNE = new Circle(DRAG_NODE_SIZE, Color.BLACK)
    private final Circle connectorNodeSE = new Circle(DRAG_NODE_SIZE, Color.BLACK)
    private final Circle connectorNodeSW = new Circle(DRAG_NODE_SIZE, Color.BLACK)
    private final Circle connectorNodeNW = new Circle(DRAG_NODE_SIZE, Color.BLACK)

    private final Parent pane

    private Region componentToResize

    private boolean currentlyDraggingNode

    ConnectorsComponent(Parent pane) {
        this.pane = pane

        this.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, null)))

        AnchorPane.setTopAnchor(connectorNodeN, -DRAG_NODE_RADIUS)
        AnchorPane.setRightAnchor(connectorNodeE, -DRAG_NODE_RADIUS)
        AnchorPane.setBottomAnchor(connectorNodeS, -DRAG_NODE_RADIUS)
        AnchorPane.setLeftAnchor(connectorNodeW, -DRAG_NODE_RADIUS)

        AnchorPane.setTopAnchor(connectorNodeNE, -DRAG_NODE_RADIUS)
        AnchorPane.setRightAnchor(connectorNodeNE, -DRAG_NODE_RADIUS)

        AnchorPane.setBottomAnchor(connectorNodeSE, -DRAG_NODE_RADIUS)
        AnchorPane.setRightAnchor(connectorNodeSE, -DRAG_NODE_RADIUS)

        AnchorPane.setBottomAnchor(connectorNodeSW, -DRAG_NODE_RADIUS)
        AnchorPane.setLeftAnchor(connectorNodeSW, -DRAG_NODE_RADIUS)

        AnchorPane.setTopAnchor(connectorNodeNW, -DRAG_NODE_RADIUS)
        AnchorPane.setLeftAnchor(connectorNodeNW, -DRAG_NODE_RADIUS)

        connectorNodeN.setOnMousePressed(this.&nodeOnMousePressed)
        connectorNodeN.setOnMouseReleased(this.&nodeOnMouseReleased)
        connectorNodeN.setOnMouseDragged(this.&nodeNOnMouseDragged)

        connectorNodeE.setOnMousePressed(this.&nodeOnMousePressed)
        connectorNodeE.setOnMouseReleased(this.&nodeOnMouseReleased)
        connectorNodeE.setOnMouseDragged(this.&nodeEOnMouseDragged)

        connectorNodeS.setOnMousePressed(this.&nodeOnMousePressed)
        connectorNodeS.setOnMouseReleased(this.&nodeOnMouseReleased)
        connectorNodeS.setOnMouseDragged(this.&nodeSOnMouseDragged)

        connectorNodeW.setOnMousePressed(this.&nodeOnMousePressed)
        connectorNodeW.setOnMouseReleased(this.&nodeOnMouseReleased)
        connectorNodeW.setOnMouseDragged(this.&nodeWOnMouseDragged)

        connectorNodeNE.setOnMousePressed(this.&nodeOnMousePressed)
        connectorNodeNE.setOnMouseReleased(this.&nodeOnMouseReleased)
        connectorNodeNE.setOnMouseDragged(this.&nodeNEOnMouseDragged)

        connectorNodeSE.setOnMousePressed(this.&nodeOnMousePressed)
        connectorNodeSE.setOnMouseReleased(this.&nodeOnMouseReleased)
        connectorNodeSE.setOnMouseDragged(this.&nodeSEOnMouseDragged)

        connectorNodeSW.setOnMousePressed(this.&nodeOnMousePressed)
        connectorNodeSW.setOnMouseReleased(this.&nodeOnMouseReleased)
        connectorNodeSW.setOnMouseDragged(this.&nodeSWOnMouseDragged)

        connectorNodeNW.setOnMousePressed(this.&nodeOnMousePressed)
        connectorNodeNW.setOnMouseReleased(this.&nodeOnMouseReleased)
        connectorNodeNW.setOnMouseDragged(this.&nodeNWOnMouseDragged)

        this.getChildren().add(connectorNodeN)
        this.getChildren().add(connectorNodeE)
        this.getChildren().add(connectorNodeS)
        this.getChildren().add(connectorNodeW)
        this.getChildren().add(connectorNodeNE)
        this.getChildren().add(connectorNodeSE)
        this.getChildren().add(connectorNodeSW)
        this.getChildren().add(connectorNodeNW)
    }

    void setComponentToControl(Region node, double offsetX, double offsetY) {
        componentToResize = node

        final double currentX = node.getLayoutX()
        final double currentY = node.getLayoutY()

        setLayoutX(currentX)
        setLayoutY(currentY)

        setMinSize(node.getLayoutBounds().getWidth(), node.getLayoutBounds().getHeight())
        setMaxSize(node.getLayoutBounds().getWidth(), node.getLayoutBounds().getHeight())
        setPrefSize(node.getLayoutBounds().getWidth(), node.getLayoutBounds().getHeight())
    }

    private void nodeOnMousePressed(MouseEvent event) {
        println("node pressed")
        this.currentlyDraggingNode = true
    }

    private void nodeOnMouseReleased(MouseEvent event) {
        println("node released")
    }

    //
    // Dragging of corner nodes
    //

    private void nodeNOnMouseDragged(MouseEvent event) {
        println("nodeN dragged")
//        final double currentWidth = componentToResize.getWidth()
//        final double currentHeight = componentToResize.getHeight()
//        final double currentX = componentToResize.getLayoutX()
//        final double currentY = componentToResize.getLayoutY()
//
//        final double newDesiredHeight = currentHeight - event.getY() + DRAG_NODE_RADIUS
//        final double newY = currentY - (newDesiredHeight-currentHeight)
//
//        repositionComponent(false, currentX, true, newY, currentWidth, newDesiredHeight)
    }

    private void nodeEOnMouseDragged(MouseEvent event) {
        println("nodeE dragged")
    }

    private void nodeSOnMouseDragged(MouseEvent event) {
        println("nodeS dragged")
    }

    private void nodeWOnMouseDragged(MouseEvent event) {
        println("nodeW dragged")
    }

    private void nodeNEOnMouseDragged(MouseEvent event) {
        println("nodeNE dragged")
    }

    private void nodeSEOnMouseDragged(MouseEvent event) {
        println("nodeSE dragged")
    }

    private void nodeSWOnMouseDragged(MouseEvent event) {
        println("nodeSW dragged")
    }

    private void nodeNWOnMouseDragged(MouseEvent event) {
        println("nodeNW dragged")
    }

    //
    // END OF Dragging of corner nodes
    //

    @Override
    void resize(double width, double height) {
        super.resize(width, height)

        final Double widthOffset = this.getWidth() / 2 - DRAG_NODE_RADIUS
        final Double heightOffset = this.getHeight() / 2 - DRAG_NODE_RADIUS

        AnchorPane.setRightAnchor(connectorNodeN, widthOffset)
        AnchorPane.setLeftAnchor(connectorNodeN, widthOffset)

        AnchorPane.setTopAnchor(connectorNodeE, heightOffset)
        AnchorPane.setBottomAnchor(connectorNodeE, heightOffset)

        AnchorPane.setRightAnchor(connectorNodeS, widthOffset)
        AnchorPane.setLeftAnchor(connectorNodeS, widthOffset)

        AnchorPane.setTopAnchor(connectorNodeW, heightOffset)
        AnchorPane.setBottomAnchor(connectorNodeW, heightOffset)
    }

    void onMousePressedComponent(MouseEvent event) {
        final Region region = event.getSource() as Region

        println("component pressed")

        // remove the existing connectors component, if any
        this.pane.getChildren().remove(this)

        // add the connectors component, over the selected item
        setComponentToControl(region, event.getX(), event.getY())
        this.pane.getChildren().add(this)
    }

    void onMouseReleasedComponent(MouseEvent event) {
        println("component released")
    }

    void removeComponent() {
        println("pane clicked")
        this.pane.getChildren().remove(this)
    }

}
