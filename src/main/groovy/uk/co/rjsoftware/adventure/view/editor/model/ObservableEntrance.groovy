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
    private ObservableRoom observableOrigin
    private final JavaBeanObjectProperty<Direction> originDirection
    private final JavaBeanObjectProperty<Direction> entranceDirection

    ObservableEntrance(Entrance entrance) {
        this.entrance = entrance
        this.originDirection = new JavaBeanObjectPropertyBuilder().bean(entrance).name("originDirection").build();
        this.entranceDirection = new JavaBeanObjectPropertyBuilder().bean(entrance).name("entranceDirection").build();
    }

    Entrance getEntrance() {
        this.entrance
    }

    ObservableRoom getObservableOrigin() {
        this.observableOrigin
    }

    void setObservableOrigin(ObservableRoom origin) {
        this.observableOrigin = origin
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

