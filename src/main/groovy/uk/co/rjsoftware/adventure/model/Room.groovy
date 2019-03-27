package uk.co.rjsoftware.adventure.model

import groovy.transform.TypeChecked

@TypeChecked
class Room implements ItemContainer, VerbContainer {

    private final UUID id
    private final Map<Direction, Exit> exits = new TreeMap<>()
    private final Map<String, String> customVerbs = new HashMap<>()
    private final Set<Item> items = new TreeSet<>()
    private String name
    private String description
    private String descriptionScript
    private String beforeEnterRoomScript
    private String afterEnterRoomScript
    private String afterLeaveRoomScript
    private String beforeEnterRoomFirstTimeScript
    private String afterEnterRoomFirstTimeScript

    Room(String name, String description,
         String beforeEnterRoomScript = null, String afterEnterRoomScript = null,
         String afterLeaveRoomScript = null,
         String beforeEnterRoomFirstTimeScript = null, String afterEnterRoomFirstTimeScript = null) {

        this.id = UUID.randomUUID()

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
        roomCopy.descriptionScript = this.descriptionScript
        roomCopy.beforeEnterRoomScript = this.beforeEnterRoomScript
        roomCopy.afterEnterRoomScript = this.afterEnterRoomScript
        roomCopy.afterLeaveRoomScript = this.afterLeaveRoomScript
        roomCopy.beforeEnterRoomFirstTimeScript = this.beforeEnterRoomFirstTimeScript
        roomCopy.afterEnterRoomFirstTimeScript = this.afterEnterRoomFirstTimeScript

        roomCopy
    }

    UUID getId() {
        this.id
    }

    String getName() {
        this.name
    }

    void setName(String name) {
        this.name = name
    }

    String getDescription() {
        this.description
    }

    void setDescription(String description) {
        this.description = description
    }

    String getDescriptionScript() {
        this.descriptionScript
    }

    void setDescriptionScript(String script) {
        this.descriptionScript = script
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

    void addVerb(CustomVerb verb, String script) {
        this.customVerbs.put(verb.getId(), script)
    }

    String getVerbScript(CustomVerb verb) {
        this.customVerbs.get(verb.id)
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
