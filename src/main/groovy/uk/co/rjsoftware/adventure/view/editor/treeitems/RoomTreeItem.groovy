package uk.co.rjsoftware.adventure.view.editor.treeitems

import groovy.transform.TypeChecked
import javafx.stage.Stage
import uk.co.rjsoftware.adventure.model.Room
import uk.co.rjsoftware.adventure.view.editor.components.CustomComponent
import uk.co.rjsoftware.adventure.view.editor.components.RoomComponent

@TypeChecked
class RoomTreeItem implements CustomTreeItem {

    private final RoomComponent component

    RoomTreeItem(Room room, Stage primaryStage) {
        component = new RoomComponent(room, primaryStage)
    }

    @Override
    CustomComponent getComponent() {
        return component
    }

    @Override
    public String toString() {
        return component.getText()
    }
}
