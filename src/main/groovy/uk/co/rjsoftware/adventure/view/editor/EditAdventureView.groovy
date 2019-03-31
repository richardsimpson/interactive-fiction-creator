package uk.co.rjsoftware.adventure.view.editor

import groovy.transform.TypeChecked
import javafx.fxml.FXML
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.view.AbstractEditDomainObjectDialogView

@TypeChecked
class EditAdventureView extends AbstractEditDomainObjectDialogView<Adventure> {

    @FXML private TextField titleTextField
    @FXML private TextArea introductionTextArea
    @FXML private TextArea waitTextTextArea
    @FXML private TextArea getTextTextArea

    private Adventure adventure

    @Override
    String fxmlLocation() {
        return "../editAdventure.fxml"
    }

    void setDomainObject(Adventure adventure) {
        this.adventure = adventure

        this.titleTextField.setText(adventure.getTitle())
        this.introductionTextArea.setText(adventure.getIntroduction())
        this.waitTextTextArea.setText(adventure.getWaitText())
        this.getTextTextArea.setText(adventure.getGetText())
    }

    void doSave() {
        this.adventure.setTitle(this.titleTextField.getText())
        this.adventure.setIntroduction(this.introductionTextArea.getText())
        this.adventure.setWaitText(this.waitTextTextArea.getText())
        this.adventure.setGetText(this.getTextTextArea.getText())
    }

}
