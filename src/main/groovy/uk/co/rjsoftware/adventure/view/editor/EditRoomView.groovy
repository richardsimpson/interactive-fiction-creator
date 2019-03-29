package uk.co.rjsoftware.adventure.view.editor

import groovy.transform.TypeChecked
import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.stage.Stage
import uk.co.rjsoftware.adventure.model.Room
import uk.co.rjsoftware.adventure.view.AbstractModelDialogView
import uk.co.rjsoftware.adventure.view.CommandEvent
import uk.co.rjsoftware.adventure.view.CommandListener
import uk.co.rjsoftware.adventure.view.editor.components.RoomComponent

@TypeChecked
class EditRoomView extends AbstractModelDialogView {

    @FXML private TextField nameTextField
    @FXML private TextArea descriptionTextArea

    private Room room
    private List<ChangeListener> changeListeners = new ArrayList<>()

    void init(Parent rootLayout, Stage owner, RoomComponent roomComponent) {
        super.init(rootLayout, owner)

        this.room = roomComponent.getRoom()

        this.nameTextField.setText(room.getName())
        this.descriptionTextArea.setText((room.getDescription()))
    }

    @Override
    protected void save() {
        this.room.setName(this.nameTextField.getText())
        this.room.setDescription(this.descriptionTextArea.getText())
        fireChangeEvent()
        close()
    }

    void addChangeListener(ChangeListener listener) {
        this.changeListeners.add(listener)
    }

    private void fireChangeEvent() {
        for (ChangeListener listener : this.changeListeners) {
            listener.changed()
        }
    }

}
