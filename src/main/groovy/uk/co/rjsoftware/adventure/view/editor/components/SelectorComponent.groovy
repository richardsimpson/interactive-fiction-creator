package uk.co.rjsoftware.adventure.view.editor.components

import groovy.transform.TypeChecked
import javafx.scene.Parent
import javafx.scene.input.MouseDragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Region

@TypeChecked
enum SelectMode {
    CONNECTOR,
    RESIZE
}

@TypeChecked
class SelectorComponent {

    private final ResizeComponent resizeComponent
    private final ConnectorsComponent connectorsComponent
    private SelectMode selectMode = SelectMode.RESIZE
    private final Parent pane

    SelectorComponent(Parent pane) {
        this.pane = pane
        this.pane.setOnMouseClicked(this.&onMouseClickedParentPane)
        this.resizeComponent = new ResizeComponent(pane)
        this.connectorsComponent = new ConnectorsComponent(pane)
    }

    //
    // ResizeComponent
    //

    void registerComponent(Region node) {
        final MultiEventHandler<MouseEvent> onMousePressedMultiEventHandler = new MultiEventHandler<>()
        onMousePressedMultiEventHandler.addHandler(this.&onMousePressedComponent)
        onMousePressedMultiEventHandler.addBaseHandler(node.getOnMousePressed())

        final MultiEventHandler<MouseEvent> onMouseReleasedMultiEventHandler = new MultiEventHandler<>()
        onMouseReleasedMultiEventHandler.addHandler(this.&onMouseReleasedComponent)
        onMouseReleasedMultiEventHandler.addBaseHandler(node.getOnMouseReleased())

        node.setOnMousePressed(onMousePressedMultiEventHandler)
        node.setOnMouseReleased(onMouseReleasedMultiEventHandler)
        node.setOnMouseClicked(this.&onMouseClickedComponent)
    }

    private void onMousePressedComponent(MouseEvent event) {
        if (this.selectMode == SelectMode.RESIZE) {
            this.resizeComponent.onMousePressedComponent(event)
        }
    }

    private void onMouseReleasedComponent(MouseEvent event) {
        if (this.selectMode == SelectMode.RESIZE) {
            this.resizeComponent.onMouseReleasedComponent(event)
        }
    }

    private void onMouseClickedComponent(MouseEvent event) {
        println("component clicked")
        event.consume()
    }

    //
    // ConnectorsComponent
    //
    private void onMouseMovedMapPane(MouseEvent event) {
        println("onMouseMoved (map pane)")
        println("target: " + event.getTarget())
        println("source: " + event.getSource())
        println("pickResult: " + event.getPickResult())
        this.connectorsComponent.onMouseMovedMapPane(event)
    }

    private void onMouseDraggedMapPane(MouseEvent event) {
        println("onMouseDragged (map pane)")
        println("target: " + event.getTarget())
        println("source: " + event.getSource())
        println("pickResult: " + event.getPickResult())
        this.connectorsComponent.onMouseDraggedMapPane(event)
    }

    private void onMouseDragReleasedMapPane(MouseDragEvent event) {
        println("onMouseDragReleased (map pane)")
        this.connectorsComponent.onMouseDragReleasedMapPane(event)
    }

    //
    // other methods
    //

    void setSelectMode(SelectMode selectMode) {
        if (this.selectMode != selectMode) {
            removeComponent()
            this.selectMode = selectMode
        }

        switch (this.selectMode) {
            case SelectMode.RESIZE:
                this.pane.setOnMouseMoved(null)
                this.pane.setOnMouseDragged(null)
                this.pane.setOnMouseDragReleased(null)
                break;
            case SelectMode.CONNECTOR:
                this.pane.setOnMouseMoved(this.&onMouseMovedMapPane)
                this.pane.setOnMouseDragged(this.&onMouseDraggedMapPane)
                this.pane.setOnMouseDragReleased(this.&onMouseDragReleasedMapPane)
                break;
            default:
                throw new RuntimeException("Unexpected selectMode " + this.selectMode.name())
        }
    }

    private void onMouseClickedParentPane(MouseEvent event) {
        if (event.getPickResult().getIntersectedNode() == this.pane) {
            println("pane clicked")
            removeComponent()
        }
    }

    private void removeComponent() {
        switch (this.selectMode) {
            case SelectMode.RESIZE:
                this.resizeComponent.removeComponent()
                break;
            case SelectMode.CONNECTOR:
                this.connectorsComponent.removeComponent()
                break;
            default:
                throw new RuntimeException("Unexpected selectMode " + this.selectMode.name())
        }
    }

}
