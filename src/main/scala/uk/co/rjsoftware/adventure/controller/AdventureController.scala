package uk.co.rjsoftware.adventure.controller

import groovy.lang.Closure
import uk.co.rjsoftware.adventure.controller.customscripts.ScriptExecutor
import uk.co.rjsoftware.adventure.controller.load.Loader
import uk.co.rjsoftware.adventure.model._
import uk.co.rjsoftware.adventure.utils.StringUtils
import uk.co.rjsoftware.adventure.view.{CommandEvent, LoadEvent, MainWindow}

import scala.annotation.tailrec

/**
  * Created by richardsimpson on 19/05/2017.
  */
class AdventureController(private val mainWindow: MainWindow) {

    private var adventure : Adventure = _
    private var currentRoom : Room = _
    private var player : Player = _
    private var visitedRooms : List[Room] = Nil
    private var scheduledScripts:List[ScheduledScript] = Nil
    private var turnCounter : Int = 0
    private var disambiguating : Boolean = false
    private var disambiguatingVerb : Verb = _
    private var disambiguatingNouns : List[Item] = Nil

    private var verbs : List[Verb] = Nil
    private var nouns : Map[String, Item] = Map[String, Item]()
    private var rooms : Map[String, Room] = Map[String, Room]()

    mainWindow.addCommandListener(executeCommand)
    mainWindow.addLoadListener(loadAdventure)

    // FOR TESTING ONLY
    def getCurrentRoom : Room = {
        this.currentRoom
    }

    // FOR TESTING ONLY
    def getPlayer : Player = {
        this.player
    }

    private def loadAdventure(event:LoadEvent) : Unit = {
        if (event.getFile != null) {
            val adventure:Adventure = Loader.loadAdventure(event.getFile)
            loadAdventure(adventure)
        }
    }

    def loadAdventure(adventure:Adventure) {
        this.player = new Player()
        this.visitedRooms = Nil
        this.scheduledScripts = Nil
        this.turnCounter = 0

        this.verbs = StandardVerbs.getVerbs
        // add in any custom verbs
        this.verbs ++= adventure.getCustomVerbs

        this.nouns = Map[String, Item]()
        this.rooms = Map[String, Room]()
        for (room:Room <- adventure.getRooms) {
            nouns ++= room.getItems.map({case (key, value) => (key.toUpperCase, value)})
            rooms += (room.getName.toUpperCase -> room)
        }

        // initialise the view
        this.mainWindow.loadAdventure(adventure.getTitle, adventure.getIntroduction)

        this.adventure = adventure

        moveTo(adventure.getStartRoom)
        say("")
    }

    // TODO: Extract the parser into another class

    private def determineVerbNoun(inputWords:Array[String]): VerbNoun = {

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
        def determineNouns(nouns:Iterable[Item], inputWord:String, validNouns:Set[Item]) : Set[Item] = {
            if (nouns == Nil) {
                return validNouns
            }

            val synonym:String = iterateNounSynonyms(nouns.head.getSynonyms, inputWord)

            if (synonym == null) {
                determineNouns(nouns.tail, inputWord, validNouns)
            }
            else {
                determineNouns(nouns.tail, inputWord, validNouns + nouns.head)
            }
        }

        @tailrec
        def iterateInputToFindNouns(inputWords:Array[String], validNouns:Set[Item]) : Set[Item] = {
            if (inputWords.length == 0) {
                return validNouns
            }

            val items:Set[Item] = determineNouns(
                this.nouns.values.filter((item) => isItemInRoomOrPlayerInventory(item)),
                inputWords.head, Set.empty)

            iterateInputToFindNouns(inputWords.tail, validNouns ++ items)
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
                    val nouns:Set[Item] = iterateInputToFindNouns(inputWords, Set.empty)
                    if (nouns == null) {
                        return null
                    }
                    else result.nouns = nouns.toList
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

            val verbNoun:VerbNoun = iterateVerbSynonymns(inputWords, verbs.head.getSynonyms, new VerbNoun(verbs.head, Nil))

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
        if (this.disambiguating) {
            doDisambiguateCommand(event)
        }
        else {
            doExecuteCommand(event)
        }
    }

    private def doExecuteCommand(event:CommandEvent): Unit = {
        val words:Array[String] = event.getCommand.trim.replaceAll(" +", " ").toUpperCase.split(" ")

        try {
            this.turnCounter = this.turnCounter + 1

            if (words.length < 1) {
                say("There are no words...")
                return
            }

            // TODO: HELP
            // TODO: GET ALL / TAKE ALL

            val verbNoun:VerbNoun = determineVerbNoun(words)
            if (verbNoun == null) {
                say("I don't understand what you are trying to do.")
                return
            }

            // TODO: if verb requires noun, and there is not one, say(verb + " what?")

            verbNoun.verb match {
                case customVerb: CustomVerb => executeCustomVerb(customVerb, verbNoun.nouns)
                case _ => executeCommand(verbNoun.verb.getVerb, verbNoun.nouns)
            }
        }
        finally {
            if (!this.disambiguating) {
                doPostCommandActions()
            }
            say("")
        }
    }

    private def doPostCommandActions() : Unit = {
        // execute any scripts which should be executed on this turn.
        this.scheduledScripts.foreach((scheduledScript) => {
            if (scheduledScript.getTurnToExecuteOn <= this.turnCounter) {
                scheduledScript.getScript().call()
            }
        })

        // and then remove them from the list
        this.scheduledScripts = this.scheduledScripts.filter((scheduledScript) => {
            scheduledScript.getTurnToExecuteOn > this.turnCounter
        })
    }

    private def doDisambiguateCommand(event:CommandEvent) : Unit = {
        try {
            val selection:String = event.getCommand.trim
            val selectionAsInt : Int = selection.toInt
            if ((selectionAsInt < 1) || (selectionAsInt > this.disambiguatingNouns.size)) {
                say("I'm sorry, I don't understand")
                return
            }
            val noun = this.disambiguatingNouns(selectionAsInt-1)

            this.disambiguatingVerb match {
                case customVerb: CustomVerb => executeCustomVerb(customVerb, List(noun))
                case _ => executeCommand(this.disambiguatingVerb.getVerb, List(noun))
            }
        }
        catch {
            case e:NumberFormatException => {
                say("I'm sorry, I don't understand")
            }
        }
        finally {
            this.disambiguating = false
            this.disambiguatingNouns = Nil
            this.disambiguatingVerb = null
            doPostCommandActions()
            say("")
        }
    }

    private def isItemInRoomOrPlayerInventory(item:Item) : Boolean = {
        this.currentRoom.contains(item) || this.player.contains(item)
    }

    private def determineIntendedNoun(items:List[Item]) : List[Item] = {
        return items.filter((item) => item.isVisible)
    }

    private def determineIntendedNoun(container:ItemContainer, items:List[Item]) : List[Item] = {
        return items.filter((item) => item.isVisible && container.contains(item))
    }

    private def askUserToDisambiguate(verb:Verb, items:List[Item]): Unit = {
        this.disambiguating = true
        this.disambiguatingVerb = verb
        this.disambiguatingNouns = items

        say(verb.getFriendlyName + " what?")
        for (index:Int <- 0 until items.size) {
            say((index+1).toString + ") " + items(index).getName)
        }
    }

    private def executeCustomVerb(verb:CustomVerb, candidateItems:List[Item]): Unit = {
        val items:List[Item] = determineIntendedNoun(candidateItems)

        // first, determine the verb container
        var verbContainer:VerbContainer = null
        if (items.size > 1) {
            askUserToDisambiguate(verb, items)
            return
        }
        else if (items.size == 1) {
            verbContainer = items.head
        }
        else if (this.currentRoom.getVerbs.contains(verb)) {
            // user did not specify a noun, so imply the current room is the noun
            verbContainer = this.currentRoom
        }
        else {
            say("You cannot do that right now.")
            return
        }

        // then check that the item / room that was referred to by the user actually contains this verb
        if (!verbContainer.getVerbs.contains(verb)) {
            say("You cannot do that right now.")
            return
        }

        var script:Option[String] = verbContainer.getVerbs.get(verb)

        executeScript(script.get)
    }

    private def executeCommand(verb:String, items:List[Item]): Unit = {
        verb match {
            case "NORTH" => move(Direction.NORTH)
            case "SOUTH" => move(Direction.SOUTH)
            case "EAST" => move(Direction.EAST)
            case "WEST" => move(Direction.WEST)
            case "UP" => move(Direction.UP)
            case "DOWN" => move(Direction.DOWN)
            case "LOOK" => look()
            case "EXITS" => exits()
            case "EXAMINE {noun}" => examine(items)
            case "GET {noun}" => get(items)
            case "DROP {noun}" => drop(items)
            case "INVENTORY" => inventory()
            case "TURN ON {noun}" => turnOn(items)
            case "TURN OFF {noun}" => turnOff(items)
            case "WAIT" => waitTurn()
            case "OPEN {noun}" => open(items)
            case "CLOSE {noun}" => close(items)
            case "EAT {noun}" => eat(items)
            case _ => throw new RuntimeException("Unexpected verb")
        }
    }

    //
    // Standard Verbs
    //

    private def look() : Unit = {
        say(this.currentRoom.getDescription)
        if (!this.currentRoom.getItems.isEmpty) {
            var firstItemOutput = false

            for (item <- this.currentRoom.getItems.values) {
                if (item.isVisible && !item.isScenery) {
                    if (!firstItemOutput) {
                        firstItemOutput = true
                        say("You can also see:")
                    }
                    say(item.getLookDescription)
                }
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
            moveTo(newRoom.get)
        }
    }

    private def moveTo(room:Room) : Unit = {
        // process leaving the previous room
        if (this.currentRoom != null) {
            if (this.currentRoom.getAfterLeaveRoomScript != null) {
                executeScript(this.currentRoom.getAfterLeaveRoomScript)
            }
        }

        // now deal with entering the new room
        var firstVisit : Boolean = false
        this.currentRoom = room

        if (!this.visitedRooms.contains(room)) {
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

    private def get(candidateItems:List[Item]) : Unit = {
        val items:List[Item] = determineIntendedNoun(this.currentRoom, candidateItems)

        if (items.size > 1) {
            askUserToDisambiguate(StandardVerbs.GET, items)
            return
        }
        else if (items == Nil) {
            say("You cannot do that right now.")
            return
        }

        val item:Item = items.head

        if (!item.isGettable) {
            say("You cannot pick up the " + item.getName)
        }
        else {
            this.currentRoom.removeItem(item)
            this.player.addItem(item)
            say("You pick up the " + item.getName)
        }
    }

    private def drop(candidateItems:List[Item]) : Unit = {
        val items:List[Item] = determineIntendedNoun(this.player, candidateItems)

        if (items.size > 1) {
            askUserToDisambiguate(StandardVerbs.DROP, items)
            return
        }
        else if (items == Nil) {
            say("You cannot do that right now.")
            return
        }

        val item:Item = items.head

        if (!item.isDroppable) {
            say("You cannot drop the " + item.getName)
        }
        else {
            this.player.removeItem(item)
            this.currentRoom.addItem(item)
            say("You drop the " + item.getName)
        }
    }

    private def examine(candidateItems: List[Item]): Unit = {
        val items:List[Item] = determineIntendedNoun(candidateItems)

        if (items.size > 1) {
            askUserToDisambiguate(StandardVerbs.EXAMINE, items)
            return
        }
        else if (items == Nil) {
            say("You cannot do that right now.")
            return
        }

        val item:Item = items.head

        item.setItemPreviouslyExamined(true)
        say(item.getItemDescription)
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

    private def turnOn(candidateItems: List[Item]) : Unit = {
        val items:List[Item] = determineIntendedNoun(candidateItems)

        if (items.size > 1) {
            askUserToDisambiguate(StandardVerbs.TURNON, items)
            return
        }
        else if (items == Nil) {
            say("You cannot do that right now.")
            return
        }

        val item:Item = items.head

        if (!item.isSwitchable) {
            say("You can't turn on the " + item.getName)
        }
        else {
            if (item.isSwitchedOn) {
                say(item.getName + " is already on")
            }
            else {
                item.switchOn()
                var message:String = item.getSwitchOnMessage
                if (message == null) {
                    message = "You turn on the " + item.getName
                }
                say(message)
            }
        }
    }

    private def turnOff(candidateItems: List[Item]) : Unit = {
        val items:List[Item] = determineIntendedNoun(candidateItems)

        if (items.size > 1) {
            askUserToDisambiguate(StandardVerbs.TURNOFF, items)
            return
        }
        else if (items == Nil) {
            say("You cannot do that right now.")
            return
        }

        val item:Item = items.head

        if (!item.isSwitchable) {
            say("You can't turn off the " + item.getName)
        }
        else {
            if (item.isSwitchedOff) {
                say(item.getName + " is already off")
            }
            else {
                item.switchOff()
                var message:String = item.getSwitchOffMessage
                if (message == null) {
                    message = "You turn off the " + item.getName
                }
                say(message)
            }
        }
    }

    private def waitTurn() : Unit = {
        val waitText:String = this.adventure.getWaitText
        if (waitText == null) {
            say("time passes...")
        }
        else {
            say(waitText)
        }
    }

    private def open(candidateItems: List[Item]) : Unit = {
        val items:List[Item] = determineIntendedNoun(candidateItems)

        if (items.size > 1) {
            askUserToDisambiguate(StandardVerbs.OPEN, items)
            return
        }
        else if (items == Nil) {
            say("You cannot do that right now.")
            return
        }

        val item:Item = items.head

        if (!item.isOpenable) {
            say("You cannot open the " + item.getName)
        }
        else if (item.isOpen) {
            say(item.getName + " is already open")
        }
        else {
            item.setOpen(true)
            var message:String = item.getOpenMessage
            if (message == null) {
                message = "You open the " + item.getName
            }
            say(message)

            if (item.getOnOpenScript != null) {
                executeScript(item.getOnOpenScript)
            }

        }
    }

    private def close(candidateItems: List[Item]) : Unit = {
        val items:List[Item] = determineIntendedNoun(candidateItems)

        if (items.size > 1) {
            askUserToDisambiguate(StandardVerbs.CLOSE, items)
            return
        }
        else if (items == Nil) {
            say("You cannot do that right now.")
            return
        }

        val item:Item = items.head

        if (!item.isCloseable) {
            say("You cannot close the " + item.getName)
        }
        else if (!item.isOpen) {
            say(item.getName + " is already closed")
        }
        else {
            item.setOpen(false)
            var message:String = item.getCloseMessage
            if (message == null) {
                message = "You close the " + item.getName
            }
            say(message)

            if (item.getOnCloseScript != null) {
                executeScript(item.getOnCloseScript)
            }

        }
    }

    private def eat(candidateItems: List[Item]) : Unit = {
        val items:List[Item] = determineIntendedNoun(candidateItems)

        if (items.size > 1) {
            askUserToDisambiguate(StandardVerbs.EAT, items)
            return
        }
        else if (items == Nil) {
            say("You cannot do that right now.")
            return
        }

        val item:Item = items.head

        if (!item.isEdible) {
            say("You cannot eat the " + item.getName)
        }
        else {
            this.player.removeItem(item)
            this.currentRoom.removeItem(item)

            var message:String = item.getEatMessage
            if (message == null) {
                message = "You eat the " + item.getName
            }
            say(message)

            if (item.getOnEatScript != null) {
                executeScript(item.getOnEatScript)
            }

        }
    }

    private def executeScript(script:String): Unit = {
        val executor : ScriptExecutor = new ScriptExecutor(this)
        executor.executeScript(script)
    }

    //
    // Utility functions - can be used by Scripts.
    //

    def say(outputText:String) : Unit = {
        this.mainWindow.say(StringUtils.sanitiseString(outputText))
    }

    def isSwitchedOn(itemId:String) : Boolean = {
        val item:Item = getItem(itemId)
        item.isSwitchedOn
    }

    def isOpen(itemId:String) : Boolean = {
        val item:Item = getItem(itemId)
        item.isOpen
    }

    def executeAfterTurns(turns:Int, script:Closure[Unit]) : Unit = {
        this.scheduledScripts :+= new ScheduledScript(this.turnCounter + turns, script)
    }

    def setVisible(itemId:String, visible:Boolean) : Unit = {
        val item:Item = getItem(itemId)
        item.setVisible(visible)
    }

    def playerInRoom(roomName:String) : Boolean = {
        this.currentRoom.getName.equals(roomName)
    }

    def moveTo(roomName:String) : Unit = {
        val room:Room = getRoom(roomName)
        moveTo(room)
    }

    // TODO: add script functions for:
    //      print a message (without carriage return)
    //      clear the screen.
    //      player is carrying object
    //      player is NOT carrying object
    //      object is visible
    //      object is not visible.

    //
    // private helper methods to support the methods that can be used by Scripts
    //
    private def getItem(itemId:String) : Item = {
        val item:Option[Item] = this.nouns.get(itemId.toUpperCase)

        if (item.isEmpty) {
            say("ERROR: Specified item does not exist.")
            throw new RuntimeException("Specified item does not exist.")
        }

        item.get
    }

    private def getRoom(roomName:String) : Room = {
        val room:Option[Room] = this.rooms.get(roomName.toUpperCase)

        if (room.isEmpty) {
            say("ERROR: Specified room does not exist.")
            throw new RuntimeException("Specified room does not exist.")
        }

        room.get
    }

}

private class VerbNoun(var verb:Verb, var nouns:List[Item])

