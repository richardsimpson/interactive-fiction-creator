package uk.co.rjsoftware.adventure.controller

import groovy.transform.TypeChecked
import uk.co.rjsoftware.adventure.model.Verb

@TypeChecked
class StandardVerbs {

    static final Verb NORTH = new Verb("North", "North", ["NORTH", "N"])
    static final Verb SOUTH = new Verb("South", "South", ["SOUTH", "S"])
    static final Verb EAST = new Verb("East", "East", ["EAST", "E"])
    static final Verb WEST = new Verb("West", "West", ["WEST", "W"])
    static final Verb UP = new Verb("Up", "Up", ["UP", "U"])
    static final Verb DOWN = new Verb("Down", "Down", ["DOWN", "D"])
    static final Verb LOOK = new Verb("Look", "Look", ["LOOK", "L"])
    static final Verb EXITS = new Verb("Exits", "Exits", ["EXITS"])
    static final Verb EXAMINE = new Verb("Examine", "Examine", ["EXAMINE {noun}", "EXAM {noun}", "X {noun}"])
    static final Verb GET = new Verb("Get", "Get", ["GET {noun}", "TAKE {noun}"])
    static final Verb DROP = new Verb("Drop", "Drop", ["DROP {noun}"])
    static final Verb INVENTORY = new Verb("Inventory", "Inventory", ["INVENTORY", "INV", "I"])
    static final Verb TURNON = new Verb("TurnOn","Turn on", ["TURN ON {noun}", "TURN {noun} ON", "SWITCH ON {noun}", "SWITCH {noun} ON"])
    static final Verb TURNOFF = new Verb("TurnOff","Turn off", ["TURN OFF {noun}", "TURN {noun} OFF", "SWITCH OFF {noun}", "SWITCH {noun} OFF"])
    static final Verb WAIT = new Verb("Wait", "Wait", ["WAIT"])
    static final Verb OPEN = new Verb("Open", "Open", ["OPEN {noun}"])
    static final Verb CLOSE = new Verb("Close", "Close", ["CLOSE {noun}"])
    static final Verb EAT = new Verb("Eat", "Eat", ["EAT {noun}"])

    private static final List<Verb> verbs = [NORTH, SOUTH, EAST, WEST, UP, DOWN, LOOK, EXITS, EXAMINE,
                                             GET, DROP, INVENTORY, TURNON, TURNOFF, WAIT, OPEN, CLOSE, EAT]

    // TODO: Add verbs (or should these just be custom verbs):
    //          READ, SEARCH, TASTE, WEAR, LOCK, UNLOCK, DRINK, LIE ON / LIE UPON / LIE DOWN ON / LIE DOWN UPON
    //          SIT ON / SIT UPON / SIT DOWN ON / SIT DOWN UPON, HIT, PUSH, PULL, THROW, TOUCH, KILL, TIE, UNTIE,
    //          CLIMB INTO / CLIMB OUT,
    //          SPEAK TO / SPEAK / TALK TO / TALK, LISTEN TO, MOVE, SMELL / SNIFF, KNOCK, SHOW, BUY,
    //
    //          INSERT
    //
    //          PUT {noun} IN {noun}

    // Add diagonal directions

    // add inventory limit (items have weight, and player can only carry so much).  worn items should not add to total weight

    // TODO: Allow custom verb to override a standard one for a specific room or item.

    static List<Verb> getVerbs () {
        verbs
    }
}
