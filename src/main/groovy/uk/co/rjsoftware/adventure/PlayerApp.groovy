package uk.co.rjsoftware.adventure

import groovy.transform.TypeChecked
import javafx.application.Application
import javafx.stage.Stage
import uk.co.rjsoftware.adventure.controller.AdventureController
import uk.co.rjsoftware.adventure.controller.load.Loader
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.view.PlayerAppView

import java.nio.file.Paths

@TypeChecked
class PlayerApp extends Application {

    static void main(String[] args) {
        launch(PlayerApp.class, args)
    }

    void start(Stage primaryStage) {
        PlayerAppView playerAppView = new PlayerAppView()
        playerAppView.show()

        final AdventureController controller = new AdventureController(playerAppView)

        final File file = new File(Paths.get("").toAbsolutePath().toString() + "/adventures/TheBoggit.groovy")
        final Adventure adventure = Loader.loadAdventure(file)
        controller.loadAdventure(adventure)
    }

}
