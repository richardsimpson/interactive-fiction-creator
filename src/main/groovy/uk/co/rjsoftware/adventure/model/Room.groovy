package uk.co.rjsoftware.adventure.model

import groovy.transform.TypeChecked
import uk.co.rjsoftware.adventure.controller.ScriptRuntimeDelegate

@TypeChecked
class Room implements ItemContainer, VerbContainer {

    private final UUID id
    private final Map<Direction, Exit> exits = new TreeMap<>()
    private final Map<String, Closure> customVerbs = new HashMap<>()
    private final Set<Item> items = new TreeSet<>()
    private String name
    private String description
    private Closure descriptionClosure
    private Closure beforeEnterRoom
    private Closure afterEnterRoom
    private Closure afterLeaveRoom
    private Closure beforeEnterRoomFirstTime
    private Closure afterEnterRoomFirstTime

    Room(String name, String description,
         Closure beforeEnterRoom = null, Closure afterEnterRoom = null,
         Closure afterLeaveRoom = null,
         Closure beforeEnterRoomFirstTime = null, Closure afterEnterRoomFirstTime = null) {

        this.id = UUID.randomUUID()

        this.name = name
        this.description = description
        this.beforeEnterRoom = beforeEnterRoom
        this.afterEnterRoom = afterEnterRoom
        this.afterLeaveRoom = afterLeaveRoom
        this.beforeEnterRoomFirstTime = beforeEnterRoomFirstTime
        this.afterEnterRoomFirstTime = afterEnterRoomFirstTime
    }

    Room(String name) {
        this(name, "")
    }

    Room copy() {
        final Room roomCopy = new Room(this.name)

        for (Exit exit : this.exits.values()) {
            roomCopy.addExit(exit.copy())
        }
        roomCopy.customVerbs.putAll(customVerbs)
        for (Item item : this.items) {
//            roomCopy.items.put(entry.key, entry.value.copy(roomCopy))
            item.copy(roomCopy)
        }
        roomCopy.description = this.description
        roomCopy.descriptionClosure = this.descriptionClosure
        roomCopy.beforeEnterRoom = this.beforeEnterRoom
        roomCopy.afterEnterRoom = this.afterEnterRoom
        roomCopy.afterLeaveRoom = this.afterLeaveRoom
        roomCopy.beforeEnterRoomFirstTime = this.beforeEnterRoomFirstTime
        roomCopy.afterEnterRoomFirstTime = this.afterEnterRoomFirstTime

        roomCopy
    }

    UUID getId() {
        this.id
    }

    String getName() {
        this.name
    }

    String getDescription() {
        this.description
    }

    void setDescription(String description) {
        this.description = description
    }

    Closure getDescriptionClosure() {
        this.descriptionClosure
    }

    void setDescriptionClosure(Closure closure) {
        this.descriptionClosure = closure
    }

    void addExit(Exit exit) {
        this.exits.put(exit.getDirection(), exit)
    }

    Map<Direction, Exit> getExits() {
        this.exits
    }

    Exit getExit(Direction direction) {
        this.exits.get(direction)
    }

    void addItem(Item item) {
        if (!contains(item)) {
            this.items.add(item)
            item.setParent(this)
        }
    }

    Item getItemByName(String itemName) {
        this.items.find {item ->
            item.getName().equals(itemName)
        }
    }

    void removeItem(Item itemToRemove) {
        final Item item = this.items.find {item ->
            item.getName().equals(itemToRemove.getName())
        }

        if (item != null) {
            this.items.remove(item)
            item.setParent(null)
        }
    }

    boolean contains(Item item) {
        this.items.find {existingItem ->
            existingItem.getName().equals(item.getName())
        } != null
    }

    Set<Item> getItems() {
        this.items
    }

    Set<Item> getAllItems() {
        final Set<Item> items = new HashSet<>()
        items.addAll(this.items)

        for (Item item : this.items) {
            items.addAll(item.getAllItems())
        }

        items
    }

    boolean containsVerb(CustomVerb verb) {
        this.customVerbs.containsKey(verb.id)
    }

    void addVerb(CustomVerb verb, @DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=ScriptRuntimeDelegate) Closure closure) {
        this.customVerbs.put(verb.getId(), closure)
    }

    Closure getVerbClosure(CustomVerb verb) {
        this.customVerbs.get(verb.id)
    }

    Closure getBeforeEnterRoom() {
        this.beforeEnterRoom
    }

    void setBeforeEnterRoom(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=ScriptRuntimeDelegate) Closure closure) {
        this.beforeEnterRoom = closure
    }

    Closure getAfterEnterRoom() {
        this.afterEnterRoom
    }

    void setAfterEnterRoom(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=ScriptRuntimeDelegate) Closure closure) {
        this.afterEnterRoom = closure
    }

    Closure getAfterLeaveRoom() {
        this.afterLeaveRoom
    }

    void setAfterLeaveRoom(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=ScriptRuntimeDelegate) Closure closure) {
        this.afterLeaveRoom = closure
    }

    Closure getBeforeEnterRoomFirstTime() {
        this.beforeEnterRoomFirstTime
    }

    void setBeforeEnterRoomFirstTime(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=ScriptRuntimeDelegate) Closure closure) {
        this.beforeEnterRoomFirstTime = closure
    }

    Closure getAfterEnterRoomFirstTime() {
        this.afterEnterRoomFirstTime
    }

    void setAfterEnterRoomFirstTime(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=ScriptRuntimeDelegate) Closure closure) {
        this.afterEnterRoomFirstTime = closure
    }

}
