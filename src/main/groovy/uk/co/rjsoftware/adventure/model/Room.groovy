package uk.co.rjsoftware.adventure.model

class Room implements ItemContainer, VerbContainer {

    private final Map<Direction, Room> exits = new TreeMap()
    private final Map<CustomVerb, String> customVerbs = new HashMap()
    private final Map<String, Item> items = new TreeMap<String, Item>()
    private String name
    private String description
    private String beforeEnterRoomScript
    private String afterEnterRoomScript
    private String afterLeaveRoomScript
    private String beforeEnterRoomFirstTimeScript
    private String afterEnterRoomFirstTimeScript

    Room(String name, String description,
         String beforeEnterRoomScript = null, String afterEnterRoomScript = null,
         String afterLeaveRoomScript = null,
         String beforeEnterRoomFirstTimeScript = null, String afterEnterRoomFirstTimeScript = null) {

        this.name = name
        this.description = description
        this.beforeEnterRoomScript = beforeEnterRoomScript
        this.afterEnterRoomScript = afterEnterRoomScript
        this.afterLeaveRoomScript = afterLeaveRoomScript
        this.beforeEnterRoomFirstTimeScript = beforeEnterRoomFirstTimeScript
        this.afterEnterRoomFirstTimeScript = afterEnterRoomFirstTimeScript
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

    Map<CustomVerb, String> getVerbs() {
        this.customVerbs
    }

    void addVerb(CustomVerb verb, String script) {
        this.customVerbs.put(verb, script)
    }

    String getBeforeEnterRoomScript() {
        this.beforeEnterRoomScript
    }

    void setBeforeEnterRoomScript(String script) {
        this.beforeEnterRoomScript = script
    }

    String getAfterEnterRoomScript() {
        this.afterEnterRoomScript
    }

    void setAfterEnterRoomScript(String script) {
        this.afterEnterRoomScript = script
    }

    String getAfterLeaveRoomScript() {
        this.afterLeaveRoomScript
    }

    void setAfterLeaveRoomScript(String script) {
        this.afterLeaveRoomScript = script
    }

    String getBeforeEnterRoomFirstTimeScript() {
        this.beforeEnterRoomFirstTimeScript
    }

    void setBeforeEnterRoomFirstTimeScript(String script) {
        this.beforeEnterRoomFirstTimeScript = script
    }

    String getAfterEnterRoomFirstTimeScript() {
        this.afterEnterRoomFirstTimeScript
    }

    void setAfterEnterRoomFirstTimeScript(String script) {
        this.afterEnterRoomFirstTimeScript = script
    }

}
