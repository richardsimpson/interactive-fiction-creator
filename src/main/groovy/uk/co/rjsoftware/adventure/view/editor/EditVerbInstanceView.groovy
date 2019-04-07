package uk.co.rjsoftware.adventure.view.editor

import groovy.transform.TypeChecked
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.cell.TextFieldListCell
import javafx.util.Callback
import javafx.util.StringConverter
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.model.CustomVerb
import uk.co.rjsoftware.adventure.view.AbstractEditDomainObjectDialogView

@TypeChecked
class EditVerbInstanceView extends AbstractEditDomainObjectDialogView {

    @FXML private ComboBox<CustomVerb> verbComboBox
    @FXML private TextArea scriptTextArea

    private final Adventure adventure
    private final ObservableVerbInstance verbInstance

    EditVerbInstanceView(Adventure adventure, ObservableVerbInstance verbInstance) {
        super("../editVerbInstance.fxml")
        this.adventure = adventure
        this.verbInstance = verbInstance
    }

    protected void onShow() {
        this.scriptTextArea.setText(verbInstance.getScript())

        this.verbComboBox.setItems(FXCollections.observableArrayList(this.adventure.getCustomVerbs()))

        this.verbComboBox.setConverter(new StringConverter<CustomVerb>() {
            @Override
            public String toString(CustomVerb verb) {
                if (verb == null){
                    return null;
                } else {
                    return verb.getName();
                }
            }

            @Override
            public CustomVerb fromString(String name) {
                return null;
            }
        });

        if (this.verbInstance.getName() != null && this.verbInstance.getName() != "") {
            final CustomVerb verb = this.adventure.getVerbByName(this.verbInstance.getName())
            this.verbComboBox.getSelectionModel().select(verb)
        }
    }

    void doSave() {
        this.verbInstance.setId(this.verbComboBox.getSelectionModel().getSelectedItem().getId())
        this.verbInstance.setName(this.verbComboBox.getSelectionModel().getSelectedItem().getName())
        this.verbInstance.setScript(this.scriptTextArea.getText())
    }

}
