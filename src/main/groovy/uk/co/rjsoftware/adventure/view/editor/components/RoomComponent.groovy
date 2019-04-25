package uk.co.rjsoftware.adventure.view.editor.components

import groovy.transform.TypeChecked
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.text.Font
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import uk.co.rjsoftware.adventure.model.Direction
import uk.co.rjsoftware.adventure.model.Exit
import uk.co.rjsoftware.adventure.model.Room
import uk.co.rjsoftware.adventure.view.editor.model.ObservableAdventure
import uk.co.rjsoftware.adventure.view.editor.model.ObservableEntrance
import uk.co.rjsoftware.adventure.view.editor.model.ObservableExit
import uk.co.rjsoftware.adventure.view.editor.model.ObservableRoom
import uk.co.rjsoftware.adventure.view.editor.treeitems.RoomTreeItem

@TypeChecked
class RoomComponent extends CustomComponent {

    private static final double MIN_WIDTH = 160.0
    private static final double MIN_HEIGHT = 130.0

    private Label name = new Label()
    private Label description = new Label()
    private ObservableRoom room

    private Map<Direction, PathComponent> exits = new HashMap<>()
    private Map<Direction, PathComponent> entrances = new HashMap<>()

    RoomComponent(ObservableRoom room, RoomTreeItem roomTreeItem) {
        this.room = room

        this.setMinSize(MIN_WIDTH, MIN_HEIGHT)

        this.name.setFont(Font.font("Helvetica", FontWeight.BOLD, 20))
        this.name.setMouseTransparent(true)

        this.description.setWrapText(true)
        this.description.setMaxWidth(200)
        this.description.setFont(Font.font("Arial", FontPosture.ITALIC, 13))
        this.description.setMouseTransparent(true)

        this.getChildren().add(name)
        this.getChildren().add(description)

        this.name.textProperty().bind(room.nameProperty())
        this.description.textProperty().bind(room.descriptionProperty())

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

        final ChangeListener<Number> changeListener = new ChangeListener<Number>() {
            @Override
            void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                updateAllPaths()
            }
        }
        layoutXProperty().addListener(changeListener)
        layoutYProperty().addListener(changeListener)
        widthProperty().addListener(changeListener)
        heightProperty().addListener(changeListener)
    }

    @Override
    void show(Pane pane, ObservableAdventure observableAdventure) {
        pane.getChildren().add(this)

        // create paths for all the exits that lead to RoomComponents that are already visible (or are this room)
        for (ObservableExit observableExit : this.room.getObservableExits()) {
            final Room destination = observableExit.getDestination()
            final Direction direction = observableExit.getDirection()
            final Direction entranceDirection = observableExit.getEntranceDirection()

            final RoomComponent targetRoom = findRoomComponent(destination, pane)
            if (targetRoom != null) {
                final PathComponent path = new PathComponent(this, direction)
                path.setTarget(targetRoom, entranceDirection)
                pane.getChildren().add(path)
                this.exits.put(direction, path)
                // TODO: Support multiple entrances in the same direction
                targetRoom.entrances.put(entranceDirection, path)
            }
        }

        // create paths for all entrances that lead to RoomComponents that are already visible (or are this room)
        for (ObservableEntrance observableEntrance : this.room.getObservableEntrances()) {
            final Room origin = observableEntrance.getOrigin()
            final Direction originDirection = observableEntrance.getOriginDirection()
            final Direction entranceDirection = observableEntrance.getEntranceDirection()

            // make sure we don't create a second path if the origin of this entrance is ourselves
            if (this.room.getRoom() != origin) {
                final RoomComponent sourceRoom = findRoomComponent(origin, pane)
                if (sourceRoom != null) {
                    final PathComponent path = new PathComponent(sourceRoom, originDirection)
                    path.setTarget(this, entranceDirection)
                    pane.getChildren().add(path)
                    sourceRoom.exits.put(originDirection, path)
                    this.entrances.put(entranceDirection, path)
                }
            }
        }
    }

    private RoomComponent findRoomComponent(Room room, Pane pane) {
        (RoomComponent)pane.getChildren().stream().find { Node node ->
            node instanceof RoomComponent && ((RoomComponent)node).room.getRoom() == room
        }
    }

    private void updateAllPaths() {
        // TODO: Update all the unique paths in the exits and entrances.

        // get a unique list of the paths
        final Set<PathComponent> paths = new HashSet<>(this.exits.values())
        paths.addAll(this.entrances.values())

        // tell all of them to update the endpoint that connects to this room component
        for (PathComponent path : paths) {
            path.updatePathTo(this)
        }
    }

    void addExit(Direction direction, PathComponent pathComponent, RoomComponent destination, Direction entranceDirection) {
        this.exits.put(direction, pathComponent)
        this.room.addExit(
                new ObservableExit(
                        new Exit(direction, destination.room.getRoom(), entranceDirection)
                )
        )
    }

    void addEntrance(Direction direction, PathComponent pathComponent) {
        this.entrances.put(direction, pathComponent)
    }

}