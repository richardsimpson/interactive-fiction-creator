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

    // TODO: Clean up Verb class, and make it's constructor simpler

    private var verbs : List[Verb] = Nil
    verbs ::= new Verb(List("NORTH"), List(List("N")), false, false)
    verbs ::= new Verb(List("EAST"), List(List("E")), false, false)
    verbs ::= new Verb(List("SOUTH"), List(List("S")), false, false)
    verbs ::= new Verb(List("WEST"), List(List("W")), false, false)
    verbs ::= new Verb(List("LOOK"), List(List("L")), false, false)
    verbs ::= new Verb(List("EXITS"), false, false)
    verbs ::= new Verb(List("EXAMINE", "{noun}"), List(List("EXAM", "{noun}"), List("X", "{noun}")), false, true)
    verbs ::= new Verb(List("GET", "{noun}"), List(List("TAKE", "{noun}")), false, true)
    verbs ::= new Verb(List("DROP", "{noun}"), false, true)
    verbs ::= new Verb(List("INVENTORY"), List(List("INV"), List("I")), false, false)

    // TODO: Remove 'preposition required' from Verb class

    verbs ::= new Verb(List("TURN", "ON", "{noun}"), true, true)
    verbs ::= new Verb(List("TURN", "OFF", "{noun}"), true, true)

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

    private def determineNoun(nouns:Iterable[Item], inputWord:String) : Item = {
        if (nouns == Nil) {
            return null
        }

        if (nouns.head.getName.toUpperCase == inputWord) {
            return nouns.head
        }

        determineNoun(nouns.tail, inputWord)
    }

    private def newDetermineVerb(inputWords:Array[String]): VerbNoun = {

        @tailrec
        def iterateVerbWords(inputWords:Array[String], verbWords:List[String], result:VerbNoun): VerbNoun = {
            if (verbWords == Nil) {
                return result
            }

            if (inputWords.length == 0) {
                return null
            }

            // TODO: Allow for additional words, so 'GET THE TV' would be allowed

            if (verbWords.head.equals("{noun}")) {
                val noun = determineNoun(this.nouns.values, inputWords.head)
                if (noun == null) {
                    return null
                }
                else result.noun = noun
            }
            else if (!verbWords.head.equals(inputWords.head)) {
                return null
            }

            iterateVerbWords(inputWords.tail, verbWords.tail, result)
        }

        @tailrec
        def iterateVerbSynonymns(inputWords:Array[String], verbSynonymns:List[List[String]], result:VerbNoun): VerbNoun = {
            if (verbSynonymns == Nil) {
                return null
            }

            val verbNoun:VerbNoun = iterateVerbWords(inputWords, verbSynonymns.head, result)

            if (verbNoun != null) {
                return verbNoun
            }
            else {
                iterateVerbSynonymns(inputWords, verbSynonymns.tail, result)
            }
        }

        @tailrec
        def iterateVerbs(inputWords:Array[String], verbs:List[Verb]) : VerbNoun = {
            if (verbs == Nil) {
                return null
            }

            val verbNoun:VerbNoun = iterateVerbSynonymns(inputWords, verbs.head.getSynonyms, new VerbNoun(verbs.head, null))

            if (verbNoun != null) {
                return verbNoun
            }
            else {
                iterateVerbs(inputWords, verbs.tail)
            }
        }

        @tailrec
        def iterateWords(inputWords:Array[String]): VerbNoun = {
            if (inputWords.length == 0) {
                return null
            }

            val verbNoun:VerbNoun = iterateVerbs(inputWords, this.verbs)
            if (verbNoun != null) {
                return verbNoun
            }
            else {
                iterateWords(inputWords.tail)
            }
        }

        iterateWords(inputWords)
    }

    private def executeCommand(event:CommandEvent): Unit = {
        val words:Array[String] = event.getCommand.trim.replaceAll(" +", " ").toUpperCase.split(" ")

        if (words.length < 1) {
            say("There are no words...")
            return
        }

        // TODO: HELP
        // TODO: GET ALL / TAKE ALL

//        val verbLocation : VerbLocation = determineVerb(words)
//        if (verbLocation == null) {
//            say("I don't understand what you are trying to do.")
//            return
//        }
//
//        var nextWordIndex = verbLocation.location+1
//        var verb = verbLocation.verb.getVerb
//
//        if (verbLocation.verb.isPrepositionRequired) {
//            verb = verb + " " + words(nextWordIndex)
//            nextWordIndex = nextWordIndex + 1
//        }
//
//        if (!verbLocation.verb.isNounRequired) {
//            executeCommand(verb, null)
//            return
//        }
//
//        // assuming here that the noun immediately follows the verb (or the preposition)
//        var noun : Item = null
//        if (words.length > nextWordIndex) {
//            noun = determineNoun(this.nouns.values, words(nextWordIndex))
//        }
//
//        if (noun == null) {
//            say(verb + " what?")
//            return
//        }
//
//        // scala's preferred equivalent of 'instanceOf' is to use pattern matching
//        verbLocation.verb match {
//            case customVerb: CustomVerb => executeCustomVerb(customVerb, noun)
//            case _ => executeCommand(verb, noun)
//        }

        val verbNoun:VerbNoun = newDetermineVerb(words)
        if (verbNoun == null) {
            say("I don't understand what you are trying to do.")
            return
        }

        // TODO: if verb requires noun, and there is not one, say(verb + " what?")

        verbNoun.verb match {
            case customVerb: CustomVerb => executeCustomVerb(customVerb, verbNoun.noun)
            case _ => executeCommand(verbNoun.verb.getVerb, verbNoun.noun)
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
            case "EXAMINE {noun}" => examine(item)
            case "GET {noun}" => get(item)
            case "DROP {noun}" => drop(item)
            case "INVENTORY" => inventory()
            case "TURN ON {noun}" => turnOn(item)
            case "TURN OFF {noun}" => turnOff(item)
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

private class VerbNoun(var verb:Verb, var noun:Item)

