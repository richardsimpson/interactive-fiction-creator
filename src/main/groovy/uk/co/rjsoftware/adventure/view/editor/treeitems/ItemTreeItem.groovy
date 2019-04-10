package uk.co.rjsoftware.adventure.view.editor.treeitems

import groovy.transform.TypeChecked
import javafx.scene.control.TreeItem
import javafx.scene.layout.BorderPane
import uk.co.rjsoftware.adventure.view.AbstractDialogView
import uk.co.rjsoftware.adventure.view.editor.EditItemView
import uk.co.rjsoftware.adventure.view.editor.ObservableItem
import uk.co.rjsoftware.adventure.view.editor.components.CustomComponent
import uk.co.rjsoftware.adventure.view.editor.components.ItemComponent

@TypeChecked
class ItemTreeItem extends CustomTreeItem {

    private final ObservableItem observableItem
    private final ItemComponent component

    ItemTreeItem(ObservableItem observableItem, TreeItem<CustomTreeItem> treeItem, BorderPane parent) {
        super(treeItem, parent, observableItem.nameProperty())
        this.observableItem = observableItem
        component = new ItemComponent(observableItem)
    }

    @Override
    AbstractDialogView createDialogView() {
        new EditItemView(observableItem)
    }

    CustomComponent getComponent() {
        return component
    }

    // toString() is used by the TreeItem to determine th text to display
    @Override
    String toString() {
        return observableItem.nameProperty().getValue()
    }

}
