package uk.co.rjsoftware.adventure.view.editor.components

import groovy.transform.TypeChecked
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.input.MouseDragEvent
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

    private CustomComponent componentToControl
    private PathComponent path = new PathComponent()

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

        connectorNodeN.setOnDragDetected(this.&nodeOnDragDetected)
        connectorNodeN.setOnMouseDragReleased(this.&nodeOnMouseDragReleased)

        connectorNodeE.setOnDragDetected(this.&nodeOnDragDetected)
        connectorNodeE.setOnMouseDragReleased(this.&nodeOnMouseDragReleased)

        connectorNodeS.setOnDragDetected(this.&nodeOnDragDetected)
        connectorNodeS.setOnMouseDragReleased(this.&nodeOnMouseDragReleased)

        connectorNodeW.setOnDragDetected(this.&nodeOnDragDetected)
        connectorNodeW.setOnMouseDragReleased(this.&nodeOnMouseDragReleased)

        connectorNodeNE.setOnDragDetected(this.&nodeOnDragDetected)
        connectorNodeNE.setOnMouseDragReleased(this.&nodeOnMouseDragReleased)

        connectorNodeSE.setOnDragDetected(this.&nodeOnDragDetected)
        connectorNodeSE.setOnMouseDragReleased(this.&nodeOnMouseDragReleased)

        connectorNodeSW.setOnDragDetected(this.&nodeOnDragDetected)
        connectorNodeSW.setOnMouseDragReleased(this.&nodeOnMouseDragReleased)

        connectorNodeNW.setOnDragDetected(this.&nodeOnDragDetected)
        connectorNodeNW.setOnMouseDragReleased(this.&nodeOnMouseDragReleased)

        this.getChildren().add(connectorNodeN)
        this.getChildren().add(connectorNodeE)
        this.getChildren().add(connectorNodeS)
        this.getChildren().add(connectorNodeW)
        this.getChildren().add(connectorNodeNE)
        this.getChildren().add(connectorNodeSE)
        this.getChildren().add(connectorNodeSW)
        this.getChildren().add(connectorNodeNW)
    }

    private void nodeOnDragDetected(MouseEvent event) {
        println("drag detected.")
        println("target: " + event.getTarget())
        println("source: " + event.getSource())

        this.currentlyDraggingNode = true

        Circle sourceCircle = (Circle)event.getTarget()
        sourceCircle.startFullDrag()

        this.path.setLayoutX(this.componentToControl.getLayoutX() + sourceCircle.getLayoutX())
        this.path.setLayoutY(this.componentToControl.getLayoutY() + sourceCircle.getLayoutY())
        this.path.setEndpoint(0, 0)
        this.pane.getChildren().add(this.path)
    }

    private void nodeOnMouseDragReleased(MouseDragEvent event) {
        println("drag release detected.")
        println("target: " + event.getTarget())
        println("source: " + event.getSource())

        event.consume()
        this.currentlyDraggingNode = false

        if (event.getGestureSource() == event.getSource()) {
            // TODO: Start and end nodes are the same - remove the line
        }

        Circle targetCircle = (Circle)event.getTarget()
        final double nodeX = targetCircle.getLayoutX() + targetCircle.getParent().getLayoutX()
        final double nodeY = targetCircle.getLayoutY() + targetCircle.getParent().getLayoutY()

        this.path.setEndpoint(nodeX - this.path.getLayoutX(), nodeY - this.path.getLayoutY())
    }

    void onMouseDragReleasedMapPane(MouseDragEvent event) {
        this.pane.getChildren().remove(this.path)
    }

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

    private void setComponentToControl(CustomComponent node) {
        // remove the existing connectors component, if any
        this.pane.getChildren().remove(this)

        componentToControl = node

        final double currentX = node.getLayoutX()
        final double currentY = node.getLayoutY()

        setLayoutX(currentX)
        setLayoutY(currentY)

        setMinSize(node.getLayoutBounds().getWidth(), node.getLayoutBounds().getHeight())
        setMaxSize(node.getLayoutBounds().getWidth(), node.getLayoutBounds().getHeight())
        setPrefSize(node.getLayoutBounds().getWidth(), node.getLayoutBounds().getHeight())
        this.pane.getChildren().add(this)
    }

    void removeComponent() {
        this.pane.getChildren().remove(this)
        this.componentToControl = null
    }

    void onMouseMovedMapPane(MouseEvent event) {
        final Node node = event.getPickResult().getIntersectedNode()
        if (node instanceof CustomComponent && node != componentToControl) {
            setComponentToControl((CustomComponent)node)
        }
    }

    void onMouseDraggedMapPane(MouseEvent event) {
        final Node node = event.getPickResult().getIntersectedNode()

        if (node instanceof CustomComponent && node != componentToControl) {
            setComponentToControl((CustomComponent)node)
        }

        if (this.currentlyDraggingNode) {
            this.path.setEndpoint(event.getX() - this.path.getLayoutX(), event.getY() - this.path.getLayoutY())
        }
    }

}
