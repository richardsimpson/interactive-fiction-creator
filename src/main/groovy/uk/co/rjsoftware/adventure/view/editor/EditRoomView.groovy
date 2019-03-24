package uk.co.rjsoftware.adventure.view.editor

import groovy.transform.TypeChecked
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.MenuItem
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.stage.FileChooser
import javafx.stage.Stage
import uk.co.rjsoftware.adventure.controller.EditorController
import uk.co.rjsoftware.adventure.controller.load.Loader
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
class EditRoomView {

    @FXML private TextField nameTextField = null
    @FXML private TextArea descriptionTextArea = null
    @FXML private Button cancelButton = null
    @FXML private Button okButton = null

    @FXML void initialize() {
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            void handle(ActionEvent event) {
                close()
            }
        })

        okButton.setOnAction(this.&saveAndClose)
    }

    private Stage stage = null
    private Room room = null

    void init(Stage stage, Room room) {
        this.stage = stage
        this.room = room
        this.nameTextField.setText(room.getName())
        this.descriptionTextArea.setText((room.getDescription()))
    }

    private void saveAndClose(ActionEvent event) {
        // TODO: Enable name to be changed - it's currently used as a map key in AdventureController
        // TODO: Changing the room here does not change the view in the editor
        //this.room.setName(this.nameTextField.getText())
        this.room.setDescription(this.descriptionTextArea.getText())
        close()
    }

    private void close() {
        stage.close()
    }
}
