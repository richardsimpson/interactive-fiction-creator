package uk.co.rjsoftware.adventure.controller.load

import groovy.transform.TypeChecked
import org.codehaus.groovy.control.CompilerConfiguration
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.model.ContentVisibility
import uk.co.rjsoftware.adventure.model.CustomVerb
import uk.co.rjsoftware.adventure.model.Direction
import uk.co.rjsoftware.adventure.model.Item
import uk.co.rjsoftware.adventure.model.Room
import uk.co.rjsoftware.adventure.utils.StringUtils

/**
 * Created by richardsimpson on 29/05/2017.
 */
class Loader {
    static Adventure loadAdventure(File dsl) {
        def config = new CompilerConfiguration();
        config.scriptBaseClass = AdventureLoaderScript.getCanonicalName()

        def shell = new GroovyShell(new Binding(), config)
        def script = shell.parse(dsl.text)

        script.run()

        return script.getAdventure()
    }

}

@TypeChecked
abstract class AdventureLoaderScript extends Script {

    private AdventureDelegate adventureDelegate = new AdventureDelegate();

    void adventure(Closure closure) {
        closure.delegate = adventureDelegate
        closure.resolveStrategy = Closure.DELEGATE_ONLY
        closure()
    }

    Adventure getAdventure() {
        // Resolve any forward references, such as adventure.player, or any room exits.
        this.adventureDelegate.resolveForwardReferences()

        return this.adventureDelegate.getAdventure()
    }
}

@TypeChecked
class AdventureDelegate {
    private final Adventure adventure = new Adventure("")
    private String playerForwardReference
    private List<RoomDelegate> roomDelegates = new ArrayList<>()

    private void title(String title) {
        this.adventure.setTitle(StringUtils.sanitiseString(title))
    }

    private void introduction(String introduction) {
        this.adventure.setIntroduction(StringUtils.sanitiseString(introduction))
    }

    private void waitText(String waitText) {
        this.adventure.setWaitText(StringUtils.sanitiseString(waitText))
    }

    private void getText(String getText) {
        this.adventure.setGetText(StringUtils.sanitiseString(getText))
    }

    private void verb(String id, String friendlyName, String command, Optional<Closure> closure) {
        if (this.adventure.findCustomVerb(id) != null) {
            throw new RuntimeException("Cannot declare custom verbs twice")
        }

        CustomVerb customVerb = new CustomVerb(id, friendlyName, command)
        this.adventure.addCustomVerb(customVerb)

        closure.ifPresent {clo ->
            clo.delegate = new VerbDelegate(customVerb)
            clo.resolveStrategy = Closure.DELEGATE_ONLY
            clo()
        }
    }

    private void verb(String id, String friendlyName, String command, Closure closure) {
        verb(id, friendlyName, command, Optional.of(closure))
    }

    private void verb(String id, String friendlyName, String command) {
        verb(id, friendlyName, command, Optional.empty())
    }

    private void verb(String id, String command, Closure closure) {
        verb(id, id, command, closure)
    }

    private void verb(String id, String command) {
        verb(id, id, command)
    }

    private void room(String roomName, Closure closure) {
        Room room = this.adventure.getRoom(roomName)

        if (room == null) {
            room = new Room(roomName)
            this.adventure.addRoom(room)
        }

        final RoomDelegate roomDelegate = new RoomDelegate(room, this.adventure)
        this.roomDelegates.add(roomDelegate)
        closure.delegate = roomDelegate
        closure.resolveStrategy = Closure.DELEGATE_ONLY
        closure()
    }

    private void player(String itemName) {
        Item item = this.adventure.getItem(itemName)

        if (item == null) {
            this.playerForwardReference = itemName
        }
        else {
            this.adventure.setPlayer(item)
            this.playerForwardReference = null
        }
    }

    void resolveForwardReferences() {
        if (this.playerForwardReference != null) {
            player(this.playerForwardReference)
        }

        for (RoomDelegate roomDelegate : this.roomDelegates) {
            roomDelegate.resolveForwardRoomReferences()
        }
    }

    Adventure getAdventure() {
        return this.adventure
    }
}

@TypeChecked
class VerbDelegate {

    private final CustomVerb customVerb

    VerbDelegate(final CustomVerb customVerb) {
        this.customVerb = customVerb
    }

    private void synonyms(String... synonyms) {
        for (String synonym : synonyms) {
            this.customVerb.addSynonym(synonym)
        }
    }

}

@TypeChecked
class RoomDelegate {
    private Room room
    private final Adventure adventure

    private final List<LinkedHashMap> forwardRoomReferences = new ArrayList<>()

    private Direction NORTH = Direction.NORTH
    private Direction EAST = Direction.EAST
    private Direction SOUTH = Direction.SOUTH
    private Direction WEST = Direction.WEST
    private Direction UP = Direction.UP
    private Direction DOWN = Direction.DOWN

    RoomDelegate(final Room room, final Adventure adventure) {
        this.adventure = adventure
        this.room = room
    }

    private void description(String description) {
        this.room.setDescription(StringUtils.sanitiseString(description))
    }

    private void description(Closure closure) {
        this.room.setDescriptionClosure(closure)
    }

    private void beforeEnterRoom(Closure closure) {
        this.room.setBeforeEnterRoom(closure)
    }

    private void afterEnterRoom(Closure closure) {
        this.room.setAfterEnterRoom(closure)
    }

    private void afterLeaveRoom(Closure closure) {
        this.room.setAfterLeaveRoom(closure)
    }

    private void beforeEnterRoomFirstTime(Closure closure) {
        this.room.setBeforeEnterRoomFirstTime(closure)
    }

    private void afterEnterRoomFirstTime(Closure closure) {
        this.room.setAfterEnterRoomFirstTime(closure)
    }

    private boolean exit(LinkedHashMap linkedHashMap) {
        Room room = this.adventure.getRoom((String)linkedHashMap.get("room"))

        if (room == null) {
            this.forwardRoomReferences.add(linkedHashMap)
            false
        }
        else {
            this.room.addExit((Direction)linkedHashMap.get("direction"), room)
            true
        }
    }

    void resolveForwardRoomReferences() {
        for (LinkedHashMap linkedHashMap : this.forwardRoomReferences) {
            final boolean ableToResolve = exit(linkedHashMap)
            if (!ableToResolve) {
                throw new RuntimeException("Room '${this.room.name}' contains an exit to a room named '${linkedHashMap.get("room")}', which does not exist.")
            }
        }
    }

    private void item(String itemId, Optional<Closure> closure) {
        Item item = this.room.getItem(itemId)
        if (item == null) {
            item = new Item(itemId)
            this.room.addItem(item)
        }

        closure.ifPresent {clo ->
            clo.delegate = new ItemDelegate(this.adventure, item)
            clo.resolveStrategy = Closure.DELEGATE_ONLY
            clo()
        }
    }

    private void item(String itemId, Closure closure) {
        item(itemId, Optional.of(closure))
    }

    private void item(String itemId) {
        item(itemId, Optional.empty())
    }

    private void verb(String verbId, Closure closure) {
        final CustomVerb customVerb = this.adventure.findCustomVerb(verbId)

        if (customVerb == null) {
            throw new RuntimeException("Cannot locate custom verb '" + verbId + "'")
        }

        this.room.addVerb(customVerb, closure)
    }

}

@TypeChecked
class ItemDelegate {

    private final Item item
    private final Adventure adventure

    private ContentVisibility ALWAYS = ContentVisibility.ALWAYS
    private ContentVisibility AFTER_EXAMINE = ContentVisibility.AFTER_EXAMINE
    private ContentVisibility NEVER = ContentVisibility.NEVER

    ItemDelegate(Adventure adventure, Item item) {
        this.adventure = adventure
        this.item = item
    }

    private void synonyms(String... synonyms) {
        this.item.clearSynonyms()
        for (String synonym : synonyms) {
            this.item.addSynonym(synonym)
        }
    }

    private void description(String description) {
        this.item.setDescription(StringUtils.sanitiseString(description))
    }

    private void description(Closure closure) {
        this.item.setDescriptionClosure(closure)
    }

    private void visible(Boolean visible) {
        this.item.setVisible(visible)
    }

    private void scenery(Boolean scenery) {
        this.item.setScenery(scenery)
    }

    private void gettable(Boolean gettable) {
        this.item.setGettable(gettable)
    }

    private void droppable(Boolean droppable) {
        this.item.setDroppable(droppable)
    }

    private void switchable(Boolean switchable) {
        this.item.setSwitchable(switchable)
    }

    private void switchOnMessage(String switchOnMessage) {
        this.item.setSwitchOnMessage(StringUtils.sanitiseString(switchOnMessage))
    }

    private void switchOffMessage(String switchOffMessage) {
        this.item.setSwitchOffMessage(StringUtils.sanitiseString(switchOffMessage))
    }

    private void extraMessageWhenSwitchedOn(String extraMessageWhenSwitchedOn) {
        this.item.setExtraMessageWhenSwitchedOn(StringUtils.sanitiseString(extraMessageWhenSwitchedOn))
    }

    private void extraMessageWhenSwitchedOff(String extraMessageWhenSwitchedOff) {
        this.item.setExtraMessageWhenSwitchedOff(StringUtils.sanitiseString(extraMessageWhenSwitchedOff))
    }

    private void verb(String verbId, Closure closure) {
        final CustomVerb customVerb = this.adventure.findCustomVerb(verbId)

        if (customVerb == null) {
            throw new RuntimeException("Cannot locate custom verb '" + verbId + "'")
        }

        this.item.addVerb(customVerb, closure)
    }

    private void container(Boolean container) {
        this.item.setContainer(container)
    }

    private void openable(Boolean openable) {
        this.item.setOpenable(openable)
    }

    private void closeable(Boolean closeable) {
        this.item.setCloseable(closeable)
    }

    private void open(Boolean open) {
        this.item.setOpen(open)
    }

    private void openMessage(String openMessage) {
        this.item.setOpenMessage(openMessage)
    }

    private void closeMessage(String closeMessage) {
        this.item.setCloseMessage(closeMessage)
    }

    private void onOpen(Closure closure) {
        this.item.setOnOpen(closure)
    }

    private void onClose(Closure closure) {
        this.item.setOnClose(closure)
    }

    private void contentVisibility(ContentVisibility contentVisibility) {
        this.item.setContentVisibility(contentVisibility)
    }

    private void edible(Boolean edible) {
        this.item.setEdible(edible)
    }

    private void eatMessage(String eatMessage) {
        this.item.setEatMessage(eatMessage)
    }

    private void onEat(Closure closure) {
        this.item.setOnEat(closure)
    }
}
