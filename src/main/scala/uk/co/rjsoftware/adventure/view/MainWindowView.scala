package uk.co.rjsoftware.adventure.view

import javafx.fxml.FXML
import javafx.scene.control.{Button, MenuItem, TextArea, TextField}
import javafx.scene.input.{KeyCode, KeyEvent}

import uk.co.rjsoftware.adventure.controller.AdventureController
import uk.co.rjsoftware.adventure.model._
import java.util
import javafx.event.{ActionEvent, EventHandler}

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

    private var listeners: List[CommandEvent => Unit] = Nil

    def init(adventure:Adventure) : Unit = {
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
                // TODO
            }
        })
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

    def addListener(listener: CommandEvent => Unit) : Unit = {
        this.listeners ::= listener
    }

    private def fireCommand(event: CommandEvent) : Unit = {
        for (listener <- this.listeners) {
            listener(event)
        }
    }
}
