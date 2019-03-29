package uk.co.rjsoftware.adventure.view.editor.treeitems

import groovy.transform.TypeChecked
import javafx.event.Event
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeItem.TreeModificationEvent
import javafx.stage.Stage
import uk.co.rjsoftware.adventure.model.Room
import uk.co.rjsoftware.adventure.view.editor.ChangeListener
import uk.co.rjsoftware.adventure.view.editor.components.CustomComponent
import uk.co.rjsoftware.adventure.view.editor.components.RoomComponent

@TypeChecked
class RoomTreeItem implements CustomTreeItem {

    private final RoomComponent component
    private List<ChangeListener> changeListeners = new ArrayList<>()
    private TreeItem<CustomTreeItem> treeItem
    private String oldName

    RoomTreeItem(Room room, TreeItem<CustomTreeItem> treeItem,  Stage primaryStage) {
        this.treeItem = treeItem
        component = new RoomComponent(room, primaryStage)
        component.addChangeListener(this.&onChanged)

        this.oldName = room.getName()
    }

    @Override
    CustomComponent getComponent() {
        return component
    }

    void onChanged() {
        final String newName = this.component.getRoom().getName()
        if (!oldName.equals(newName)) {
            oldName = newName
            TreeModificationEvent event = new TreeModificationEvent(TreeItem.valueChangedEvent(), treeItem);
            Event.fireEvent(treeItem, event);
        }
    }

    @Override
    public String toString() {
        return component.getText()
    }
}
