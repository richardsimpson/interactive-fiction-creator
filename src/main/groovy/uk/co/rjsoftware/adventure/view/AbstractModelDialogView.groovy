package uk.co.rjsoftware.adventure.view

import groovy.transform.TypeChecked
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.stage.Modality
import javafx.stage.Stage
import uk.co.rjsoftware.adventure.model.Room
import uk.co.rjsoftware.adventure.view.editor.ChangeListener

@TypeChecked
abstract class AbstractModelDialogView {

    @FXML private Button cancelButton
    @FXML private Button okButton

    private Stage stage

    private List<ChangeListener> changeListeners = new ArrayList<>()

    @FXML void initialize() {
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            void handle(ActionEvent event) {
                close()
            }
        })

        okButton.setOnAction(new EventHandler<ActionEvent>() {
            void handle(ActionEvent event) {
                save()
                close()
            }
        })
    }

    void init(Parent rootLayout, Stage owner) {
        final Scene scene = new Scene(rootLayout)
        this.stage = new Stage()
        stage.initOwner(owner)
        stage.initModality(Modality.WINDOW_MODAL)
        stage.setResizable(false)
        stage.setScene(scene)
        stage.show()
    }

    private void close() {
        stage.close()
    }

    private void save() {
        doSave()
        fireChangeEvent()
    }

    abstract void doSave()

    void addChangeListener(ChangeListener listener) {
        this.changeListeners.add(listener)
    }

    private void fireChangeEvent() {
        for (ChangeListener listener : this.changeListeners) {
            listener.changed()
        }
    }

}
