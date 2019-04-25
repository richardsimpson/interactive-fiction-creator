package uk.co.rjsoftware.adventure.view.editor.model

import groovy.transform.TypeChecked
import javafx.beans.property.adapter.*
import uk.co.rjsoftware.adventure.model.Direction
import uk.co.rjsoftware.adventure.model.Entrance
import uk.co.rjsoftware.adventure.model.Exit
import uk.co.rjsoftware.adventure.model.Room

@TypeChecked
class ObservableEntrance {

    private final Entrance entrance
    private final JavaBeanObjectProperty<Room> origin
    private final JavaBeanObjectProperty<Direction> originDirection
    private final JavaBeanObjectProperty<Direction> entranceDirection

    ObservableEntrance(Entrance entrance) {
        this.entrance = entrance
        this.origin = new JavaBeanObjectPropertyBuilder().bean(entrance).name("origin").build();
        this.originDirection = new JavaBeanObjectPropertyBuilder().bean(entrance).name("originDirection").build();
        this.entranceDirection = new JavaBeanObjectPropertyBuilder().bean(entrance).name("entranceDirection").build();
    }

    Entrance getEntrance() {
        this.entrance
    }

    JavaBeanObjectProperty<Room> originProperty() {
        this.origin
    }

    Room getOrigin() {
        this.origin.get()
    }

    JavaBeanObjectProperty entranceDirectionProperty() {
        this.entranceDirection
    }

    Direction getEntranceDirection() {
        this.entranceDirection.get()
    }

    JavaBeanObjectProperty originDirectionProperty() {
        this.originDirection
    }

    Direction getOriginDirection() {
        this.originDirection.get()
    }
}

