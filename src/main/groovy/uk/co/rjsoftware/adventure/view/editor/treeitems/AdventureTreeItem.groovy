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
import javafx.stage.Stage
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.view.editor.EditAdventureView
import uk.co.rjsoftware.adventure.view.editor.EditRoomView
import uk.co.rjsoftware.adventure.view.editor.components.AdventureComponent
import uk.co.rjsoftware.adventure.view.editor.components.CustomComponent

@TypeChecked
class AdventureTreeItem implements CustomTreeItem {

    private final Adventure adventure
    private final AdventureComponent component
    private TreeItem<CustomTreeItem> treeItem
    private String oldName

    private ContextMenu contextMenu = new ContextMenu()

    AdventureTreeItem(Adventure adventure, TreeItem<CustomTreeItem> treeItem, Stage primaryStage) {
        this.treeItem = treeItem
        this.adventure = adventure
        component = new AdventureComponent(adventure)

        this.oldName = adventure.getTitle()

        // set up the context menu
        MenuItem item1 = new MenuItem("Edit...");
        item1.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // Load root layout from fxml file
                final FXMLLoader loader = new FXMLLoader()
                loader.setLocation(getClass().getResource("../../editAdventure.fxml"))
                final Parent rootLayout = loader.load()

                // initialise the view after showing the scene
                final EditAdventureView editAdventureView = loader.getController()
                editAdventureView.init(rootLayout, primaryStage, adventure)
                editAdventureView.addChangeListener(AdventureTreeItem.this.&onChanged)

                // TODO: Stop creating a new room view on each menu click
            }
        });
        contextMenu.getItems().addAll(item1);
    }

    @Override
    CustomComponent getComponent() {
        return component
    }

    private void onChanged() {
        final String newName = this.adventure.getTitle()
        if (!oldName.equals(newName)) {
            oldName = newName
            TreeItem.TreeModificationEvent event = new TreeItem.TreeModificationEvent(TreeItem.valueChangedEvent(), treeItem);
            Event.fireEvent(treeItem, event);
        }
    }

    @Override
    public String toString() {
        return adventure.getTitle()
    }

    @Override
    ContextMenu getContextMenu() {
        this.contextMenu
    }

}
