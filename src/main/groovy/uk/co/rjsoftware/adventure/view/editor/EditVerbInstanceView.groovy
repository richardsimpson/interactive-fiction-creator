package uk.co.rjsoftware.adventure.view.editor

import groovy.transform.TypeChecked
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import javafx.util.StringConverter
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.model.CustomVerb
import uk.co.rjsoftware.adventure.view.AbstractEditDomainObjectDialogView
import uk.co.rjsoftware.adventure.view.editor.model.ObservableAdventure
import uk.co.rjsoftware.adventure.view.editor.model.ObservableVerbInstance

@TypeChecked
class EditVerbInstanceView extends AbstractEditDomainObjectDialogView {

    @FXML private ComboBox<CustomVerb> verbComboBox
    @FXML private TextArea scriptTextArea

    private final ObservableAdventure observableAdventure
    private final ObservableVerbInstance verbInstance

    EditVerbInstanceView(ObservableAdventure observableAdventure, ObservableVerbInstance verbInstance) {
        super("../editVerbInstance.fxml")
        this.observableAdventure = observableAdventure
        this.verbInstance = verbInstance
    }

    protected void onShow() {
        this.scriptTextArea.setText(verbInstance.getScript())

        this.verbComboBox.setItems(FXCollections.observableArrayList(this.observableAdventure.getCustomVerbs()))

        this.verbComboBox.setConverter(new StringConverter<CustomVerb>() {
            @Override
            public String toString(CustomVerb verb) {
                if (verb == null){
                    return null
                } else {
                    return verb.getName()
                }
            }

            @Override
            public CustomVerb fromString(String name) {
                return null
            }
        })

        if (this.verbInstance.getId() != null) {
            final CustomVerb verb = this.observableAdventure.getCustomVerbs().find {verb -> verb.getId() == this.verbInstance.getId()}
            this.verbComboBox.getSelectionModel().select(verb)
        }
    }

    protected void doSave() {
        this.verbInstance.setId(this.verbComboBox.getSelectionModel().getSelectedItem().getId())
        this.verbInstance.setScript(this.scriptTextArea.getText())
    }

}
