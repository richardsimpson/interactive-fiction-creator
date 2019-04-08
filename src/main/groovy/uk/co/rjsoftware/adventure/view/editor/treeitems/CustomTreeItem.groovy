package uk.co.rjsoftware.adventure.view.editor.treeitems

import groovy.transform.TypeChecked
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.TreeItem
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.stage.Stage
import uk.co.rjsoftware.adventure.view.AbstractDialogView
import uk.co.rjsoftware.adventure.view.editor.ChangeListener
import uk.co.rjsoftware.adventure.view.editor.components.CustomComponent

@TypeChecked
abstract class CustomTreeItem {

    private final TreeItem<CustomTreeItem> treeItem
    private final BorderPane parent

    private ContextMenu contextMenu = new ContextMenu()
    private List<ChangeListener> changeListeners = new ArrayList<>()

    // This 'view' attribute could be local to onActionEditMenuItem, except for the fact that it can get garbage
    // collected if declared locally.  Controllers are not references by virtue of them being set as the
    // controller in FXMLLoader, nor are they referenced by JavaBeanProperties (those only have WeakReferences to
    // their properties).  If the view gets garbage collected, then changes in the GUI don't get persisted back
    // to the domain object (since the JavaBeanProperty.get() method would then return 'null'.
    private AbstractDialogView view

    CustomTreeItem(TreeItem<CustomTreeItem> treeItem, BorderPane parent) {
        this.treeItem = treeItem
        this.parent = parent

        // set up the context menu
        MenuItem item1 = new MenuItem("Edit...");
        item1.setOnAction(this.&onActionEditMenuItem)
        contextMenu.getItems().addAll(item1);
    }

    // has to be protected, as otherwise the method doesn't get found at runtime
    protected onActionEditMenuItem(ActionEvent event) {
        // toString() is used by the TreeItem to determine the text to display, so
        // we need to know if the edit dialog will change this
        final String existingName = toString()

        view = createDialogView()

        view.show(parent)

        // now check if the tree item text will have changed
        final String newName = toString()
        if (!existingName.equals(newName)) {
            TreeItem.TreeModificationEvent treeEvent = new TreeItem.TreeModificationEvent(TreeItem.valueChangedEvent(), treeItem);
            Event.fireEvent(treeItem, treeEvent);
        }

        // fire event that the RoomComponent listens to
        fireChangeEvent()
    }

    abstract AbstractDialogView createDialogView()

    abstract CustomComponent getComponent()

    ContextMenu getContextMenu() {
        this.contextMenu
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