package uk.co.rjsoftware.adventure.model

import uk.co.rjsoftware.adventure.utils.StringUtils

interface Entrance {

    Room getOrigin()

    Direction getOriginDirection()

    void setOrigin(Room origin)

    Direction getEntranceDirection()

    void setEntranceDirection(Direction entranceDirection)

}
