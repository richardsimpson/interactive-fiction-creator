package uk.co.rjsoftware.adventure.view.editor

import groovy.transform.TypeChecked
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.util.StringConverter
import uk.co.rjsoftware.adventure.model.ContentVisibility
import uk.co.rjsoftware.adventure.model.CustomVerb
import uk.co.rjsoftware.adventure.view.AbstractDialogView
import uk.co.rjsoftware.adventure.view.editor.model.ObservableItem

@TypeChecked
class EditItemView extends AbstractDialogView {

    // General Tab
    @FXML private CheckBox descriptionScriptEnabledCheckBox
    @FXML private AnchorPane descriptionAnchorPane
    @FXML private AnchorPane descriptionScriptAnchorPane
    @FXML private TextField nameTextField
    @FXML private TextField displayNameTextField
    @FXML private CheckBox visibleCheckBox
    @FXML private CheckBox sceneryCheckBox
    @FXML private CheckBox gettableCheckBox
    @FXML private CheckBox droppableCheckBox
    @FXML private TextArea descriptionTextArea
    @FXML private TextArea descriptionScriptTextArea

    // Items Tab
    @FXML private Button addItemButton
    @FXML private Button editItemButton
    @FXML private Button deleteItemButton

    @FXML private TableView<ObservableItem> itemsTableView
    @FXML private TableColumn<ObservableItem, String> nameColumn
    @FXML private TableColumn<ObservableItem, String> descriptionColumn

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
    @FXML private ComboBox<ContentVisibility> contentVisibilityComboBox
    @FXML private TextArea openMessageTextArea
    @FXML private TextArea closeMessageTextArea
    @FXML private TextArea onOpenScriptTextArea
    @FXML private TextArea onCloseScriptTextArea

    // Edible Tab
    @FXML private TextArea eatMessageTextArea
    @FXML private TextArea onEatScriptTextArea

    private AbstractDialogView view
    private BorderPane parent
    private final ObservableItem observableItem

    EditItemView(ObservableItem observableItem, BorderPane parent) {
        super("../editItem.fxml")
        this.parent = parent
        this.observableItem = observableItem
    }

    // TODO: Add ability to edit:
    //       verbs
    //       synonyms

    private descriptionScriptEnabledOnChange(boolean newValue) {
        if (newValue) {
            descriptionScriptAnchorPane.setVisible(true)
            descriptionAnchorPane.setVisible(false)
        }
        else {
            descriptionAnchorPane.setVisible(true)
            descriptionScriptAnchorPane.setVisible(false)
        }
    }

    protected void onShow() {
        // General Tab
        this.nameTextField.textProperty().bindBidirectional(this.observableItem.nameProperty())
        this.displayNameTextField.textProperty().bindBidirectional(this.observableItem.displayNameProperty())
        this.visibleCheckBox.selectedProperty().bindBidirectional(this.observableItem.visibleProperty())
        this.sceneryCheckBox.selectedProperty().bindBidirectional(this.observableItem.sceneryProperty())
        this.gettableCheckBox.selectedProperty().bindBidirectional(this.observableItem.gettableProperty())
        this.droppableCheckBox.selectedProperty().bindBidirectional(this.observableItem.droppableProperty())

        this.descriptionScriptEnabledCheckBox.selectedProperty().bindBidirectional(this.observableItem.descriptionScriptEnabledProperty())
        this.descriptionTextArea.textProperty().bindBidirectional(this.observableItem.descriptionProperty())
        this.descriptionScriptTextArea.textProperty().bindBidirectional(this.observableItem.descriptionScriptProperty())

        // Switch between description and the script anchor panes when the check box is clicked.
        this.descriptionScriptEnabledCheckBox.selectedProperty().addListener(new javafx.beans.value.ChangeListener<Boolean>() {
            @Override
            void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                descriptionScriptEnabledOnChange(newValue)
            }
        })

        // setup the anchor panes based on the initial value of the script flag
        descriptionScriptEnabledOnChange(this.descriptionScriptEnabledCheckBox.isSelected())

        // Items Tab

        // Setup the table view of the items

        itemsTableView.setEditable(true)

        nameColumn.setCellValueFactory({ cellData -> cellData.getValue().nameProperty()})
        descriptionColumn.setCellValueFactory({ cellData -> cellData.getValue().descriptionProperty()})

        itemsTableView.setItems(this.observableItem.getObservableItems())

        addItemButton.setOnAction(this.&addItemButtonClick)
        editItemButton.setOnAction(this.&editItemButtonClick)
        deleteItemButton.setOnAction(this.&deleteItemButtonClick)

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

        this.contentVisibilityComboBox.setItems(FXCollections.observableArrayList(ContentVisibility.values()))

        this.contentVisibilityComboBox.setConverter(new StringConverter<ContentVisibility>() {
            @Override
            public String toString(ContentVisibility verb) {
                if (verb == null){
                    return null;
                } else {
                    return verb.getFriendlyName();
                }
            }

            @Override
            public ContentVisibility fromString(String name) {
                return null;
            }
        });

        this.contentVisibilityComboBox.valueProperty().bindBidirectional(this.observableItem.contentVisibilityProperty())

        this.openMessageTextArea.textProperty().bindBidirectional(this.observableItem.openMessageProperty())
        this.closeMessageTextArea.textProperty().bindBidirectional(this.observableItem.closeMessageProperty())
        this.onOpenScriptTextArea.textProperty().bindBidirectional(this.observableItem.onOpenScriptProperty())
        this.onCloseScriptTextArea.textProperty().bindBidirectional(this.observableItem.onCloseScriptProperty())

        // Edible Tab
        this.eatMessageTextArea.textProperty().bindBidirectional(this.observableItem.eatMessageProperty())
        this.onEatScriptTextArea.textProperty().bindBidirectional(this.observableItem.onEatScriptProperty())
    }

    private void addItemButtonClick(ActionEvent event) {
        final ObservableItem newObservableItem = new ObservableItem()
        this.itemsTableView.getItems().add(newObservableItem)
        this.view = new EditItemView(newObservableItem, this.parent)
        this.view.show(this.parent)
    }

    private void editItemButtonClick(ActionEvent event) {
        this.view = new EditItemView(this.itemsTableView.getSelectionModel().getSelectedItem(), this.parent)
        this.view.show(this.parent)
    }

    private void deleteItemButtonClick(ActionEvent event) {
        this.itemsTableView.getItems().remove(this.itemsTableView.getSelectionModel().getSelectedIndex())
    }

}
