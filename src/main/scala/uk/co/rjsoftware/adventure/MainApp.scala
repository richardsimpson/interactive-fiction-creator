package uk.co.rjsoftware.adventure

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage

import uk.co.rjsoftware.adventure.controller.AdventureController
import uk.co.rjsoftware.adventure.model._
import uk.co.rjsoftware.adventure.view.MainWindowView


/**
  * Created by richardsimpson on 15/05/2017.
  */
class MainApp extends Application {
    private val adventure:Adventure = new Adventure("Welcome to the Adventure!")

    val bedroom:Room = new Room("bedroom", "This is your bedroom.  Clothes are strewn " +
            "across the floor, there is a TV, and a lamp sits on the bedsite table.",
            afterEnterRoomFirstTimeScript = "executeAfterTurns(5, \"say('you decide you should tidy up')\")")

    val lamp:Item = new Item(List("lamp"), "A bedside lamp. with a simple on/off switch", switchable = true)
    bedroom.addItem(lamp)

    val tv:Item = new Item(List("TV", "television"), "A 28\" TV.",
        visible = true, scenery = false, gettable = false, droppable = false,
        switchable = true,
        extraMessageWhenSwitchedOn = "It is showing an old western.",
        extraMessageWhenSwitchedOff = "It is currently switched off.")
    tv.addVerb(new CustomVerb(List("WATCH {noun}")),
                "if (isSwitchedOn('tv')) {" +
                "    say('You watch the TV for a while.  It\\'s showing a Western of some kind.');" +
                "}" +
                "else if (isSwitchedOff('tv')) {" +
                "    say('You watch the TV for a while.  It\\'s just a black screen.');" +
                "}" +
                "else {" +
                "    say('This is weird.  The TV is neither switched on or off!');" +
                "}"
    )
    bedroom.addItem(tv)

    val landing:Room = new Room("landing", "You are in the landing.  There is not much here, " +
            "except for a coffee stained carpet")

    bedroom.addExit(Direction.EAST, landing)
    landing.addExit(Direction.WEST, bedroom)

    this.adventure.addRoom(bedroom)
    this.adventure.addRoom(landing)

    this.adventure.setStartRoom(bedroom)

    override def start(primaryStage: Stage): Unit = {
        primaryStage.setTitle("Adventure Game")

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
        mainWindowView.init(this.adventure)

        val adventureController:AdventureController = new AdventureController(adventure, mainWindowView)
    }

}

object MainApp {
    def main(args: Array[String]): Unit = {
        Application.launch(classOf[MainApp], args: _*)
    }
}
