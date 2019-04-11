package uk.co.rjsoftware.adventure.view.editor

import groovy.transform.TypeChecked
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import uk.co.rjsoftware.adventure.view.AbstractDialogView
import uk.co.rjsoftware.adventure.view.editor.model.ObservableItem

@TypeChecked
class EditItemView extends AbstractDialogView {

    // General Tab
    @FXML private TextField nameTextField
    @FXML private TextField displayNameTextField
    @FXML private CheckBox visibleCheckBox
    @FXML private CheckBox sceneryCheckBox
    @FXML private CheckBox gettableCheckBox
    @FXML private CheckBox droppableCheckBox
    @FXML private TextArea descriptionTextArea

    // Features Tab
    @FXML private CheckBox switchableCheckBox
    @FXML private CheckBox containerCheckBox
    @FXML private CheckBox edibleCheckBox

    // Switchable Tab
    @FXML private CheckBox switchedOnCheckBox
    @FXML private TextArea switchOnMessageTextArea
    @FXML private TextArea switchOffMessageTextArea
    @FXML private TextArea extraDescriptionWhenSwitchedOnTextArea
    @FXML private TextArea extraDescriptionWhenSwitchedOffTextArea

    // Container Tab
    @FXML private CheckBox openableCheckBox
    @FXML private CheckBox closeableCheckBox
    @FXML private CheckBox openCheckBox
    @FXML private ComboBox contentVisibilityComboBox
    @FXML private TextArea openMessageTextArea
    @FXML private TextArea closeMessageTextArea
    @FXML private TextArea onOpenScriptTextArea
    @FXML private TextArea onCloseScriptTextArea

    // Edible Tab
    @FXML private TextArea eatMessageTextArea
    @FXML private TextArea onEatScriptTextArea

    private final ObservableItem observableItem

    EditItemView(ObservableItem observableItem) {
        super("../editItem.fxml")
        this.observableItem = observableItem
    }

    // TODO: Add ability to edit:
    //       verbs
    //       items
    //       descriptionScript
    //       contentVisibility (not currently populating the combo box)

    protected void onShow() {
        // General Tab
        this.nameTextField.textProperty().bindBidirectional(this.observableItem.nameProperty())
        this.displayNameTextField.textProperty().bindBidirectional(this.observableItem.displayNameProperty())
        this.visibleCheckBox.selectedProperty().bindBidirectional(this.observableItem.visibleProperty())
        this.sceneryCheckBox.selectedProperty().bindBidirectional(this.observableItem.sceneryProperty())
        this.gettableCheckBox.selectedProperty().bindBidirectional(this.observableItem.gettableProperty())
        this.droppableCheckBox.selectedProperty().bindBidirectional(this.observableItem.droppableProperty())
        this.descriptionTextArea.textProperty().bindBidirectional(this.observableItem.descriptionProperty())

        // Features Tab
        this.switchableCheckBox.selectedProperty().bindBidirectional(this.observableItem.switchableProperty())
        this.containerCheckBox.selectedProperty().bindBidirectional(this.observableItem.containerProperty())
        this.edibleCheckBox.selectedProperty().bindBidirectional(this.observableItem.edibleProperty())

        // Switchable Tab
        this.switchedOnCheckBox.selectedProperty().bindBidirectional(this.observableItem.switchedOnProperty())
        this.switchOnMessageTextArea.textProperty().bindBidirectional(this.observableItem.switchOnMessageProperty())
        this.switchOffMessageTextArea.textProperty().bindBidirectional(this.observableItem.switchOffMessageProperty())
        this.extraDescriptionWhenSwitchedOnTextArea.textProperty().bindBidirectional(this.observableItem.extraDescriptionWhenSwitchedOnProperty())
        this.extraDescriptionWhenSwitchedOffTextArea.textProperty().bindBidirectional(this.observableItem.extraDescriptionWhenSwitchedOffProperty())

        // Container Tab
        this.openableCheckBox.selectedProperty().bindBidirectional(this.observableItem.openableProperty())
        this.closeableCheckBox.selectedProperty().bindBidirectional(this.observableItem.closeableProperty())
        this.openCheckBox.selectedProperty().bindBidirectional(this.observableItem.openProperty())
//        this.contentVisibilityComboBox.setSelected(item.getContentVisibility())
        this.openMessageTextArea.textProperty().bindBidirectional(this.observableItem.openMessageProperty())
        this.closeMessageTextArea.textProperty().bindBidirectional(this.observableItem.closeMessageProperty())
        this.onOpenScriptTextArea.textProperty().bindBidirectional(this.observableItem.onOpenScriptProperty())
        this.onCloseScriptTextArea.textProperty().bindBidirectional(this.observableItem.onCloseScriptProperty())

        // Edible Tab
        this.eatMessageTextArea.textProperty().bindBidirectional(this.observableItem.eatMessageProperty())
        this.onEatScriptTextArea.textProperty().bindBidirectional(this.observableItem.onEatScriptProperty())
    }

}
