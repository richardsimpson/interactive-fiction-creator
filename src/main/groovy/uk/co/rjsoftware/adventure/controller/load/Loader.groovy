package uk.co.rjsoftware.adventure.controller.load

import org.codehaus.groovy.control.CompilerConfiguration
import scala.Option
import scala.collection.immutable.List
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.model.Direction
import uk.co.rjsoftware.adventure.model.Item
import uk.co.rjsoftware.adventure.model.Room

/**
 * Created by richardsimpson on 29/05/2017.
 */
class Loader {
    static Adventure loadAdventure(File dsl) {
        def config = new CompilerConfiguration();
        config.scriptBaseClass = 'AdventureLoaderScript'

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

    private void introduction(String introduction) {
        this.adventure.setIntroduction(introduction)
    }

    private void rooms(Closure closure) {
        closure.delegate = new RoomsDelegate(adventure)
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()
    }

    private Adventure getAdventure() {
        return this.adventure
    }
}

public class RoomsDelegate {

    private final Adventure adventure

    private RoomsDelegate(final Adventure adventure) {
        this.adventure = adventure
    }

    private void methodMissing(String roomName, args) {
        Room room = this.adventure.findRoom(roomName)

        if (room == null) {
            room = new Room(roomName)
            this.adventure.addRoom(room)
        }

        final Closure closure = (Closure) args[0]

        closure.delegate = new RoomDelegate(room, this.adventure)
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()
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
        this.room.setDescription(description)
    }

    private void beforeEnterRoomScript(String script) {
        this.room.beforeEnterRoomScript = script
    }

    private void afterEnterRoomScript(String script) {
        this.room.setAfterEnterRoomScript(script)
    }

    private void afterLeaveRoomScript(String script) {
        this.room.setAfterLeaveRoomScript(script)
    }

    private void beforeEnterRoomFirstTimeScript(String script) {
        this.room.setBeforeEnterRoomFirstTimeScript(script)
    }

    private void afterEnterRoomFirstTimeScript(String script) {
        this.room.setAfterEnterRoomFirstTimeScript(script)
    }

    private void exit(LinkedHashMap linkedHashMap) {
        Room room = this.adventure.findRoom(linkedHashMap.get("room"))

        if (room == null) {
            throw new RuntimeException("Cannot reference a room before it is defined")
        }

        this.room.addExit(linkedHashMap.get("direction"), room)
    }

    private void items(Closure closure) {
        closure.delegate = new ItemsDelegate(this.room)
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()
    }
}

public class ItemsDelegate {

    private Room room

    private ItemsDelegate(Room room) {
        this.room = room
    }

    private void methodMissing(String itemName, args) {
        Item item = this.room.getItem(itemName).orNull()
        if (item == null) {
            item = new Item(itemName)
            this.room.addItem(item)
        }

        final Closure closure = (Closure) args[0]

        closure.delegate = new ItemDelegate(item)
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()
    }

}

public class ItemDelegate {

    private final Item item

    private ItemDelegate(Item item) {
        this.item = item
    }


}