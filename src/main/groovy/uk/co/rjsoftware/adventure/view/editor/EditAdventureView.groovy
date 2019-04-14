package uk.co.rjsoftware.adventure.view.editor

import groovy.transform.TypeChecked
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.TableColumn.CellEditEvent
import javafx.util.Callback
import javafx.util.StringConverter
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.model.ContentVisibility
import uk.co.rjsoftware.adventure.model.Item
import uk.co.rjsoftware.adventure.view.AbstractDialogView
import uk.co.rjsoftware.adventure.view.editor.model.ObservableAdventure
import uk.co.rjsoftware.adventure.view.editor.model.ObservableCustomVerb

import static uk.co.rjsoftware.adventure.view.ModalResult.mrOk

@TypeChecked
class EditAdventureView extends AbstractDialogView {

    @FXML private TextField titleTextField
    @FXML private TextArea introductionTextArea
    @FXML private TextArea waitTextTextArea
    @FXML private TextArea getTextTextArea
    @FXML private ComboBox playerComboBox

    @FXML private TableView<ObservableCustomVerb> verbsTableView
    @FXML private TableColumn<ObservableCustomVerb, String> nameColumn
    @FXML private TableColumn<ObservableCustomVerb, String> displayNameColumn
    @FXML private TableColumn<ObservableCustomVerb, String> synonymsColumn

    @FXML private Button addButton
    @FXML private Button editButton
    @FXML private Button deleteButton


    private final Adventure adventure
    private final ObservableAdventure observableAdventure

    EditAdventureView(Adventure adventure, ObservableAdventure observableAdventure) {
        super("../editAdventure.fxml")
        this.adventure = adventure
        this.observableAdventure = observableAdventure

    }

    // TODO: Add ability to:
    // - Add / Delete rooms
    // - Move Items
    //
    // To do this, Add a top bar to the Room and Item views, with 'Move' and "Delete' buttons.
    // Add a bar to the adventure view for consistency, but without the buttons
    // Add a Toolbar with 'Add Room' button
    // Bonus points, add a play button to the Toolbar


    protected void onShow() {
        this.titleTextField.textProperty().bindBidirectional(this.observableAdventure.titleProperty())
        this.introductionTextArea.textProperty().bindBidirectional(this.observableAdventure.introductionProperty())
        this.waitTextTextArea.textProperty().bindBidirectional(this.observableAdventure.waitTextProperty())
        this.getTextTextArea.textProperty().bindBidirectional(this.observableAdventure.getTextProperty())

        final ObservableList<Item> observableAllItems = FXCollections.observableArrayList(this.adventure.getAllItems())
        this.playerComboBox.setItems(observableAllItems)
        this.playerComboBox.setConverter(new StringConverter<Item>() {
            @Override
            public String toString(Item item) {
                if (item == null){
                    return null
                } else {
                    return item.getName()
                }
            }

            @Override
            public Item fromString(String name) {
                return null
            }
        })

        this.playerComboBox.valueProperty().bindBidirectional(this.observableAdventure.playerProperty())

        // Setup the table view of the custom verbs

        verbsTableView.setEditable(true)

        // to support the tab key saving the changes (when editing in place)
        Callback<TableColumn, TableCell> cellFactory = new Callback<TableColumn, TableCell>() {
            TableCell call(TableColumn p) {
                return new EditingCell()
            }
        };

        // TODO: Where else can we use this closure syntax instead of an anonymous class?
        // to have the table view listening for changes in the data.
        nameColumn.setCellValueFactory({ cellData -> cellData.getValue().nameProperty()})
        displayNameColumn.setCellValueFactory({ cellData -> cellData.getValue().displayNameProperty()})
        synonymsColumn.setCellValueFactory({cellData -> cellData.getValue().displayedSynonymsProperty()})

        // to enable in-place editing
        nameColumn.setCellFactory(cellFactory);
        nameColumn.setOnEditCommit(
                new EventHandler<CellEditEvent<ObservableCustomVerb, String>>() {
                    @Override
                    void handle(CellEditEvent<ObservableCustomVerb, String> t) {
                        ((ObservableCustomVerb) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setName(t.getNewValue())
                    }
                }
        )

        // to enable in-place editing
        displayNameColumn.setCellFactory(cellFactory);
        displayNameColumn.setOnEditCommit(
                new EventHandler<CellEditEvent<ObservableCustomVerb, String>>() {
                    @Override
                    void handle(CellEditEvent<ObservableCustomVerb, String> t) {
                        ((ObservableCustomVerb) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setDisplayName(t.getNewValue())
                    }
                }
        )

        verbsTableView.setItems(this.observableAdventure.getObservableCustomVerbs())

        // wire up the remaining buttons
        addButton.setOnAction(this.&addButtonClick)
        editButton.setOnAction(this.&editButtonClick)
        deleteButton.setOnAction(this.&deleteButtonClick)
    }

    private void addButtonClick(ActionEvent event) {
        final ObservableCustomVerb newObservableCustomVerb = new ObservableCustomVerb()
        EditVerbView editVerbView = new EditVerbView(newObservableCustomVerb)
        if (editVerbView.showModal(getStage()) == mrOk) {
            this.verbsTableView.getItems().add(newObservableCustomVerb)
        }
    }

    private void editButtonClick(ActionEvent event) {
        EditVerbView editVerbView = new EditVerbView(this.verbsTableView.getSelectionModel().getSelectedItem())
        editVerbView.showModal(getStage())
    }

    private void deleteButtonClick(ActionEvent event) {
        this.verbsTableView.getItems().remove(this.verbsTableView.getSelectionModel().getSelectedIndex())
    }

}

class EditingCell extends TableCell<ObservableCustomVerb, String> {

    private TextField textField;

    public EditingCell() {
    }

    @Override
    public void startEdit() {
        if (!isEmpty()) {
            super.startEdit();
            createTextField();
            setText(null);
            setGraphic(textField);
            textField.selectAll();
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();

        setText((String) getItem());
        setGraphic(null);
    }

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(getString());
                }
                setText(null);
                setGraphic(textField);
            } else {
                setText(getString());
                setGraphic(null);
            }
        }
    }

    private void createTextField() {
        textField = new TextField(getString());
        textField.setMinWidth(this.getWidth() - this.getGraphicTextGap()* 2);
        textField.focusedProperty().addListener(new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0,
                                Boolean arg1, Boolean arg2) {
                if (!arg2) {
                    commitEdit(textField.getText());
                }
            }
        });
    }

    private String getString() {
        return getItem() == null ? "" : getItem().toString();
    }
}