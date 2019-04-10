package uk.co.rjsoftware.adventure.view.editor.treeitems

import groovy.transform.TypeChecked
import javafx.scene.control.TreeItem
import javafx.scene.layout.BorderPane
import uk.co.rjsoftware.adventure.view.AbstractDialogView
import uk.co.rjsoftware.adventure.view.editor.EditAdventureView
import uk.co.rjsoftware.adventure.view.editor.ObservableAdventure
import uk.co.rjsoftware.adventure.view.editor.components.AdventureComponent
import uk.co.rjsoftware.adventure.view.editor.components.CustomComponent

@TypeChecked
class AdventureTreeItem extends CustomTreeItem {

    private final ObservableAdventure observableAdventure
    private final AdventureComponent component

    AdventureTreeItem(ObservableAdventure observableAdventure, TreeItem<CustomTreeItem> treeItem, BorderPane parent) {
        super(treeItem, parent, observableAdventure.titleProperty())
        this.observableAdventure = observableAdventure
        component = new AdventureComponent(observableAdventure)
    }

    @Override
    AbstractDialogView createDialogView() {
        new EditAdventureView(observableAdventure)
    }

    CustomComponent getComponent() {
        return component
    }

    // toString() is used by the TreeItem to determine th text to display
    @Override
    public String toString() {
        return observableAdventure.titleProperty().getValue()
    }

}
