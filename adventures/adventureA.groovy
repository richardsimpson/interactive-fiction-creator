adventure {
    title "Adventure Game"
    introduction "Welcome to the Adventure!"

    verb ("Watch", "Watch", "WATCH {noun}") {}

    room ("bedroom") {
        description "This is your bedroom.  Clothes are strewn across the floor, there is a TV, and a lamp sits on the bedsite table."
        afterEnterRoomFirstTime {
            executeAfterTurns(5) {
                say('you decide you should tidy up')
            }
        }

        item ("player")

        item ("lamp") {
            description "A bedside lamp. with a simple on/off switch"
            switchable true
        }

        item("TV") {
            synonyms "television", "tv"
            description "A 28\" TV."
            visible true
            scenery false
            gettable false
            droppable false
            switchable true
            extraMessageWhenSwitchedOn "It is showing an old western."
            extraMessageWhenSwitchedOff "It is currently switched off."

            verb ("Watch") {
                if (isSwitchedOn('tv')) {
                    say("You watch the TV for a while.  It's showing a Western of some kind.")
                }
                else {
                    say("You watch the TV for a while.  It's just a black screen.")
                }
            }
        }
    }

    room ("landing") {
        description "You are in the landing.  There is not much here, except for a coffee stained carpet"
        exit direction: WEST, room: "bedroom"
    }

    room ("bedroom") {
        exit direction: EAST, room: "landing"
    }

}