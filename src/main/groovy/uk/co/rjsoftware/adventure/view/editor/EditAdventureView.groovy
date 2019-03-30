package uk.co.rjsoftware.adventure.view.editor

import groovy.transform.TypeChecked
import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.stage.Stage
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.model.Room
import uk.co.rjsoftware.adventure.view.AbstractModelDialogView

@TypeChecked
class EditAdventureView extends AbstractModelDialogView {

    @FXML private TextField titleTextField
    @FXML private TextArea introductionTextArea
    @FXML private TextArea waitTextTextArea
    @FXML private TextArea getTextTextArea

    private Adventure adventure

    void init(Parent rootLayout, Stage owner, Adventure adventure) {
        super.init(rootLayout, owner)

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
