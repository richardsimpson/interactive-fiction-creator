package uk.co.rjsoftware.adventure.view.editor.treeitems

import groovy.transform.TypeChecked
import javafx.scene.control.TreeItem
import javafx.scene.layout.BorderPane
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.view.AbstractDialogView
import uk.co.rjsoftware.adventure.view.editor.EditAdventureView
import uk.co.rjsoftware.adventure.view.editor.model.ObservableAdventure
import uk.co.rjsoftware.adventure.view.editor.model.ObservableDomainObject
import uk.co.rjsoftware.adventure.view.editor.model.ObservableRoom

@TypeChecked
class AdventureTreeItem extends CustomTreeItem {

    private final ObservableAdventure observableAdventure

    AdventureTreeItem(ObservableAdventure observableAdventure, TreeItem<CustomTreeItem> treeItem, BorderPane parent) {
        super(treeItem, parent, observableAdventure)
        this.observableAdventure = observableAdventure

        for (ObservableRoom observableRoom : observableAdventure.getObservableRooms()) {
            addItem(observableRoom)
        }
    }

    @Override
    protected CustomTreeItem createChildCustomTreeItem(ObservableDomainObject item, TreeItem<CustomTreeItem> treeItem) {
        new RoomTreeItem(observableAdventure, (ObservableRoom)item, treeItem, getParentForView())
    }

    @Override
    protected AbstractDialogView createDialogView() {
        new EditAdventureView(observableAdventure)
    }

    // toString() is used by the TreeItem to determine th text to display
    @Override
    public String toString() {
        this.observableAdventure.titleProperty().getValue()
    }

    @Override
    protected ObservableDomainObject getObservableDomainObject() {
        this.observableAdventure
    }

}
