package uk.co.rjsoftware.adventure.view.editor

import groovy.transform.TypeChecked
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.control.cell.TextFieldListCell
import uk.co.rjsoftware.adventure.view.AbstractEditDomainObjectDialogView

@TypeChecked
class EditVerbView extends AbstractEditDomainObjectDialogView {

    @FXML private TextField nameTextField
    @FXML private TextField displayNameTextField
    @FXML private TextField newSynonymTextField
    @FXML private ListView<String> synonymsListView

    @FXML private Button addButton
    @FXML private Button deleteButton

    private final ObservableCustomVerb customVerb

    EditVerbView(ObservableCustomVerb customVerb) {
        super("../editVerb.fxml")
        this.customVerb = customVerb
    }

    protected void onShow() {
        this.nameTextField.setText(customVerb.getName())
        this.displayNameTextField.setText(customVerb.getDisplayName())
        this.synonymsListView.setItems(customVerb.getSynonyms())
        this.synonymsListView.setEditable(true)

        // to enable in-place editing
        synonymsListView.setCellFactory(TextFieldListCell.forListView());
        synonymsListView.setOnEditCommit(
                new EventHandler<ListView.EditEvent<String>>() {
                    @Override
                    void handle(ListView.EditEvent<String> t) {
                        t.getSource().getItems().set(t.getIndex(), t.getNewValue())
                    }
                }
        )

        // wire up the remaining buttons
        addButton.setOnAction(this.&addButtonClick)
        deleteButton.setOnAction(this.&deleteButtonClick)

    }

    private void addButtonClick(ActionEvent event) {
        this.synonymsListView.getItems().add(this.newSynonymTextField.getText())
    }

    private void deleteButtonClick(ActionEvent event) {
        this.synonymsListView.getItems().remove(this.synonymsListView.getSelectionModel().getSelectedIndex())
    }

    void doSave() {
        this.customVerb.setName(this.nameTextField.getText())
        this.customVerb.setDisplayName(this.displayNameTextField.getText())
        this.customVerb.setSynonyms(this.synonymsListView.getItems())
    }

}
