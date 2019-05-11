package uk.co.rjsoftware.adventure.view.editor.components

import groovy.transform.TypeChecked
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.Parent
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
    private List<PathComponent> entrances = new ArrayList<>()

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
            final ObservableRoom destination = observableExit.getObservableDestination()
            final Direction direction = observableExit.getDirection()
            final Direction entranceDirection = observableExit.getEntranceDirection()

            final RoomComponent targetRoom = findRoomComponent(destination, pane)
            if (targetRoom != null) {
                final PathComponent path = new PathComponent(this, direction)
                path.setTarget(targetRoom, entranceDirection)
                pane.getChildren().add(path)
                this.exits.put(direction, path)
                targetRoom.entrances.add(path)
            }
        }

        // create paths for all entrances that lead to RoomComponents that are already visible.
        // Re-use paths that have a reciprocal exit
        for (ObservableEntrance observableEntrance : this.room.getObservableEntrances()) {
            final ObservableRoom origin = observableEntrance.getObservableOrigin()
            final Direction originDirection = observableEntrance.getOriginDirection()
            final Direction entranceDirection = observableEntrance.getEntranceDirection()

            final RoomComponent sourceRoom = findRoomComponent(origin, pane)
            if (sourceRoom != null) {
                PathComponent path = findExit(entranceDirection, sourceRoom, originDirection)
                if (path == null) {
                    path = new PathComponent(sourceRoom, originDirection)
                    path.setTarget(this, entranceDirection)
                    pane.getChildren().add(path)
                }
                sourceRoom.exits.put(originDirection, path)
                this.entrances.add(path)
            }
        }

        // Listen to the observableExits in the ObservableRoom (this.room), so that we can create / remove paths as necessary.
        this.room.observableExits.addListener(new ListChangeListener<ObservableExit>() {
            @Override
            void onChanged(ListChangeListener.Change<? extends ObservableExit> c) {
                listOnChanged(c)
            }
        })

        // Listen to each exit's direction, destination and entranceDirection properties to update the path when it changes.
        for (ObservableExit exit : this.room.observableExits) {
            exit.directionProperty().addListener(new DirectionChangeListener(this, exit))
            exit.destinationProperty().addListener(new DestinationChangeListener(this, exit))
            exit.entranceDirectionProperty().addListener(new EntranceDirectionChangeListener(this, exit))
        }

        // TODO: When edit or remove an exit in the EditRoomView/EditExitView, if there is a reciprocal entrance,
        // remove that as well.
    }

    private static class DirectionChangeListener implements ChangeListener<Direction> {
        private final RoomComponent roomComponent
        private final ObservableExit exit

        DirectionChangeListener(RoomComponent roomComponent, ObservableExit exit) {
            this.exit = exit
            this.roomComponent = roomComponent
        }

        @Override
        void changed(ObservableValue<? extends Direction> observable, Direction oldValue, Direction newValue) {
            this.roomComponent.updatePathDirection(exit, oldValue)
        }
    }

    private static class EntranceDirectionChangeListener implements ChangeListener<Direction> {
        private final RoomComponent roomComponent
        private final ObservableExit exit

        EntranceDirectionChangeListener(RoomComponent roomComponent, ObservableExit exit) {
            this.exit = exit
            this.roomComponent = roomComponent
        }

        @Override
        void changed(ObservableValue<? extends Direction> observable, Direction oldValue, Direction newValue) {
            this.roomComponent.updatePathEntranceDirection(exit)
        }
    }

    private static class DestinationChangeListener implements ChangeListener<Room> {
        private final RoomComponent roomComponent
        private final ObservableExit exit

        DestinationChangeListener(RoomComponent roomComponent, ObservableExit exit) {
            this.exit = exit
            this.roomComponent = roomComponent
        }

        @Override
        void changed(ObservableValue<? extends Room> observable, Room oldValue, Room newValue) {
            this.roomComponent.updatePathDestination(exit)
        }
    }

    private void listOnChanged(ListChangeListener.Change<? extends ObservableExit> c) {
        while (c.next()) {
            if (c.wasPermutated()) {
                println "Exit list permutated from " + c.getFrom() + " to " + c.getTo() + "."
                for (int i = c.getFrom(); i < c.getTo(); ++i) {
                    //permutate
                }
            } else if (c.wasUpdated()) {
                println "Exit list updated from " + c.getFrom() + " to " + c.getTo() + "."
            } else {
                for (ObservableExit exitToRemove : c.getRemoved()) {
                    println "Exit removed: " + exitToRemove.getDirection()
                    removePath(exitToRemove)
                }
                for (ObservableExit exitToAdd : c.getAddedSubList()) {
                    println "Exit Added: " + exitToAdd.getDirection()
                    exitToAdd.directionProperty().addListener(new DirectionChangeListener(this, exitToAdd))
                    exitToAdd.destinationProperty().addListener(new DestinationChangeListener(this, exitToAdd))
                    exitToAdd.entranceDirectionProperty().addListener(new EntranceDirectionChangeListener(this, exitToAdd))
                    createPath(exitToAdd)
                }
            }
        }
    }

    private void createPath(ObservableExit exit) {
        final RoomComponent destinationRoomComponent = findRoomComponent(exit.getObservableDestination(), this.getParent())

        if (destinationRoomComponent != null) {
            PathComponent path = findEntrance(destinationRoomComponent, exit.getEntranceDirection(), exit.getDirection())
            if (path == null) {
                path = new PathComponent(this, exit.getDirection())
                path.setTarget(destinationRoomComponent, exit.getEntranceDirection())
                this.getParent().getChildren().add(path)
            }

            this.exits.put(exit.getDirection(), path)
            destinationRoomComponent.entrances.add(path)
        }
    }

    private PathComponent findEntrance(RoomComponent sourceRoom, Direction sourceDirection, Direction targetDirection) {
        // if there is an existing, matching entrance from the specified target room, then return it
        final PathComponent path =  this.entrances.find {
            it.getSourceRoom() == sourceRoom &&
            it.getSourceDirection() == sourceDirection &&
            it.getTargetDirection() == targetDirection
        }

        if (path != null) {
            return path
        }
        else {
            // try to find the reverse path - may exist since paths are shared between reciprocal exits
            this.entrances.find {
                it.getTargetRoom() == sourceRoom &&
                        it.getTargetDirection() == sourceDirection &&
                        it.getSourceDirection() == targetDirection
            }
        }
    }

    private PathComponent findExit(Direction direction, RoomComponent origin, Direction originDirection) {
        // if there is an existing, matching exit from the specified origin, then return it
        final PathComponent path = this.exits.get(direction)
        if (path == null) {
            return null
        }
        else if (path.getTargetRoom() == origin && path.getTargetDirection() == originDirection) {
            return path
        }
        else {
            return null
        }
    }

    private void removePath(ObservableExit exit) {
        final PathComponent pathToRemove = this.exits.get(exit.getDirection())

        this.exits.remove(exit.getDirection())

        final RoomComponent destinationRoomComponent = findRoomComponent(exit.getObservableDestination(), this.getParent())

        if (destinationRoomComponent != null) {
            destinationRoomComponent.entrances.remove(pathToRemove)
        }

        // having removed the path from the exits, check if the path is used in any entrances.  If it is not,
        // then remove it.
        if (!this.entrances.contains(pathToRemove)) {
            this.getParent().getChildren().remove(pathToRemove)
        }
    }

    private void updatePathDirection(ObservableExit exit, Direction oldDirection) {
        final PathComponent pathToUpdate = this.exits.get(oldDirection)

        this.exits.remove(oldDirection)
        this.exits.put(exit.getDirection(), pathToUpdate)

        pathToUpdate.setSourceDirection(exit.getDirection())
    }

    private void updatePathEntranceDirection(ObservableExit exit) {
        final PathComponent pathToUpdate = this.exits.get(exit.getDirection())
        pathToUpdate.setTargetDirection(exit.getEntranceDirection())
    }

    private void updatePathDestination(ObservableExit exit) {
        final PathComponent pathToUpdate = this.exits.get(exit.getDirection())
        final RoomComponent roomComponent = findRoomComponent(exit.getObservableDestination(), this.getParent())

        if (roomComponent != null) {
            pathToUpdate.setTarget(roomComponent)
        }
    }

    private RoomComponent findRoomComponent(ObservableRoom room, Parent pane) {
        (RoomComponent)pane.getChildren().stream().find { Node node ->
            node instanceof RoomComponent && ((RoomComponent)node).room == room
        }
    }

    private void updateAllPaths() {
        // get a unique list of the paths
        final Set<PathComponent> paths = new HashSet<>(this.exits.values())
        paths.addAll(this.entrances)

        // tell all of them to update the endpoint that connects to this room component
        for (PathComponent path : paths) {
            path.updatePathTo(this)
        }
    }

    void addExit(Direction direction, RoomComponent destination, Direction entranceDirection) {
        this.room.addExit(
                new ObservableExit(
                        new Exit(direction, destination.room.getRoom(), entranceDirection),
                        this.room, destination.room
                )
        )
    }

}