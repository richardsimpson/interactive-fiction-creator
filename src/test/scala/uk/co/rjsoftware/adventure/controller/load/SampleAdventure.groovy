package uk.co.rjsoftware.adventure.controller.load

adventure {
    introduction "Welcome to the Adventure!"

    rooms {
        "bedroom" {
            description "custom description"
            beforeEnterRoomScript "beforeEnterRoomScript"
            afterEnterRoomScript "afterEnterRoomScript"
            afterLeaveRoomScript "afterLeaveRoomScript"
            beforeEnterRoomFirstTimeScript "beforeEnterRoomFirstTimeScript"
            afterEnterRoomFirstTimeScript "afterEnterRoomFirstTimeScript"

            items {
//                "lamp" {
//                    synonyms: List[String]
//                    description "description"
//                    visible true
//                    scenery false
//                    gettable true
//                    droppable true
//                    switchable false
//                    switchOnMessage "switchOnMessage"
//                    switchOffMessage "switchOffMessage"
//                    extraMessageWhenSwitchedOn "extraMessageWhenSwitchedOn"
//                    extraMessageWhenSwitchedOff "extraMessageWhenSwitchedOff"
//
//                    // TODO: Verbs
//                }
            }
        }

        "landing" {
            description "custom description2"
            beforeEnterRoomScript "beforeEnterRoomScript2"
            afterEnterRoomScript "afterEnterRoomScript2"
            afterLeaveRoomScript "afterLeaveRoomScript2"
            beforeEnterRoomFirstTimeScript "beforeEnterRoomFirstTimeScript2"
            afterEnterRoomFirstTimeScript "afterEnterRoomFirstTimeScript2"

            exit direction: WEST, room: "bedroom"
        }

        "bedroom" {
            exit direction: EAST, room: "landing"
        }
    }
}