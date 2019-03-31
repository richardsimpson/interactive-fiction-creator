package uk.co.rjsoftware.adventure.view.editor.components

import groovy.transform.TypeChecked
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Border
import javafx.scene.layout.BorderStroke
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

@TypeChecked
class ResizeComponent extends AnchorPane {

    private static final int DRAG_NODE_SIZE = 10
    private static final int DRAG_NODE_RADIUS = DRAG_NODE_SIZE.intdiv(2).intValue()

    private final Rectangle dragNodeN = new Rectangle(DRAG_NODE_SIZE, DRAG_NODE_SIZE, Color.BLACK)
    private final Rectangle dragNodeS = new Rectangle(DRAG_NODE_SIZE, DRAG_NODE_SIZE, Color.BLACK)
    private final Rectangle dragNodeE = new Rectangle(DRAG_NODE_SIZE, DRAG_NODE_SIZE, Color.BLACK)
    private final Rectangle dragNodeW = new Rectangle(DRAG_NODE_SIZE, DRAG_NODE_SIZE, Color.BLACK)
    private final Rectangle dragNodeNE = new Rectangle(DRAG_NODE_SIZE, DRAG_NODE_SIZE, Color.BLACK)
    private final Rectangle dragNodeSE = new Rectangle(DRAG_NODE_SIZE, DRAG_NODE_SIZE, Color.BLACK)
    private final Rectangle dragNodeSW = new Rectangle(DRAG_NODE_SIZE, DRAG_NODE_SIZE, Color.BLACK)
    private final Rectangle dragNodeNW = new Rectangle(DRAG_NODE_SIZE, DRAG_NODE_SIZE, Color.BLACK)

    private final Parent pane

    private double currentX
    private double currentY
    private double offsetX
    private double offsetY

    private Region componentToResize

    private boolean currentlyDraggingNode
    private boolean currentlyDraggingComponent

    private MoveComponent moveComponent = new MoveComponent()

    ResizeComponent(Parent pane) {
        this.pane = pane
        this.pane.setOnMouseClicked(this.&onMouseClickedParentPane)

        this.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, null)))

        AnchorPane.setTopAnchor(dragNodeN, -DRAG_NODE_RADIUS)
        AnchorPane.setRightAnchor(dragNodeE, -DRAG_NODE_RADIUS)
        AnchorPane.setBottomAnchor(dragNodeS, -DRAG_NODE_RADIUS)
        AnchorPane.setLeftAnchor(dragNodeW, -DRAG_NODE_RADIUS)

        AnchorPane.setTopAnchor(dragNodeNE, -DRAG_NODE_RADIUS)
        AnchorPane.setRightAnchor(dragNodeNE, -DRAG_NODE_RADIUS)

        AnchorPane.setBottomAnchor(dragNodeSE, -DRAG_NODE_RADIUS)
        AnchorPane.setRightAnchor(dragNodeSE, -DRAG_NODE_RADIUS)

        AnchorPane.setBottomAnchor(dragNodeSW, -DRAG_NODE_RADIUS)
        AnchorPane.setLeftAnchor(dragNodeSW, -DRAG_NODE_RADIUS)

        AnchorPane.setTopAnchor(dragNodeNW, -DRAG_NODE_RADIUS)
        AnchorPane.setLeftAnchor(dragNodeNW, -DRAG_NODE_RADIUS)

        dragNodeN.setOnMousePressed(this.&nodeOnMousePressed)
        dragNodeN.setOnMouseReleased(this.&nodeOnMouseReleased)
        dragNodeN.setOnMouseDragged(this.&nodeNOnMouseDragged)

        dragNodeE.setOnMousePressed(this.&nodeOnMousePressed)
        dragNodeE.setOnMouseReleased(this.&nodeOnMouseReleased)
        dragNodeE.setOnMouseDragged(this.&nodeEOnMouseDragged)

        dragNodeS.setOnMousePressed(this.&nodeOnMousePressed)
        dragNodeS.setOnMouseReleased(this.&nodeOnMouseReleased)
        dragNodeS.setOnMouseDragged(this.&nodeSOnMouseDragged)

        dragNodeW.setOnMousePressed(this.&nodeOnMousePressed)
        dragNodeW.setOnMouseReleased(this.&nodeOnMouseReleased)
        dragNodeW.setOnMouseDragged(this.&nodeWOnMouseDragged)

        dragNodeNE.setOnMousePressed(this.&nodeOnMousePressed)
        dragNodeNE.setOnMouseReleased(this.&nodeOnMouseReleased)
        dragNodeNE.setOnMouseDragged(this.&nodeNEOnMouseDragged)

        dragNodeSE.setOnMousePressed(this.&nodeOnMousePressed)
        dragNodeSE.setOnMouseReleased(this.&nodeOnMouseReleased)
        dragNodeSE.setOnMouseDragged(this.&nodeSEOnMouseDragged)

        dragNodeSW.setOnMousePressed(this.&nodeOnMousePressed)
        dragNodeSW.setOnMouseReleased(this.&nodeOnMouseReleased)
        dragNodeSW.setOnMouseDragged(this.&nodeSWOnMouseDragged)

        dragNodeNW.setOnMousePressed(this.&nodeOnMousePressed)
        dragNodeNW.setOnMouseReleased(this.&nodeOnMouseReleased)
        dragNodeNW.setOnMouseDragged(this.&nodeNWOnMouseDragged)

        this.getChildren().add(dragNodeN)
        this.getChildren().add(dragNodeE)
        this.getChildren().add(dragNodeS)
        this.getChildren().add(dragNodeW)
        this.getChildren().add(dragNodeNE)
        this.getChildren().add(dragNodeSE)
        this.getChildren().add(dragNodeSW)
        this.getChildren().add(dragNodeNW)
    }

    void setComponentToResize(Region node, double offsetX, double offsetY) {
        componentToResize = node

        final MultiEventHandler<MouseEvent> onMousePressedMultiEventHandler = new MultiEventHandler<>()
        onMousePressedMultiEventHandler.addHandler(this.&onMousePressedThis)
        onMousePressedMultiEventHandler.addBaseHandler(
                ((MultiEventHandler)node.getOnMousePressed()).baseHandler
        )

        final MultiEventHandler<MouseEvent> onMouseReleasedMultiEventHandler = new MultiEventHandler<>()
        onMouseReleasedMultiEventHandler.addHandler(this.&onMouseReleasedThis)
        onMouseReleasedMultiEventHandler.addBaseHandler(
                ((MultiEventHandler)node.getOnMouseReleased()).baseHandler
        )

        this.setOnMousePressed(onMousePressedMultiEventHandler)
        this.setOnMouseReleased(onMouseReleasedMultiEventHandler)

        this.offsetX = offsetX
        this.offsetY = offsetY

        currentX = node.getLayoutX()
        currentY = node.getLayoutY()

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
        final double currentWidth = componentToResize.getWidth()
        final double currentHeight = componentToResize.getHeight()
        final double currentX = componentToResize.getLayoutX()
        final double currentY = componentToResize.getLayoutY()

        final double newDesiredHeight = currentHeight - event.getY() + DRAG_NODE_RADIUS
        final double newY = currentY - (newDesiredHeight-currentHeight)

        repositionComponent(false, currentX, true, newY, currentWidth, newDesiredHeight)
    }

    private void nodeEOnMouseDragged(MouseEvent event) {
        println("nodeE dragged")
        final double currentWidth = componentToResize.getWidth()
        final double currentHeight = componentToResize.getHeight()
        final double currentX = componentToResize.getLayoutX()
        final double currentY = componentToResize.getLayoutY()

        final double newDesiredWidth = currentWidth + event.getX() - DRAG_NODE_RADIUS

        repositionComponent(false, currentX, false, currentY, newDesiredWidth, currentHeight)
    }

    private void nodeSOnMouseDragged(MouseEvent event) {
        println("nodeS dragged")
        final double currentWidth = componentToResize.getWidth()
        final double currentHeight = componentToResize.getHeight()
        final double currentX = componentToResize.getLayoutX()
        final double currentY = componentToResize.getLayoutY()

        final double newDesiredHeight = currentHeight + event.getY() - DRAG_NODE_RADIUS

        repositionComponent(false, currentX, false, currentY, currentWidth, newDesiredHeight)
    }

    private void nodeWOnMouseDragged(MouseEvent event) {
        println("nodeW dragged")
        final double currentWidth = componentToResize.getWidth()
        final double currentHeight = componentToResize.getHeight()
        final double currentX = componentToResize.getLayoutX()
        final double currentY = componentToResize.getLayoutY()

        final double newDesiredWidth = currentWidth - event.getX() + DRAG_NODE_RADIUS
        final double newX = currentX - (newDesiredWidth-currentWidth)

        repositionComponent(true, newX, false, currentY, newDesiredWidth, currentHeight)
    }

    private void nodeNEOnMouseDragged(MouseEvent event) {
        println("nodeNE dragged")
        final double currentWidth = componentToResize.getWidth()
        final double currentHeight = componentToResize.getHeight()
        final double currentX = componentToResize.getLayoutX()
        final double currentY = componentToResize.getLayoutY()

        final double newDesiredWidth = currentWidth + event.getX() - DRAG_NODE_RADIUS
        final double newDesiredHeight = currentHeight - event.getY() + DRAG_NODE_RADIUS
        final double newY = currentY - (newDesiredHeight-currentHeight)

        repositionComponent(false, currentX, true, newY, newDesiredWidth, newDesiredHeight)
    }

    private void nodeSEOnMouseDragged(MouseEvent event) {
        println("nodeSE dragged")
        final double currentWidth = componentToResize.getWidth()
        final double currentHeight = componentToResize.getHeight()
        final double currentX = componentToResize.getLayoutX()
        final double currentY = componentToResize.getLayoutY()

        final double newDesiredWidth = currentWidth + event.getX() - DRAG_NODE_RADIUS
        final double newDesiredHeight = currentHeight + event.getY() - DRAG_NODE_RADIUS

        repositionComponent(false, currentX, false, currentY, newDesiredWidth, newDesiredHeight)
    }

    private void nodeSWOnMouseDragged(MouseEvent event) {
        println("nodeSW dragged")
        final double currentWidth = componentToResize.getWidth()
        final double currentHeight = componentToResize.getHeight()
        final double currentX = componentToResize.getLayoutX()
        final double currentY = componentToResize.getLayoutY()

        final double newDesiredWidth = currentWidth - event.getX() + DRAG_NODE_RADIUS
        final double newDesiredHeight = currentHeight + event.getY() - DRAG_NODE_RADIUS
        final double newX = currentX - (newDesiredWidth-currentWidth)

        repositionComponent(true, newX, false, currentY, newDesiredWidth, newDesiredHeight)
    }

    private void nodeNWOnMouseDragged(MouseEvent event) {
        println("nodeNW dragged")
        final double currentWidth = componentToResize.getWidth()
        final double currentHeight = componentToResize.getHeight()
        final double currentX = componentToResize.getLayoutX()
        final double currentY = componentToResize.getLayoutY()

        final double newDesiredWidth = currentWidth - event.getX() + DRAG_NODE_RADIUS
        final double newDesiredHeight = currentHeight - event.getY() + DRAG_NODE_RADIUS
        final double newX = currentX - (newDesiredWidth-currentWidth)
        final double newY = currentY - (newDesiredHeight-currentHeight)

        repositionComponent(true, newX, true, newY, newDesiredWidth, newDesiredHeight)
    }

    private repositionComponent(boolean movingX, double newX, boolean movingY, double newY, double newDesiredWidth, double newDesiredHeight) {

        // attempt to set the components new X / Y / width / height
        if (movingX) {
            componentToResize.setLayoutX(newX)
        }
        if (movingY) {
            componentToResize.setLayoutY(newY)
        }
        componentToResize.setMaxSize(newDesiredWidth, newDesiredHeight)
        componentToResize.setPrefSize(newDesiredWidth, newDesiredHeight)

        // force layout to occur
        componentToResize.parent.layout()

        // find out what the actual X / Y / width / height are
        double actualX = componentToResize.getLayoutX()
        double actualY = componentToResize.getLayoutY()
        final double actualNewWidth = componentToResize.getWidth()
        final double actualNewHeight = componentToResize.getHeight()

        // if the actual new width or height is greater than the desired width / height (due to it hitting it's minSize),
        // reposition it.
        if ((movingX && (actualNewWidth > newDesiredWidth)) || (movingY && (actualNewHeight > newDesiredHeight))) {
            if (movingX && actualNewWidth > newDesiredWidth) {
                actualX = actualX - (actualNewWidth - newDesiredWidth)
                componentToResize.setLayoutX(actualX)
            }
            if (movingY && actualNewHeight > newDesiredHeight) {
                actualY = actualY - (actualNewHeight - newDesiredHeight)
                componentToResize.setLayoutY(actualY)
            }
            componentToResize.parent.layout()
        }

        // set the size and location of the resize component to match
        if (movingX) {
            setLayoutX(actualX)
        }
        if (movingY) {
            setLayoutY(actualY)
        }
        setMinSize(actualNewWidth, actualNewHeight)
        setMaxSize(actualNewWidth, actualNewHeight)
        setPrefSize(actualNewWidth, actualNewHeight)

        // force layout again, so the drag operation gets the correct width / height as it continues
        componentToResize.parent.layout()
    }

    //
    // END OF Dragging of corner nodes
    //

    @Override
    void resize(double width, double height) {
        super.resize(width, height)

        final Double widthOffset = this.getWidth() / 2 - DRAG_NODE_RADIUS
        final Double heightOffset = this.getHeight() / 2 - DRAG_NODE_RADIUS

        AnchorPane.setRightAnchor(dragNodeN, widthOffset)
        AnchorPane.setLeftAnchor(dragNodeN, widthOffset)

        AnchorPane.setTopAnchor(dragNodeE, heightOffset)
        AnchorPane.setBottomAnchor(dragNodeE, heightOffset)

        AnchorPane.setRightAnchor(dragNodeS, widthOffset)
        AnchorPane.setLeftAnchor(dragNodeS, widthOffset)

        AnchorPane.setTopAnchor(dragNodeW, heightOffset)
        AnchorPane.setBottomAnchor(dragNodeW, heightOffset)
    }

    void updateLocation() {
        setLayoutX(componentToResize.getLayoutX())
        setLayoutY(componentToResize.getLayoutY())
    }

    void registerComponent(Region node) {
        final MultiEventHandler<MouseEvent> onMousePressedMultiEventHandler = new MultiEventHandler<>()
        onMousePressedMultiEventHandler.addHandler(this.&onMousePressedComponent)
        onMousePressedMultiEventHandler.addBaseHandler(node.getOnMousePressed())

        final MultiEventHandler<MouseEvent> onMouseReleasedMultiEventHandler = new MultiEventHandler<>()
        onMouseReleasedMultiEventHandler.addHandler(this.&onMouseReleasedComponent)
        onMouseReleasedMultiEventHandler.addBaseHandler(node.getOnMouseReleased())

        node.setOnMousePressed(onMousePressedMultiEventHandler)
        node.setOnMouseReleased(onMouseReleasedMultiEventHandler)
    }

    private void onMousePressedComponent(MouseEvent event) {
        final Region region = event.getSource() as Region

        println("component pressed")
        currentlyDraggingComponent = true

        // remove the existing resize component, if any
        this.pane.getChildren().remove(this)

        // add the resize component, over the selected item
        setComponentToResize(region, event.getX(), event.getY())
        this.pane.getChildren().add(this)

        //add the move component over the selected item
        moveComponent.setComponentToMove(this, region, event.getX(), event.getY())
        this.pane.getChildren().add(moveComponent)
    }

    private void onMouseReleasedComponent(MouseEvent event) {
        if (currentlyDraggingComponent) {
            currentlyDraggingComponent = false
            println("component released")

            //move the component
            moveComponent.mouseReleased()

            // update the resize component location to match the new location of the component being moved.
            updateLocation()

            // remove the move component
            this.pane.getChildren().remove(moveComponent)
        }
    }

    private void onMousePressedThis(MouseEvent event) {
        println("resize component pressed")

        if (!this.currentlyDraggingNode) {
            if (event.primaryButtonDown) {
                currentlyDraggingComponent = true

                //add the move component over the selected item
                moveComponent.setComponentToMove(this, this.componentToResize, event.getX(), event.getY())
                this.pane.getChildren().add(moveComponent)
            }
        }
    }

    private void onMouseReleasedThis(MouseEvent event) {
        println("resize component released")

        if (this.currentlyDraggingNode) {
            this.currentlyDraggingNode = false
        }
        else {
            if (currentlyDraggingComponent) {
                currentlyDraggingComponent = false

                //move the component
                moveComponent.mouseReleased()

                // update the resize component location to match the new location of the component being moved.
                updateLocation()

                // remove the move component
                this.pane.getChildren().remove(moveComponent)
            }
        }
    }

    private void onMouseClickedParentPane(MouseEvent event) {
        if (event.target == this.pane) {
            println("pane clicked")
            this.pane.getChildren().remove(this)
        }
    }

}
