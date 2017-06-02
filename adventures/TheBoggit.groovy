adventure {
    title "The Boggit"
    introduction ""

    room ("tunnel like hall") {
        description """
             Bimbo stood in his comfortable tunnel like hall.  To the east was the round green door and a small
             window was set high into the wall.
             To the south, was the round green toilet."""

        beforeEnterRoomFirstTimeScript '''
            executeAfterTurns(3) {
                say("""
                    Suddenly, there was a resounding crash, and Grandalf tarzaned in through the window, collapsing
                    elegantly on the floot beside  Bimbo.
                    He hastily fumbled about in his robes for something, then placed a box of chocolates and a card carefully 
                    on the carpet before scrambling out through the now broken window.""")
                setVisible("a small card")
                setVisible("a box of expensive chocolates")
            }
'''
        item ("a large, wooden chest") {
            synonyms "chest"
            description "The chest was both heavy and closed"

            // OPEN CHEST:
            // Bimbo opened the chest, but couldn't quite see inside
        }

        item ("door") {
            description """
                Examining the door, Bimbo saw that it had a combination lock.  A small plate below it
                bore the words,
                "Enter the correct combination."
                """
            scenery true
        }

        item ("window") {
            description "Bimbo couldn't reach the window"
            scenery true
        }

        // TODO: WAIT - allow the adventure to specify the text for the WAIT command.  Also extend to other feedback.
        // Bimbo waited
        // Time crawled slowly past

        item ("a small card") {
            synonyms "card"
            description """
                On the card, in large, type-writer runes, were the words,
                I wish thee to accompany some er, 'friends' on an adventure.  See you soon.
                                         -GG-
                                         
                P.S. These chocs will self destruct in 10 minutes"""
            visible false
        }

        item ("a box of expensive chocolates") {
            synonyms "chocolates", "chocs"
            description """
                The chocolates were small and brown.  They probably contained milk chocolate, milk solids
                (20% minimum) and vegetable fat."""
            visible false
        }

        // WHEN eat the chocolates
        // Bimbo ate the chocolates.  They really were very good though had a somewhat unusual flavour.
        // A few moments later, he exploded for no apparent reason.

        // AFTER 10 turns:
        // The chocolates exploded, with a loud "BABOOM", blowing Bimbo and his surroundings to pieces.
        // OR, if in the toilet:
        // A muffled report sounded as the chocolates exploded nearby


        item ("a wrecked box of chocolates") {
            synonyms "chocolates", "chocs"
            description """
                Bimbo examined the chocolates.  "The cleaning elf is gonna kill that old wizard one day!" he
                remarked to no one in particular"""
            visible false
        }
    }

    // climb into chest:

    room ("inside chest") {
        description "Bimbo was in the large wooden chest"

        item ("an old, dusty diary") {
            synonyms "diary"
            description """
                The diary was blank, save for some birthdays
                
                Old Goop: 12/3/26
                Fordo: 29/2/85
                Farmer Faggot: 4/7/31"""
        }

        // GET <noun>: This magnificent act, to put it plainly, was done!

    }

    // CLIMB OUT (of chest): Returns you to the 'tunnel like hall'


    room ("round green toilet") {
        description """
            Bimbo was in the round green toilet.  Furnished  with a basin, the obligatory bowl and a rather
            decrepid medicine cabinet, the room was floored with cream tiles"""
        exit direction: NORTH, room: "tunnel like hall"

        item ("toilet") {
            description "The toilet looked a bit dubious"
            scenery true
        }

        item ("basin") {
            description "The basin contained some ancient mouthwash stains but little else"
            scenery true
        }

        item ("cabinet") {
            description "The cabinet was a hefty, steel structure, securely welded shut"
            scenery true
        }

        item ("tiles") {
            description "The tiles were a bit wet but otherwise uninteresting"
            scenery true
        }
    }

    room ("tunnel like hall") {
        exit direction: SOUTH, room: "round green toilet"
    }

    startRoom "tunnel like hall"

    // end games...
}