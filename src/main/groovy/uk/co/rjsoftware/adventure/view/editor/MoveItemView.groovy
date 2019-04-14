package uk.co.rjsoftware.adventure.view.editor

import groovy.transform.TypeChecked
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.util.StringConverter
import uk.co.rjsoftware.adventure.model.CustomVerb
import uk.co.rjsoftware.adventure.view.AbstractEditDomainObjectDialogView
import uk.co.rjsoftware.adventure.view.editor.model.ObservableAdventure
import uk.co.rjsoftware.adventure.view.editor.model.ObservableItem
import uk.co.rjsoftware.adventure.view.editor.model.ObservableItemContainer
import uk.co.rjsoftware.adventure.view.editor.model.ObservableRoom
import uk.co.rjsoftware.adventure.view.editor.model.ObservableVerbInstance

@TypeChecked
class MoveItemView extends AbstractEditDomainObjectDialogView {

    @FXML private Label moveItemLabel
    @FXML private ComboBox<ObservableItemContainer> itemContainersComboBox

    private final ObservableAdventure observableAdventure
    private final ObservableItem observableItem
    private final ObservableVerbInstance verbInstance

    MoveItemView(ObservableAdventure observableAdventure, ObservableItem observableItem) {
        super("../moveItem.fxml")
        this.observableAdventure = observableAdventure
        this.observableItem = observableItem
    }

    protected void onShow() {
        this.moveItemLabel.setText("Move Item '" + observableItem.nameProperty().get() + "' To:")

        this.itemContainersComboBox.setItems(FXCollections.observableArrayList(this.observableAdventure.getAllItemContainers()))

        this.itemContainersComboBox.setConverter(new StringConverter<ObservableItemContainer>() {
            @Override
            public String toString(ObservableItemContainer itemContainer) {
                if (itemContainer == null){
                    return null
                } else {
                    return itemContainer.nameProperty().get()
                }
            }

            @Override
            public ObservableItemContainer fromString(String name) {
                return null
            }
        })

    }

    protected void doSave() {
        this.itemContainersComboBox.getSelectionModel().getSelectedItem().addItem(this.observableItem)
    }

}
