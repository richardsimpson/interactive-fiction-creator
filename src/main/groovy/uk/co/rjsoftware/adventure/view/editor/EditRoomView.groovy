package uk.co.rjsoftware.adventure.view.editor

import groovy.transform.TypeChecked
import javafx.fxml.FXML
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import uk.co.rjsoftware.adventure.model.Room
import uk.co.rjsoftware.adventure.view.AbstractEditDomainObjectDialogView

@TypeChecked
class EditRoomView extends AbstractEditDomainObjectDialogView<Room> {

    @FXML private TextField nameTextField
    @FXML private TextArea descriptionTextArea

    private Room room

    @Override
    String fxmlLocation() {
        return "../editRoom.fxml"
    }

    void setDomainObject(Room room) {
        this.room = room

        this.nameTextField.setText(room.getName())
        this.descriptionTextArea.setText(room.getDescription())
    }

    void doSave() {
        this.room.setName(this.nameTextField.getText())
        this.room.setDescription(this.descriptionTextArea.getText())
    }

}
