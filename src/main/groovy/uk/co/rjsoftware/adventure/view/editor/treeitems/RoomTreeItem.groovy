package uk.co.rjsoftware.adventure.view.editor.treeitems

import groovy.transform.TypeChecked
import javafx.scene.control.TreeItem
import javafx.stage.Stage
import uk.co.rjsoftware.adventure.model.Room
import uk.co.rjsoftware.adventure.view.AbstractEditDomainObjectDialogView
import uk.co.rjsoftware.adventure.view.editor.EditRoomView
import uk.co.rjsoftware.adventure.view.editor.components.CustomComponent
import uk.co.rjsoftware.adventure.view.editor.components.RoomComponent

@TypeChecked
class RoomTreeItem extends CustomTreeItem {

    private final Room room
    private final RoomComponent component

    RoomTreeItem(Room room, TreeItem<CustomTreeItem> treeItem, Stage owner) {
        super(treeItem, owner)
        this.room = room
        component = new RoomComponent(room, this)
    }

    @Override
    AbstractEditDomainObjectDialogView createDialogView() {
        new EditRoomView(room)
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
