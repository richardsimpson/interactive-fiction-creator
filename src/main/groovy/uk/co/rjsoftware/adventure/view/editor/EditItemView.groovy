package uk.co.rjsoftware.adventure.view.editor

import groovy.transform.TypeChecked
import javafx.beans.property.adapter.JavaBeanBooleanProperty
import javafx.beans.property.adapter.JavaBeanBooleanPropertyBuilder
import javafx.beans.property.adapter.JavaBeanStringProperty
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.model.Item
import uk.co.rjsoftware.adventure.view.AbstractDialogView

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

    private final Item item
    private final ObservableItem observableItem

    EditItemView(Item item) {
        super("../editItem.fxml")
        this.item = item
        this.observableItem = new ObservableItem(item)
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

@TypeChecked
class ObservableItem {

    private final Item item
    // General Tab
    private final JavaBeanStringProperty name
    private final JavaBeanStringProperty displayName
    private final JavaBeanBooleanProperty visible
    private final JavaBeanBooleanProperty scenery
    private final JavaBeanBooleanProperty gettable
    private final JavaBeanBooleanProperty droppable
    private final JavaBeanStringProperty description
    // Features Tab
    private final JavaBeanBooleanProperty switchable
    private final JavaBeanBooleanProperty container
    private final JavaBeanBooleanProperty edible
    // Switchable Tab
    private final JavaBeanBooleanProperty switchedOn
    private final JavaBeanStringProperty switchOnMessage
    private final JavaBeanStringProperty switchOffMessage
    private final JavaBeanStringProperty extraDescriptionWhenSwitchedOn
    private final JavaBeanStringProperty extraDescriptionWhenSwitchedOff
    // Container Tab
    private final JavaBeanBooleanProperty openable
    private final JavaBeanBooleanProperty closeable
    private final JavaBeanBooleanProperty open
//        this.contentVisibilityComboBox.setSelected(item.getContentVisibility())
    private final JavaBeanStringProperty openMessage
    private final JavaBeanStringProperty closeMessage
    private final JavaBeanStringProperty onOpenScript
    private final JavaBeanStringProperty onCloseScript
    // Edible Tab
    private final JavaBeanStringProperty eatMessage
    private final JavaBeanStringProperty onEatScript

    ObservableItem(Item item) {
        this.item = item

        // General Tab
        name = new JavaBeanStringPropertyBuilder().bean(item).name("name").build();
        displayName = new JavaBeanStringPropertyBuilder().bean(item).name("displayName").build();
        visible = new JavaBeanBooleanPropertyBuilder().bean(item).name("visible").build();
        scenery = new JavaBeanBooleanPropertyBuilder().bean(item).name("scenery").build();
        gettable = new JavaBeanBooleanPropertyBuilder().bean(item).name("gettable").build();
        droppable = new JavaBeanBooleanPropertyBuilder().bean(item).name("droppable").build();
        description = new JavaBeanStringPropertyBuilder().bean(item).name("description").build();
        // Features Tab
        switchable = new JavaBeanBooleanPropertyBuilder().bean(item).name("switchable").build();
        container = new JavaBeanBooleanPropertyBuilder().bean(item).name("container").build();
        edible = new JavaBeanBooleanPropertyBuilder().bean(item).name("edible").build();
        // Switchable Tab
        switchedOn = new JavaBeanBooleanPropertyBuilder().bean(item).name("switchedOn").build();
        switchOnMessage = new JavaBeanStringPropertyBuilder().bean(item).name("switchOnMessage").build();
        switchOffMessage = new JavaBeanStringPropertyBuilder().bean(item).name("switchOffMessage").build();
        extraDescriptionWhenSwitchedOn = new JavaBeanStringPropertyBuilder().bean(item).name("extraDescriptionWhenSwitchedOn").build();
        extraDescriptionWhenSwitchedOff = new JavaBeanStringPropertyBuilder().bean(item).name("extraDescriptionWhenSwitchedOff").build();
        // Container Tab
        openable = new JavaBeanBooleanPropertyBuilder().bean(item).name("openable").build();
        closeable = new JavaBeanBooleanPropertyBuilder().bean(item).name("closeable").build();
        open = new JavaBeanBooleanPropertyBuilder().bean(item).name("open").build();
//        this.contentVisibilityComboBox.setSelected(item.getContentVisibility())
        openMessage = new JavaBeanStringPropertyBuilder().bean(item).name("openMessage").build();
        closeMessage = new JavaBeanStringPropertyBuilder().bean(item).name("closeMessage").build();
        onOpenScript = new JavaBeanStringPropertyBuilder().bean(item).name("onOpenScript").build();
        onCloseScript = new JavaBeanStringPropertyBuilder().bean(item).name("onCloseScript").build();
        // Edible Tab
        eatMessage = new JavaBeanStringPropertyBuilder().bean(item).name("eatMessage").build();
        onEatScript = new JavaBeanStringPropertyBuilder().bean(item).name("onEatScript").build();

    }

    JavaBeanStringProperty nameProperty() {
        this.name
    }
    JavaBeanStringProperty displayNameProperty() {
        this.displayName
    }
    JavaBeanBooleanProperty visibleProperty() {
        this.visible
    }
    JavaBeanBooleanProperty sceneryProperty() {
        this.scenery
    }
    JavaBeanBooleanProperty gettableProperty() {
        this.gettable
    }
    JavaBeanBooleanProperty droppableProperty() {
        this.droppable
    }
    JavaBeanStringProperty descriptionProperty() {
        this.description
    }
    JavaBeanBooleanProperty switchableProperty() {
        this.switchable
    }
    JavaBeanBooleanProperty containerProperty() {
        this.container
    }
    JavaBeanBooleanProperty edibleProperty() {
        this.edible
    }
    JavaBeanBooleanProperty switchedOnProperty() {
        this.switchedOn
    }
    JavaBeanStringProperty switchOnMessageProperty() {
        this.switchOnMessage
    }
    JavaBeanStringProperty switchOffMessageProperty() {
        this.switchOffMessage
    }
    JavaBeanStringProperty extraDescriptionWhenSwitchedOnProperty() {
        this.extraDescriptionWhenSwitchedOn
    }
    JavaBeanStringProperty extraDescriptionWhenSwitchedOffProperty() {
        this.extraDescriptionWhenSwitchedOff
    }
    JavaBeanBooleanProperty openableProperty() {
        this.openable
    }
    JavaBeanBooleanProperty closeableProperty() {
        this.closeable
    }
    JavaBeanBooleanProperty openProperty() {
        this.open
    }
    JavaBeanStringProperty openMessageProperty() {
        this.openMessage
    }
    JavaBeanStringProperty closeMessageProperty() {
        this.closeMessage
    }
    JavaBeanStringProperty onOpenScriptProperty() {
        this.onOpenScript
    }
    JavaBeanStringProperty onCloseScriptProperty() {
        this.onCloseScript
    }
    JavaBeanStringProperty eatMessageProperty() {
        this.eatMessage
    }
    JavaBeanStringProperty onEatScriptProperty() {
        this.onEatScript
    }
}

