adventure {
    title "Adventure Game"
    introduction "Welcome to the Adventure!"

    verb ("WATCH {noun}") {
        synonyms "LOOK AT {noun}", "VIEW {noun}"
    }

    room ("bedroom") {
        description "custom description"
        beforeEnterRoomScript "beforeEnterRoomScript"
        afterEnterRoomScript "afterEnterRoomScript"
        afterLeaveRoomScript "afterLeaveRoomScript"
        beforeEnterRoomFirstTimeScript "beforeEnterRoomFirstTimeScript"
        afterEnterRoomFirstTimeScript "afterEnterRoomFirstTimeScript"

        item ("lamp") {
            synonyms "lampshade", "shade"
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

        item("TV") {
            synonyms "television"
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

            // TODO: Improve the DSL for custom verb scripts
            verb ("WATCH {noun}") {
                script """
                if (isSwitchedOn('tv')) {
                    say('You watch the TV for a while.  It's showing a Western of some kind.')
                }
                else {
                    say('You watch the TV for a while.  It's just a black screen.')
                }
                """
            }
        }
    }

    room ("landing") {
        description "custom description2"
        beforeEnterRoomScript "beforeEnterRoomScript2"
        afterEnterRoomScript "afterEnterRoomScript2"
        afterLeaveRoomScript "afterLeaveRoomScript2"
        beforeEnterRoomFirstTimeScript "beforeEnterRoomFirstTimeScript2"
        afterEnterRoomFirstTimeScript "afterEnterRoomFirstTimeScript2"

        exit direction: WEST, room: "bedroom"
    }

    room ("bedroom") {
        exit direction: EAST, room: "landing"
    }

    startRoom "bedroom"

}