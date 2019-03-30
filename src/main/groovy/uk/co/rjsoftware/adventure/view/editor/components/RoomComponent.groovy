package uk.co.rjsoftware.adventure.view.editor.components

import groovy.transform.TypeChecked
import javafx.event.EventHandler
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.text.Font
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import uk.co.rjsoftware.adventure.model.Room
import uk.co.rjsoftware.adventure.view.editor.treeitems.RoomTreeItem

@TypeChecked
class RoomComponent extends CustomComponent {

    private static final double MIN_WIDTH = 160.0
    private static final double MIN_HEIGHT = 130.0

    private Label name = new Label()
    private Label description = new Label()
    private Room room

    RoomComponent(Room room, RoomTreeItem roomTreeItem) {
        this.room = room

        this.setMinSize(MIN_WIDTH, MIN_HEIGHT)

        this.name.setFont(Font.font("Helvetica", FontWeight.BOLD, 20))

        this.description.setWrapText(true)
        this.description.setMaxWidth(200)

        this.description.setFont(Font.font("Arial", FontPosture.ITALIC, 13))

        this.getChildren().add(name)
        this.getChildren().add(description)

        // plug in the context menu
        this.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                println("RoomComponent.onMousePressed called")
                if (event.secondaryButtonDown) {
                    roomTreeItem.getContextMenu().show(RoomComponent.this, event.getScreenX(), event.getScreenY())
                };
            }
        })

        onChanged()

        roomTreeItem.addChangeListener(this.&onChanged)
    }

    String getText() {
        return room.getName()
    }

    Room getRoom() {
        this.room
    }

    private void onChanged() {
        this.name.setText(room.getName())
        this.description.setText(room.getDescription())
    }

}