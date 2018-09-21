package uk.co.rjsoftware.adventure.model

import groovy.transform.TypeChecked
import uk.co.rjsoftware.adventure.controller.ScriptRuntimeDelegate

@TypeChecked
class Room implements ItemContainer, VerbContainer {

    private final Map<Direction, Room> exits = new TreeMap()
    private final Map<CustomVerb, Closure> customVerbs = new HashMap()
    private final Map<String, Item> items = new TreeMap<String, Item>()
    private String name
    private String description
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

    void addExit(Direction direction, Room room) {
        this.exits.put(direction, room)
    }

    void addItem(Item item) {
        this.items.put(item.getId().toUpperCase(), item)
    }

    Map<Direction, Room> getExits() {
        this.exits
    }

    Room getExit(Direction direction) {
        this.exits.get(direction)
    }

    Item getItem(String itemId) {
        this.items.get(itemId.toUpperCase())
    }

    void removeItem(Item item) {
        this.items.remove(item.getId().toUpperCase())
    }

    boolean contains(Item item) {
        this.items.containsKey(item.getId().toUpperCase())
    }

    Map<String, Item> getItems() {
        this.items
    }

    Map<CustomVerb, Closure> getVerbs() {
        this.customVerbs
    }

    void addVerb(CustomVerb verb, @DelegatesTo(strategy=Closure.DELEGATE_ONLY, value= ScriptRuntimeDelegate) Closure closure) {
        this.customVerbs.put(verb, closure)
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
