package uk.co.rjsoftware.adventure.view.editor.components

import groovy.transform.TypeChecked
import javafx.scene.control.Label
import javafx.scene.text.Font
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import uk.co.rjsoftware.adventure.model.Room

@TypeChecked
class RoomComponent extends CustomComponent {

    private static final double MIN_WIDTH = 160.0
    private static final double MIN_HEIGHT = 130.0

    private Label name = new Label()
    private Label label = new Label()
    private Room room

    RoomComponent(Room room) {
        this.room = room

        this.setMinSize(MIN_WIDTH, MIN_HEIGHT)

        this.name.setText(room.getName())
        this.name.setFont(Font.font("Helvetica", FontWeight.BOLD, 20))

        this.label.setWrapText(true)
        this.label.setMaxWidth(200)

        this.label.setFont(Font.font("Arial", FontPosture.ITALIC, 13))
        this.label.setText(room.getDescription())

        this.getChildren().add(name)
        this.getChildren().add(label)
    }

    String getText() {
        return room.getName()
    }

}