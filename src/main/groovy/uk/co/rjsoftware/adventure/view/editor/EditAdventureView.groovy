package uk.co.rjsoftware.adventure.view.editor

import groovy.transform.TypeChecked
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.adapter.JavaBeanStringProperty
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.TableColumn.CellEditEvent
import javafx.util.Callback
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.model.CustomVerb
import uk.co.rjsoftware.adventure.view.AbstractDialogView

import java.util.stream.Collectors
import java.util.stream.Stream

import static uk.co.rjsoftware.adventure.view.ModalResult.mrOk

@TypeChecked
class EditAdventureView extends AbstractDialogView {

    @FXML private TextField titleTextField
    @FXML private TextArea introductionTextArea
    @FXML private TextArea waitTextTextArea
    @FXML private TextArea getTextTextArea

    @FXML private TableView<ObservableCustomVerb> verbsTableView
    @FXML private TableColumn<ObservableCustomVerb, String> nameColumn
    @FXML private TableColumn<ObservableCustomVerb, String> displayNameColumn
    @FXML private TableColumn<ObservableCustomVerb, String> synonymsColumn

    @FXML private Button addButton
    @FXML private Button editButton
    @FXML private Button deleteButton


    private final Adventure adventure
    private final ObservableAdventure observableAdventure

    EditAdventureView(Adventure adventure) {
        super("../editAdventure.fxml")
        this.adventure = adventure
        this.observableAdventure = new ObservableAdventure(adventure)

    }

    protected void onShow() {
        this.titleTextField.textProperty().bindBidirectional(this.observableAdventure.titleProperty())
        this.introductionTextArea.textProperty().bindBidirectional(this.observableAdventure.introductionProperty())
        this.waitTextTextArea.textProperty().bindBidirectional(this.observableAdventure.waitTextProperty())
        this.getTextTextArea.textProperty().bindBidirectional(this.observableAdventure.getTextProperty())

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

        // create the ObservableList and assign it to the TableView
        final List<ObservableCustomVerb> customVerbs = adventure.getCustomVerbs().stream()
                .map { verb -> new ObservableCustomVerb(verb)}
                .collect(Collectors.toList())
        final ObservableList<ObservableCustomVerb> observableCustomVerbs = FXCollections.observableList(customVerbs)
        verbsTableView.setItems(observableCustomVerbs)

        // listen to changes in the observableList of verbs, so that we can update the original items in the adventure
        observableCustomVerbs.addListener(new ListChangeListener<ObservableCustomVerb>() {
            @Override
            void onChanged(ListChangeListener.Change<? extends ObservableCustomVerb> c) {
                Stream<CustomVerb> tempVerbs = observableCustomVerbs.stream()
                        .map{verb -> verb.getCustomVerb()}
                List<CustomVerb> verbs = tempVerbs.collect(Collectors.toList())
                adventure.setCustomVerbs(verbs)
            }
        })

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

@TypeChecked
class ObservableAdventure {

    private final Adventure adventure
    private final JavaBeanStringProperty introduction
    private final JavaBeanStringProperty title
    private final JavaBeanStringProperty waitText
    private final JavaBeanStringProperty getText

    ObservableAdventure(Adventure adventure) {
        this.adventure = adventure
        this.introduction = new JavaBeanStringPropertyBuilder().bean(adventure).name("introduction").build();
        this.title = new JavaBeanStringPropertyBuilder().bean(adventure).name("title").build();
        this.waitText = new JavaBeanStringPropertyBuilder().bean(adventure).name("waitText").build();
        this.getText = new JavaBeanStringPropertyBuilder().bean(adventure).name("getText").build();
    }

    JavaBeanStringProperty introductionProperty() {
        this.introduction
    }

    JavaBeanStringProperty titleProperty() {
        this.title
    }

    JavaBeanStringProperty waitTextProperty() {
        this.waitText
    }

    JavaBeanStringProperty getTextProperty() {
        this.getText
    }


}

@TypeChecked
class ObservableCustomVerb {
    private final JavaBeanStringProperty name
    private final JavaBeanStringProperty displayName
    private final SimpleStringProperty displayedSynonyms
    private final ObservableList<String> synonyms

    private final CustomVerb customVerb

    ObservableCustomVerb(CustomVerb customVerb) {
        this.name = new JavaBeanStringPropertyBuilder().bean(customVerb).name("name").build();
        this.displayName = new JavaBeanStringPropertyBuilder().bean(customVerb).name("displayName").build();
        this.displayedSynonyms = new SimpleStringProperty(customVerb.getSynonyms().toListString())
        this.synonyms = FXCollections.observableList(customVerb.getSynonyms())

        this.customVerb = customVerb
    }

    ObservableCustomVerb() {
        this(new CustomVerb("", "", ""))
    }

    CustomVerb getCustomVerb() {
        this.customVerb
    }

    String getName() {
        this.name.get()
    }

    JavaBeanStringProperty nameProperty() {
        this.name
    }

    void setName(String name) {
        this.name.set(name)
    }

    String getDisplayName() {
        this.displayName.get()
    }

    JavaBeanStringProperty displayNameProperty() {
        this.displayName
    }

    void setDisplayName(String displayName) {
        this.displayName.set(displayName)
    }

    SimpleStringProperty displayedSynonymsProperty() {
        this.displayedSynonyms
    }

    ObservableList<String> getSynonyms() {
        FXCollections.observableArrayList(this.customVerb.getSynonyms())
    }

    void setSynonyms(ObservableList<String> synonyms) {
        if (this.synonyms != synonyms) {
            this.synonyms.setAll(synonyms)
        }
        this.displayedSynonyms.set(synonyms.toListString())
        this.customVerb.setSynonyms(synonyms)
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