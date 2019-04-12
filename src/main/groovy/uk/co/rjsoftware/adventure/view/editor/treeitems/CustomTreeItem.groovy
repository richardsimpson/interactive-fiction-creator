package uk.co.rjsoftware.adventure.view.editor.treeitems

import groovy.transform.TypeChecked
import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.TreeItem
import javafx.scene.layout.BorderPane
import uk.co.rjsoftware.adventure.view.AbstractDialogView
import uk.co.rjsoftware.adventure.view.editor.ChangeListener
import uk.co.rjsoftware.adventure.view.editor.components.CustomComponent
import uk.co.rjsoftware.adventure.view.editor.model.ObservableDomainObject

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

    CustomTreeItem(TreeItem<CustomTreeItem> treeItem, BorderPane parent, ObservableDomainObject observableDomainObject) {
        this.treeItem = treeItem
        this.parent = parent

        // set up the context menu
        MenuItem item1 = new MenuItem("Edit...");
        item1.setOnAction(this.&onActionEditMenuItem)
        contextMenu.getItems().addAll(item1);

        if (observableDomainObject.getTreeItemTextProperty() != null) {
            observableDomainObject.getTreeItemTextProperty().addListener(new javafx.beans.value.ChangeListener<String>() {
                @Override
                void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    TreeItem.TreeModificationEvent treeEvent = new TreeItem.TreeModificationEvent(TreeItem.valueChangedEvent(), treeItem);
                    Event.fireEvent(treeItem, treeEvent);
                }
            })
        }

        observableDomainObject.getObservableTreeItemChildren().addListener(new ListChangeListener<ObservableDomainObject>() {
            @Override
            void onChanged(ListChangeListener.Change<? extends ObservableDomainObject> c) {
                listOnChanged(c)
            }
        })
    }

    private void listOnChanged(ListChangeListener.Change<? extends ObservableDomainObject> c) {
        while (c.next()) {
            if (c.wasPermutated()) {
                println "Item list permutated from " + c.getFrom() + " to " + c.getTo() + "."
                for (int i = c.getFrom(); i < c.getTo(); ++i) {
                    //permutate
                }
            } else if (c.wasUpdated()) {
                println "Item list updated from " + c.getFrom() + " to " + c.getTo() + "."
            } else {
                for (ObservableDomainObject remitem : c.getRemoved()) {
                    println "Item removed: " + remitem.getTreeItemTextProperty().getValue()
                    removeItem(remitem)
                }
                for (ObservableDomainObject additem : c.getAddedSubList()) {
                    println "Item Added: " + additem.getTreeItemTextProperty().getValue()
                    addItem(additem)
                }
            }
        }
    }

    private removeItem(ObservableDomainObject item) {
        final TreeItem<CustomTreeItem> treeItemToRemove = treeItem.getChildren().find {
            it.getValue().getObservableDomainObject() == item
        }

        if (treeItemToRemove != null) {
            treeItem.getChildren().remove(treeItemToRemove)
        }
    }

    protected addItem(ObservableDomainObject item) {
        final TreeItem<CustomTreeItem> newTreeItem = new TreeItem<>()
        final CustomTreeItem customTreeItem = createChildCustomTreeItem(item, newTreeItem)
        newTreeItem.setValue(customTreeItem)

        treeItem.getChildren().add(newTreeItem)
    }

    abstract protected CustomTreeItem createChildCustomTreeItem(ObservableDomainObject item, TreeItem<CustomTreeItem> treeItem)

    // has to be protected, as otherwise the method doesn't get found at runtime
    protected onActionEditMenuItem(ActionEvent event) {
        view = createDialogView()
        view.show(parent)
    }

    abstract protected AbstractDialogView createDialogView()

    abstract CustomComponent getComponent()

    ContextMenu getContextMenu() {
        this.contextMenu
    }

    BorderPane getParentForView() {
        this.parent
    }

    abstract protected ObservableDomainObject getObservableDomainObject()
}