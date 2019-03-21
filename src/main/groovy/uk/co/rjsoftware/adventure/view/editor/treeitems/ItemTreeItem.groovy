package uk.co.rjsoftware.adventure.view.editor.treeitems

import groovy.transform.TypeChecked
import uk.co.rjsoftware.adventure.model.Item
import uk.co.rjsoftware.adventure.view.editor.components.CustomComponent
import uk.co.rjsoftware.adventure.view.editor.components.ItemComponent

@TypeChecked
class ItemTreeItem implements CustomTreeItem {

    private final ItemComponent component

    ItemTreeItem(Item item) {
        component = new ItemComponent(item)
    }

    @Override
    CustomComponent getComponent() {
        return component
    }

    @Override
    public String toString() {
        return component.getText()
    }
}
