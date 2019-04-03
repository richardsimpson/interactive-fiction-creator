package uk.co.rjsoftware.adventure.view.editor

import groovy.transform.TypeChecked
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
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
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.model.CustomVerb
import uk.co.rjsoftware.adventure.view.AbstractEditDomainObjectDialogView
import uk.co.rjsoftware.adventure.view.ModalResult

import java.util.stream.Collectors

import static uk.co.rjsoftware.adventure.view.ModalResult.mrOk

@TypeChecked
class EditAdventureView extends AbstractEditDomainObjectDialogView<Adventure> {

    @FXML private TextField titleTextField
    @FXML private TextArea introductionTextArea
    @FXML private TextArea waitTextTextArea
    @FXML private TextArea getTextTextArea

    @FXML private TableView<ObservableCustomVerb> verbsTableView
    @FXML private TableColumn<ObservableCustomVerb, String> nameColumn
    @FXML private TableColumn<ObservableCustomVerb, String> friendlyNameColumn
    @FXML private TableColumn<ObservableCustomVerb, String> synonymsColumn

    @FXML private Button addButton
    @FXML private Button editButton
    @FXML private Button deleteButton


    private final Adventure adventure

    EditAdventureView(Adventure adventure) {
        super("../editAdventure.fxml")
        this.adventure = adventure
    }

    protected void onShow() {
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
        synonymsColumn.setCellValueFactory({cellData -> cellData.getValue().displayedSynonymsProperty()})

        // to enable in-place editing
        nameColumn.setCellFactory(cellFactory);
        nameColumn.setOnEditCommit(
                new EventHandler<CellEditEvent<ObservableCustomVerb, String>>() {
                    @Override
                    void handle(CellEditEvent<ObservableCustomVerb, String> t) {
                        ((ObservableCustomVerb) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setId(t.getNewValue())
                    }
                }
        )

        // to enable in-place editing
        friendlyNameColumn.setCellFactory(cellFactory);
        friendlyNameColumn.setOnEditCommit(
                new EventHandler<CellEditEvent<ObservableCustomVerb, String>>() {
                    @Override
                    void handle(CellEditEvent<ObservableCustomVerb, String> t) {
                        ((ObservableCustomVerb) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setFriendlyName(t.getNewValue())
                    }
                }
        )

        // to enable in-place editing
//        synonymsColumn.setCellFactory(cellFactory);
//        synonymsColumn.setOnEditCommit(
//                new EventHandler<CellEditEvent<ObservableCustomVerb, String>>() {
//                    @Override
//                    void handle(CellEditEvent<ObservableCustomVerb, String> t) {
//                        ((ObservableCustomVerb) t.getTableView().getItems().get(
//                                t.getTablePosition().getRow())
//                        ).setSynonyms(t.getNewValue())
//                    }
//                }
//        )

        // create the ObservableList and assign it to the TableView
        final List<ObservableCustomVerb> customVerbs = adventure.getCustomVerbs().stream()
                .map { verb -> new ObservableCustomVerb(verb)}
                .collect(Collectors.toList())
        final ObservableList<ObservableCustomVerb> observableCustomVerbs = FXCollections.observableArrayList(customVerbs)
        verbsTableView.setItems(observableCustomVerbs)

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

    void doSave() {
        this.adventure.setTitle(this.titleTextField.getText())
        this.adventure.setIntroduction(this.introductionTextArea.getText())
        this.adventure.setWaitText(this.waitTextTextArea.getText())
        this.adventure.setGetText(this.getTextTextArea.getText())

        final List<CustomVerb> newCustomVerbs = this.verbsTableView.getItems().stream()
                .map { verb -> verb.toCustomVerb()}
                .collect(Collectors.toList())
        this.adventure.setCustomVerbs(newCustomVerbs)
    }

}

@TypeChecked
class ObservableCustomVerb {
    private final SimpleStringProperty id
    private final SimpleStringProperty friendlyName
    private final SimpleStringProperty displayedSynonyms
    private final ObservableList<String> synonyms

    private ObservableCustomVerb(String id, String friendlyName, List<String> synonyms) {
        this.id = new SimpleStringProperty(id)
        this.friendlyName = new SimpleStringProperty(friendlyName)
        this.displayedSynonyms = new SimpleStringProperty(synonyms.toListString())
        this.synonyms = FXCollections.observableArrayList(synonyms)
    }

    ObservableCustomVerb(CustomVerb customVerb) {
        this(customVerb.getId(), customVerb.getFriendlyName(), customVerb.getSynonyms())
    }

    ObservableCustomVerb() {
        this("", "", new ArrayList())
    }

    CustomVerb toCustomVerb() {
        new CustomVerb(this.id.get(), this.friendlyName.get(), this.synonyms.toList())
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

    String getDisplayedSynonyms() {
        this.displayedSynonyms.get()
    }

    SimpleStringProperty displayedSynonymsProperty() {
        this.displayedSynonyms
    }

    void setDisplayedSynonyms(String displayedSynonyms) {
        this.displayedSynonyms.set(displayedSynonyms)
    }

    ObservableList<String> getSynonyms() {
        this.synonyms
    }

    void setSynonyms(ObservableList<String> synonyms) {
        if (this.synonyms != synonyms) {
            this.synonyms.setAll(synonyms)
        }
        this.displayedSynonyms.set(synonyms.toListString())
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