package uk.co.rjsoftware.adventure.view.editor

import groovy.transform.TypeChecked
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.util.StringConverter
import uk.co.rjsoftware.adventure.model.CustomVerb
import uk.co.rjsoftware.adventure.model.Direction
import uk.co.rjsoftware.adventure.view.AbstractEditDomainObjectDialogView
import uk.co.rjsoftware.adventure.view.editor.model.ObservableAdventure
import uk.co.rjsoftware.adventure.view.editor.model.ObservableExit
import uk.co.rjsoftware.adventure.view.editor.model.ObservableRoom
import uk.co.rjsoftware.adventure.view.editor.model.ObservableVerbInstance

@TypeChecked
class EditExitView extends AbstractEditDomainObjectDialogView {

    @FXML private ComboBox<Direction> directionComboBox
    @FXML private ComboBox<ObservableRoom> destinationComboBox
    @FXML private ComboBox<Direction> entranceDirectionComboBox
    @FXML private CheckBox sceneryCheckBox
    @FXML private TextField prefixTextField
    @FXML private TextField suffixTextField

    private final ObservableAdventure observableAdventure
    private final ObservableExit exit

    EditExitView(ObservableAdventure observableAdventure, ObservableExit exit) {
        super("../editExit.fxml")
        this.observableAdventure = observableAdventure
        this.exit = exit
    }

    protected void onShow() {
        final StringConverter roomConverter = new StringConverter<ObservableRoom>() {
            @Override
            public String toString(ObservableRoom room) {
                if (room == null){
                    return null
                } else {
                    return room.getName()
                }
            }

            @Override
            public ObservableRoom fromString(String name) {
                return null
            }
        }

        final StringConverter directionConverter = new StringConverter<Direction>() {
            @Override
            public String toString(Direction direction) {
                if (direction == null){
                    return null
                } else {
                    return direction.getDescription()
                }
            }

            @Override
            public Direction fromString(String direction) {
                return null
            }
        }

        final ObservableList<Direction> observableDirections = FXCollections.observableArrayList(Direction.values())

        this.directionComboBox.setItems(observableDirections)
        this.directionComboBox.setConverter(directionConverter)

        this.destinationComboBox.setItems(this.observableAdventure.getObservableRooms())
        this.destinationComboBox.setConverter(roomConverter)

        this.entranceDirectionComboBox.setItems(observableDirections)
        this.entranceDirectionComboBox.setConverter(directionConverter)

        this.directionComboBox.getSelectionModel().select(this.exit.getDirection())
        this.destinationComboBox.getSelectionModel().select(this.exit.getObservableDestination())
        this.entranceDirectionComboBox.getSelectionModel().select(this.exit.getEntranceDirection())
        this.sceneryCheckBox.setSelected(this.exit.isScenery())
        this.prefixTextField.setText(this.exit.getPrefix())
        this.suffixTextField.setText(this.exit.getSuffix())
    }

    protected void doSave() {
        this.exit.setDirection(this.directionComboBox.getSelectionModel().getSelectedItem())
        this.exit.setObservableDestination(this.destinationComboBox.getSelectionModel().getSelectedItem())
        this.exit.setEntranceDirection(this.entranceDirectionComboBox.getSelectionModel().getSelectedItem())
        this.exit.setScenery(this.sceneryCheckBox.isSelected())
        this.exit.setPrefix(this.prefixTextField.getText())
        this.exit.setSuffix(this.suffixTextField.getText())
    }

}
