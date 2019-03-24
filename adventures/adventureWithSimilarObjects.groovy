adventure {
    title "Adventure Game"
    introduction "Welcome to the Adventure!"

    room (1, "bedroom") {
        description "This is your bedroom."

        item (1, "player")

        item (2, "red box") {
            synonyms "box"
            description "A box, coloured red."
        }

        item (3, "blue box") {
            synonyms "box"
            description "A box, coloured blue."
        }

    }

    room (2, "landing") {
        description "You are in the landing.  There is not much here, except for a coffee stained carpet"
        exit direction: WEST, room: "bedroom"
    }

    room (3, "bedroom") {
        exit direction: EAST, room: "landing"
    }
}