package uk.co.rjsoftware.adventure.view.editor.treeitems

import groovy.transform.TypeChecked
import javafx.scene.control.TreeItem
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.stage.Stage
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.model.Room
import uk.co.rjsoftware.adventure.view.AbstractEditDomainObjectDialogView
import uk.co.rjsoftware.adventure.view.editor.EditRoomView
import uk.co.rjsoftware.adventure.view.editor.components.CustomComponent
import uk.co.rjsoftware.adventure.view.editor.components.RoomComponent

@TypeChecked
class RoomTreeItem extends CustomTreeItem {

    private final Adventure adventure
    private final Room room
    private final RoomComponent component

    RoomTreeItem(Adventure adventure, Room room, TreeItem<CustomTreeItem> treeItem, BorderPane parent) {
        super(treeItem, parent)
        this.adventure = adventure
        this.room = room
        component = new RoomComponent(room, this)
    }

    @Override
    AbstractEditDomainObjectDialogView createDialogView() {
        new EditRoomView(adventure, room)
    }

    CustomComponent getComponent() {
        return component
    }

    // toString() is used by the TreeItem to determine th text to display
    @Override
    String toString() {
        return room.getName()
    }

}
