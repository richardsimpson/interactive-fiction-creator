package uk.co.rjsoftware.adventure.view

import groovy.transform.TypeChecked
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Button
import uk.co.rjsoftware.adventure.view.editor.ChangeListener

import static uk.co.rjsoftware.adventure.view.ModalResult.mrCancel
import static uk.co.rjsoftware.adventure.view.ModalResult.mrOk

@TypeChecked
abstract class AbstractEditDomainObjectDialogView extends AbstractDialogView {

    @FXML private Button cancelButton
    @FXML private Button okButton

    private List<ChangeListener> changeListeners = new ArrayList<>()

    AbstractEditDomainObjectDialogView(String fxmlLocation) {
        super(fxmlLocation)
        setResizable(false)
    }

    @FXML void initialize() {
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            void handle(ActionEvent event) {
                modalResult = mrCancel
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

    private void save() {
        doSave()
        fireChangeEvent()
        modalResult = mrOk
    }

    abstract protected void doSave()

    void addChangeListener(ChangeListener listener) {
        this.changeListeners.add(listener)
    }

    private void fireChangeEvent() {
        for (ChangeListener listener : this.changeListeners) {
            listener.changed()
        }
    }

}
