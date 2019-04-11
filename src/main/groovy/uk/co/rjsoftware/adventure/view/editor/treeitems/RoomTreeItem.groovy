package uk.co.rjsoftware.adventure.view.editor.treeitems

import groovy.transform.TypeChecked
import javafx.scene.control.TreeItem
import javafx.scene.layout.BorderPane
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.view.AbstractDialogView
import uk.co.rjsoftware.adventure.view.editor.EditRoomView
import uk.co.rjsoftware.adventure.view.editor.components.CustomComponent
import uk.co.rjsoftware.adventure.view.editor.components.RoomComponent
import uk.co.rjsoftware.adventure.view.editor.model.ObservableDomainObject
import uk.co.rjsoftware.adventure.view.editor.model.ObservableRoom

@TypeChecked
class RoomTreeItem extends CustomTreeItem {

    private final Adventure adventure
    private final ObservableRoom observableRoom
    private final RoomComponent component
    private final TreeItem<CustomTreeItem> treeItem

    RoomTreeItem(Adventure adventure, ObservableRoom observableRoom, TreeItem<CustomTreeItem> treeItem, BorderPane parent) {
        super(treeItem, parent, observableRoom)
        this.adventure = adventure
        this.observableRoom = observableRoom
        this.component = new RoomComponent(observableRoom, this)
        this.treeItem = treeItem
    }

    @Override
    AbstractDialogView createDialogView() {
        new EditRoomView(adventure, observableRoom, getParentForView())
    }

    CustomComponent getComponent() {
        this.component
    }

    // toString() is used by the TreeItem to determine the text to display
    @Override
    String toString() {
        this.observableRoom.nameProperty().getValue()
    }

    @Override
    ObservableDomainObject getObservableDomainObject() {
        this.observableRoom
    }
}
