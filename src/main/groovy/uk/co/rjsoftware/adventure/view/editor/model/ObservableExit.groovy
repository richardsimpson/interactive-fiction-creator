package uk.co.rjsoftware.adventure.view.editor.model

import groovy.transform.TypeChecked
import javafx.beans.Observable
import javafx.beans.property.adapter.JavaBeanBooleanProperty
import javafx.beans.property.adapter.JavaBeanBooleanPropertyBuilder
import javafx.beans.property.adapter.JavaBeanObjectProperty
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder
import javafx.beans.property.adapter.JavaBeanStringProperty
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import uk.co.rjsoftware.adventure.model.CustomVerbInstance
import uk.co.rjsoftware.adventure.model.Direction
import uk.co.rjsoftware.adventure.model.Exit
import uk.co.rjsoftware.adventure.model.Item
import uk.co.rjsoftware.adventure.model.Room

import java.util.function.Function
import java.util.stream.Collectors
import java.util.stream.Stream

@TypeChecked
class ObservableExit implements ObservableEntrance {

    private final Exit exit
    private ObservableRoom observableOrigin
    private final JavaBeanObjectProperty<Direction> direction
    private final JavaBeanObjectProperty<Room> destination
    private ObservableRoom observableDestination
    private final JavaBeanObjectProperty<Direction> entranceDirection
    private final JavaBeanBooleanProperty scenery
    private final JavaBeanStringProperty prefix
    private final JavaBeanStringProperty suffix

    ObservableExit(Exit exit) {
        this.exit = exit
        this.direction = new JavaBeanObjectPropertyBuilder().bean(exit).name("direction").build();
        this.destination = new JavaBeanObjectPropertyBuilder().bean(exit).name("destination").build();
        this.entranceDirection = new JavaBeanObjectPropertyBuilder().bean(exit).name("entranceDirection").build();
        this.scenery = new JavaBeanBooleanPropertyBuilder().bean(exit).name("scenery").build();
        this.prefix = new JavaBeanStringPropertyBuilder().bean(exit).name("prefix").build();
        this.suffix = new JavaBeanStringPropertyBuilder().bean(exit).name("suffix").build();


    }

    ObservableExit(Exit exit, ObservableRoom origin, ObservableRoom destination) {
        this(exit)
        this.observableOrigin = origin
        this.observableDestination = destination
    }

    ObservableExit() {
        this(new Exit())
    }

    Exit getExit() {
        this.exit
    }

    void setObservableOrigin(ObservableRoom origin) {
        this.observableOrigin = origin
    }

    ObservableRoom getObservableOrigin() {
        this.observableOrigin
    }

    JavaBeanObjectProperty directionProperty() {
        this.direction
    }

    Direction getDirection() {
        this.direction.get()
    }

    @Override
    Direction getOriginDirection() {
        this.direction.get()
    }

    void setDirection(Direction direction) {
        this.direction.set(direction)
    }

    ObservableRoom getObservableDestination() {
        this.observableDestination
    }

    JavaBeanObjectProperty<Room> destinationProperty() {
        this.destination
    }

    void setObservableDestination(ObservableRoom destination) {
        if (this.observableDestination != destination) {
            if (this.observableDestination != null) {
                this.observableDestination.removeEntrance(this)
            }

            this.observableDestination = destination
            this.destination.set(destination.getRoom())

            if (this.observableDestination != null) {
                this.observableDestination.addEntrance(this)
            }
        }
    }

    Direction getEntranceDirection() {
        this.entranceDirection.get()
    }

    JavaBeanObjectProperty entranceDirectionProperty() {
        this.entranceDirection
    }

    void setEntranceDirection(Direction direction) {
        this.entranceDirection.set(direction)
    }

    boolean isScenery() {
        this.scenery.get()
    }

    JavaBeanBooleanProperty sceneryProperty() {
        this.scenery
    }

    void setScenery(boolean isScenery) {
        this.scenery.set(isScenery)
    }

    String getPrefix() {
        this.prefix.get()
    }

    JavaBeanStringProperty prefixProperty() {
        this.prefix
    }

    void setPrefix(String prefix) {
        this.prefix.set(prefix)
    }

    String getSuffix() {
        this.suffix.get()
    }

    JavaBeanStringProperty suffixProperty() {
        this.suffix
    }

    void setSuffix(String suffix) {
        this.suffix.set(suffix)
    }

}

