package uk.co.rjsoftware.adventure.view

import groovy.transform.TypeChecked
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.control.ScrollPane
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.layout.Pane
import javafx.stage.FileChooser
import javafx.stage.Stage
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.model.Item
import uk.co.rjsoftware.adventure.model.Room

import java.nio.file.Paths

@TypeChecked
class EditorAppView {

    @FXML private Pane pane = null

    @FXML private TreeView<String> treeView = null

    @FXML private MenuItem loadMenuItem = null

    @FXML void initialize() {
    }

    private Stage primaryStage = null

    private List<LoadListener> loadListeners = new ArrayList()

    void init(Stage primaryStage) {
        this.primaryStage = primaryStage

        Label label = new Label()
        label.setText("")
        label.setLayoutX(100)
        label.setLayoutY(100)

        this.pane.getChildren().add(label)

        loadMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            void handle(ActionEvent event) {
                loadAdventureInternal()
            }
        })

        treeView.getSelectionModel().selectedItemProperty().addListener( new ChangeListener<TreeItem<String>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue,
                                TreeItem<String> newValue) {
                label.setText(newValue.value)
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
        // add the adventure rooms / items to the treeView
        final TreeItem<String> root = new TreeItem<String>("Adventure");
        root.setExpanded(true);

        for (Room room : adventure.getRooms()) {
            final TreeItem<String> roomTreeItem = new TreeItem<>(room.name)
            root.getChildren().add(roomTreeItem)

            for (Map.Entry<String, Item> entry : room.getItems()) {
                populateTreeView(roomTreeItem, entry.getValue())
            }
        }

        this.treeView.setRoot(root)

    }

    void populateTreeView(TreeItem<String> parent, Item item) {
        final TreeItem<String> itemTreeItem = new TreeItem<>(item.name)
        parent.getChildren().add(itemTreeItem)

        for (Map.Entry<String, Item> entry : item.getItems()) {
            populateTreeView(itemTreeItem, entry.getValue())
        }
    }
}
