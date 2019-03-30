package uk.co.rjsoftware.adventure.view.editor.treeitems

import groovy.transform.TypeChecked
import javafx.scene.control.ContextMenu
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.view.editor.components.AdventureComponent
import uk.co.rjsoftware.adventure.view.editor.components.CustomComponent

@TypeChecked
class AdventureTreeItem implements CustomTreeItem {

    private final AdventureComponent component

    AdventureTreeItem(Adventure adventure) {
        component = new AdventureComponent(adventure)
    }

    @Override
    CustomComponent getComponent() {
        return component
    }

    @Override
    public String toString() {
        return component.getText()
    }

    @Override
    ContextMenu getContextMenu() {
        throw new RuntimeException("not yet implemented")
    }

}
