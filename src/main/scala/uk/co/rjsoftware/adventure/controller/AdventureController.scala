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

    private var verbs : List[Verb] = StandardVerbs.getVerbs
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

    // initialise the view
    say(this.adventure.getIntroduction)
    say("")

    this.currentRoom = this.adventure.getStartRoom

    look()

    // FOR TESTING ONLY
    def getCurrentRoom : Room = {
        this.currentRoom
    }

    // FOR TESTING ONLY
    def getPlayer : Player = {
        this.player
    }

    // TODO: Extract the parser into another class

    private def newDetermineVerb(inputWords:Array[String]): VerbNoun = {

        @tailrec
        def iterateNounSynonyms(synonyms:List[String], inputWord:String) : String = {
            if (synonyms == Nil) {
                return null
            }

            if (synonyms.head.toUpperCase == inputWord) {
                return synonyms.head
            }

            iterateNounSynonyms(synonyms.tail, inputWord)
        }

        @tailrec
        def determineNoun(nouns:Iterable[Item], inputWord:String) : Item = {
            if (nouns == Nil) {
                return null
            }

            val synonym:String = iterateNounSynonyms(nouns.head.getSynonyms, inputWord)

            if (synonym != null) {
                return nouns.head
            }

            determineNoun(nouns.tail, inputWord)
        }

        @tailrec
        def iterateInputToFindNoun(inputWords:Array[String]) : Item = {
            if (inputWords.length == 0) {
                return null
            }

            val item:Item = determineNoun(this.nouns.values, inputWords.head)

            if (item != null) {
                return item
            }

            iterateInputToFindNoun(inputWords.tail)
        }

        def iterateVerbWords(inputWords:Array[String], verbWords:String, result:VerbNoun): VerbNoun = {

            @tailrec
            def doIterateVerbWords(inputWords:Array[String], verbWords:Array[String], result:VerbNoun): VerbNoun = {
                if (verbWords.length == 0) {
                    return result
                }

                if (inputWords.length == 0) {
                    return null
                }

                if (verbWords.head.equals("{noun}")) {
                    val noun = iterateInputToFindNoun(inputWords)
                    if (noun == null) {
                        return null
                    }
                    else result.noun = noun
                }
                else if (!verbWords.head.equals(inputWords.head)) {
                    return null
                }

                doIterateVerbWords(inputWords.tail, verbWords.tail, result)
            }

            doIterateVerbWords(inputWords, verbWords.split(" "), result)
        }

        @tailrec
        def iterateVerbSynonymns(inputWords:Array[String], verbSynonymns:List[String], result:VerbNoun): VerbNoun = {
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

    private def isItemInRoomOrPlayerInventory(item:Item) : Boolean = {
        this.currentRoom.contains(item) || this.player.contains(item)
    }

    private def executeCustomVerb(verb:CustomVerb, item:Item): Unit = {
        var found:Boolean = isItemInRoomOrPlayerInventory(item)

        if (!found) {
            say("Cannot find the " + item.getName)
            return
        }

        if (!item.getVerbs.contains(verb)) {
            say("You cannot do that with the " + item.getName)
            return
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
            case "SOUTH" => move(Direction.SOUTH)
            case "EAST" => move(Direction.EAST)
            case "WEST" => move(Direction.WEST)
            case "UP" => move(Direction.UP)
            case "DOWN" => move(Direction.DOWN)
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
        if (!this.currentRoom.getItems.isEmpty) {
            say("You can also see:")
            for (item <- this.currentRoom.getItems.values) {
                say(item.getName)
            }
        }
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
        else if (!item.isGettable) {
            say("You cannot pick up the " + item.getName)
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
        else if (!item.isDroppable) {
            say("You cannot drop the " + item.getName)
        }
        else {
            this.player.removeItem(item)
            this.currentRoom.addItem(item)
            say("You drop the " + item.getName)
        }
    }

    private def examine(item: Item): Unit = {
        var found:Boolean = isItemInRoomOrPlayerInventory(item)

        if (!found) {
            say("Cannot find the " + item.getName)
        }
        else {
            say(item.getItemDescription)
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

    private def turnOn(item: Item) : Unit = {
        var found:Boolean = isItemInRoomOrPlayerInventory(item)

        if (!found) {
            say("Cannot find the " + item.getName)
        }
        else if (!item.isSwitchable) {
            say("You can't turn on the " + item.getName)
        }
        else {
            if (item.isOn) {
                say(item.getName + " is already on")
            }
            else {
                item.switchOn()
                say(item.getSwitchOnMessage.getOrElse("You turn on the " + item.getName))
            }
        }
    }

    private def turnOff(item: Item) : Unit = {
        var found:Boolean = isItemInRoomOrPlayerInventory(item)

        if (!found) {
            say("Cannot find the " + item.getName)
        }
        else if (!item.isSwitchable) {
            say("You can't turn off the " + item.getName)
        }
        else {
            if (item.isOff) {
                say(item.getName + " is already off")
            }
            else {
                item.switchOff()
                say(item.getSwitchOffMessage.getOrElse("You turn off the " + item.getName))
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

private class VerbNoun(var verb:Verb, var noun:Item)

