package uk.co.rjsoftware.adventure.view.editor

import groovy.transform.TypeChecked
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import uk.co.rjsoftware.adventure.model.Item
import uk.co.rjsoftware.adventure.view.AbstractEditDomainObjectDialogView

@TypeChecked
class EditItemView extends AbstractEditDomainObjectDialogView {

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

    private final Item item

    EditItemView(Item item) {
        super("../editItem.fxml")
        this.item = item
    }

    // TODO: Add ability to edit:
    //       verbs
    //       items
    //       descriptionScript
    //       contentVisibility (not currently populating the combo box)

    protected void onShow() {
        // General Tab
        this.nameTextField.setText(item.getName())
        this.displayNameTextField.setText(item.getDisplayName())
        this.visibleCheckBox.setSelected(item.isVisible())
        this.sceneryCheckBox.setSelected(item.isScenery())
        this.gettableCheckBox.setSelected(item.isGettable())
        this.droppableCheckBox.setSelected(item.isDroppable())
        this.descriptionTextArea.setText(item.getDescription())

        // Features Tab
        this.switchableCheckBox.setSelected(item.isSwitchable())
        this.containerCheckBox.setSelected(item.isContainer())
        this.edibleCheckBox.setSelected(item.isEdible())

        // Switchable Tab
        this.switchedOnCheckBox.setSelected(item.isSwitchedOn())
        this.switchOnMessageTextArea.setText(item.getSwitchOnMessage())
        this.switchOffMessageTextArea.setText(item.getSwitchOffMessage())
        this.extraDescriptionWhenSwitchedOnTextArea.setText(item.getExtraDescriptionWhenSwitchedOn())
        this.extraDescriptionWhenSwitchedOffTextArea.setText(item.getExtraMessageWhenSwitchedOff())

        // Container Tab
        this.openableCheckBox.setSelected(item.isOpenable())
        this.closeableCheckBox.setSelected(item.isCloseable())
        this.openCheckBox.setSelected(item.isOpen())
//        this.contentVisibilityComboBox.setSelected(item.getContentVisibility())
        this.openMessageTextArea.setText(item.getOpenMessage())
        this.closeMessageTextArea.setText(item.getCloseMessage())
        this.onOpenScriptTextArea.setText(item.getOnOpenScript())
        this.onCloseScriptTextArea.setText(item.getOnCloseScript())

        // Edible Tab
        this.eatMessageTextArea.setText(item.getEatMessage())
        this.onEatScriptTextArea.setText(item.getOnEatScript())
    }

    void doSave() {
        // General Tab
        this.item.setName(this.nameTextField.getText())
        this.item.setDisplayName(this.displayNameTextField.getText())
        this.item.setVisible(this.visibleCheckBox.isSelected())
        this.item.setScenery(this.sceneryCheckBox.isSelected())
        this.item.setGettable(this.gettableCheckBox.isSelected())
        this.item.setDroppable(this.droppableCheckBox.isSelected())
        this.item.setDescription(this.descriptionTextArea.getText())

        // Features Tab
        this.item.setSwitchable(switchableCheckBox.isSelected())
        this.item.setContainer(containerCheckBox.isSelected())
        this.item.setEdible(edibleCheckBox.isSelected())

        // Switchable Tab
        this.item.setSwitchedOn(switchedOnCheckBox.isSelected())
        this.item.setSwitchOnMessage(this.switchOnMessageTextArea.getText())
        this.item.setSwitchOffMessage(this.switchOffMessageTextArea.getText())
        this.item.setExtraDescriptionWhenSwitchedOn(this.extraDescriptionWhenSwitchedOnTextArea.getText())
        this.item.setExtraMessageWhenSwitchedOff(this.extraDescriptionWhenSwitchedOffTextArea.getText())

        // Container Tab
        this.item.setOpenable(openableCheckBox.isSelected())
        this.item.setCloseable(closeableCheckBox.isSelected())
        this.item.setOpen(openCheckBox.isSelected())
//        this.item.setContentVisibility(...)
        this.item.setOpenMessage(this.openMessageTextArea.getText())
        this.item.setCloseMessage(this.closeMessageTextArea.getText())
        this.item.setOnOpenScript(this.onOpenScriptTextArea.getText())
        this.item.setOnCloseScript(this.onCloseScriptTextArea.getText())

        // Edible Tab
        this.item.setEatMessage(this.eatMessageTextArea.getText())
        this.item.setOnEatScript(this.onEatScriptTextArea.getText())
    }

}
