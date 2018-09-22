package uk.co.rjsoftware.adventure.controller.load

adventure {
    title "Adventure Game"
    introduction "Welcome to the Adventure!"

    verb ("Watch", "Watch", "WATCH {noun}") {
        synonyms "LOOK AT {noun}", "VIEW {noun}"
    }

    verb ("Throw", "THROW {noun}") {}

    room ("bedroom") {
        description "custom description"
        beforeEnterRoom { say("beforeEnterRoomScript") }
        afterEnterRoom { say("afterEnterRoomScript") }
        afterLeaveRoom { say("afterLeaveRoomScript") }
        beforeEnterRoomFirstTime { say("beforeEnterRoomFirstTimeScript") }
        afterEnterRoomFirstTime { say("afterEnterRoomFirstTimeScript") }

        item ("lamp") {
            synonyms "lampshade", "lamp", "shade"
            description "description"
            visible true
            scenery false
            gettable true
            droppable true
            switchable false
            switchOnMessage "switchOnMessage"
            switchOffMessage "switchOffMessage"
            extraMessageWhenSwitchedOn "extraMessageWhenSwitchedOn"
            extraMessageWhenSwitchedOff "extraMessageWhenSwitchedOff"
        }

        item("tv") {
            synonyms "television", "tv"
            description "description"
            visible true
            scenery false
            gettable false
            droppable false
            switchable true
            switchOnMessage "switchOnMessage"
            switchOffMessage "switchOffMessage"
            extraMessageWhenSwitchedOn "extraMessageWhenSwitchedOn"
            extraMessageWhenSwitchedOff "extraMessageWhenSwitchedOff"

            verb ("Watch") { say("watchVerb") }
            verb ("Throw") { say("throwVerb") }
        }
    }

    room ("landing") {
        description "custom description2"
        beforeEnterRoom { say("beforeEnterRoomScript2") }
        afterEnterRoom { say("afterEnterRoomScript2") }
        afterLeaveRoom { say("afterLeaveRoomScript2") }
        beforeEnterRoomFirstTime { say("beforeEnterRoomFirstTimeScript2") }
        afterEnterRoomFirstTime { say("afterEnterRoomFirstTimeScript2") }

        exit direction: WEST, room: "bedroom"
    }

    room ("bedroom") {
        exit direction: EAST, room: "landing"
    }

    startRoom "bedroom"

}