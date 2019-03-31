package uk.co.rjsoftware.adventure.view.editor

import groovy.transform.TypeChecked
import javafx.fxml.FXML
import javafx.scene.control.TextField
import uk.co.rjsoftware.adventure.view.AbstractEditDomainObjectDialogView

@TypeChecked
class EditVerbView extends AbstractEditDomainObjectDialogView<CustomVerbView> {

    @FXML private TextField nameTextField
    @FXML private TextField friendlyNameTextField

    private CustomVerbView customVerb

    EditVerbView() {
        super("../editVerb.fxml")
    }

    void setDomainObject(CustomVerbView customVerb) {
        this.customVerb = customVerb

        this.nameTextField.setText(customVerb.getId())
        this.friendlyNameTextField.setText(customVerb.getFriendlyName())
    }

    void doSave() {
        this.customVerb.setId(this.nameTextField.getText())
        this.customVerb.setFriendlyName(this.friendlyNameTextField.getText())
    }

}
