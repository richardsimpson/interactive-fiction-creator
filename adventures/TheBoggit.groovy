adventure {
    title "The Boggit"
    introduction ""
    waitText """
        Bimbo waited.
        Time crawled slowly past."""
    getText "This magnificent act, to put it plainly, was done!"

    player "thePlayer"

    //id, friendlyName, command { synonyms }
    // both friendlyName and synonyms are optional.
    verb ("ClimbInto", "Climb into", "CLIMB INTO {noun}") {}
    verb ("ClimbOut", "Climb out", "CLIMB OUT")

    room ("tunnel like hall") {
        description """
             Bimbo stood in his comfortable tunnel like hall.  To the east was the round green door and a small
             window was set high into the wall.
             To the south, was the round green toilet."""

        beforeEnterRoomFirstTime {
            executeAfterTurns(3) {
                if (playerInRoom("tunnel like hall")) {
                    say("""
                        Suddenly, there was a resounding crash, and Grandalf tarzaned in through the window, collapsing
                        elegantly on the floor beside  Bimbo.
                        He hastily fumbled about in his robes for something, then placed a box of chocolates and a card carefully 
                        on the carpet before scrambling out through the now broken window.""")
                }
                else {
                    say('A muffled "thwump" eminates from the hall, followed by fumbling noises')
                }
                moveItemTo("card", "tunnel like hall")
                moveItemTo("expensiveChocolates", "tunnel like hall")
                executeAfterTurns(10) {
                    def chocsLocation = getItem("expensiveChocolates").getParent()
                    if (chocsLocation == getCurrentRoom() || chocsLocation == getPlayer()) {
                        say('The chocolates exploded, with a loud "BABOOM", blowing Bimbo and his surroundings to pieces.')
                        movePlayerTo('game over')
                    }
                    else {
                        say("A muffled report sounded as the chocolates exploded nearby")
                    }
                    moveItemTo("wreckedChocolates", chocsLocation.getName())
                    moveItemTo("expensiveChocolates", "hiddenRoom")
                }
            }
        }

        item ("thePlayer")

        item ("chest") {
            synonyms "large, wooden chest", "chest"
            description {
                if (isOpen("chest")) {
                    say("The chest was both heavy and open")
                }
                else {
                    say("The chest was both heavy and closed")
                }
            }
            container true
            openable true
            closeable true
            openMessage "Bimbo opened the chest, but couldn't quite see inside"
            closeMessage "Bimbo closed the chest, neatly avoiding trapping his thumb, and crunching all his fingers instead"
            contentVisibility NEVER

            verb ("ClimbInto") {
                if (isOpen('chest')) {
                    movePlayerTo("inside chest")
                }
                else {
                    say("Bimbo could not climb into the chest at this time.")
                }
            }
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
    }

    room ("inside chest") {
        description "Bimbo was in the large wooden chest"

        item ("diary") {
            synonyms "old, dusty diary", "diary"
            description """
                The diary was blank, save for some birthdays
                
                Old Goop: 12/3/26
                Fordo: 29/2/85
                Farmer Faggot: 4/7/31"""
        }

        verb ("ClimbOut") {
            movePlayerTo("tunnel like hall")
        }

    }

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

    room ("game over") {
        description {
            say("""
                Shortly, the Grim Reaper, Death, arrived to claim Bimbo.
                The end had come.
                
                For you attempts you are worth ${getScore()} lenslok devices.
                You have entered ${getTurnCounter()} profound utterances
                
                And so, amid assorted whoops, cheers, rasps, gongs and whistles, the sun set on another improbable
                chapter in Muddle Earth's sordid history....""")
            endGame()
        }
    }

    room ("hiddenRoom") {
        item ("card") {
            synonyms "small card", "card"
            description """
                On the card, in large, type-writer runes, were the words,
                I wish thee to accompany some er, 'friends' on an adventure.  See you soon.
                                         -GG-
                                         
                P.S. These chocs will self destruct in 10 minutes"""
        }

        item ("expensiveChocolates") {
            synonyms "box of expensive chocolates", "chocolates", "chocs"
            description """
                The chocolates were small and brown.  They probably contained milk chocolate, milk solids
                (20% minimum) and vegetable fat."""
            edible true
            eatMessage """
                Bimbo ate the chocolates.  They really were very good though had a somewhat unusual flavour.
                A few moments later, he exploded for no apparent reason.
                """
            onEat {
                movePlayerTo('game over')
            }
        }

        item ("wreckedChocolates") {
            synonyms "wrecked box of chocolates", "chocolates", "chocs"
            description """
                Bimbo examined the chocolates.  "The cleaning elf is gonna kill that old wizard one day!" he
                remarked to no one in particular"""
        }

    }

    room ("tunnel like hall") {
        exit direction: SOUTH, room: "round green toilet"
    }

}