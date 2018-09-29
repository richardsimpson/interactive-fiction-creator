package uk.co.rjsoftware.adventure.controller

import groovy.transform.TailRecursive
import groovy.transform.TypeChecked
import uk.co.rjsoftware.adventure.controller.load.Loader
import uk.co.rjsoftware.adventure.model.*
import uk.co.rjsoftware.adventure.view.CommandEvent
import uk.co.rjsoftware.adventure.view.LoadEvent
import uk.co.rjsoftware.adventure.view.MainWindow

import java.util.concurrent.CopyOnWriteArrayList

import static java.util.stream.Collectors.toList

@TypeChecked
class AdventureController {

    private final MainWindow mainWindow

    private Adventure adventure
    private Room currentRoom
    private Item player
    private List<Room> visitedRooms = new ArrayList<>()
    private List<ScheduledScript> scheduledScripts = new CopyOnWriteArrayList<>()
    private int turnCounter = 0
    private boolean disambiguating = false
    private Verb disambiguatingVerb
    private List<Item> disambiguatingNouns = new ArrayList<>()

    private List<Verb> verbs = new ArrayList<>()
    private Map<String, Item> nouns = new HashMap<>()
    private Map<String, Room> rooms = new HashMap<>()

    final ScriptRuntimeDelegate scriptRuntimeDelegate = new ScriptRuntimeDelegate(this)

    AdventureController(MainWindow mainWindow) {
        this.mainWindow = mainWindow
        mainWindow.addCommandListener(this.&executeCommand)
        mainWindow.addLoadListener(this.&loadAdventureInternal)
    }

    private void loadAdventureInternal(LoadEvent event) {
        if (event.getFile() != null) {
            final Adventure adventure = Loader.loadAdventure(event.getFile())
            loadAdventure(adventure)
        }
    }

    void loadAdventure(Adventure adventure) {
        this.visitedRooms.clear()
        this.scheduledScripts.clear()
        this.turnCounter = 0

        this.verbs.clear()
        this.verbs.addAll(StandardVerbs.getVerbs())
        // add in any custom verbs
        this.verbs.addAll(adventure.getCustomVerbs())

        this.nouns.clear()
        this.rooms.clear()

        for (Room room : adventure.getRooms()) {
            rooms.put(room.getName().toUpperCase(), room)
        }

        for (Map.Entry<String, Item> itemEntry : adventure.getAllItems()) {
            nouns.put(itemEntry.key.toUpperCase(), itemEntry.value)
        }

        this.player = adventure.getPlayer()
        if (this.player == null) {
            throw new RuntimeException("Cannot locate player item.  Either provide a value for adventure.player, or create an item with the id 'player'.")
        }
        if (!(this.player.getParent() instanceof Room)) {
            throw new RuntimeException("player item must exist in a Room, not another item.")
        }

        // initialise the view
        this.mainWindow.loadAdventure(adventure.getTitle(), adventure.getIntroduction())

        this.adventure = adventure

        movePlayerToInternal((Room)this.player.getParent())
        say("")
    }

    // TODO: Extract the parser into another class

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
        VerbNoun verbNoun = iterateVerbSynonyms(inputWords, verbs.get(0).getSynonyms(), new VerbNoun(verbs.get(0), new ArrayList()))

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
            final Set<Item> nouns = iterateInputToFindNouns(inputWords, new HashSet<Item>())
            if (nouns == null) {
                return null
            }
            else result.nouns = nouns.toList()
        }
        else if (!verbWords[0].equals(inputWords[0])) {
            return null
        }

        doIterateVerbWords(inputWords.tail(), verbWords.tail(), result)
    }

    @TailRecursive
    private Set<Item> iterateInputToFindNouns(String[] inputWords, Set<Item> validNouns) {
        if (inputWords.length == 0) {
            return validNouns
        }

        final Set<Item> items = determineNouns(
                this.nouns.values().findAll {item -> isItemInRoomOrPlayerInventory(item)}.asList(),
                inputWords[0], new HashSet<Item>())

        iterateInputToFindNouns(inputWords.tail(), validNouns + items)
    }

    @TailRecursive
    private Set<Item> determineNouns(List<Item> nouns, String inputWord, Set<Item> validNouns) {
        if (nouns.isEmpty()) {
            return validNouns
        }

        final String synonym = iterateNounSynonyms(nouns.get(0).getSynonyms(), inputWord)

        if (synonym == null) {
            determineNouns(nouns.tail(), inputWord, validNouns)
        }
        else {
            determineNouns(nouns.tail(), inputWord, validNouns + nouns.get(0))
        }
    }

    @TailRecursive
    private String iterateNounSynonyms(List<String> synonyms, String inputWord) {
        if (synonyms.isEmpty()) {
            return null
        }

        if (synonyms.get(0).toUpperCase() == inputWord) {
            return synonyms.get(0)
        }

        iterateNounSynonyms(synonyms.tail(), inputWord)
    }

    private void executeCommand(CommandEvent event) {
        if (this.disambiguating) {
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
                executeCustomVerb(verbNoun.verb as CustomVerb, verbNoun.nouns)
            }
            else {
                executeCommand(verbNoun.verb.getVerb(), verbNoun.nouns)
            }
        }
        finally {
            if (!this.disambiguating) {
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
            item.getName()
        })
        this.disambiguating = true
        this.disambiguatingVerb = verb
        this.disambiguatingNouns = sortedItems

        say(verb.getFriendlyName() + " what?")
        for (int index = 0; index < sortedItems.size(); index++) {
            say((index+1).toString() + ") " + sortedItems.get(index).getName())
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

    private void executeCommand(String verb, List<Item> items) {
        switch (verb) {
            case "NORTH" : move(Direction.NORTH); break
            case "SOUTH" : move(Direction.SOUTH); break
            case "EAST" : move(Direction.EAST); break
            case "WEST" : move(Direction.WEST); break
            case "UP" : move(Direction.UP); break
            case "DOWN" : move(Direction.DOWN); break
            case "LOOK" : look(); break
            case "EXITS" : exits(); break
            case "EXAMINE {noun}" : examine(items); break
            case "GET {noun}" : get(items); break
            case "DROP {noun}" : drop(items); break
            case "INVENTORY" : inventory(); break
            case "TURN ON {noun}" : turnOn(items); break
            case "TURN OFF {noun}" : turnOff(items); break
            case "WAIT" : waitTurn(); break
            case "OPEN {noun}" : open(items); break
            case "CLOSE {noun}" : close(items); break
            case "EAT {noun}" : eat(items); break
            default : throw new RuntimeException("Unexpected verb")
        }
    }

    //
    // Standard Verbs
    //

    private void look() {
        say(this.currentRoom.getDescription())
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

    private void exits() {
        final List<String> validExits = this.currentRoom.getExits().keySet().stream()
                .map {direction -> direction.getDescription()}
                .collect(toList())

        say("From here you can go " + validExits.join(", "))
    }

    private void move(Direction direction) {
        final Room newRoom = this.currentRoom.getExit(direction)

        if (newRoom == null) {
            say("You cannot go that way.")
        }
        else {
            movePlayerToInternal(newRoom)
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
            say("You cannot pick up the " + item.getName())
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
            say("You cannot drop the " + item.getName())
        }
        else {
            this.player.removeItem(item)
            this.currentRoom.addItem(item)
            say("You drop the " + item.getName())
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

        // TODO: Should we maybe rename outputItemDescription to be 'examine', and then it could also set 'setItemPreviouslyExamined'
        item.setItemPreviouslyExamined(true)
        item.outputItemDescription(this.scriptRuntimeDelegate)
    }

    private void inventory() {
        say("You are currently carrying:")
        if (this.player.getItems().isEmpty()) {
            say("Nothing")
        }
        else {
            for (Item item : this.player.getItems().values()) {
                say(item.getName())
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
            say("You can't turn on the " + item.getName())
        }
        else {
            if (item.isSwitchedOn()) {
                say(item.getName() + " is already on")
            }
            else {
                item.switchOn()
                String message = item.getSwitchOnMessage()
                if (message == null) {
                    message = "You turn on the " + item.getName()
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
            say("You can't turn off the " + item.getName())
        }
        else {
            if (item.isSwitchedOff()) {
                say(item.getName() + " is already off")
            }
            else {
                item.switchOff()
                String message = item.getSwitchOffMessage()
                if (message == null) {
                    message = "You turn off the " + item.getName()
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
            say("You cannot open the " + item.getName())
        }
        else if (item.isOpen()) {
            say(item.getName() + " is already open")
        }
        else {
            item.setOpen(true)
            String message = item.getOpenMessage()
            if (message == null) {
                message = "You open the " + item.getName()
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
            say("You cannot close the " + item.getName())
        }
        else if (!item.isOpen()) {
            say(item.getName() + " is already closed")
        }
        else {
            item.setOpen(false)
            String message = item.getCloseMessage()
            if (message == null) {
                message = "You close the " + item.getName()
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
            say("You cannot eat the " + item.getName())
        }
        else {
            this.player.removeItem(item)
            this.currentRoom.removeItem(item)

            String message = item.getEatMessage()
            if (message == null) {
                message = "You eat the " + item.getName()
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

    //
    // Utility functions - can be used by Scripts.
    //

    void say(String outputText) {
        this.mainWindow.say(outputText)
    }

    void sayWithoutLineBreak(String outputText) {
        this.mainWindow.sayWithoutLineBreak(outputText)
    }

    boolean isSwitchedOn(String itemId) {
        final Item item = getItem(itemId)
        item.isSwitchedOn()
    }

    boolean isOpen(String itemId) {
        final Item item = getItem(itemId)
        item.isOpen()
    }

    void executeAfterTurns(int turns, Closure<Void> script) {
        this.scheduledScripts.add(new ScheduledScript(this.turnCounter + turns, script))
    }

    void setVisible(String itemId, boolean visible) {
        final Item item = getItem(itemId)
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
        final Item item = getItem(itemName)
        ItemContainer container = getRoomOrNull(itemContainerName)
        if (container == null) {
            container = getItemOrNull(itemContainerName)
        }
        if (container == null) {
            throw new RuntimeException("Specified itemContainer does not exist")
        }
        item.setParent(container)
    }

    Item getItem(String itemId) {
        final Item item = getItemOrNull(itemId)

        if (item == null) {
            throw new RuntimeException("Specified item does not exist.")
        }

        item
    }

    private Item getItemOrNull(String itemId) {
        this.adventure.getItem(itemId)
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

    // TODO: add script functions for:
    //      print a message (without carriage return)
    //      clear the screen.
    //      player is carrying object
    //      player is NOT carrying object
    //      object is visible
    //      object is not visible.

    private static class VerbNoun {
        final Verb verb
        List<Item> nouns

        VerbNoun(Verb verb, List<Item> nouns) {
            this.verb = verb
            this.nouns = nouns
        }
    }
}


