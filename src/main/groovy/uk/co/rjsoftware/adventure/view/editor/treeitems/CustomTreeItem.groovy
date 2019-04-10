package uk.co.rjsoftware.adventure.view.editor.treeitems

import groovy.transform.TypeChecked
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.TreeItem
import javafx.scene.layout.BorderPane
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

    CustomTreeItem(TreeItem<CustomTreeItem> treeItem, BorderPane parent, ObservableValue treeItemTextProperty) {
        this.treeItem = treeItem
        this.parent = parent

        // set up the context menu
        MenuItem item1 = new MenuItem("Edit...");
        item1.setOnAction(this.&onActionEditMenuItem)
        contextMenu.getItems().addAll(item1);

        if (treeItemTextProperty != null) {
            treeItemTextProperty.addListener(new javafx.beans.value.ChangeListener<String>() {
                @Override
                void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    TreeItem.TreeModificationEvent treeEvent = new TreeItem.TreeModificationEvent(TreeItem.valueChangedEvent(), treeItem);
                    Event.fireEvent(treeItem, treeEvent);
                }
            })
        }
    }

    // has to be protected, as otherwise the method doesn't get found at runtime
    protected onActionEditMenuItem(ActionEvent event) {
        view = createDialogView()
        view.show(parent)
    }

    abstract AbstractDialogView createDialogView()

    abstract CustomComponent getComponent()

    ContextMenu getContextMenu() {
        this.contextMenu
    }

    BorderPane getParentForView() {
        this.parent
    }

}