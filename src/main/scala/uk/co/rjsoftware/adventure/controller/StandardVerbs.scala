package uk.co.rjsoftware.adventure.controller

import uk.co.rjsoftware.adventure.model.Verb

/**
  * Created by richardsimpson on 23/05/2017.
  */
object StandardVerbs {

    var verbs:List[Verb] = Nil

    verbs ::= new Verb(List("NORTH", "N"))
    verbs ::= new Verb(List("EAST", "E"))
    verbs ::= new Verb(List("SOUTH", "S"))
    verbs ::= new Verb(List("WEST", "W"))
    verbs ::= new Verb(List("LOOK", "L"))
    verbs ::= new Verb(List("EXITS"))
    verbs ::= new Verb(List("EXAMINE {noun}", "EXAM {noun}", "X {noun}"))
    verbs ::= new Verb(List("GET {noun}", "TAKE {noun}"))
    verbs ::= new Verb(List("DROP {noun}"))
    verbs ::= new Verb(List("INVENTORY", "INV", "I"))
    verbs ::= new Verb(List("TURN ON {noun}", "TURN {noun} ON", "SWITCH ON {noun}", "SWITCH {noun} ON"))
    verbs ::= new Verb(List("TURN OFF {noun}", "TURN {noun} OFF", "SWITCH OFF {noun}", "SWITCH {noun} OFF"))

    // TODO: Add verbs:
    //          READ, SEARCH, TASTE, WEAR, SWITCH ON/OFF, LOCK, UNLOCK, EAT, DRINK, LIE ON / LIE UPON / LIE DOWN ON / LIE DOWN UPON
    //          SIT ON / SIT UPON / SIT DOWN ON / SIT DOWN UPON, HIT, PUSH, PULL, THROW, TOUCH, KILL, TIE, UNTIE, CLIMB,
    //          SPEAK TO / SPEAK / TALK TO / TALK, LISTEN TO, MOVE, SMELL / SNIFF, KNOCK, SHOW, BUY,

    // TODO: Allow verbs to be associated with Rooms?  Like WEST, LOOK, etc?
    // TODO: Allow custom verb to override a standard one for a specific room or item.
    // TODO: Support synonyms for items: e.g. Newspaper and Paper

    def getVerbs : List[Verb] = {
        this.verbs
    }
}
