package uk.co.rjsoftware.adventure.controller.load

adventure {
    title "Adventure Game"
    introduction "Welcome to the Adventure!"

    player "player"

    verb ("Watch", "Watch", "WATCH {noun}") {
        synonyms "LOOK AT {noun}", "VIEW {noun}"
    }

    verb ("Throw", "THROW {noun}")

    room ("bedroom") {
        description "custom description"
        beforeEnterRoomScript "beforeEnterRoomScript"
        afterEnterRoomScript "afterEnterRoomScript"
        afterLeaveRoomScript "afterLeaveRoomScript"
        beforeEnterRoomFirstTimeScript "beforeEnterRoomFirstTimeScript"
        afterEnterRoomFirstTimeScript "afterEnterRoomFirstTimeScript"

        exit(EAST, "landing") {
            scenery true
            prefix "prefix"
            suffix "suffix"
        }

        item ("player")

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
            extraDescriptionWhenSwitchedOn "extraDescriptionWhenSwitchedOn"
            extraDescriptionWhenSwitchedOff "extraDescriptionWhenSwitchedOff"
        }

        item("tv") {
            synonyms "television"
            description "description"
            visible true
            scenery false
            gettable false
            droppable false
            switchable true
            switchOnMessage "switchOnMessage"
            switchOffMessage "switchOffMessage"
            extraDescriptionWhenSwitchedOn "extraDescriptionWhenSwitchedOn"
            extraDescriptionWhenSwitchedOff "extraDescriptionWhenSwitchedOff"

            verb ("Watch") {
                script "say('watchVerb')"
            }

            verb ("Throw") {
                script "say('throwVerb')"
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

        exit(WEST, "bedroom")
    }

    room ("roomWithDescriptionClosure") {
        descriptionScriptEnabled true
        descriptionScript """
            say 'roomDescriptionClosure'
        """
        item("itemWithDescriptionClosure") {
            descriptionScriptEnabled true
            descriptionScript """
                say 'itemDescriptionClosure'
            """
        }
    }

}