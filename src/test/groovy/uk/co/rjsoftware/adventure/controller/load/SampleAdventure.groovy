package uk.co.rjsoftware.adventure.controller.load

adventure {
    title "Adventure Game"
    introduction "Welcome to the Adventure!"

    player "player"

    verb ("Watch", "Watch", "WATCH {noun}") {
        synonyms "LOOK AT {noun}", "VIEW {noun}"
    }

    verb ("Throw", "THROW {noun}")

    room (1, "bedroom") {
        description "custom description"
        beforeEnterRoom { say("beforeEnterRoomScript") }
        afterEnterRoom { say("afterEnterRoomScript") }
        afterLeaveRoom { say("afterLeaveRoomScript") }
        beforeEnterRoomFirstTime { say("beforeEnterRoomFirstTimeScript") }
        afterEnterRoomFirstTime { say("afterEnterRoomFirstTimeScript") }

        exit(EAST, "landing") {
            scenery true
            prefix "prefix"
            suffix "suffix"
        }

        item (1, "player")

        item (2, "lamp") {
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

        item(3, "tv") {
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

            verb ("Watch") { say("watchVerb") }
            verb ("Throw") { say("throwVerb") }
        }
    }

    room (2, "landing") {
        description "custom description2"
        beforeEnterRoom { say("beforeEnterRoomScript2") }
        afterEnterRoom { say("afterEnterRoomScript2") }
        afterLeaveRoom { say("afterLeaveRoomScript2") }
        beforeEnterRoomFirstTime { say("beforeEnterRoomFirstTimeScript2") }
        afterEnterRoomFirstTime { say("afterEnterRoomFirstTimeScript2") }

        exit(WEST, "bedroom")
    }

    room (3, "roomWithDescriptionClosure") {
        description {
            say "roomDescriptionClosure"
        }
        item(4, "itemWithDescriptionClosure") {
            description {
                say "itemDescriptionClosure"
            }
        }
    }

}