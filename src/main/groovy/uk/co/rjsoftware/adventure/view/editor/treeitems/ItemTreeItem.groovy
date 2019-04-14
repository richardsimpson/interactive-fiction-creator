package uk.co.rjsoftware.adventure.view.editor.treeitems

import groovy.transform.TypeChecked
import javafx.scene.control.TreeItem
import javafx.scene.layout.BorderPane
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.view.AbstractDialogView
import uk.co.rjsoftware.adventure.view.editor.EditItemView
import uk.co.rjsoftware.adventure.view.editor.model.ObservableAdventure
import uk.co.rjsoftware.adventure.view.editor.model.ObservableDomainObject
import uk.co.rjsoftware.adventure.view.editor.model.ObservableItem

@TypeChecked
class ItemTreeItem extends CustomTreeItem {

    private final ObservableAdventure observableAdventure
    private final ObservableItem observableItem

    ItemTreeItem(ObservableAdventure observableAdventure, ObservableItem observableItem, TreeItem<CustomTreeItem> treeItem, BorderPane parent) {
        super(treeItem, parent, observableItem)
        this.observableAdventure = observableAdventure
        this.observableItem = observableItem

        for (ObservableItem childObservableItem : observableItem.getObservableItems()) {
            addItem(childObservableItem)
        }
    }

    @Override
    protected CustomTreeItem createChildCustomTreeItem(ObservableDomainObject item, TreeItem<CustomTreeItem> treeItem) {
        new ItemTreeItem(observableAdventure, (ObservableItem)item, treeItem, getParentForView())
    }

    @Override
    protected AbstractDialogView createDialogView() {
        new EditItemView(observableAdventure, observableItem, getParentForView())
    }

    // toString() is used by the TreeItem to determine th text to display
    @Override
    String toString() {
        this.observableItem.nameProperty().getValue()
    }


    protected ObservableDomainObject getObservableDomainObject() {
        this.observableItem
    }
}
