package uk.co.rjsoftware.adventure.view.editor.treeitems

import groovy.transform.TypeChecked
import javafx.scene.control.TreeItem
import javafx.stage.Stage
import uk.co.rjsoftware.adventure.model.Item
import uk.co.rjsoftware.adventure.view.AbstractEditDomainObjectDialogView
import uk.co.rjsoftware.adventure.view.editor.components.CustomComponent
import uk.co.rjsoftware.adventure.view.editor.components.ItemComponent

@TypeChecked
class ItemTreeItem extends CustomTreeItem {

    private final Item item
    private final ItemComponent component

    ItemTreeItem(Item item, TreeItem<CustomTreeItem> treeItem, Stage primaryStage) {
        super(treeItem, primaryStage, "../../editItem.fxml")
        this.item = item
        component = new ItemComponent(item)
    }

    void initialiseEditView(AbstractEditDomainObjectDialogView view) {
        view.setDomainObject(item)
    }

    CustomComponent getComponent() {
        return component
    }

    // toString() is used by the TreeItem to determine th text to display
    @Override
    public String toString() {
        return component.getText()
    }

}
