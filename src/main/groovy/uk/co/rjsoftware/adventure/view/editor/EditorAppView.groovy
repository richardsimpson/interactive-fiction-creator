package uk.co.rjsoftware.adventure.view.editor

import groovy.transform.TypeChecked
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.MenuItem
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.layout.Pane
import javafx.stage.FileChooser
import javafx.stage.Stage
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.model.Item
import uk.co.rjsoftware.adventure.model.Room
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
class EditorAppView {

    @FXML private Pane pane = null

    @FXML private TreeView<CustomTreeItem> treeView = null
    private TreeViewComparator treeViewComparator = new TreeViewComparator()

    @FXML private MenuItem loadMenuItem = null

    private ResizeComponent resizeComponent

    private Adventure adventure

    @FXML void initialize() {
        resizeComponent = new ResizeComponent(this.pane)
    }

    private Stage primaryStage = null

    private List<LoadListener> loadListeners = new ArrayList()

    void init(Stage primaryStage) {
        this.primaryStage = primaryStage

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

        File file = fileChooser.showOpenDialog(this.primaryStage)
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
        final TreeItem<CustomTreeItem> root = new TreeItem<>(new AdventureTreeItem(adventure));
        root.setExpanded(true);

        for (Room room : adventure.getRooms()) {
            final TreeItem<CustomTreeItem> treeItem = new TreeItem<>()
            final RoomTreeItem roomTreeItem = new RoomTreeItem(room, treeItem, this.primaryStage)
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
    }

    private void populateTreeView(TreeItem<CustomTreeItem> parent, Item item) {
        final TreeItem<CustomTreeItem> itemTreeItem = new TreeItem<>(new ItemTreeItem(item))
        parent.getChildren().add(itemTreeItem)

        for (Map.Entry<String, Item> entry : item.getItems()) {
            populateTreeView(itemTreeItem, entry.getValue())
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

