package uk.co.rjsoftware.adventure.controller

import javax.script.ScriptEngineManager

import uk.co.rjsoftware.adventure.model._
import uk.co.rjsoftware.adventure.view.{CommandEvent, MainWindow}

import scala.annotation.tailrec

/**
  * Created by richardsimpson on 19/05/2017.
  */
class AdventureController(private val adventure:Adventure, private val mainWindow: MainWindow) {

    private val manager = new ScriptEngineManager
    private val engine = manager.getEngineByName("nashorn")


    private var currentRoom : Room = null
    private val player : Player = new Player()

    private var verbs : List[Verb] = Nil
    verbs ::= new Verb("NORTH", List("N"), false, false)
    verbs ::= new Verb("EAST", List("E"), false, false)
    verbs ::= new Verb("SOUTH", List("S"), false, false)
    verbs ::= new Verb("WEST", List("W"), false, false)
    verbs ::= new Verb("LOOK", List("L"), false, false)
    verbs ::= new Verb("EXITS", false, false)
    verbs ::= new Verb("EXAMINE", List("EXAM", "X"), false, true)
    verbs ::= new Verb("GET", List("TAKE"), false, true)
    verbs ::= new Verb("DROP", false, true)
    verbs ::= new Verb("INVENTORY", List("INV", "I"), false, false)

    // TODO: change TURN verb to be 'TURN ON', to differentiate it from TURN, and to allow complex
    //       'verbs' such as DO A THING WITH <OBJECT>
    //       Then can remove 'preposition required' from Verb class

    verbs ::= new Verb("TURN", true, true)

    // add in any custom verbs
    for (room <- adventure.getRooms) {
        for (item <- room.getItems.values) {
            for (customVerb <- item.getVerbs.keys) {
                verbs ::= customVerb
            }
        }
    }

    private var nouns : Map[String, Item] = Map[String, Item]()
    for (room <- adventure.getRooms) {
        // TODO: Why does this work?
        nouns ++= room.getItems.map({case (key, value) => (key.toUpperCase, value)})
    }

    mainWindow.addListener(executeCommand)

    // TODO: See if you can move init into the constructor
    def init() : Unit = {
        say(this.adventure.getIntroduction)
        say("")

        this.currentRoom = this.adventure.getStartRoom

        look()
        exits()
    }

    // FOR TESTING ONLY
    def getCurrentRoom : Room = {
        this.currentRoom
    }

    // FOR TESTING ONLY
    def getPlayer : Player = {
        this.player
    }

    @tailrec
    private def determineVerb(verbs:List[Verb], inputWord:String): Verb = {
        if (verbs == Nil) {
            return null
        }

        if (verbs.head.getSynonyms.contains(inputWord)) {
            return verbs.head
        }

        determineVerb(verbs.tail, inputWord)
    }

    private def determineVerb(inputWords:Array[String]): VerbLocation = {
        @tailrec
        def doDetermineVerb(inputWords:Array[String], wordNumber:Int) : VerbLocation = {
            if (inputWords.length == 0) {
                return null
            }

            val possibleVerb:Verb = determineVerb(this.verbs, inputWords.head)
            if (possibleVerb != null) {
                if (possibleVerb.isPrepositionRequired) {
                    if (inputWords.tail.length > 0) {
                        return new VerbLocation(possibleVerb, wordNumber)
                    }
                }
                else {
                    return new VerbLocation(possibleVerb, wordNumber)
                }
            }

            doDetermineVerb(inputWords.tail, wordNumber+1)
        }

        doDetermineVerb(inputWords, 0)
    }

    private def determineNoun(nouns:Iterable[Item], inputWord:String) : Item = {
        if (nouns == Nil) {
            return null
        }

        if (nouns.head.getName.toUpperCase == inputWord) {
            return nouns.head
        }

        determineNoun(nouns.tail, inputWord)
    }

    private def executeCommand(event:CommandEvent): Unit = {
        val words:Array[String] = event.getCommand.trim.replaceAll(" +", " ").toUpperCase.split(" ")

        if (words.length < 1) {
            say("There are no words...")
            return
        }

        // TODO: Improve the parser:
        //          1) Put back support for 'TURN ON'
        //          2) Support arbitrary verbs
        // TODO: HELP
        // TODO: GET ALL / TAKE ALL

        val verbLocation : VerbLocation = determineVerb(words)
        if (verbLocation == null) {
            say("I don't understand what you are trying to do.")
            return
        }

        var nextWordIndex = verbLocation.location+1
        var verb = verbLocation.verb.getVerb

        if (verbLocation.verb.isPrepositionRequired) {
            verb = verb + " " + words(nextWordIndex)
            nextWordIndex = nextWordIndex + 1
        }

        if (!verbLocation.verb.isNounRequired) {
            executeCommand(verb, null)
            return
        }

        // assuming here that the noun immediately follows the verb (or the preposition)
        var noun : Item = null
        if (words.length > nextWordIndex) {
            noun = determineNoun(this.nouns.values, words(nextWordIndex))
        }

        if (noun == null) {
            say(verb + " what?")
            return
        }

        // scala's preferred equivilent of 'instanceOf' is to use pattern matching
        verbLocation.verb match {
            case customVerb: CustomVerb => executeCustomVerb(customVerb, noun)
            case _ => executeCommand(verb, noun)
        }
    }

    private def executeCustomVerb(verb:CustomVerb, item:Item): Unit = {
        var found:Boolean = this.currentRoom.contains(item)

        if (!found) {
            found = this.player.contains(item)
        }

        if (!found) {
            say("Cannot find the " + item.getName)
            return
        }

        if (!item.getVerbs.contains(verb)) {
            say("You cannot do that with the " + item.getName)
        }

        var script:Option[String] = item.getVerbs.get(verb)

        engine.put("controller", new AdventureControllerWrapper(this))
        engine.eval(
            "function say(message) { controller.say(message) }\n" +
            "function isSwitchedOn(itemName) { return controller.isSwitchedOn(itemName) }\n" +
            "function isSwitchedOff(itemName) { return controller.isSwitchedOff(itemName) }\n" +
            "\n" +
            script.get)
    }

    private def executeCommand(verb:String, item:Item): Unit = {
        verb match {
            case "NORTH" => move(Direction.NORTH)
            case "EAST" => move(Direction.EAST)
            case "SOUTH" => move(Direction.SOUTH)
            case "WEST" => move(Direction.WEST)
            case "LOOK" => look()
            case "EXITS" => exits()
            case "EXAMINE" => examine(item)
            case "GET" => get(item)
            case "DROP" => drop(item)
            case "INVENTORY" => inventory()
            case "TURN ON" => turnOn(item)
            case "TURN OFF" => turnOff(item)
            case _ => throw new RuntimeException("Unexpected verb")
        }
    }

    //
    // Standard Verbs
    //

    private def look() : Unit = {
        say(this.currentRoom.getDescription)
    }

    private def exits() : Unit = {
        val outputText : StringBuilder = new StringBuilder("From here you can go ")

        for (direction:Direction <- this.currentRoom.getExits.keySet) {
            outputText.append(direction.getDescription).append(", ")
        }

        say(outputText.toString())
    }

    private def move(direction: Direction) : Unit = {
        val newRoom:Option[Room] = this.currentRoom.getExit(direction)

        if (newRoom.isEmpty) {
            say("You cannot go that way.")
        }
        else {
            this.currentRoom = newRoom.get
            look()
        }
    }

    private def get(item:Item) : Unit = {
        val found:Boolean = this.currentRoom.contains(item)

        if (!found) {
            say("Cannot find the " + item.getName)
        }
        else {
            this.currentRoom.removeItem(item)
            this.player.addItem(item)
            say("You pick up the " + item.getName)
        }
    }

    private def drop(item:Item) : Unit = {
        val found:Boolean = this.player.contains(item)

        if (!found) {
            say("You do not have the " + item.getName)
        }
        else {
            this.player.removeItem(item)
            this.currentRoom.addItem(item)
            say("You drop the " + item.getName)
        }
    }

    private def examine(item: Item): Unit = {
        var found:Boolean = this.currentRoom.contains(item)

        if (!found) {
            found = this.player.contains(item)
        }

        if (!found) {
            say("Cannot find the " + item.getName)
        }
        else {
            say(item.getDescription)
        }
    }

    private def inventory() : Unit = {
        say("You are currently carrying:")
        if (this.player.getItems.isEmpty) {
            say("Nothing")
        }
        else {
            for (item:Item <- this.player.getItems.values) {
                say(item.getName)
            }
        }
    }

    // TODO: Provide feedback when turning on an item
    private def turnOn(item: Item) : Unit = {
        turnOnOrOff(item, turningOn = true)
    }

    private def turnOff(item: Item) : Unit = {
        turnOnOrOff(item, turningOn = false)
    }

    private def turnOnOrOff(item:Item, turningOn:Boolean) : Unit = {
        var found:Boolean = this.currentRoom.contains(item)

        if (!found) {
            found = this.player.contains(item)
        }

        if (!found) {
            say("Cannot find the " + item.getName)
        }
        else {
            if (turningOn && item.isOn) {
                say(item.getName + " is already on")
            }
            if (!turningOn && item.isOff) {
                say(item.getName + " is already off")
            }
            else if (turningOn) {
                item.switchOn()
                // TODO: now, how to create a custom verb, such as 'watch'?
                // scripts need to be attached to verbs
                // items can have arbitrary verbs, as well as standard ones.
                // check for 'standard' verbs that don't require an object (NORTH, INV, etc), then check the
                // verb against the object, which can have custom (or standard) verbs associated with it, together
                // with custom scripts.
            }
            else {
                item.switchOff()
            }
        }
    }

    //
    // Wrapper for AdventureController.  This exists purely to expose the various methods that might be
    // required by scripts.  The wrapper makes these public, to avoid Scala mangling their names
    //

    private class AdventureControllerWrapper(controller:AdventureController) {
        def say(outputText:String) : Unit = controller.say(outputText)
        def isSwitchedOn(itemName:String) : Boolean = controller.isSwitchedOn(itemName)
        def isSwitchedOff(itemName:String) : Boolean = !controller.isSwitchedOn(itemName)
    }

    //
    // Utility functions - can be used by Scripts.
    //

    private def say(outputText:String) : Unit = {
        this.mainWindow.say(outputText)
    }

    private def isSwitchedOn(itemName:String) : Boolean = {
        val item:Item = getItem(itemName)
        item.isOn
    }

    //
    // private helper methods to support the methods that can be used by Scripts
    //
    private def getItem(itemName:String) : Item = {
        val item:Option[Item] = this.nouns.get(itemName.toUpperCase)

        if (item.isEmpty) {
            say("ERROR: Specified item does not exist.")
            throw new RuntimeException("Specified item does not exist.")
        }

        item.get
    }


}

private class VerbLocation(val verb:Verb, val location:Int)