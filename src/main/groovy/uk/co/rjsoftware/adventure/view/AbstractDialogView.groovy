package uk.co.rjsoftware.adventure.view

import groovy.transform.TypeChecked
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Modality
import javafx.stage.Stage

@TypeChecked
abstract class AbstractDialogView {

    private Stage stage
    private Stage owner
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
        this.owner = owner

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

    // TODO: Make concrete, with an exception (so implementations don't have to be public
    abstract String fxmlLocation()

    protected void onShow() {
    }

    /**
     * @return
     *     The stage that owns the stage for this component.  Can be null
     */
    Stage getOwner() {
        this.owner
    }

    /**
     * @return
     *     Returns the stage for this component
     */
    Stage getStage() {
        this.stage
    }

    void close() {
        stage.close()
    }

}
