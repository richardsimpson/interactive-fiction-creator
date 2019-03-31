package uk.co.rjsoftware.adventure.view

import groovy.transform.TypeChecked
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
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
abstract class AbstractDialogView {

    private Stage stage
    // TODO: Make resizeable a property

    void show(Stage stage = null) {
        if (stage == null) {
            this.stage = new Stage()
        }
        else {
            this.stage = stage
        }

        // Load root layout from fxml file
        final FXMLLoader loader = new FXMLLoader()
        loader.setLocation(getClass().getResource(fxmlLocation()))
        loader.setController(this)
        final Parent rootLayout = loader.load()

        // Show the scene containing the root layout
        final Scene scene = new Scene(rootLayout)
        this.stage.setScene(scene)
        this.stage.show()

        onShow()
    }

    void showModal(Stage owner) {
        // Load root layout from fxml file
        final FXMLLoader loader = new FXMLLoader()
        loader.setLocation(getClass().getResource(fxmlLocation()))
        loader.setController(this)
        final Parent rootLayout = loader.load()

        // Show the scene containing the root layout
        final Scene scene = new Scene(rootLayout)
        this.stage = new Stage()
        stage.initOwner(owner)
        stage.initModality(Modality.WINDOW_MODAL)
        stage.setResizable(false)
        stage.setScene(scene)
        stage.show()

        // initialise the view after showing the scene
        onShow()
    }

    abstract String fxmlLocation()

    protected onShow() {
    }

    void close() {
        stage.close()
    }

}
