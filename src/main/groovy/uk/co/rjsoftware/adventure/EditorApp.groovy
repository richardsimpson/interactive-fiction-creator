package uk.co.rjsoftware.adventure

import groovy.transform.TypeChecked
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import uk.co.rjsoftware.adventure.controller.AdventureController
import uk.co.rjsoftware.adventure.controller.EditorController
import uk.co.rjsoftware.adventure.controller.load.Loader
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.view.EditorAppView
import uk.co.rjsoftware.adventure.view.PlayerAppView

import java.nio.file.Paths

@TypeChecked
class EditorApp extends Application {

    static void main(String[] args) {
        launch(EditorApp.class, args)
    }

    void start(Stage primaryStage) {
        // Load root layout from fxml file
        final FXMLLoader loader = new FXMLLoader()
        loader.setLocation(getClass().getResource("view/editorApp.fxml"))
        final BorderPane rootLayout = loader.load()

        // Show the scene containing the root layout
        final Scene scene = new Scene(rootLayout)
        primaryStage.setScene(scene)
        primaryStage.show()

        // initialise the view after showing the scene
        final EditorAppView editorAppView = loader.getController()
        editorAppView.init(primaryStage)

        // TODO: think of another word for 'player', then put the views and the controllers into different sub-packages
        // TODO: Immplement MVC for the editor.  The editor should not extract data from the model, rather, it
        // should be listening for changes in the model.  Actions in the editor (e.g. add room) need to be
        // passed to the controller, which then updates the model.
        // see https://stackoverflow.com/questions/32342864/applying-mvc-with-javafx
        // see https://examples.javacodegeeks.com/core-java/javafx-treeview-example/


        final EditorController controller = new EditorController(editorAppView)

        final File file = new File(Paths.get("").toAbsolutePath().toString() + "/adventures/TheBoggit.groovy")
        final Adventure adventure = Loader.loadAdventure(file)
        controller.loadAdventure(adventure)
    }

}
