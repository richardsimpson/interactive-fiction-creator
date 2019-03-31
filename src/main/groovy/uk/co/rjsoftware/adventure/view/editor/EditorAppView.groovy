package uk.co.rjsoftware.adventure.view.editor

import groovy.transform.TypeChecked
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.stage.FileChooser
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.model.Item
import uk.co.rjsoftware.adventure.model.Room
import uk.co.rjsoftware.adventure.view.AbstractDialogView
import uk.co.rjsoftware.adventure.view.LoadEvent
import uk.co.rjsoftware.adventure.view.LoadListener
import uk.co.rjsoftware.adventure.view.editor.components.CustomComponent
import uk.co.rjsoftware.adventure.view.editor.components.ResizeComponent
import uk.co.rjsoftware.adventure.view.editor.treeitems.AdventureTreeItem
import uk.co.rjsoftware.adventure.view.editor.treeitems.CustomTreeItem
import uk.co.rjsoftware.adventure.view.editor.treeitems.ItemTreeItem
import uk.co.rjsoftware.adventure.view.editor.treeitems.RoomTreeItem

import java.nio.file.Paths

@TypeChecked
class EditorAppView extends AbstractDialogView {

    @FXML private Pane pane = null

    @FXML private TreeView<CustomTreeItem> treeView = null
    private TreeViewComparator treeViewComparator = new TreeViewComparator()

    @FXML private MenuItem loadMenuItem = null

    private ResizeComponent resizeComponent

    private Adventure adventure

    private List<LoadListener> loadListeners = new ArrayList()

    @FXML void initialize() {
        resizeComponent = new ResizeComponent(this.pane)
    }

    EditorAppView() {
        super("../editorApp.fxml")
    }

    @Override
    protected void onShow() {
        loadMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            void handle(ActionEvent event) {
                loadAdventureInternal()
            }
        })

        treeView.getSelectionModel().selectedItemProperty().addListener( new ChangeListener<TreeItem<CustomTreeItem>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<CustomTreeItem>> observable, TreeItem<CustomTreeItem> oldValue,
                                TreeItem<CustomTreeItem> newValue) {

                final CustomComponent component = newValue.getValue().getComponent()

                // Add the component to the editor view, if it's not already there
                if (component.getParent() != EditorAppView.this.pane) {
                    component.setLayoutX(100)
                    component.setLayoutY(100)

                    resizeComponent.registerComponent(component)
                    EditorAppView.this.pane.getChildren().add(component)
                }
            }
        });
    }

    private void loadAdventureInternal() {
        FileChooser fileChooser = new FileChooser()
        fileChooser.setTitle("Open Adventure")
        fileChooser.setInitialDirectory(new File(Paths.get("").toAbsolutePath().toString()))
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Adventures", "*.groovy"))

        File file = fileChooser.showOpenDialog(getStage())
        if (file != null) {
            fireLoadCommand(new LoadEvent(file))
        }
    }


    void addLoadListener(LoadListener listener) {
        this.loadListeners.add(listener)
    }

    private void fireLoadCommand(LoadEvent event) {
        for (LoadListener listener : this.loadListeners) {
            listener.callback(event)
        }
    }

    void loadAdventure(Adventure adventure) {
        this.adventure = adventure

        // add the adventure rooms / items to the treeView
        final TreeItem<CustomTreeItem> root = new TreeItem<>()
        final AdventureTreeItem adventureTreeItem = new AdventureTreeItem(adventure, root, getStage())
        root.setValue(adventureTreeItem)
        root.setExpanded(true);

        for (Room room : adventure.getRooms()) {
            final TreeItem<CustomTreeItem> treeItem = new TreeItem<>()
            final RoomTreeItem roomTreeItem = new RoomTreeItem(room, treeItem, getStage())
            treeItem.setValue(roomTreeItem)

            root.getChildren().add(treeItem)

            for (Map.Entry<String, Item> entry : room.getItems()) {
                populateTreeView(treeItem, entry.getValue())
            }
        }

        this.treeView.setRoot(root)
        // sort the TreeView when the name of a TreeView item changes
        this.treeView.root.addEventHandler(TreeItem.valueChangedEvent(), this.&onValueChangedEventTreeView)
        // initial sort
        sortTreeView()
        this.treeView.setOnMousePressed(this.&onMousePressedTreeView)
    }

    private void onMousePressedTreeView(MouseEvent event) {
        if (event.secondaryButtonDown) {
            final TreeItem treeItem = treeView.getSelectionModel().getSelectedItem()
            if (treeItem != null) {
                println "Selected Item: " + treeItem.getValue()

                Node node = event.getPickResult().getIntersectedNode();
                // Accept clicks only on node cells, and not on empty spaces of the TreeView
                if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
                    String name = (String) ((TreeItem)treeView.getSelectionModel().getSelectedItem()).getValue();
                    System.out.println("Node click: " + name);

                    ContextMenu contextMenu = treeItem.getValue().getContextMenu()
                    if (contextMenu != null) {
                        contextMenu.show(node, event.getScreenX(), event.getScreenY())
                    }
                }
            }
        }
    }

    private void populateTreeView(TreeItem<CustomTreeItem> parent, Item item) {
        final TreeItem<CustomTreeItem> treeItem = new TreeItem<>()
        final ItemTreeItem itemTreeItem = new ItemTreeItem(item, treeItem, getStage())
        treeItem.setValue(itemTreeItem)

        parent.getChildren().add(treeItem)

        for (Map.Entry<String, Item> entry : item.getItems()) {
            populateTreeView(treeItem, entry.getValue())
        }
    }

    private void onValueChangedEventTreeView(TreeItem.TreeModificationEvent<CustomTreeItem> event) {
        sortTreeView()
    }

    private static class TreeViewComparator implements Comparator<TreeItem<CustomTreeItem>> {
        @Override
        int compare(TreeItem<CustomTreeItem> o1, TreeItem<CustomTreeItem> o2) {
            return o1.getValue().toString().compareTo(o2.getValue().toString())
        }
    }

    private void sortTreeView() {
        this.treeView.getRoot().getChildren().sort(treeViewComparator)
    }

}

