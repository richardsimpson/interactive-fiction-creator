package uk.co.rjsoftware.adventure.view.editor.components

import groovy.transform.TypeChecked
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.input.MouseEvent
import javafx.scene.text.Font
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import javafx.stage.Stage
import uk.co.rjsoftware.adventure.model.Room
import uk.co.rjsoftware.adventure.view.editor.EditRoomView

@TypeChecked
class RoomComponent extends CustomComponent {

    private static final double MIN_WIDTH = 160.0
    private static final double MIN_HEIGHT = 130.0

    private Label name = new Label()
    private Label description = new Label()
    private Room room

    private ContextMenu contextMenu = new ContextMenu()

    // TODO: Remove the need to pass in the primaryStage
    RoomComponent(Room room, Stage primaryStage) {
        this.room = room

        this.setMinSize(MIN_WIDTH, MIN_HEIGHT)

        this.name.setFont(Font.font("Helvetica", FontWeight.BOLD, 20))

        this.description.setWrapText(true)
        this.description.setMaxWidth(200)

        this.description.setFont(Font.font("Arial", FontPosture.ITALIC, 13))

        this.getChildren().add(name)
        this.getChildren().add(description)

        // set up the context menu
        // TODO: put in the superclass
        MenuItem item1 = new MenuItem("Edit...");
        item1.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // Load root layout from fxml file
                final FXMLLoader loader = new FXMLLoader()
                loader.setLocation(getClass().getResource("../../editRoom.fxml"))
                final Parent rootLayout = loader.load()

                // initialise the view after showing the scene
                final EditRoomView editRoomView = loader.getController()
                editRoomView.init(rootLayout, primaryStage, RoomComponent.this)
            }
        });
        contextMenu.getItems().addAll(item1);

        // plug in the context menu
        this.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // TODO: Why is this menu only shown once?  Try chaining to this event from the one in ResizeComponent
                println("RoomComponent.onMousePressed called via event handler")
                if (event.secondaryButtonDown) {
                    contextMenu.show(RoomComponent.this, event.getScreenX(), event.getScreenY())
                };
            }
        })

        refresh()
    }

    String getText() {
        return room.getName()
    }

    Room getRoom() {
        this.room
    }

    @Override
    void refresh() {
        this.name.setText(room.getName())
        this.description.setText(room.getDescription())
    }
}