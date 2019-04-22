package uk.co.rjsoftware.adventure.controller.load

import groovy.transform.TypeChecked
import org.codehaus.groovy.control.CompilerConfiguration
import uk.co.rjsoftware.adventure.model.*
import uk.co.rjsoftware.adventure.utils.StringUtils

/**
 * Created by richardsimpson on 29/05/2017.
 */
@TypeChecked
class Loader {
    static Adventure loadAdventure(File dsl) {
        def config = new CompilerConfiguration();
        config.scriptBaseClass = AdventureLoaderScript.getCanonicalName()

        def shell = new GroovyShell(new Binding(), config)
        def script = (AdventureLoaderScript)shell.parse(dsl.text)

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

    private void verb(String name, String displayName, String command, Optional<Closure> closure) {
        if (this.adventure.getVerbByName(name) != null) {
            throw new RuntimeException("Cannot declare custom verbs twice")
        }

        CustomVerb customVerb = new CustomVerb(name, displayName, command)
        this.adventure.addCustomVerb(customVerb)

        closure.ifPresent {clo ->
            clo.delegate = new VerbDelegate(customVerb)
            clo.resolveStrategy = Closure.DELEGATE_ONLY
            clo()
        }
    }

    private void verb(String name, String displayName, String command, Closure closure) {
        verb(name, displayName, command, Optional.of(closure))
    }

    private void verb(String name, String displayName, String command) {
        verb(name, displayName, command, Optional.empty())
    }

    private void verb(String name, String command, Closure closure) {
        verb(name, name, command, closure)
    }

    private void verb(String name, String command) {
        verb(name, name, command)
    }

    private void room(String roomName, Closure closure) {
        Room room = this.adventure.getRoomByName(roomName)

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
        Item item = this.adventure.getItemByName(itemName)

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

    private final Map<Direction, Tuple2<String, Optional<Closure>>> forwardRoomReferences = new HashMap<>()

    private Direction NORTH = Direction.NORTH
    private Direction EAST = Direction.EAST
    private Direction SOUTH = Direction.SOUTH
    private Direction WEST = Direction.WEST
    private Direction NORTHEAST = Direction.NORTHEAST
    private Direction SOUTHEAST = Direction.SOUTHEAST
    private Direction SOUTHWEST = Direction.SOUTHWEST
    private Direction NORTHWEST = Direction.NORTHWEST
    private Direction UP = Direction.UP
    private Direction DOWN = Direction.DOWN

    RoomDelegate(final Room room, final Adventure adventure) {
        this.adventure = adventure
        this.room = room
    }

    private void description(String description) {
        this.room.setDescription(StringUtils.sanitiseString(description))
    }

    private void descriptionScript(String script) {
        this.room.setDescriptionScript(StringUtils.sanitiseString(script))
    }

    private void descriptionScriptEnabled(boolean scriptEnabled) {
        this.room.setDescriptionScriptEnabled(scriptEnabled)
    }

    private void beforeEnterRoomScript(String script) {
        this.room.setBeforeEnterRoomScript(StringUtils.sanitiseString(script))
    }

    private void afterEnterRoomScript(String script) {
        this.room.setAfterEnterRoomScript(StringUtils.sanitiseString(script))
    }

    private void afterLeaveRoomScript(String script) {
        this.room.setAfterLeaveRoomScript(StringUtils.sanitiseString(script))
    }

    private void beforeEnterRoomFirstTimeScript(String script) {
        this.room.setBeforeEnterRoomFirstTimeScript(StringUtils.sanitiseString(script))
    }

    private void afterEnterRoomFirstTimeScript(String script) {
        this.room.setAfterEnterRoomFirstTimeScript(StringUtils.sanitiseString(script))
    }

    private boolean exit(Direction direction, String destination, Optional<Closure> closure) {
        Exit exit = this.room.getExit(direction)
        if (exit == null) {
            exit = new Exit(direction)
            this.room.addExit(exit)
        }

        Room room = this.adventure.getRoomByName(destination)
        if (room == null) {
            this.forwardRoomReferences.put(direction, new Tuple2<>(destination, closure))
            false
        }
        else {
            exit.setDestination(room)

            closure.ifPresent {clo ->
                clo.delegate = new ExitDelegate(exit)
                clo.resolveStrategy = Closure.DELEGATE_ONLY
                clo()
            }

            true
        }
    }

    private void exit(Direction direction, String destination, Closure closure) {
        exit(direction, destination, Optional.of(closure))
    }

    private void exit(Direction direction, String destination) {
        exit(direction, destination, Optional.empty())
    }

    void resolveForwardRoomReferences() {
        for (Map.Entry<Direction, Tuple2<String, Optional<Closure>>> entry : this.forwardRoomReferences) {
            final boolean ableToResolve = exit(entry.key, entry.value.getFirst(), entry.value.getSecond())
            if (!ableToResolve) {
                throw new RuntimeException("Room '${this.room.name}' contains an exit to a room named '${entry.value.getFirst()}', which does not exist.")
            }
        }
    }

    private void item(String itemName, String itemDisplayName, Optional<Closure> closure) {
        Item item = this.room.getItemByName(itemName)
        if (item == null) {
            item = new Item(itemName, itemDisplayName)
            this.room.addItem(item)
        }

        closure.ifPresent {clo ->
            clo.delegate = new ItemDelegate(this.adventure, item)
            clo.resolveStrategy = Closure.DELEGATE_ONLY
            clo()
        }
    }

    private void item(String itemName, Closure closure) {
        item(itemName, itemName, Optional.of(closure))
    }

    private void item(String itemName) {
        item(itemName, itemName, Optional.empty())
    }

    private void item(String itemName, String itemDisplayName, Closure closure) {
        item(itemName, itemDisplayName, Optional.of(closure))
    }

    private void item(String itemName, String itemDisplayName) {
        item(itemName, itemDisplayName, Optional.empty())
    }

    private void verb(String name, Closure closure) {
        final CustomVerb customVerb = this.adventure.getVerbByName(name)

        if (customVerb == null) {
            throw new RuntimeException("Cannot locate custom verb '" + name + "'")
        }

        final CustomVerbInstance verbInstance = new CustomVerbInstance(customVerb.getId())
        final VerbInstanceDelegate delegate = new VerbInstanceDelegate(verbInstance)
        closure.delegate = delegate
        closure.resolveStrategy = Closure.DELEGATE_ONLY
        closure()

        this.room.addVerb(customVerb, verbInstance)

    }

}

@TypeChecked
class ExitDelegate {

    private final Exit exit

    ExitDelegate(Exit exit) {
        this.exit = exit
    }

    private void scenery(Boolean scenery) {
        this.exit.setScenery(scenery)
    }

    private void prefix(String prefix) {
        this.exit.setPrefix(prefix)
    }

    private void suffix(String suffix) {
        this.exit.setSuffix(suffix)
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
        for (String synonym : synonyms) {
            this.item.addSynonym(synonym)
        }
    }

    private void description(String description) {
        this.item.setDescription(StringUtils.sanitiseString(description))
    }

    private void descriptionScript(String script) {
        this.item.setDescriptionScript(StringUtils.sanitiseString(script))
    }

    private void descriptionScriptEnabled(boolean scriptEnabled) {
        this.item.setDescriptionScriptEnabled(scriptEnabled)
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

    private void extraDescriptionWhenSwitchedOn(String extraDescriptionWhenSwitchedOn) {
        this.item.setExtraDescriptionWhenSwitchedOn(StringUtils.sanitiseString(extraDescriptionWhenSwitchedOn))
    }

    private void extraDescriptionWhenSwitchedOff(String extraDescriptionWhenSwitchedOff) {
        this.item.setExtraDescriptionWhenSwitchedOff(StringUtils.sanitiseString(extraDescriptionWhenSwitchedOff))
    }

    private void verb(String name, Closure closure) {
        final CustomVerb customVerb = this.adventure.getVerbByName(name)

        if (customVerb == null) {
            throw new RuntimeException("Cannot locate custom verb '" + name + "'")
        }

        final CustomVerbInstance verbInstance = new CustomVerbInstance(customVerb.getId())
        final VerbInstanceDelegate delegate = new VerbInstanceDelegate(verbInstance)
        closure.delegate = delegate
        closure.resolveStrategy = Closure.DELEGATE_ONLY
        closure()

        this.item.addVerb(customVerb, verbInstance)

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

    private void onOpenScript(String onOpenScript) {
        this.item.setOnOpenScript(onOpenScript)
    }

    private void onCloseScript(String onCloseScript) {
        this.item.setOnCloseScript(onCloseScript)
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

    private void onEatScript(String onEatScript) {
        this.item.setOnEatScript(onEatScript)
    }
}

class VerbInstanceDelegate {

    CustomVerbInstance verbInstance

    VerbInstanceDelegate(CustomVerbInstance verbInstance) {
        this.verbInstance = verbInstance
    }

    private void script(String script) {
        this.verbInstance.setScript(StringUtils.sanitiseString(script))
    }
}

