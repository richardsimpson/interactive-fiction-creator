adventure {
    title "Adventure Game"
    introduction "Welcome to the Adventure!"

    room ("bedroom") {
        description "This is your bedroom."

        item ("player")

        item ("red box") {
            synonyms "box"
            description "A box, coloured red."
        }

        item ("blue box") {
            synonyms "box"
            description "A box, coloured blue."
        }

    }

    room ("landing") {
        description "You are in the landing.  There is not much here, except for a coffee stained carpet"
        exit(WEST, "bedroom")
    }

    room ("bedroom") {
        exit(EAST, "landing")
    }
}