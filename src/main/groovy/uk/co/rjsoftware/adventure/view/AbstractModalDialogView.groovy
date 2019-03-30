package uk.co.rjsoftware.adventure.view

import groovy.transform.TypeChecked
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.stage.Modality
import javafx.stage.Stage
import uk.co.rjsoftware.adventure.model.Room
import uk.co.rjsoftware.adventure.view.editor.ChangeListener

@TypeChecked
abstract class AbstractModalDialogView {

    private Stage stage

    void init(Parent rootLayout, Stage owner) {
        final Scene scene = new Scene(rootLayout)
        this.stage = new Stage()
        stage.initOwner(owner)
        stage.initModality(Modality.WINDOW_MODAL)
        stage.setResizable(false)
        stage.setScene(scene)
        stage.show()
    }

    void close() {
        stage.close()
    }

}
