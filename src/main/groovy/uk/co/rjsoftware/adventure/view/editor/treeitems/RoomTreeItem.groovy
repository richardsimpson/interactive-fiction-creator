package uk.co.rjsoftware.adventure.view.editor.treeitems

import groovy.transform.TypeChecked
import uk.co.rjsoftware.adventure.model.Room
import uk.co.rjsoftware.adventure.view.editor.components.CustomComponent
import uk.co.rjsoftware.adventure.view.editor.components.RoomComponent

@TypeChecked
class RoomTreeItem implements CustomTreeItem {

    private final RoomComponent component

    RoomTreeItem(Room room) {
        component = new RoomComponent(room)
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
