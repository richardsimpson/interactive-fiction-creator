package uk.co.rjsoftware.adventure.view.editor

import groovy.transform.TypeChecked
import javafx.beans.Observable
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.TableColumn.CellEditEvent
import javafx.scene.control.TableView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
import javafx.util.Callback
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.view.AbstractEditDomainObjectDialogView

import java.util.stream.Collectors
import java.util.stream.Stream

@TypeChecked
class EditAdventureView extends AbstractEditDomainObjectDialogView<Adventure> {

    @FXML private TextField titleTextField
    @FXML private TextArea introductionTextArea
    @FXML private TextArea waitTextTextArea
    @FXML private TextArea getTextTextArea

    @FXML private TableView<CustomVerbView> verbsTableView
    @FXML private TableColumn<CustomVerbView, String> nameColumn
    @FXML private TableColumn<CustomVerbView, String> friendlyNameColumn
    @FXML private TableColumn<CustomVerbView, String> synonymsColumn

    @FXML private Button addButton
    @FXML private Button editButton
    @FXML private Button deleteButton


    private Adventure adventure

    EditAdventureView() {
        super("../editAdventure.fxml")
    }

    void setDomainObject(Adventure adventure) {
        this.adventure = adventure

        this.titleTextField.setText(adventure.getTitle())
        this.introductionTextArea.setText(adventure.getIntroduction())
        this.waitTextTextArea.setText(adventure.getWaitText())
        this.getTextTextArea.setText(adventure.getGetText())

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
        nameColumn.setCellValueFactory({cellData -> cellData.getValue().idProperty()})
        friendlyNameColumn.setCellValueFactory({cellData -> cellData.getValue().friendlyNameProperty()})
        synonymsColumn.setCellValueFactory({cellData -> cellData.getValue().synonymsProperty()})

        // to enable in-place editing
        nameColumn.setCellFactory(cellFactory);
        nameColumn.setOnEditCommit(
                new EventHandler<CellEditEvent<CustomVerbView, String>>() {
                    @Override
                    void handle(CellEditEvent<CustomVerbView, String> t) {
                        ((CustomVerbView) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setId(t.getNewValue())
                    }
                }
        )

        // to enable in-place editing
        friendlyNameColumn.setCellFactory(cellFactory);
        friendlyNameColumn.setOnEditCommit(
                new EventHandler<CellEditEvent<CustomVerbView, String>>() {
                    @Override
                    void handle(CellEditEvent<CustomVerbView, String> t) {
                        ((CustomVerbView) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setFriendlyName(t.getNewValue())
                    }
                }
        )

        // to enable in-place editing
        synonymsColumn.setCellFactory(cellFactory);
        synonymsColumn.setOnEditCommit(
                new EventHandler<CellEditEvent<CustomVerbView, String>>() {
                    @Override
                    void handle(CellEditEvent<CustomVerbView, String> t) {
                        ((CustomVerbView) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setSynonyms(t.getNewValue())
                    }
                }
        )

        // create the ObservableList and assign it to the TableView
        final List<CustomVerbView> customVerbs = adventure.getCustomVerbs().stream()
                .map { verb -> new CustomVerbView(verb.getId(), verb.getFriendlyName(), verb.getSynonyms().toListString())}
                .collect(Collectors.toList())
        final ObservableList<CustomVerbView> observableCustomVerbs = FXCollections.observableArrayList(customVerbs)
        verbsTableView.setItems(observableCustomVerbs)

        // wire up the remaining buttons
        addButton.setOnAction(this.&addButtonClick)
        editButton.setOnAction(this.&editButtonClick)
        deleteButton.setOnAction(this.&deleteButtonClick)
    }

    private void addButtonClick(ActionEvent event) {
        // TODO: How to support both adding and editing in the single EditVerbView form?
    }

    private void editButtonClick(ActionEvent event) {
        EditVerbView editVerbView = new EditVerbView()
        editVerbView.showModal(getStage())
        editVerbView.setDomainObject(this.verbsTableView.getSelectionModel().getSelectedItem())
    }

    private void deleteButtonClick(ActionEvent event) {

    }

    void doSave() {
        this.adventure.setTitle(this.titleTextField.getText())
        this.adventure.setIntroduction(this.introductionTextArea.getText())
        this.adventure.setWaitText(this.waitTextTextArea.getText())
        this.adventure.setGetText(this.getTextTextArea.getText())
    }

}

class CustomVerbView {
    private final SimpleStringProperty id
    private final SimpleStringProperty friendlyName
    private final SimpleStringProperty synonyms

    CustomVerbView(String id, String friendlyName, String synonyms) {
        this.id = new SimpleStringProperty(id)
        this.friendlyName = new SimpleStringProperty(friendlyName)
        this.synonyms = new SimpleStringProperty(synonyms)
    }

    String getId() {
        this.id.get()
    }

    SimpleStringProperty idProperty() {
        this.id
    }

    void setId(String id) {
        this.id.set(id)
    }

    String getFriendlyName() {
        this.friendlyName.get()
    }

    SimpleStringProperty friendlyNameProperty() {
        this.friendlyName
    }

    void setFriendlyName(String friendlyName) {
        this.friendlyName.set(friendlyName)
    }

    String getSynonyms() {
        this.synonyms.get()
    }

    SimpleStringProperty synonymsProperty() {
        this.synonyms
    }

    void setSynonyms(String synonyms) {
        this.synonyms.set(synonyms)
    }

}

class EditingCell extends TableCell<CustomVerbView, String> {

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