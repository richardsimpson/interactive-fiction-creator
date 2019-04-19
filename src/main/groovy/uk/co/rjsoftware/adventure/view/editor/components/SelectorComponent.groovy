package uk.co.rjsoftware.adventure.view.editor.components

import groovy.transform.TypeChecked
import javafx.scene.Parent
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

    void setSelectMode(SelectMode selectMode) {
        if (this.selectMode != selectMode) {
            removeComponent()
            this.selectMode = selectMode
        }
    }

    // TODO:
    // - DONE: Rename this to be SelectorComponent
    // - DONE: Move most of this into a new class - ResizeComponent.
    //      - Keep the onMousePressedComponent / onMouseReleasedComponent, and have them call
    //        call through to the ResizeComponent
    //      - Keep the onMouseClickedParentPane, and have it call through to the ResizeComponent.
    // - Have the 'draw lines' button toggle edit modes.  When toggle, call onMouseClickedParentPane to remove the resize component.
    // - Create a 'DrawLinesComponent', that puts on different nodes, and does the line drawing.

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
        switch (this.selectMode) {
            case SelectMode.RESIZE:
                this.resizeComponent.onMousePressedComponent(event)
                break;
            case SelectMode.CONNECTOR:
                this.connectorsComponent.onMousePressedComponent(event)
                break;
            default:
                throw new RuntimeException("Unexpected selectMode " + this.selectMode.name())
        }
    }

    private void onMouseReleasedComponent(MouseEvent event) {
        switch (this.selectMode) {
            case SelectMode.RESIZE:
                this.resizeComponent.onMouseReleasedComponent(event)
                break;
            case SelectMode.CONNECTOR:
                this.connectorsComponent.onMouseReleasedComponent(event)
                break;
            default:
                throw new RuntimeException("Unexpected selectMode " + this.selectMode.name())
        }
    }

    private void onMouseClickedComponent(MouseEvent event) {
        println("component clicked")
        event.consume()
    }

    private void onMouseClickedParentPane(MouseEvent event) {
        if (event.getPickResult().getIntersectedNode() == this.pane) {
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
