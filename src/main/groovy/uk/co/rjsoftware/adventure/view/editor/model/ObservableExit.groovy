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
class ObservableExit {

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

    JavaBeanObjectProperty<Room> destinationProperty() {
        this.destination
    }

    void setObservableDestination(ObservableRoom destination) {
        this.observableDestination = destination
    }

    ObservableRoom getObservableDestination() {
        this.observableDestination
    }

    JavaBeanObjectProperty entranceDirectionProperty() {
        this.entranceDirection
    }

    Direction getEntranceDirection() {
        this.entranceDirection.get()
    }

    JavaBeanBooleanProperty sceneryProperty() {
        this.scenery
    }

    JavaBeanStringProperty prefixProperty() {
        this.prefix
    }

    JavaBeanStringProperty suffixProperty() {
        this.suffix
    }

}

