package uk.co.rjsoftware.adventure.controller

import uk.co.rjsoftware.adventure.model.Verb

/**
  * Created by richardsimpson on 23/05/2017.
  */
object StandardVerbs {

    val NORTH:Verb = new Verb("North", List("NORTH", "N"))
    val SOUTH:Verb = new Verb("South", List("SOUTH", "S"))
    val EAST:Verb = new Verb("East", List("EAST", "E"))
    val WEST:Verb = new Verb("West", List("WEST", "W"))
    val UP:Verb = new Verb("Up", List("UP", "U"))
    val DOWN:Verb = new Verb("Down", List("DOWN", "D"))
    val LOOK:Verb = new Verb("Look", List("LOOK", "L"))
    val EXITS:Verb = new Verb("Exits", List("EXITS"))
    val EXAMINE:Verb = new Verb("Examine", List("EXAMINE {noun}", "EXAM {noun}", "X {noun}"))
    val GET:Verb = new Verb("Get", List("GET {noun}", "TAKE {noun}"))
    val DROP:Verb = new Verb("Drop", List("DROP {noun}"))
    val INVENTORY:Verb = new Verb("Inventory", List("INVENTORY", "INV", "I"))
    val TURNON:Verb = new Verb("Turn on", List("TURN ON {noun}", "TURN {noun} ON", "SWITCH ON {noun}", "SWITCH {noun} ON"))
    val TURNOFF:Verb = new Verb("Turn off", List("TURN OFF {noun}", "TURN {noun} OFF", "SWITCH OFF {noun}", "SWITCH {noun} OFF"))
    val WAIT:Verb = new Verb("Wait", List("WAIT"))
    val OPEN:Verb = new Verb("Open", List("OPEN {noun}"))
    val CLOSE:Verb = new Verb("Close", List("CLOSE {noun}"))
    val EAT:Verb = new Verb("Eat", List("EAT {noun}"))

    private val verbs:List[Verb] = NORTH :: SOUTH :: EAST :: WEST :: UP :: DOWN :: LOOK :: EXITS :: EXAMINE ::
            GET :: DROP :: INVENTORY :: TURNON :: TURNOFF :: WAIT :: OPEN :: CLOSE :: EAT :: Nil

    // TODO: Add verbs (or should these just be custom verbs):
    //          READ, SEARCH, TASTE, WEAR, LOCK, UNLOCK, DRINK, LIE ON / LIE UPON / LIE DOWN ON / LIE DOWN UPON
    //          SIT ON / SIT UPON / SIT DOWN ON / SIT DOWN UPON, HIT, PUSH, PULL, THROW, TOUCH, KILL, TIE, UNTIE,
    //          CLIMB INTO / CLIMB OUT,
    //          SPEAK TO / SPEAK / TALK TO / TALK, LISTEN TO, MOVE, SMELL / SNIFF, KNOCK, SHOW, BUY,
    //
    //          INSERT

    // Add diagonal directions

    // add inventory limit (items have weight, and player can only carry so much).  worn items should not add to total weight

    // TODO: Allow custom verb to override a standard one for a specific room or item.

    def getVerbs : List[Verb] = {
        this.verbs
    }
}
