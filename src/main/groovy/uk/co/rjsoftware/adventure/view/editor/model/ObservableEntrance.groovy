package uk.co.rjsoftware.adventure.view.editor.model

import groovy.transform.TypeChecked
import uk.co.rjsoftware.adventure.model.Direction

@TypeChecked
interface ObservableEntrance {

    ObservableRoom getObservableOrigin()

    Direction getOriginDirection()

    void setObservableOrigin(ObservableRoom origin)

    Direction getEntranceDirection()

    void setEntranceDirection(Direction entranceDirection)

}

