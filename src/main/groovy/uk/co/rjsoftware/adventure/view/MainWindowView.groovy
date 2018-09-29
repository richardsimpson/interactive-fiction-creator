package uk.co.rjsoftware.adventure.view

import groovy.transform.TypeChecked
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.MenuItem
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.FileChooser
import javafx.stage.FileChooser.ExtensionFilter
import javafx.stage.Stage

import java.nio.file.Paths

@TypeChecked
class MainWindowView implements MainWindow {

    @FXML private TextArea outputTextArea = null

    @FXML private TextField inputTextField = null

    @FXML private Button enterButton = null

    @FXML private MenuItem loadMenuItem = null

    @FXML void initialize() {
    }

    private Stage primaryStage = null

    private List<CommandListener> commandListeners = new ArrayList()
    private List<LoadListener> loadListeners = new ArrayList()

    void init(Stage primaryStage) {
        this.primaryStage = primaryStage
        this.outputTextArea.setText("")

        inputTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    submitCommand()
                }
            }
        })
        enterButton.setOnAction(new EventHandler<ActionEvent>() {
            void handle(ActionEvent event) {
                submitCommand()
            }
        })
        inputTextField.requestFocus()

        loadMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            void handle(ActionEvent event) {
                loadAdventureInternal()
            }
        })
    }

    private void loadAdventureInternal() {
        FileChooser fileChooser = new FileChooser()
        fileChooser.setTitle("Open Adventure")
        fileChooser.setInitialDirectory(new File(Paths.get("").toAbsolutePath().toString()))
        fileChooser.setSelectedExtensionFilter(new ExtensionFilter("Adventures", "*.groovy"))

        File file = fileChooser.showOpenDialog(this.primaryStage)
        if (file != null) {
            fireLoadCommand(new LoadEvent(file))
        }
    }


    void say(String outputText) {
        this.outputTextArea.appendText(outputText + System.lineSeparator())
    }

    void sayWithoutLineBreak(String outputText) {
        this.outputTextArea.appendText(outputText)
    }

    private void submitCommand() {
        say("> " + this.inputTextField.getText())
        processCommand(this.inputTextField.getText())
        this.inputTextField.setText("")
    }

    private void processCommand(String command) {
        fireCommand(new CommandEvent(command))
    }

    void addCommandListener(CommandListener listener) {
        this.commandListeners.add(listener)
    }

    private void fireCommand(CommandEvent event) {
        for (CommandListener listener : this.commandListeners) {
            listener.callback(event)
        }
    }

    void addLoadListener(LoadListener listener) {
        this.loadListeners.add(listener)
    }

    private void fireLoadCommand(LoadEvent event) {
        for (LoadListener listener : this.loadListeners) {
            listener.callback(event)
        }
    }

    void loadAdventure(String title, String introduction) {
        this.outputTextArea.setText("")
        this.primaryStage.setTitle(title)

        if (introduction.length() > 0) {
            say(introduction)
            say("")
        }
    }
}
