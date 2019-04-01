package uk.co.rjsoftware.adventure.view.editor.treeitems

import groovy.transform.TypeChecked
import javafx.scene.control.TreeItem
import javafx.stage.Stage
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.view.AbstractEditDomainObjectDialogView
import uk.co.rjsoftware.adventure.view.editor.EditAdventureView
import uk.co.rjsoftware.adventure.view.editor.components.AdventureComponent
import uk.co.rjsoftware.adventure.view.editor.components.CustomComponent

@TypeChecked
class AdventureTreeItem extends CustomTreeItem {

    private final Adventure adventure
    private final AdventureComponent component

    AdventureTreeItem(Adventure adventure, TreeItem<CustomTreeItem> treeItem, Stage owner) {
        super(treeItem, owner, EditAdventureView.class)
        this.adventure = adventure
        component = new AdventureComponent(adventure)
    }

    @Override
    AbstractEditDomainObjectDialogView createDialogView() {
        new EditAdventureView(adventure)
    }

    CustomComponent getComponent() {
        return component
    }

    // toString() is used by the TreeItem to determine th text to display
    @Override
    public String toString() {
        return adventure.getTitle()
    }

}
