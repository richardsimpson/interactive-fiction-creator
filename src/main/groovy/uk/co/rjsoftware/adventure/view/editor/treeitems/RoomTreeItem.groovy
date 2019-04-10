package uk.co.rjsoftware.adventure.view.editor.treeitems

import groovy.transform.TypeChecked
import javafx.scene.control.TreeItem
import javafx.scene.layout.BorderPane
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.view.AbstractDialogView
import uk.co.rjsoftware.adventure.view.editor.EditRoomView
import uk.co.rjsoftware.adventure.view.editor.ObservableRoom
import uk.co.rjsoftware.adventure.view.editor.components.CustomComponent
import uk.co.rjsoftware.adventure.view.editor.components.RoomComponent

@TypeChecked
class RoomTreeItem extends CustomTreeItem {

    private final Adventure adventure
    private final ObservableRoom observableRoom
    private final RoomComponent component

    RoomTreeItem(Adventure adventure, ObservableRoom observableRoom, TreeItem<CustomTreeItem> treeItem, BorderPane parent) {
        super(treeItem, parent, observableRoom.nameProperty())
        this.adventure = adventure
        this.observableRoom = observableRoom
        component = new RoomComponent(observableRoom, this)
    }

    @Override
    AbstractDialogView createDialogView() {
        new EditRoomView(adventure, observableRoom, getParentForView())
    }

    CustomComponent getComponent() {
        return component
    }

    // toString() is used by the TreeItem to determine the text to display
    @Override
    String toString() {
        return observableRoom.nameProperty().getValue()
    }

}
