package uk.co.rjsoftware.adventure.view.editor

import groovy.transform.TypeChecked
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.cell.ComboBoxTableCell
import javafx.scene.control.cell.ComboBoxTreeTableCell
import javafx.util.StringConverter
import sun.security.pkcs11.wrapper.Functions
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.model.ContentVisibility
import uk.co.rjsoftware.adventure.model.CustomVerb
import uk.co.rjsoftware.adventure.model.Item
import uk.co.rjsoftware.adventure.model.ItemContainer
import uk.co.rjsoftware.adventure.model.Room
import uk.co.rjsoftware.adventure.view.AbstractEditDomainObjectDialogView

import java.util.function.Function
import java.util.stream.Collectors
import java.util.stream.Stream

import static uk.co.rjsoftware.adventure.view.ModalResult.mrOk

@TypeChecked
class EditRoomView extends AbstractEditDomainObjectDialogView<Room> {

    @FXML private TextField nameTextField
    @FXML private TextArea descriptionTextArea

    @FXML private TableView<ObservableVerbInstance> verbsTableView
    @FXML private TableColumn<ObservableVerbInstance, String> verbColumn
    @FXML private TableColumn<ObservableVerbInstance, String> scriptColumn

    @FXML private Button addButton
    @FXML private Button editButton
    @FXML private Button deleteButton

    @FXML private TableView<ObservableItem> itemsTableView
    @FXML private TableColumn<ObservableItem, String> nameColumn
    @FXML private TableColumn<ObservableItem, String> descriptionColumn

    private final Adventure adventure
    private final Room room

    EditRoomView(Adventure adventure, Room room) {
        super("../editRoom.fxml")
        this.adventure = adventure
        this.room = room

    }

    // TODO: Add ability to edit:
    //       exits
    //       items
    //       descriptionScript
    //       beforeEnterRoomScript
    //       afterEnterRoomScript
    //       afterLeaveRoomScript
    //       beforeEnterRoomFirstTimeScript
    //       afterEnterRoomFirstTimeScript

    protected void onShow() {
        this.nameTextField.setText(room.getName())
        this.descriptionTextArea.setText(room.getDescription())

        // Setup the table view of the custom verbs

        verbsTableView.setEditable(true)

        verbColumn.setCellValueFactory({ cellData -> cellData.getValue().nameProperty()})
        scriptColumn.setCellValueFactory({ cellData -> cellData.getValue().scriptProperty()})

        final List<CustomVerb> customVerbList = this.adventure.getCustomVerbs()
        final Map<UUID, CustomVerb> customVerbMap = customVerbList.stream()
        .collect(Collectors.toMap(
                new Function<CustomVerb, UUID>() {
                    @Override
                    UUID apply(CustomVerb customVerb) {
                        return customVerb.getId()
                    }
                },
                new Function<CustomVerb, CustomVerb>() {
                    @Override
                    CustomVerb apply(CustomVerb customVerb) {
                        return customVerb
                    }
                }
        ))

        final List<ObservableVerbInstance> customVerbInstances = room.getCustomVerbs().entrySet().stream()
                .map { entry -> new ObservableVerbInstance(customVerbMap.get(entry.key).getId(),
                                                           customVerbMap.get(entry.key).getName(),
                                                           entry.value)}
                .collect(Collectors.toList())
        final ObservableList<ObservableVerbInstance> observableCustomVerbInstances = FXCollections.observableArrayList(customVerbInstances)

        verbsTableView.setItems(observableCustomVerbInstances)


        // Setup the table view of the items

        itemsTableView.setEditable(true)

        nameColumn.setCellValueFactory({ cellData -> cellData.getValue().nameProperty()})
        descriptionColumn.setCellValueFactory({ cellData -> cellData.getValue().descriptionProperty()})

        final List<ObservableItem> items = room.getItems().values().stream()
                .map { item -> new ObservableItem(item)}
        .collect(Collectors.toList())
        final ObservableList<ObservableItem> observableItems = FXCollections.observableArrayList(items)

        itemsTableView.setItems(observableItems)

        
        // wire up the remaining buttons
        addButton.setOnAction(this.&addButtonClick)
        editButton.setOnAction(this.&editButtonClick)
        deleteButton.setOnAction(this.&deleteButtonClick)
    }

    private void addButtonClick(ActionEvent event) {
        final ObservableVerbInstance newObservableVerbInstance = new ObservableVerbInstance()
        EditVerbInstanceView editVerbInstanceView = new EditVerbInstanceView(this.adventure, newObservableVerbInstance)
        if (editVerbInstanceView.showModal(getStage()) == mrOk) {
            this.verbsTableView.getItems().add(newObservableVerbInstance)
        }
    }

    private void editButtonClick(ActionEvent event) {
        EditVerbInstanceView editVerbInstanceView = new EditVerbInstanceView(this.adventure, this.verbsTableView.getSelectionModel().getSelectedItem())
        editVerbInstanceView.showModal(getStage())
    }

    private void deleteButtonClick(ActionEvent event) {
        this.verbsTableView.getItems().remove(this.verbsTableView.getSelectionModel().getSelectedIndex())
    }

    void doSave() {
        this.room.setName(this.nameTextField.getText())
        this.room.setDescription(this.descriptionTextArea.getText())

        final Map<UUID, String> newCustomVerbs = new HashMap<>()
        for (ObservableVerbInstance verbInstance : this.verbsTableView.getItems()) {
            newCustomVerbs.put(verbInstance.getId(), verbInstance.getScript())
        }
        this.room.setCustomVerbs(newCustomVerbs)
    }

}

@TypeChecked
class ObservableVerbInstance {
    private UUID id
    private final SimpleStringProperty name
    private final SimpleStringProperty script

    ObservableVerbInstance(UUID id, String name, String script) {
        this.id = id
        this.name = new SimpleStringProperty(name)
        this.script = new SimpleStringProperty(script)
    }

    ObservableVerbInstance() {
        this(null, "", "")
    }

    UUID getId() {
        this.id
    }

    void setId(UUID id) {
        this.id = id
    }

    String getName() {
        this.name.get()
    }

    SimpleStringProperty nameProperty() {
        this.name
    }

    void setName(String name) {
        this.name.set(name)
    }

    String getScript() {
        this.script.get()
    }

    SimpleStringProperty scriptProperty() {
        this.script
    }

    void setScript(String script) {
        this.script.set(script)
    }

}

@TypeChecked
class ObservableItem {
    private final SimpleStringProperty name
    private final SimpleStringProperty description

    ObservableItem(Item item) {
        this.name = new SimpleStringProperty(item.getName())
        this.description = new SimpleStringProperty(item.getDescription())
    }

    ObservableItem() {
        this(new Item(""))
    }

    String getName() {
        this.name.get()
    }

    SimpleStringProperty nameProperty() {
        this.name
    }

    void setName(String name) {
        this.name.set(name)
    }

    String getDescription() {
        this.description.get()
    }

    SimpleStringProperty descriptionProperty() {
        this.description
    }

    void setDescription(String description) {
        this.description.set(description)
    }

}
