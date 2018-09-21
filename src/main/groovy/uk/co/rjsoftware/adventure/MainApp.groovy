package uk.co.rjsoftware.adventure

import groovy.transform.TypeChecked
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import uk.co.rjsoftware.adventure.controller.AdventureController
import uk.co.rjsoftware.adventure.controller.load.Loader
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.view.MainWindowView

import java.nio.file.Paths

@TypeChecked
class MainApp extends Application {

    static void main(String[] args) {
        launch(MainApp.class, args)
    }

    void start(Stage primaryStage) {
        // Load root layout from fxml file
        final FXMLLoader loader = new FXMLLoader()
        loader.setLocation(getClass().getResource("view/mainwindow.fxml"))
        final BorderPane rootLayout = loader.load()

        // Show the scene containing the root layout
        final Scene scene = new Scene(rootLayout)
        primaryStage.setScene(scene)
        primaryStage.show()

        // initialise the view after showing the scene, as then the request to focus the input box will work
        final MainWindowView mainWindowView = loader.getController()
        mainWindowView.init(primaryStage)

        final AdventureController controller = new AdventureController(mainWindowView)

        final File file = new File(Paths.get("").toAbsolutePath().toString() + "/adventures/TheBoggit.groovy")
        final Adventure adventure = Loader.loadAdventure(file)
        controller.loadAdventure(adventure)
    }

}
