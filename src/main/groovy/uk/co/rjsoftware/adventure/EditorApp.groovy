package uk.co.rjsoftware.adventure

import groovy.transform.TypeChecked
import javafx.application.Application
import javafx.stage.Stage
import uk.co.rjsoftware.adventure.controller.EditorController
import uk.co.rjsoftware.adventure.controller.load.Loader
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.view.editor.EditorAppView

import java.nio.file.Paths

@TypeChecked
class EditorApp extends Application {

    static void main(String[] args) {
        launch(EditorApp.class, args)
    }

    void start(Stage primaryStage) {
        EditorAppView editorAppView = new EditorAppView()
        editorAppView.show()

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
