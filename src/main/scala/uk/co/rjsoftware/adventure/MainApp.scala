package uk.co.rjsoftware.adventure

import java.io.File
import java.nio.file.Paths
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage

import uk.co.rjsoftware.adventure.controller.AdventureController
import uk.co.rjsoftware.adventure.controller.load.Loader
import uk.co.rjsoftware.adventure.model._
import uk.co.rjsoftware.adventure.view.MainWindowView


/**
  * Created by richardsimpson on 15/05/2017.
  */
class MainApp extends Application {

    override def start(primaryStage: Stage): Unit = {
        // Load root layout from fxml file
        val loader:FXMLLoader = new FXMLLoader()
        loader.setLocation(getClass.getResource("view/mainwindow.fxml"))
        val rootLayout:BorderPane = loader.load()

        // Show the scene containing the root layout
        val scene:Scene = new Scene(rootLayout)
        primaryStage.setScene(scene)
        primaryStage.show()

        // initialise the view after showing the scene, as then the request to focus the input box will work
        val mainWindowView:MainWindowView = loader.getController()
        mainWindowView.init(primaryStage)

        val controller:AdventureController = new AdventureController(mainWindowView)

        val file:File = new File(Paths.get("").toAbsolutePath.toString + "/adventures/adventureA.groovy")
        val adventure:Adventure = Loader.loadAdventure(file)
        controller.loadAdventure(adventure)
    }

}

object MainApp {
    def main(args: Array[String]): Unit = {
        Application.launch(classOf[MainApp], args: _*)
    }
}
