package uk.co.rjsoftware.adventure.view.editor

import groovy.transform.TypeChecked
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.util.Callback
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.model.CustomVerb
import uk.co.rjsoftware.adventure.view.AbstractDialogView
import uk.co.rjsoftware.adventure.view.editor.model.ObservableItem
import uk.co.rjsoftware.adventure.view.editor.model.ObservableRoom
import uk.co.rjsoftware.adventure.view.editor.model.ObservableVerbInstance

import java.util.function.Function
import java.util.stream.Collectors

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

    private AbstractDialogView view
    private BorderPane parent
    private final Adventure adventure
    private final ObservableRoom observableRoom

    EditRoomView(Adventure adventure, ObservableRoom observableRoom, BorderPane parent) {
        super("../editRoom.fxml")
        this.parent = parent
        this.adventure = adventure
        this.observableRoom = observableRoom
    }

    // TODO: Add ability to edit:
    //       exits
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
        final ObservableItem newObservableItem = new ObservableItem()
        this.itemsTableView.getItems().add(newObservableItem)
        this.view = new EditItemView(newObservableItem)
        this.view.show(this.parent)
    }

    private void editItemButtonClick(ActionEvent event) {
        this.view = new EditItemView(this.itemsTableView.getSelectionModel().getSelectedItem())
        this.view.show(this.parent)
    }

    private void deleteItemButtonClick(ActionEvent event) {
        this.itemsTableView.getItems().remove(this.itemsTableView.getSelectionModel().getSelectedIndex())
    }

}
