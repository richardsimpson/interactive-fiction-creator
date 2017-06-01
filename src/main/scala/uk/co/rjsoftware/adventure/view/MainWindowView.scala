package uk.co.rjsoftware.adventure.view

import java.io.File
import java.nio.file.Paths
import javafx.event.{ActionEvent, EventHandler}
import javafx.fxml.FXML
import javafx.scene.control.{Button, MenuItem, TextArea, TextField}
import javafx.scene.input.{KeyCode, KeyEvent}
import javafx.stage.FileChooser.ExtensionFilter
import javafx.stage.{FileChooser, Stage}

import uk.co.rjsoftware.adventure.controller.AdventureController
import uk.co.rjsoftware.adventure.controller.load.Loader
import uk.co.rjsoftware.adventure.model._

/**
  * Created by richardsimpson on 15/05/2017.
  */
class MainWindowView extends MainWindow {

    @FXML private var outputTextArea : TextArea = null

    @FXML private var inputTextField:TextField = null

    @FXML private var enterButton:Button = null

    @FXML private var loadMenuItem:MenuItem = null

    @FXML def initialize() = {
    }

    private var primaryStage:Stage = null

    private var commandListeners: List[CommandEvent => Unit] = Nil
    private var loadListeners: List[LoadEvent => Unit] = Nil


    def init(primaryStage: Stage) : Unit = {
        this.primaryStage = primaryStage
        this.outputTextArea.setText("")

        inputTextField.setOnKeyPressed(new EventHandler[KeyEvent] {
            override def handle(event: KeyEvent): Unit = {
                if (event.getCode == KeyCode.ENTER) {
                    submitCommand()
                }
            }
        })
        enterButton.setOnAction(new EventHandler[ActionEvent](){
            override def handle(event: ActionEvent): Unit = {
                submitCommand()
            }
        })
        inputTextField.requestFocus()

        loadMenuItem.setOnAction(new EventHandler[ActionEvent]() {
            override def handle(event: ActionEvent): Unit = {
                loadAdventure()
            }
        })
    }

    private def loadAdventure(): Unit = {
        val fileChooser:FileChooser = new FileChooser()
        fileChooser.setTitle("Open Adventure")
        fileChooser.setInitialDirectory(new File(Paths.get("").toAbsolutePath().toString()))
        fileChooser.setSelectedExtensionFilter(new ExtensionFilter("Adventures", "*.groovy"))

        val file:File = fileChooser.showOpenDialog(this.primaryStage)
        if (file != null) {
            fireLoadCommand(new LoadEvent(file))
        }
    }


    def say(outputText:String) : Unit = {
        this.outputTextArea.appendText(outputText + System.lineSeparator())
    }

    private def submitCommand() : Unit = {
        say("> " + this.inputTextField.getText)
        processCommand(this.inputTextField.getText)
        this.inputTextField.setText("")
    }

    private def processCommand(command: String) : Unit = {
        fireCommand(new CommandEvent(command))
    }

    def addCommandListener(listener: CommandEvent => Unit) : Unit = {
        this.commandListeners ::= listener
    }

    private def fireCommand(event: CommandEvent) : Unit = {
        for (listener <- this.commandListeners) {
            listener(event)
        }
    }

    def addLoadListener(listener: LoadEvent => Unit) : Unit = {
        this.loadListeners ::= listener
    }

    private def fireLoadCommand(event: LoadEvent) : Unit = {
        for (listener <- this.loadListeners) {
            listener(event)
        }
    }

    def loadAdventure(title:String, introduction:String): Unit = {
        this.outputTextArea.setText("")
        this.primaryStage.setTitle(title)

        if (introduction.length > 0) {
            say(introduction)
            say("")
        }
    }
}
