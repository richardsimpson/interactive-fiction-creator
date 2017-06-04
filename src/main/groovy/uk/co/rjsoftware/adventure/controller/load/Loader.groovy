package uk.co.rjsoftware.adventure.controller.load

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

public abstract class AdventureLoaderScript extends Script {

    private AdventureDelegate adventureDelegate = new AdventureDelegate();

    public void adventure(Closure closure) {
        closure.delegate = adventureDelegate
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()
    }

    public Adventure getAdventure() {
        return this.adventureDelegate.getAdventure()
    }
}

public class AdventureDelegate {
    private final Adventure adventure = new Adventure("");

    private void title(String title) {
        this.adventure.setTitle(StringUtils.sanitiseString(title))
    }

    private void introduction(String introduction) {
        this.adventure.setIntroduction(StringUtils.sanitiseString(introduction))
    }

    private void verb(String friendlyName, String verbName, Closure closure) {
        if (this.adventure.findCustomVerb(verbName) != null) {
            throw new RuntimeException("Cannot declare custom verbs twice")
        }

        CustomVerb customVerb = new CustomVerb(friendlyName, verbName)
        this.adventure.addCustomVerb(customVerb)

        closure.delegate = new VerbDelegate(customVerb)
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()
    }

    private void room(String roomName, Closure closure) {
        Room room = this.adventure.findRoom(roomName)

        if (room == null) {
            room = new Room(roomName)
            this.adventure.addRoom(room)
        }

        closure.delegate = new RoomDelegate(room, this.adventure)
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()
    }

    private void startRoom(String roomName) {
        Room room = this.adventure.findRoom(roomName)

        if (room == null) {
            throw new RuntimeException(("Cannot locate room named '" + roomName + "'"))
        }

        this.adventure.setStartRoom(room)
    }

    private Adventure getAdventure() {
        return this.adventure
    }
}

public class VerbDelegate {

    private final CustomVerb customVerb

    private VerbDelegate(final CustomVerb customVerb) {
        this.customVerb = customVerb
    }

    private void synonyms(String... synonyms) {
        for (String synonym : synonyms) {
            this.customVerb.addSynonym(synonym)
        }
    }

}

public class RoomDelegate {
    private Room room
    private final Adventure adventure

    private Direction NORTH = Direction.NORTH
    private Direction EAST = Direction.EAST
    private Direction SOUTH = Direction.SOUTH
    private Direction WEST = Direction.WEST
    private Direction UP = Direction.UP
    private Direction DOWN = Direction.DOWN

    private RoomDelegate(final Room room, final Adventure adventure) {
        this.adventure = adventure
        this.room = room
    }

    private void description(String description) {
        this.room.setDescription(StringUtils.sanitiseString(description))
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

    private void exit(LinkedHashMap linkedHashMap) {
        Room room = this.adventure.findRoom(linkedHashMap.get("room"))

        if (room == null) {
            throw new RuntimeException("Cannot reference a room before it is defined")
        }

        this.room.addExit(linkedHashMap.get("direction"), room)
    }

    private void item(String itemName, Closure closure) {
        Item item = this.room.getItem(itemName)
        if (item == null) {
            item = new Item(itemName)
            this.room.addItem(item)
        }

        closure.delegate = new ItemDelegate(this.adventure, item)
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()
    }

    private void verb(String verbName, Closure closure) {
        final CustomVerb customVerb = this.adventure.findCustomVerb(verbName)

        if (customVerb == null) {
            throw new RuntimeException("Cannot locate custom verb '" + verbName + "'")
        }

        final VerbInstanceDelegate delegate = new VerbInstanceDelegate()
        closure.delegate = delegate
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()

        this.room.addVerb(customVerb, delegate.getScript())

    }

}

public class ItemDelegate {

    private final Item item
    private final Adventure adventure

    private ContentVisibility ALWAYS = ContentVisibility.ALWAYS
    private ContentVisibility ÅFTER_EXAMINE = ContentVisibility.AFTER_EXAMINE
    private ContentVisibility NEVER = ContentVisibility.NEVER

    private ItemDelegate(Adventure adventure, Item item) {
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

    private void verb(String verbName, Closure closure) {
        final CustomVerb customVerb = this.adventure.findCustomVerb(verbName)

        if (customVerb == null) {
            throw new RuntimeException("Cannot locate custom verb '" + verbName + "'")
        }

        final VerbInstanceDelegate delegate = new VerbInstanceDelegate()
        closure.delegate = delegate
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()

        this.item.addVerb(customVerb, delegate.getScript())

    }

    private container(Boolean container) {
        this.item.setContainer(container)
    }

    private openable(Boolean openable) {
        this.item.setOpenable(openable)
    }

    private closeable(Boolean closeable) {
        this.item.setCloseable(closeable)
    }

    private open(Boolean open) {
        this.item.setOpen(open)
    }

    private openMessage(String openMessage) {
        this.item.setOpenMessage(openMessage)
    }

    private closeMessage(String closeMessage) {
        this.item.setCloseMessage(closeMessage)
    }

    private onOpenScript(String onOpenScript) {
        this.item.setOnOpenScript(onOpenScript)
    }

    private onCloseScript(String onCloseScript) {
        this.item.setOnCloseScript(onCloseScript)
    }

    private contentVisibility(ContentVisibility contentVisibility) {
        this.item.setContentVisibility(contentVisibility)
    }
}

public class VerbInstanceDelegate {

    private String script

    private void script(String script) {
        this.script = StringUtils.sanitiseString(script)
    }

    private String getScript() {
        this.script
    }
}

