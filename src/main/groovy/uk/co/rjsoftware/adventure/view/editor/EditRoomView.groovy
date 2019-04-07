package uk.co.rjsoftware.adventure.view.editor

import groovy.transform.TypeChecked
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.adapter.JavaBeanObjectProperty
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder
import javafx.beans.property.adapter.JavaBeanStringProperty
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.util.Callback
import uk.co.rjsoftware.adventure.model.*
import uk.co.rjsoftware.adventure.view.AbstractDialogView

import java.util.function.Function
import java.util.stream.Collectors
import java.util.stream.Stream

import static uk.co.rjsoftware.adventure.view.ModalResult.mrOk

@TypeChecked
class EditRoomView extends AbstractDialogView {

    @FXML private TextField nameTextField
    @FXML private TextArea descriptionTextArea

    @FXML private TableView<ObservableVerbInstance> verbsTableView
    @FXML private TableColumn<ObservableVerbInstance, UUID> verbColumn
    @FXML private TableColumn<ObservableVerbInstance, String> scriptColumn

    @FXML private Button addButton
    @FXML private Button editButton
    @FXML private Button deleteButton

    @FXML private TableView<ObservableItem> itemsTableView
    @FXML private TableColumn<ObservableItem, String> nameColumn
    @FXML private TableColumn<ObservableItem, String> descriptionColumn

    private final Adventure adventure
    private final Room room
    private final ObservableRoom observableRoom

    EditRoomView(Adventure adventure, Room room) {
        super("../editRoom.fxml")
        this.adventure = adventure
        this.room = room
        this.observableRoom = new ObservableRoom(room)
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
        this.nameTextField.textProperty().bindBidirectional(this.observableRoom.nameProperty())
        this.descriptionTextArea.textProperty().bindBidirectional(this.observableRoom.descriptionProperty())

        // get a handy map of the adventure's custom verbs
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

        // Setup the table view of the custom verbs

        verbColumn.setCellValueFactory({ cellData -> cellData.getValue().idProperty()})
        scriptColumn.setCellValueFactory({ cellData -> cellData.getValue().scriptProperty()})

        // have the verbColumn display the verb name instead of the UUID id field
        verbColumn.setCellFactory(new Callback<TableColumn<ObservableVerbInstance, UUID>, TableCell<ObservableVerbInstance, UUID>>() {
            @Override
            TableCell<ObservableVerbInstance, UUID> call(TableColumn<ObservableVerbInstance, UUID> param) {
                new TableCell<ObservableVerbInstance, UUID>() {
                    @Override
                    void updateItem(UUID id, boolean empty) {
                        super.updateItem(id, empty)
                        if (empty) {
                            setText(null)
                        } else {
                            setText(customVerbMap.get(id).getName())
                        }
                    }
                }
            }
        })

        final List<ObservableVerbInstance> customVerbInstances = room.getCustomVerbs().values().stream()
                .map { verbInstance -> new ObservableVerbInstance(verbInstance)}
                .collect(Collectors.toList())
        final ObservableList<ObservableVerbInstance> observableCustomVerbInstances = FXCollections.observableList(customVerbInstances)

        verbsTableView.setItems(observableCustomVerbInstances)

        // listen to changes in the observableList of verbs, so that we can update the original items in the adventure
        observableCustomVerbInstances.addListener(new ListChangeListener<ObservableVerbInstance>() {
            @Override
            void onChanged(ListChangeListener.Change<? extends ObservableVerbInstance> c) {
                Stream<ObservableVerbInstance> tempVerbs = observableCustomVerbInstances.stream()
                Map<UUID, CustomVerbInstance> verbs = tempVerbs.collect(Collectors.toMap(
                        new Function<ObservableVerbInstance, UUID>() {
                            @Override
                            UUID apply(ObservableVerbInstance verbInstance) {
                                return verbInstance.getId()
                            }
                        },
                        new Function<ObservableVerbInstance, CustomVerbInstance>() {
                            @Override
                            CustomVerbInstance apply(ObservableVerbInstance verbInstance) {
                                return verbInstance.getVerbInstance()
                            }
                        }))

                room.setCustomVerbs(verbs)
            }
        })



        // Setup the table view of the items

        itemsTableView.setEditable(true)

        nameColumn.setCellValueFactory({ cellData -> cellData.getValue().nameProperty()})
        descriptionColumn.setCellValueFactory({ cellData -> cellData.getValue().descriptionProperty()})

        final List<ObservableItem> items = room.getItems().values().stream()
                .map { item -> new ObservableItem(item)}
        .collect(Collectors.toList())
        final ObservableList<ObservableItem> observableItems = FXCollections.observableList(items)

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

}

@TypeChecked
class ObservableRoom {

    private final Room room
    private final JavaBeanStringProperty name
    private final JavaBeanStringProperty description

    ObservableRoom(Room room) {
        this.room = room
        this.name = new JavaBeanStringPropertyBuilder().bean(room).name("name").build();
        this.description = new JavaBeanStringPropertyBuilder().bean(room).name("description").build();
    }

    JavaBeanStringProperty nameProperty() {
        this.name
    }

    JavaBeanStringProperty descriptionProperty() {
        this.description
    }
}

@TypeChecked
class ObservableVerbInstance {
    private final CustomVerbInstance verbInstance
    private final JavaBeanObjectProperty<UUID> id
    private final JavaBeanStringProperty script

    ObservableVerbInstance(CustomVerbInstance verbInstance) {
        this.verbInstance = verbInstance
        this.id = new JavaBeanObjectPropertyBuilder().bean(verbInstance).name("id").build()
        this.script = new JavaBeanStringPropertyBuilder().bean(verbInstance).name("script").build();
    }

    ObservableVerbInstance() {
        this(new CustomVerbInstance(null))
    }

    CustomVerbInstance getVerbInstance() {
        this.verbInstance
    }

    UUID getId() {
        this.id.get()
    }

    JavaBeanObjectProperty<UUID> idProperty() {
        this.id
    }

    void setId(UUID id) {
        this.id.set(id)
    }

    String getScript() {
        this.script.get()
    }

    JavaBeanStringProperty scriptProperty() {
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
