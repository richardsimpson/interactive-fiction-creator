package uk.co.rjsoftware.adventure.model

import groovy.transform.TypeChecked
import uk.co.rjsoftware.adventure.controller.ScriptRuntimeDelegate

@TypeChecked
class Room implements ItemContainer, VerbContainer {

    private final Map<Direction, Room> exits = new TreeMap()
    private final Map<String, Closure> customVerbs = new HashMap()
    private final Map<String, Item> items = new TreeMap<String, Item>()
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

    void addExit(Direction direction, Room room) {
        this.exits.put(direction, room)
    }

    Map<Direction, Room> getExits() {
        this.exits
    }

    Room getExit(Direction direction) {
        this.exits.get(direction)
    }

    void addItem(Item item) {
        if (!contains(item)) {
            this.items.put(item.getId().toUpperCase(), item)
            item.setParent(this)
        }
    }

    Item getItem(String itemId) {
        this.items.get(itemId.toUpperCase())
    }

    void removeItem(Item item) {
        if (contains(item)) {
            this.items.remove(item.getId().toUpperCase())
            item.setParent(null)
        }
    }

    boolean contains(Item item) {
        this.items.containsKey(item.getId().toUpperCase())
    }

    Map<String, Item> getItems() {
        this.items
    }

    Map<String, Item> getAllItems() {
        final Map<String, Item> items = new HashMap<>()
        items.putAll(this.items)

        for (Item item : this.items.values()) {
            items.putAll(item.getAllItems())
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
