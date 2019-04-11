package uk.co.rjsoftware.adventure.view.editor.treeitems

import groovy.transform.TypeChecked
import javafx.scene.control.TreeItem
import javafx.scene.layout.BorderPane
import uk.co.rjsoftware.adventure.view.AbstractDialogView
import uk.co.rjsoftware.adventure.view.editor.EditItemView
import uk.co.rjsoftware.adventure.view.editor.components.CustomComponent
import uk.co.rjsoftware.adventure.view.editor.components.ItemComponent
import uk.co.rjsoftware.adventure.view.editor.model.ObservableDomainObject
import uk.co.rjsoftware.adventure.view.editor.model.ObservableItem

@TypeChecked
class ItemTreeItem extends CustomTreeItem {

    private final ObservableItem observableItem
    private final ItemComponent component

    ItemTreeItem(ObservableItem observableItem, TreeItem<CustomTreeItem> treeItem, BorderPane parent) {
        super(treeItem, parent, observableItem)
        this.observableItem = observableItem
        component = new ItemComponent(observableItem)

        for (ObservableItem childObservableItem : observableItem.getObservableItems()) {
            final TreeItem<CustomTreeItem> childTreeItem = new TreeItem<>()
            final ItemTreeItem itemTreeItem = new ItemTreeItem(childObservableItem, childTreeItem, parent)
            childTreeItem.setValue(itemTreeItem)

            treeItem.getChildren().add(childTreeItem)
        }
    }

    @Override
    AbstractDialogView createDialogView() {
        new EditItemView(observableItem)
    }

    CustomComponent getComponent() {
        this.component
    }

    // toString() is used by the TreeItem to determine th text to display
    @Override
    String toString() {
        this.observableItem.nameProperty().getValue()
    }


    ObservableDomainObject getObservableDomainObject() {
        this.observableItem
    }
}
