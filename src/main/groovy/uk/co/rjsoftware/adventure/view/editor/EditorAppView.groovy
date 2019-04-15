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
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.stage.FileChooser
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.view.AbstractDialogView
import uk.co.rjsoftware.adventure.view.LoadEvent
import uk.co.rjsoftware.adventure.view.LoadListener
import uk.co.rjsoftware.adventure.view.editor.components.CustomComponent
import uk.co.rjsoftware.adventure.view.editor.components.ResizeComponent
import uk.co.rjsoftware.adventure.view.editor.model.ObservableAdventure
import uk.co.rjsoftware.adventure.view.editor.model.ObservableItem
import uk.co.rjsoftware.adventure.view.editor.model.ObservableRoom
import uk.co.rjsoftware.adventure.view.editor.treeitems.AdventureTreeItem
import uk.co.rjsoftware.adventure.view.editor.treeitems.CustomTreeItem
import uk.co.rjsoftware.adventure.view.editor.treeitems.ItemTreeItem
import uk.co.rjsoftware.adventure.view.editor.treeitems.RoomTreeItem

import java.nio.file.Paths

@TypeChecked
class EditorAppView extends AbstractDialogView {

    @FXML private Pane mapPane = null
    @FXML private BorderPane editPane = null

    @FXML private TreeView<CustomTreeItem> treeView = null
    private TreeViewComparator treeViewComparator = new TreeViewComparator()

    @FXML private MenuItem loadMenuItem = null

    @FXML private Button addRoomButton

    private ResizeComponent resizeComponent

    private ObservableAdventure observableAdventure
    private AbstractDialogView view

    private List<LoadListener> loadListeners = new ArrayList()

    @FXML void initialize() {
        resizeComponent = new ResizeComponent(this.mapPane)
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
                if (component != null && component.getParent() != EditorAppView.this.mapPane) {
                    component.setLayoutX(100)
                    component.setLayoutY(100)

                    resizeComponent.registerComponent(component)
                    EditorAppView.this.mapPane.getChildren().add(component)
                }
            }
        });

        addRoomButton.setOnAction(this.&addRoomButtonClick)
    }

    private void addRoomButtonClick(ActionEvent event) {
        final ObservableRoom newObservableRoom = new ObservableRoom()
        this.observableAdventure.addRoom(newObservableRoom)
        this.view = new EditRoomView(this.observableAdventure, newObservableRoom, this.editPane)
        this.view.show(this.editPane)
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
        // add the adventure rooms / items to the treeView
        this.observableAdventure = new ObservableAdventure(adventure)
        final TreeItem<CustomTreeItem> root = new TreeItem<>()
        final AdventureTreeItem adventureTreeItem = new AdventureTreeItem(observableAdventure, root, editPane)
        root.setValue(adventureTreeItem)
        root.setExpanded(true);

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

