package uk.co.rjsoftware.adventure.view.editor.treeitems

import groovy.transform.TypeChecked
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.TreeItem
import javafx.stage.Stage
import uk.co.rjsoftware.adventure.view.AbstractEditDomainObjectDialogView
import uk.co.rjsoftware.adventure.view.editor.ChangeListener
import uk.co.rjsoftware.adventure.view.editor.components.CustomComponent

@TypeChecked
abstract class CustomTreeItem {

    private final TreeItem<CustomTreeItem> treeItem
    private final Stage owner

    private ContextMenu contextMenu = new ContextMenu()
    private String oldName
    private List<ChangeListener> changeListeners = new ArrayList<>()

    CustomTreeItem(TreeItem<CustomTreeItem> treeItem, Stage owner) {
        this.treeItem = treeItem
        this.owner = owner

        // set up the context menu
        MenuItem item1 = new MenuItem("Edit...");
        item1.setOnAction(this.&onActionEditMenuItem)
        contextMenu.getItems().addAll(item1);
    }

    // has to be protected, as otherwise the method doesn't get found at runtime
    protected onActionEditMenuItem(ActionEvent event) {
        // toString() is used by the TreeItem to determine th text to display, so
        // we need to know if the edit dialog will change this
        this.oldName = toString()

        final AbstractEditDomainObjectDialogView view = createDialogView()

        view.addChangeListener(this.&onChanged)

        view.showModal(owner)
    }

    abstract AbstractEditDomainObjectDialogView createDialogView()

    abstract CustomComponent getComponent()

    ContextMenu getContextMenu() {
        this.contextMenu
    }

    // has to be protected, as otherwise the method doesn't get found at runtime
    protected void onChanged() {
        final String newName = toString()
        if (!oldName.equals(newName)) {
            oldName = newName
            TreeItem.TreeModificationEvent event = new TreeItem.TreeModificationEvent(TreeItem.valueChangedEvent(), treeItem);
            Event.fireEvent(treeItem, event);
        }

        // fire event that the RoomComponent listens to
        fireChangeEvent()
    }

    void addChangeListener(ChangeListener listener) {
        this.changeListeners.add(listener)
    }

    private void fireChangeEvent() {
        for (ChangeListener listener : this.changeListeners) {
            listener.changed()
        }
    }

}