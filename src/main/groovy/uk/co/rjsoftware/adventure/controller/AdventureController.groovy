package uk.co.rjsoftware.adventure.controller

import groovy.transform.TailRecursive
import groovy.transform.TypeChecked
import uk.co.rjsoftware.adventure.controller.load.Loader
import uk.co.rjsoftware.adventure.model.*
import uk.co.rjsoftware.adventure.utils.StringUtils
import uk.co.rjsoftware.adventure.view.CommandEvent
import uk.co.rjsoftware.adventure.view.LoadEvent
import uk.co.rjsoftware.adventure.view.IPlayerAppView

import java.util.concurrent.CopyOnWriteArrayList

import static java.util.stream.Collectors.toList

@TypeChecked
class AdventureController {

    private final IPlayerAppView view

    private Adventure originalAdventure
    private Adventure adventure
    private Room currentRoom
    private Item player
    private List<Room> visitedRooms = new ArrayList<>()
    private List<ScheduledScript> scheduledScripts = new CopyOnWriteArrayList<>()
    private int turnCounter = 0
    private int score = 0
    private boolean disambiguating = false
    private Verb disambiguatingVerb
    private List<Item> disambiguatingNouns = new ArrayList<>()
    private boolean confirmingRestart = false
    private boolean confirmingEndGame = false

    private List<Verb> verbs = new ArrayList<>()
    private Map<String, Item> nouns = new HashMap<>()
    private Map<String, Room> rooms = new HashMap<>()

    final ScriptRuntimeDelegate scriptRuntimeDelegate = new ScriptRuntimeDelegate(this)

    AdventureController(IPlayerAppView view) {
        this.view = view
        view.addCommandListener(this.&executeCommand)
        view.addLoadListener(this.&loadAdventureInternal)
    }

    private void loadAdventureInternal(LoadEvent event) {
        if (event.getFile() != null) {
            final Adventure adventure = Loader.loadAdventure(event.getFile())
            loadAdventure(adventure)
        }
    }

    // THIS METHOD IS FOR TESTS ONLY
    Adventure getAdventure() {
        this.adventure
    }

    void loadAdventure(Adventure adventure) {
        this.originalAdventure = adventure
        this.adventure = adventure.copy()

        this.visitedRooms.clear()
        this.scheduledScripts.clear()
        this.turnCounter = 0
        this.score = 0

        this.disambiguating = false
        this.disambiguatingVerb = null
        this.disambiguatingNouns = null
        this.confirmingRestart = false
        this.confirmingEndGame = false

        this.verbs.clear()
        this.verbs.addAll(StandardVerbs.getVerbs())
        // add in any custom verbs
        this.verbs.addAll(this.adventure.getCustomVerbs())

        this.nouns.clear()
        this.rooms.clear()

        for (Room room : this.adventure.getRooms()) {
            rooms.put(room.getName().toUpperCase(), room)
        }

        for (Map.Entry<Integer, Item> itemEntry : this.adventure.getAllItems()) {
            nouns.put(itemEntry.value.getName().toUpperCase(), itemEntry.value)
        }

        this.player = this.adventure.getPlayer()
        if (this.player == null) {
            throw new RuntimeException("Cannot locate player item.  Either provide a value for adventure.player, or create an item with the id 'player'.")
        }
        if (!(this.player.getParent() instanceof Room)) {
            throw new RuntimeException("player item must exist in a Room, not another item.")
        }

        // initialise the view
        this.view.loadAdventure(this.adventure.getTitle(), this.adventure.getIntroduction())

        movePlayerToInternal((Room)this.player.getParent())
        say("")
    }

    // TODO: Extract the parser into another class
    // TODO: Allow the adventure to override all of the standard responses.

    private VerbNoun determineVerbNoun(String[] inputWords) {
        iterateWords(inputWords)
    }

    @TailRecursive
    private VerbNoun iterateWords(String[] inputWords) {
        if (inputWords.length == 0) {
            return null
        }

        final VerbNoun verbNoun = iterateVerbs(inputWords, this.verbs)
        if (verbNoun != null) {
            return verbNoun
        }
        else {
            iterateWords(inputWords.tail())
        }
    }

    @TailRecursive
    private VerbNoun iterateVerbs(String[] inputWords, List<Verb> verbs) {
        if (verbs.isEmpty()) {
            return null
        }
        VerbNoun verbNoun = iterateVerbSynonyms(inputWords, verbs.get(0).getSynonyms(), new VerbNoun(verbs.get(0)))

        if (verbNoun != null) {
            return verbNoun
        }
        else {
            iterateVerbs(inputWords, verbs.tail())
        }
    }

    @TailRecursive
    private VerbNoun iterateVerbSynonyms(String[] inputWords, List<String> verbSynonyms, VerbNoun result) {
        if (verbSynonyms.isEmpty()) {
            return null
        }

        VerbNoun verbNoun = iterateVerbWords(inputWords, verbSynonyms.get(0), result)

        if (verbNoun != null) {
            return verbNoun
        }
        else {
            iterateVerbSynonyms(inputWords, verbSynonyms.tail(), result)
        }
    }

    private VerbNoun iterateVerbWords(String[] inputWords, String verbWords, VerbNoun result) {
        doIterateVerbWords(inputWords, verbWords.split(" "), result)
    }

    @TailRecursive
    private VerbNoun doIterateVerbWords(String[] inputWords, String[] verbWords, VerbNoun result) {
        if (verbWords.length == 0) {
            return result
        }

        if (inputWords.length == 0) {
            return null
        }

        if (verbWords[0].equals("{noun}")) {
            iterateInputToFindNouns(inputWords, result)
            if (result.nouns.isEmpty()) {
                return null
            }
        }
        else if (!verbWords[0].equals(inputWords[0])) {
            return null
        }

        doIterateVerbWords(inputWords.tail(), verbWords.tail(), result)
    }

    @TailRecursive
    private VerbNoun iterateInputToFindNouns(String[] inputWords, VerbNoun result) {
        if (inputWords.length == 0) {
            return result
        }

        determineNouns(
                this.nouns.values().findAll {item -> isItemInRoomOrPlayerInventory(item)}.asList(),
                inputWords, result)

        iterateInputToFindNouns(inputWords.tail(), result)
    }

    @TailRecursive
    private VerbNoun determineNouns(List<Item> nouns, String[] inputWords, VerbNoun result) {
        if (nouns.isEmpty()) {
            return result
        }

        iterateNounSynonyms(nouns.get(0), nouns.get(0).getSynonyms(), inputWords, result)

        determineNouns(nouns.tail(), inputWords, result)
    }

    @TailRecursive
    private VerbNoun iterateNounSynonyms(Item noun, List<String> synonyms, String[] inputWords, VerbNoun result) {
        if (synonyms.isEmpty()) {
            return result
        }

        final String[] synonymWords = synonyms.get(0).split(" ")
        if (iterateNounSynonymWords(synonymWords, inputWords)) {
            result.addNoun(noun, synonymWords)
        }

        iterateNounSynonyms(noun, synonyms.tail(), inputWords, result)
    }

    @TailRecursive
    private boolean iterateNounSynonymWords(String[] synonymWords, String[] inputWords) {
        if (synonymWords.length == 0) {
            return true
        }

        if (synonymWords.length > inputWords.length) {
            return false
        }

        if (synonymWords[0].toUpperCase() == inputWords[0]) {
            iterateNounSynonymWords(synonymWords.tail(), inputWords.tail())
        }
        else {
            iterateNounSynonymWords(synonymWords, inputWords.tail())
        }
    }

    private void executeCommand(CommandEvent event) {
        if (this.confirmingRestart) {
            doConfirmRestart(event)
        }
        else if (this.confirmingEndGame) {
            doConfirmEndGame(event)
        }
        else if (this.disambiguating) {
            doDisambiguateCommand(event)
        }
        else {
            doExecuteCommand(event)
        }
    }

    private void doExecuteCommand(CommandEvent event) {
        final String[] words = event.getCommand().trim().replaceAll(" +", " ").toUpperCase().split(" ")

        try {
            this.turnCounter = this.turnCounter + 1

            if (words.length < 1) {
                say("There are no words...")
                return
            }

            // TODO: HELP
            // TODO: GET ALL / TAKE ALL

            final VerbNoun verbNoun = determineVerbNoun(words)
            if (verbNoun == null) {
                say("I don't understand what you are trying to do.")
                return
            }

            // TODO: if verb requires noun, and there is not one, say(verb + " what?")

            if (verbNoun.verb instanceof CustomVerb) {
                executeCustomVerb(verbNoun.verb as CustomVerb, verbNoun.getCandidateItems())
            }
            else {
                executeCommand(verbNoun.verb.getVerb(), verbNoun.getCandidateItems())
            }
        }
        finally {
            if (!this.disambiguating && !this.confirmingRestart) {
                doPostCommandActions()
            }
            say("")
        }
    }

    private void doPostCommandActions() {
        final List<ScheduledScript> scriptsToRemove = new ArrayList<>()

        // execute any scripts which should be executed on this turn.
        for (ScheduledScript scheduledScript : this.scheduledScripts) {
            if (scheduledScript.getTurnToExecuteOn() <= this.turnCounter) {
                scheduledScript.getScript().call()
                scriptsToRemove.add(scheduledScript)
            }
        }

        this.scheduledScripts.removeAll(scriptsToRemove)
    }

    private void doDisambiguateCommand(CommandEvent event) {
        try {
            final String selection = event.getCommand().trim()
            final int selectionAsInt = selection.toInteger()
            if ((selectionAsInt < 1) || (selectionAsInt > this.disambiguatingNouns.size())) {
                say("I'm sorry, I don't understand")
                return
            }
            final Item noun = this.disambiguatingNouns.get(selectionAsInt-1)

            if (this.disambiguatingVerb instanceof CustomVerb) {
                executeCustomVerb(this.disambiguatingVerb as CustomVerb, [noun])
            }
            else {
                executeCommand(this.disambiguatingVerb.getVerb(), [noun])
            }
        }
        catch(NumberFormatException exception) {
            say("I'm sorry, I don't understand")
        }
        finally {
            this.disambiguating = false
            this.disambiguatingNouns.clear()
            this.disambiguatingVerb = null
            doPostCommandActions()
            say("")
        }
    }

    private boolean isItemInRoomOrPlayerInventory(Item item) {
        this.currentRoom.contains(item) || this.player.contains(item)
    }

    private List<Item> determineIntendedNoun(List<Item> items) {
        items.findAll {item ->
            item.isVisible()
        }
    }

    private List<Item> determineIntendedNoun(ItemContainer container, List<Item> items) {
        return items.findAll { item ->
            item.isVisible() && container.contains(item)
        }
    }

    private void askUserToDisambiguate(Verb verb, List<Item> items) {
        final List<Item> sortedItems = items.toSorted({item ->
            item.getDisplayName()
        })
        this.disambiguating = true
        this.disambiguatingVerb = verb
        this.disambiguatingNouns = sortedItems

        say(verb.getFriendlyName() + " what?")
        for (int index = 0; index < sortedItems.size(); index++) {
            say((index+1).toString() + ") " + sortedItems.get(index).getDisplayName())
        }
    }

    private void executeCustomVerb(CustomVerb verb, List<Item> candidateItems) {
        final List<Item> items = determineIntendedNoun(candidateItems)

        // first, determine the verb container
        VerbContainer verbContainer = null
        if (items.size() > 1) {
            askUserToDisambiguate(verb, items)
            return
        }
        else if (items.size() == 1) {
            verbContainer = items.get(0)
        }
        else if (this.currentRoom.containsVerb(verb)) {
            // user did not specify a noun, so imply the current room is the noun
            verbContainer = this.currentRoom
        }
        else {
            say("You cannot do that right now.")
            return
        }

        // then check that the item / room that was referred to by the user actually contains this verb
        if (!verbContainer.containsVerb(verb)) {
            say("You cannot do that right now.")
            return
        }

        final Closure closure = verbContainer.getVerbClosure(verb)
        executeClosure(closure)
    }

    private void executeCommand(String verb, List<Item> candidateItems) {
        switch (verb) {
            case "NORTH" : move(Direction.NORTH); break
            case "SOUTH" : move(Direction.SOUTH); break
            case "EAST" : move(Direction.EAST); break
            case "WEST" : move(Direction.WEST); break
            case "UP" : move(Direction.UP); break
            case "DOWN" : move(Direction.DOWN); break
            case "LOOK" : look(); break
            case "EXITS" : exits(); break
            case "EXAMINE {noun}" : examine(candidateItems); break
            case "GET {noun}" : get(candidateItems); break
            case "DROP {noun}" : drop(candidateItems); break
            case "INVENTORY" : inventory(); break
            case "TURN ON {noun}" : turnOn(candidateItems); break
            case "TURN OFF {noun}" : turnOff(candidateItems); break
            case "WAIT" : waitTurn(); break
            case "OPEN {noun}" : open(candidateItems); break
            case "CLOSE {noun}" : close(candidateItems); break
            case "EAT {noun}" : eat(candidateItems); break
            case "RESTART" : restart(); break
            default : throw new RuntimeException("Unexpected verb")
        }
    }

    //
    // Standard Verbs
    //

    private void look() {
        final Closure closure = this.currentRoom.getDescriptionClosure()
        if (closure != null) {
            executeClosure(closure)
        }
        else {
            say(this.currentRoom.getDescription())

            exits()

            if (!this.currentRoom.getItems().isEmpty()) {
                boolean firstItemOutput = false

                for (Item item : this.currentRoom.getItems().values()) {
                    if (item.isVisible() && !item.isScenery() && item != this.player) {
                        if (!firstItemOutput) {
                            firstItemOutput = true
                            say("You can also see:")
                        }
                        say(item.getLookDescription())
                    }
                }
            }
        }
    }

    private void exits() {
        final String exitsMessage = getExitsMessage()
        if (!exitsMessage.isEmpty()) {
            say(exitsMessage)
        }
    }

    private String getExitsMessage() {
        final List<String> validExits = this.currentRoom.getExits().values().stream()
                .filter {exit -> !exit.isScenery()}
                .map {exit -> exit.getDescription()}
                .collect(toList())

        String descriptions = validExits.join(", ")

        if (!descriptions.isEmpty()) {
            descriptions = "From here you can go " + descriptions
        }
        descriptions
    }

    private void move(Direction direction) {
        final Exit exit = this.currentRoom.getExit(direction)

        if (exit == null || exit.getDestination() == null) {
            say("You cannot go that way.")
        }
        else {
            movePlayerToInternal(exit.getDestination())
        }
    }

    private void movePlayerToInternal(Room room) {
        // process leaving the previous room
        if (this.currentRoom != null) {
            if (this.currentRoom.getAfterLeaveRoom() != null) {
                executeClosure(this.currentRoom.getAfterLeaveRoom())
            }
        }

        // now deal with entering the new room
        boolean firstVisit = false
        this.currentRoom = room

        if (!this.visitedRooms.contains(room)) {
            firstVisit = true
            this.visitedRooms.add(this.currentRoom)
        }

        if (firstVisit) {
            if (this.currentRoom.getBeforeEnterRoomFirstTime() != null) {
                executeClosure(this.currentRoom.getBeforeEnterRoomFirstTime())
            }
        }

        if (this.currentRoom.getBeforeEnterRoom() != null) {
            executeClosure(this.currentRoom.getBeforeEnterRoom())
        }

        look()

        if (firstVisit) {
            if (this.currentRoom.getAfterEnterRoomFirstTime() != null) {
                executeClosure(this.currentRoom.getAfterEnterRoomFirstTime())
            }
        }

        if (this.currentRoom.getAfterEnterRoom() != null) {
            executeClosure(this.currentRoom.getAfterEnterRoom())
        }

    }

    private void get(List<Item> candidateItems) {
        final List<Item> items = determineIntendedNoun(this.currentRoom, candidateItems)

        if (items.size() > 1) {
            askUserToDisambiguate(StandardVerbs.GET, items)
            return
        }
        else if (items.isEmpty()) {
            say("You cannot do that right now.")
            return
        }

        final Item item = items.get(0)

        if (!item.isGettable()) {
            say("You cannot pick up the " + item.getDisplayName())
        }
        else {
            this.currentRoom.removeItem(item)
            this.player.addItem(item)
            say(getGetText(item.getName()))
        }
    }

    private String getGetText(String itemName) {
        final String getText = this.adventure.getGetText()
        if (getText == null) {
            "You pick up the " + itemName
        }
        else {
            getText
        }
    }

    private void drop(List<Item> candidateItems) {
        final List<Item> items = determineIntendedNoun(this.player, candidateItems)

        if (items.size() > 1) {
            askUserToDisambiguate(StandardVerbs.DROP, items)
            return
        }
        else if (items.isEmpty()) {
            say("You cannot do that right now.")
            return
        }

        final Item item = items.get(0)

        if (!item.isDroppable()) {
            say("You cannot drop the " + item.getDisplayName())
        }
        else {
            this.player.removeItem(item)
            this.currentRoom.addItem(item)
            say("You drop the " + item.getDisplayName())
        }
    }

    private void examine(List<Item> candidateItems) {
        final List<Item> items = determineIntendedNoun(candidateItems)

        if (items.size() > 1) {
            askUserToDisambiguate(StandardVerbs.EXAMINE, items)
            return
        }
        else if (items.isEmpty()) {
            say("You cannot do that right now.")
            return
        }

        final Item item = items.get(0)

        item.setItemExamined(true)
        final Closure closure = item.getDescriptionClosure()
        if (closure != null) {
            executeClosure(closure)
        }
        else {
            say(item.getItemDescription())
        }
    }

    private void inventory() {
        say("You are currently carrying:")
        if (this.player.getItems().isEmpty()) {
            say("Nothing")
        }
        else {
            for (Item item : this.player.getItems().values()) {
                say(item.getDisplayName())
            }
        }
    }

    private void turnOn(List<Item> candidateItems) {
        final List<Item> items = determineIntendedNoun(candidateItems)

        if (items.size() > 1) {
            askUserToDisambiguate(StandardVerbs.TURNON, items)
            return
        }
        else if (items.isEmpty()) {
            say("You cannot do that right now.")
            return
        }

        final Item item = items.get(0)

        if (!item.isSwitchable()) {
            say("You can't turn on the " + item.getDisplayName())
        }
        else {
            if (item.isSwitchedOn()) {
                say(item.getDisplayName() + " is already on")
            }
            else {
                item.switchOn()
                String message = item.getSwitchOnMessage()
                if (message == null) {
                    message = "You turn on the " + item.getDisplayName()
                }
                say(message)
            }
        }
    }

    private void turnOff(List<Item> candidateItems) {
        final List<Item> items = determineIntendedNoun(candidateItems)

        if (items.size() > 1) {
            askUserToDisambiguate(StandardVerbs.TURNOFF, items)
            return
        }
        else if (items.isEmpty()) {
            say("You cannot do that right now.")
            return
        }

        final Item item = items.get(0)

        if (!item.isSwitchable()) {
            say("You can't turn off the " + item.getDisplayName())
        }
        else {
            if (item.isSwitchedOff()) {
                say(item.getDisplayName() + " is already off")
            }
            else {
                item.switchOff()
                String message = item.getSwitchOffMessage()
                if (message == null) {
                    message = "You turn off the " + item.getDisplayName()
                }
                say(message)
            }
        }
    }

    private void waitTurn() {
        final String waitText = this.adventure.getWaitText()
        if (waitText == null) {
            say("time passes...")
        }
        else {
            say(waitText)
        }
    }

    private void open(List<Item> candidateItems) {
        final List<Item> items = determineIntendedNoun(candidateItems)

        if (items.size() > 1) {
            askUserToDisambiguate(StandardVerbs.OPEN, items)
            return
        }
        else if (items.isEmpty()) {
            say("You cannot do that right now.")
            return
        }

        final Item item = items.get(0)

        if (!item.isOpenable()) {
            say("You cannot open the " + item.getDisplayName())
        }
        else if (item.isOpen()) {
            say(item.getDisplayName() + " is already open")
        }
        else {
            item.setOpen(true)
            String message = item.getOpenMessage()
            if (message == null) {
                message = "You open the " + item.getDisplayName()
            }
            say(message)

            if (item.getOnOpen() != null) {
                executeClosure(item.getOnOpen())
            }

        }
    }

    private void close(List<Item> candidateItems) {
        final List<Item> items = determineIntendedNoun(candidateItems)

        if (items.size() > 1) {
            askUserToDisambiguate(StandardVerbs.CLOSE, items)
            return
        }
        else if (items.isEmpty()) {
            say("You cannot do that right now.")
            return
        }

        final Item item = items.get(0)

        if (!item.isCloseable()) {
            say("You cannot close the " + item.getDisplayName())
        }
        else if (!item.isOpen()) {
            say(item.getDisplayName() + " is already closed")
        }
        else {
            item.setOpen(false)
            String message = item.getCloseMessage()
            if (message == null) {
                message = "You close the " + item.getDisplayName()
            }
            say(message)

            if (item.getOnClose() != null) {
                executeClosure(item.getOnClose())
            }

        }
    }

    private void eat(List<Item> candidateItems) {
        final List<Item> items = determineIntendedNoun(candidateItems)

        if (items.size() > 1) {
            askUserToDisambiguate(StandardVerbs.EAT, items)
            return
        }
        else if (items.isEmpty()) {
            say("You cannot do that right now.")
            return
        }

        final Item item = items.get(0)

        if (!item.isEdible()) {
            say("You cannot eat the " + item.getDisplayName())
        }
        else {
            this.player.removeItem(item)
            this.currentRoom.removeItem(item)

            String message = item.getEatMessage()
            if (message == null) {
                message = "You eat the " + item.getDisplayName()
            }
            say(message)

            if (item.getOnEat() != null) {
                executeClosure(item.getOnEat())
            }

        }
    }

    private void executeClosure(Closure closure) {
        closure.delegate = scriptRuntimeDelegate
        closure.call()
    }

    private void restart() {
        this.confirmingRestart = true
        say("Are you sure you want to restart (Yes / No) ?")
    }

    private void doConfirmRestart(CommandEvent event) {
            this.confirmingRestart = false

            final String input = event.getCommand().trim().toUpperCase()
            if (input.equals("YES")) {
                say("Ok, Restarting...")
                // TODO: The 'restarting message doesn't get displayed, despite the thread sleep.
                Thread.sleep(2000)
                loadAdventure(this.originalAdventure)
            }
            else {
                // wind back time; the restart command should not count as a turn
                this.turnCounter = this.turnCounter - 1
            }
    }

    private void doConfirmEndGame(CommandEvent event) {
        this.confirmingEndGame = false
        loadAdventure(this.originalAdventure)
    }

    //
    // Utility functions - can be used by Scripts.
    //

    void say(String outputText) {
        this.view.say(StringUtils.sanitiseString(outputText))
    }

    void sayWithoutLineBreak(String outputText) {
        this.view.sayWithoutLineBreak(StringUtils.sanitiseString(outputText))
    }

    boolean isSwitchedOn(String itemId) {
        final Item item = getItemByName(itemId)
        item.isSwitchedOn()
    }

    boolean isOpen(String itemId) {
        final Item item = getItemByName(itemId)
        item.isOpen()
    }

    void executeAfterTurns(int turns, Closure<Void> script) {
        this.scheduledScripts.add(new ScheduledScript(this.turnCounter + turns, script))
    }

    void setVisible(String itemId, boolean visible) {
        final Item item = getItemByName(itemId)
        item.setVisible(visible)
    }

    boolean playerInRoom(String roomName) {
        this.currentRoom.getName().equals(roomName)
    }

    void movePlayerTo(String roomName) {
        final Room room = getRoom(roomName)
        movePlayerToInternal(room)
    }

    void moveItemTo(String itemName, String itemContainerName) {
        final Item item = getItemByName(itemName)
        ItemContainer container = getRoomOrNull(itemContainerName)
        if (container == null) {
            container = getItemByNameOrNull(itemContainerName)
        }
        if (container == null) {
            throw new RuntimeException("Specified itemContainer does not exist")
        }
        item.setParent(container)
    }

    Item getItemByName(String itemName) {
        final Item item = getItemByNameOrNull(itemName)

        if (item == null) {
            throw new RuntimeException("Specified item does not exist.")
        }

        item
    }

    private Item getItemByNameOrNull(String itemName) {
        this.adventure.getItemByName(itemName)
    }

    Room getRoom(String roomName) {
        final Room room = getRoomOrNull(roomName)

        if (room == null) {
            say("ERROR: Specified room does not exist.")
            throw new RuntimeException("Specified room does not exist.")
        }

        room
    }

    private Room getRoomOrNull(String roomName) {
        this.rooms.get(roomName.toUpperCase())
    }

    Room getCurrentRoom() {
        this.currentRoom
    }

    Item getPlayer() {
        this.player
    }

    void increaseScore(int amount) {
        this.score += amount
    }

    void decreaseScore(int amount) {
        this.score -= amount
    }

    int getScore() {
        this.score
    }

    int getTurnCounter() {
        this.turnCounter
    }

    void endGame() {
        this.confirmingEndGame = true
        say("")
        say("Press <Enter> to start a new game.")
    }

    // TODO: add script functions for:
    //      clear the screen.
    //      player is carrying object
    //      player is NOT carrying object
    //      object is visible
    //      object is not visible.

    private static class VerbNoun {
        final Verb verb

        // Map of Score to a Map of Item to a Set of synonyms
        //
        // The Score is the number of words in the synonyms.
        // The Items with the highest Score will be considered 'candidate items' (i.e. the item(s) that the player may have been trying to refer to.)
        //
        // Although we could always remove the lower scores, and we probably also don't need to keep a Set of synonyms,
        // keeping this information is useful for debugging.  By keeping all the matches, we can see better what the
        // parser has done.
        final Map<Integer, Map<Item, Set<String>>> nouns = new HashMap<>()
        Integer longestSynonym = 0

        VerbNoun(Verb verb) {
            this.verb = verb
        }

        void addNoun(Item noun, String[] synonymWords) {
            if (synonymWords.length > longestSynonym) {
                longestSynonym = synonymWords.length
            }

            if (!nouns.containsKey(synonymWords.length)) {
                nouns.put(synonymWords.length, new HashMap<>())
            }

            final itemSynonymMap = nouns.get(synonymWords.length)
            if (!itemSynonymMap.containsKey(noun)) {
                itemSynonymMap.put(noun, new HashSet<>())
            }

            final synonymSet = itemSynonymMap.get(noun)
            final synonym = synonymWords.join(" ")
            synonymSet.add(synonym)
        }

        List<Item> getCandidateItems() {
            final candidateItems = this.nouns.get(this.longestSynonym)
            if (candidateItems == null) {
                return null
            }

            candidateItems.keySet().toList()
        }
    }
}


