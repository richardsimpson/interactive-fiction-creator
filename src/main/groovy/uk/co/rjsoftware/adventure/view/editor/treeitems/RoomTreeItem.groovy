package uk.co.rjsoftware.adventure.view.editor.treeitems

import groovy.transform.TypeChecked
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeItem.TreeModificationEvent
import javafx.stage.Stage
import uk.co.rjsoftware.adventure.model.Room
import uk.co.rjsoftware.adventure.view.editor.ChangeListener
import uk.co.rjsoftware.adventure.view.editor.EditRoomView
import uk.co.rjsoftware.adventure.view.editor.components.CustomComponent
import uk.co.rjsoftware.adventure.view.editor.components.RoomComponent

@TypeChecked
class RoomTreeItem implements CustomTreeItem {

    private final Room room
    private final RoomComponent component
    private List<ChangeListener> changeListeners = new ArrayList<>()
    private TreeItem<CustomTreeItem> treeItem
    private String oldName

    private ContextMenu contextMenu = new ContextMenu()

    RoomTreeItem(Room room, TreeItem<CustomTreeItem> treeItem,  Stage primaryStage) {
        this.treeItem = treeItem
        this.room = room
        component = new RoomComponent(room, this)

        this.oldName = room.getName()

        // set up the context menu
        MenuItem item1 = new MenuItem("Edit...");
        item1.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // Load root layout from fxml file
                final FXMLLoader loader = new FXMLLoader()
                loader.setLocation(getClass().getResource("../../editRoom.fxml"))
                final Parent rootLayout = loader.load()

                // initialise the view after showing the scene
                final EditRoomView editRoomView = loader.getController()
                editRoomView.init(rootLayout, primaryStage)
                editRoomView.setDomainObject(room)
                editRoomView.addChangeListener(RoomTreeItem.this.&onChanged)
            }
        });
        contextMenu.getItems().addAll(item1);
    }

    @Override
    CustomComponent getComponent() {
        return component
    }

    private void onChanged() {
        final String newName = this.room.getName()
        if (!oldName.equals(newName)) {
            oldName = newName
            TreeModificationEvent event = new TreeModificationEvent(TreeItem.valueChangedEvent(), treeItem);
            Event.fireEvent(treeItem, event);
        }

        // fire event that the RoomComponent listens to
        fireChangeEvent()
    }

    @Override
    String toString() {
        return room.getName()
    }

    void addChangeListener(ChangeListener listener) {
        this.changeListeners.add(listener)
    }

    private void fireChangeEvent() {
        for (ChangeListener listener : this.changeListeners) {
            listener.changed()
        }
    }

    ContextMenu getContextMenu() {
        this.contextMenu
    }

}
