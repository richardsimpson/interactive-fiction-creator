adventure {
    title "Adventure Game"
    introduction "Welcome to the Adventure!"

    room ("bedroom") {
        description "This is your bedroom."

        item ("player")

        item ("Rbox") {
            synonyms "red box", "box"
            description "A box, coloured red."
        }

        item ("Bbox") {
            synonyms "blue box", "box"
            description "A box, coloured blue."
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