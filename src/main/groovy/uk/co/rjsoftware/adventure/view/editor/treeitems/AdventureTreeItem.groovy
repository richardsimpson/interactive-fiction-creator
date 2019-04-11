package uk.co.rjsoftware.adventure.view.editor.treeitems

import groovy.transform.TypeChecked
import javafx.scene.control.TreeItem
import javafx.scene.layout.BorderPane
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.view.AbstractDialogView
import uk.co.rjsoftware.adventure.view.editor.EditAdventureView
import uk.co.rjsoftware.adventure.view.editor.components.AdventureComponent
import uk.co.rjsoftware.adventure.view.editor.components.CustomComponent
import uk.co.rjsoftware.adventure.view.editor.model.ObservableAdventure
import uk.co.rjsoftware.adventure.view.editor.model.ObservableDomainObject
import uk.co.rjsoftware.adventure.view.editor.model.ObservableItem
import uk.co.rjsoftware.adventure.view.editor.model.ObservableRoom

@TypeChecked
class AdventureTreeItem extends CustomTreeItem {

    private final ObservableAdventure observableAdventure
    private final AdventureComponent component

    AdventureTreeItem(Adventure adventure, ObservableAdventure observableAdventure, TreeItem<CustomTreeItem> treeItem, BorderPane parent) {
        super(treeItem, parent, observableAdventure)
        this.observableAdventure = observableAdventure
        this.component = new AdventureComponent(observableAdventure)

        for (ObservableRoom observableRoom : observableAdventure.getObservableRooms()) {
            final TreeItem<CustomTreeItem> childTreeItem = new TreeItem<>()
            final RoomTreeItem roomTreeItem = new RoomTreeItem(adventure, observableRoom, childTreeItem, parent)
            childTreeItem.setValue(roomTreeItem)

            treeItem.getChildren().add(childTreeItem)
        }
    }

    @Override
    AbstractDialogView createDialogView() {
        new EditAdventureView(observableAdventure)
    }

    CustomComponent getComponent() {
        this.component
    }

    // toString() is used by the TreeItem to determine th text to display
    @Override
    public String toString() {
        this.observableAdventure.titleProperty().getValue()
    }

    @Override
    ObservableDomainObject getObservableDomainObject() {
        this.observableAdventure
    }

}
