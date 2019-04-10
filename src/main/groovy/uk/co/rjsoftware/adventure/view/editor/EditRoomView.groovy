package uk.co.rjsoftware.adventure.view.editor

import groovy.transform.TypeChecked
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
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.model.CustomVerb
import uk.co.rjsoftware.adventure.model.CustomVerbInstance
import uk.co.rjsoftware.adventure.model.Room
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

    @FXML private Button addVerbButton
    @FXML private Button editVerbButton
    @FXML private Button deleteVerbButton

    @FXML private Button addItemButton
    @FXML private Button editItemButton
    @FXML private Button deleteItemButton

    @FXML private TableView<ObservableItem> itemsTableView
    @FXML private TableColumn<ObservableItem, String> nameColumn
    @FXML private TableColumn<ObservableItem, String> descriptionColumn

    private final Adventure adventure
    private final ObservableRoom observableRoom

    EditRoomView(Adventure adventure, ObservableRoom observableRoom) {
        super("../editRoom.fxml")
        this.adventure = adventure
        this.observableRoom = observableRoom
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

        verbsTableView.setItems(this.observableRoom.getObservableCustomVerbInstances())


        // Setup the table view of the items

        itemsTableView.setEditable(true)

        nameColumn.setCellValueFactory({ cellData -> cellData.getValue().nameProperty()})
        descriptionColumn.setCellValueFactory({ cellData -> cellData.getValue().descriptionProperty()})

        itemsTableView.setItems(this.observableRoom.getObservableItems())


        // wire up the remaining buttons
        addVerbButton.setOnAction(this.&addVerbButtonClick)
        editVerbButton.setOnAction(this.&editVerbButtonClick)
        deleteVerbButton.setOnAction(this.&deleteVerbButtonClick)
        addItemButton.setOnAction(this.&addItemButtonClick)
        editItemButton.setOnAction(this.&editItemButtonClick)
        deleteItemButton.setOnAction(this.&deleteItemButtonClick)
    }

    private void addVerbButtonClick(ActionEvent event) {
        final ObservableVerbInstance newObservableVerbInstance = new ObservableVerbInstance()
        EditVerbInstanceView editVerbInstanceView = new EditVerbInstanceView(this.adventure, newObservableVerbInstance)
        if (editVerbInstanceView.showModal(getStage()) == mrOk) {
            this.verbsTableView.getItems().add(newObservableVerbInstance)
        }
    }

    private void editVerbButtonClick(ActionEvent event) {
        EditVerbInstanceView editVerbInstanceView = new EditVerbInstanceView(this.adventure, this.verbsTableView.getSelectionModel().getSelectedItem())
        editVerbInstanceView.showModal(getStage())
    }

    private void deleteVerbButtonClick(ActionEvent event) {
        this.verbsTableView.getItems().remove(this.verbsTableView.getSelectionModel().getSelectedIndex())
    }

    private void addItemButtonClick(ActionEvent event) {
//        final ObservableVerbInstance newObservableVerbInstance = new ObservableVerbInstance()
//        EditVerbInstanceView editVerbInstanceView = new EditVerbInstanceView(this.adventure, newObservableVerbInstance)
//        if (editVerbInstanceView.showModal(getStage()) == mrOk) {
//            this.verbsTableView.getItems().add(newObservableVerbInstance)
//        }
    }

    private void editItemButtonClick(ActionEvent event) {
        //TODO: Can we get the treeitems to bind to their values, so we don't need to manually update the treeitem text in
        // CustomTreeItem.onActionEditMenuItem?  This will also make it easier to add/edit any items, because
        // we won't need to go through onActionEditMenuItem.  Instead, just display the appropriate edit form,
        // with their components bound to the domain object.
        //
        // Maybe start by converting the Adventure into an ObservableAdventure, and get the rooms and items from that,
        // instead of via the original adventure?
//        EditVerbInstanceView editVerbInstanceView = new EditVerbInstanceView(this.adventure, this.verbsTableView.getSelectionModel().getSelectedItem())
//        editVerbInstanceView.showModal(getStage())
    }

    private void deleteItemButtonClick(ActionEvent event) {
//        this.itemsTableView.getItems().remove(this.itemsTableView.getSelectionModel().getSelectedIndex())
    }

}

@TypeChecked
class ObservableRoom {

    private final Room room
    private final JavaBeanStringProperty name
    private final JavaBeanStringProperty description
    private final ObservableList<ObservableVerbInstance> observableCustomVerbInstances
    private final ObservableList<ObservableItem> observableItems

    ObservableRoom(Room room) {
        this.room = room
        this.name = new JavaBeanStringPropertyBuilder().bean(room).name("name").build();
        this.description = new JavaBeanStringPropertyBuilder().bean(room).name("description").build();

        // setup the observableVerbInstance's list
        final List<ObservableVerbInstance> customVerbInstances = room.getCustomVerbs().values().stream()
                .map { verbInstance -> new ObservableVerbInstance(verbInstance)}
                .collect(Collectors.toList())
        this.observableCustomVerbInstances = FXCollections.observableList(customVerbInstances)

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

        // setup the observableItem's list
        final List<ObservableItem> items = room.getItems().values().stream()
                .map { item -> new ObservableItem(item)}
                .collect(Collectors.toList())
        this.observableItems = FXCollections.observableList(items)
    }

    JavaBeanStringProperty nameProperty() {
        this.name
    }

    JavaBeanStringProperty descriptionProperty() {
        this.description
    }

    ObservableList<ObservableVerbInstance> getObservableCustomVerbInstances() {
        this.observableCustomVerbInstances
    }

    ObservableList<ObservableItem> getObservableItems() {
        this.observableItems
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
