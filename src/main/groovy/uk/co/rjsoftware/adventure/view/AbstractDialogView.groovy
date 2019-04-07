package uk.co.rjsoftware.adventure.view

import groovy.transform.TypeChecked
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.stage.Modality
import javafx.stage.Stage

import static uk.co.rjsoftware.adventure.view.ModalResult.mrNone

@TypeChecked
abstract class AbstractDialogView {

    private final String fxmlLocation
    private Stage stage
    private Stage owner
    protected ModalResult modalResult = mrNone

    private boolean resizable = true

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
        stage = new Stage()

        final Parent rootLayout = loadRoot()

        // create and show the dialog
        final Scene scene = new Scene(rootLayout)
        stage.setScene(scene)
        stage.setResizable(this.resizable)
        onShow()
        stage.show()
    }

    void show(BorderPane parent) {
        final Parent rootLayout = loadRoot()
        onShow()
        parent.setCenter(rootLayout)
    }

    ModalResult showModal(Stage owner) {
        this.owner = owner

        final Parent rootLayout = loadRoot()

        // create and show the dialog
        final Scene scene = new Scene(rootLayout)
        stage = new Stage()
        stage.initOwner(owner)
        stage.initModality(Modality.WINDOW_MODAL)
        stage.setResizable(this.resizable)
        stage.setScene(scene)
        onShow()
        stage.showAndWait()
        modalResult
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

    void setResizable(boolean resizable) {
        this.resizable = resizable
    }

    boolean getResizable() {
        resizable
    }

    void close() {
        stage.close()
    }

}
