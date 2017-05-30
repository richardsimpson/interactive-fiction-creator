package uk.co.rjsoftware.adventure.controller

import javax.script.ScriptEngineManager

import groovy.lang.Closure
import uk.co.rjsoftware.adventure.controller.customscripts.ScriptExecutor
import uk.co.rjsoftware.adventure.model._
import uk.co.rjsoftware.adventure.view.{CommandEvent, MainWindow}

import scala.annotation.tailrec

/**
  * Created by richardsimpson on 19/05/2017.
  */
class AdventureController(private val adventure:Adventure, private val mainWindow: MainWindow) {

    private var currentRoom : Room = null
    private val player : Player = new Player()
    private var visitedRooms : List[Room] = Nil
    private var scheduledScripts:List[ScheduledScript] = Nil

    private var verbs : List[Verb] = StandardVerbs.getVerbs
    // add in any custom verbs
    verbs ++= this.adventure.getCustomVerbs

    private var nouns : Map[String, Item] = Map[String, Item]()
    for (room <- adventure.getRooms) {
        // TODO: Why does this work?
        nouns ++= room.getItems.map({case (key, value) => (key.toUpperCase, value)})
    }

    mainWindow.addListener(executeCommand)

    // initialise the view
    say(this.adventure.getIntroduction)
    say("")

    move(this.adventure.getStartRoom)

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

        try {
            if (words.length < 1) {
                say("There are no words...")
                return
            }

            // TODO: HELP
            // TODO: GET ALL / TAKE ALL

            val verbNoun:VerbNoun = newDetermineVerb(words)
            if (verbNoun == null) {
                say("I don't understand what you are trying to do.")
                say("")
                return
            }

            // TODO: if verb requires noun, and there is not one, say(verb + " what?")

            verbNoun.verb match {
                case customVerb: CustomVerb => executeCustomVerb(customVerb, verbNoun.noun)
                case _ => executeCommand(verbNoun.verb.getVerb, verbNoun.noun)
            }
        }
        finally {
            // execute any scripts which should be executed on this turn.
            this.scheduledScripts.foreach((scheduledScript) => {
                scheduledScript.decrementTurnCount
                if (scheduledScript.getTurnCount <= 0) {
                    scheduledScript.getScript().call()
                }
            })

            // and then remove them from the list
            this.scheduledScripts = this.scheduledScripts.filter((scheduledScript) => {
                scheduledScript.getTurnCount > 0
            })
        }
    }

    private def isItemInRoomOrPlayerInventory(item:Item) : Boolean = {
        this.currentRoom.contains(item) || this.player.contains(item)
    }

    private def executeCustomVerb(verb:CustomVerb, item:Item): Unit = {
        var found:Boolean = isItemInRoomOrPlayerInventory(item)

        if (!found) {
            say("Cannot find the " + item.getName)
            say("")
            return
        }

        if (!item.getVerbs.contains(verb)) {
            say("You cannot do that with the " + item.getName)
            say("")
            return
        }

        var script:Option[String] = item.getVerbs.get(verb)

        executeScript(script.get)
        say("")
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
                if (item.isVisible && !item.isScenery) {
                    say(item.getName)
                }
            }
        }
        say("")
    }

    private def exits() : Unit = {
        val outputText : StringBuilder = new StringBuilder("From here you can go ")

        for (direction:Direction <- this.currentRoom.getExits.keySet) {
            outputText.append(direction.getDescription).append(", ")
        }

        say(outputText.toString())
        say("")
    }

    private def move(direction: Direction) : Unit = {
        val newRoom:Option[Room] = this.currentRoom.getExit(direction)

        if (newRoom.isEmpty) {
            say("You cannot go that way.")
            say("")
        }
        else {
            move(newRoom.get)
        }
    }

    private def move(toRoom:Room) : Unit = {
        // process leaving the previous room
        if (this.currentRoom != null) {
            if (this.currentRoom.getAfterLeaveRoomScript != null) {
                executeScript(this.currentRoom.getAfterLeaveRoomScript)
            }
        }

        // now deal with entering the new room
        var firstVisit : Boolean = false
        this.currentRoom = toRoom

        if (!this.visitedRooms.contains(toRoom)) {
            firstVisit = true
            this.visitedRooms ::= this.currentRoom
        }

        if (firstVisit) {
            if (this.currentRoom.getBeforeEnterRoomFirstTimeScript != null) {
                executeScript(this.currentRoom.getBeforeEnterRoomFirstTimeScript)
            }
        }

        if (this.currentRoom.getBeforeEnterRoomScript != null) {
            executeScript(this.currentRoom.getBeforeEnterRoomScript)
        }

        look()

        if (firstVisit) {
            if (this.currentRoom.getAfterEnterRoomFirstTimeScript != null) {
                executeScript(this.currentRoom.getAfterEnterRoomFirstTimeScript)
            }
        }

        if (this.currentRoom.getAfterEnterRoomScript != null) {
            executeScript(this.currentRoom.getAfterEnterRoomScript)
        }

    }

    private def get(item:Item) : Unit = {
        val found:Boolean = this.currentRoom.contains(item)

        if (!found || !item.isVisible) {
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
        say("")
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
        say("")
    }

    private def examine(item: Item): Unit = {
        var found:Boolean = isItemInRoomOrPlayerInventory(item)

        if (!found || !item.isVisible) {
            say("Cannot find the " + item.getName)
        }
        else {
            say(item.getItemDescription)
        }
        say("")
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
        say("")
    }

    private def turnOn(item: Item) : Unit = {
        var found:Boolean = isItemInRoomOrPlayerInventory(item)

        if (!found || !item.isVisible) {
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
                var switchOnMessage:String = item.getSwitchOnMessage
                if (switchOnMessage == null) {
                    switchOnMessage = "You turn on the " + item.getName
                }
                say(switchOnMessage)
            }
        }
        say("")
    }

    private def turnOff(item: Item) : Unit = {
        var found:Boolean = isItemInRoomOrPlayerInventory(item)

        if (!found || !item.isVisible) {
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
                var switchOffMessage:String = item.getSwitchOffMessage
                if (switchOffMessage == null) {
                    switchOffMessage = "You turn off the " + item.getName
                }
                say(switchOffMessage)
            }
        }
        say("")
    }

    private def executeScript(script:String): Unit = {
        val executor : ScriptExecutor = new ScriptExecutor(this)
        executor.executeScript(script)
    }

    //
    // Utility functions - can be used by Scripts.
    //

    def say(outputText:String) : Unit = {
        this.mainWindow.say(outputText)
    }

    def isSwitchedOn(itemName:String) : Boolean = {
        val item:Item = getItem(itemName)
        item.isOn
    }

    def executeAfterTurns(turns:Int, script:Closure[Unit]) : Unit = {
        this.scheduledScripts :+= new ScheduledScript(turns, script)
    }

    // TODO: add script functions for:
    //      print a message (without carriage return)
    //      clear the screen.
    //      player is carrying object
    //      player is NOT carrying object
    //      player is in room
    //      player is not in room
    //      object is visible
    //      object is not visible.

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

