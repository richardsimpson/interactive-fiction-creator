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
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.stage.FileChooser
import javafx.stage.Stage
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.model.Item
import uk.co.rjsoftware.adventure.model.Room
import uk.co.rjsoftware.adventure.view.LoadEvent
import uk.co.rjsoftware.adventure.view.LoadListener
import uk.co.rjsoftware.adventure.view.editor.components.CustomComponent
import uk.co.rjsoftware.adventure.view.editor.components.MoveComponent
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

    @FXML private MenuItem loadMenuItem = null

    private ResizeComponent resizeComponent = new ResizeComponent()
    private MoveComponent moveComponent = new MoveComponent()
    private boolean currentlyDraggingNode

    @FXML void initialize() {
        this.pane.setOnMouseClicked(this.&onMouseClickedEditorPane)
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

                    component.setOnMousePressed(EditorAppView.this.&onMousePressedComponent)
                    component.setOnMouseReleased(EditorAppView.this.&onMouseReleasedComponent)
                    EditorAppView.this.pane.getChildren().add(component)
                }
            }
        });

        resizeComponent.setOnMousePressed(this.&onMousePressedResizeComponent)
        resizeComponent.setOnMouseReleased(this.&onMouseReleasedResizeComponent)

    }

    private void onMousePressedComponent(MouseEvent event) {
        println("component pressed")

        final Region region = event.getSource() as Region

        // remove the existing resize component, if any
        this.pane.getChildren().remove(resizeComponent)

        // add the resize component, over the selected item
        resizeComponent.setComponentToResize(region, event.getX(), event.getY())
        this.pane.getChildren().add(resizeComponent)

        //add the move component over the selected item
        moveComponent.setComponentToMove(resizeComponent, region, event.getX(), event.getY())
        this.pane.getChildren().add(moveComponent)
    }

    private void onMouseReleasedComponent(MouseEvent event) {
        println("component released")

        //move the component
        moveComponent.mouseReleased()

        // update the resize component location to match the new location of the component being moved.
        resizeComponent.updateLocation()

        // remove the move component
        this.pane.getChildren().remove(moveComponent)
    }

    private void onMousePressedResizeComponent(MouseEvent event) {
        println("resize component pressed")

        if (resizeComponent.isCurrentlyDraggingNode()) {
            this.currentlyDraggingNode = true
        }
        else {
            final Region region = resizeComponent.getComponentToResize()

            //add the move component over the selected item
            moveComponent.setComponentToMove(resizeComponent, region, event.getX(), event.getY())
            this.pane.getChildren().add(moveComponent)
        }
    }

    private void onMouseReleasedResizeComponent(MouseEvent event) {
        println("resize component released")

        if (this.currentlyDraggingNode) {
            this.currentlyDraggingNode = false
        }
        else {
            //move the component
            moveComponent.mouseReleased()

            // update the resize component location to match the new location of the component being moved.
            resizeComponent.updateLocation()

            // remove the move component
            this.pane.getChildren().remove(moveComponent)
        }
    }

    private void onMouseClickedEditorPane(MouseEvent event) {
        if (event.target == this.pane) {
            println("pane clicked")
            this.pane.getChildren().remove(resizeComponent)
        }
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
        final TreeItem<CustomTreeItem> root = new TreeItem<>(new AdventureTreeItem(adventure));
        root.setExpanded(true);

        for (Room room : adventure.getRooms()) {
            final TreeItem<CustomTreeItem> roomTreeItem = new TreeItem<>(new RoomTreeItem(room))
            root.getChildren().add(roomTreeItem)

            for (Map.Entry<String, Item> entry : room.getItems()) {
                populateTreeView(roomTreeItem, entry.getValue())
            }
        }

        this.treeView.setRoot(root)

    }

    void populateTreeView(TreeItem<CustomTreeItem> parent, Item item) {
        final TreeItem<CustomTreeItem> itemTreeItem = new TreeItem<>(new ItemTreeItem(item))
        parent.getChildren().add(itemTreeItem)

        for (Map.Entry<String, Item> entry : item.getItems()) {
            populateTreeView(itemTreeItem, entry.getValue())
        }
    }
}
