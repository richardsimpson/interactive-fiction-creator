package uk.co.rjsoftware.adventure.view

import groovy.transform.TypeChecked
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Modality
import javafx.stage.Stage

@TypeChecked
abstract class AbstractDialogView {

    private final String fxmlLocation
    private Stage stage
    private Stage owner
    // TODO: Make resizeable a property

    static {
        // for simplicity, we're never going to use the primary stage, so we therefore have to
        // enable implicit exit, otherwise the app will never terminate.  This simplicity means that
        // the show() method never needs to be passed a stage - it can always create it's own.
        Platform.setImplicitExit(true)
    }

    AbstractDialogView(String fxmlLocation) {
        this.fxmlLocation = fxmlLocation
    }

    void show() {
        this.stage = new Stage()

        final Parent rootLayout = loadRoot()

        // create and show the dialog
        final Scene scene = new Scene(rootLayout)
        this.stage.setScene(scene)
        this.stage.show()

        onShow()
    }

    void showModal(Stage owner) {
        this.owner = owner

        final Parent rootLayout = loadRoot()

        // create and show the dialog
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

    private Parent loadRoot() {
        // Load root layout from fxml file
        final FXMLLoader loader = new FXMLLoader()
        loader.setLocation(getClass().getResource(this.fxmlLocation))
        loader.setController(this)
        loader.load()
    }

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
