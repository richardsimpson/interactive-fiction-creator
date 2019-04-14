package uk.co.rjsoftware.adventure.model

import groovy.transform.TypeChecked

@TypeChecked
class Room implements ItemContainer, VerbContainer {

    private final UUID id
    private final Map<Direction, Exit> exits = new TreeMap<>()
    private final Map<UUID, CustomVerbInstance> customVerbs = new HashMap<>()
    // items map: key is the UPPER CASE name, to ensure the map is ordered by the name, and to ensure that items can be found regardless of case
    private final List<Item> items = new ArrayList<>()
    private String name
    private String description
    private String descriptionScript
    private boolean descriptionScriptEnabled
    private String beforeEnterRoomScript
    private String afterEnterRoomScript
    private String afterLeaveRoomScript
    private String beforeEnterRoomFirstTimeScript
    private String afterEnterRoomFirstTimeScript

    private Room(UUID id) {
        this.id = id
    }

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
        final Room roomCopy = new Room(this.id)

        roomCopy.name = this.name

        for (Exit exit : this.exits.values()) {
            roomCopy.addExit(exit.copy())
        }
        roomCopy.customVerbs.putAll(customVerbs)
        for (Item item : this.items) {
            roomCopy.addItem(item.copy(roomCopy))
        }
        roomCopy.description = this.description
        roomCopy.descriptionScript = this.descriptionScript
        roomCopy.descriptionScriptEnabled = this.descriptionScriptEnabled
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

    boolean isDescriptionScriptEnabled() {
        this.descriptionScriptEnabled
    }

    void setDescriptionScriptEnabled(boolean scriptEnabled) {
        this.descriptionScriptEnabled = scriptEnabled
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
        final String itemNameToGet = itemName.toUpperCase()
        this.items.find {
            itemNameToGet.equals(it.getName().toUpperCase())
        }
    }

    void removeItem(Item item) {
        if (contains(item)) {
            this.items.remove(item)
            item.setParent(null)
        }
    }

    boolean contains(Item item) {
        this.items.any {it.getId().equals(item.id)}
    }

    List<Item> getItems() {
        this.items
    }

    List<Item> getSortedItems() {
        this.items.sort(false) {o1, o2 -> o1.name.compareTo(o2.name)}
    }

    void setItems(List<Item> newItems) {
        this.items.clear()
        for (Item item : newItems) {
            addItem(item)
        }
    }

    List<Item> getAllItems() {
        final List<Item> items = new ArrayList<>()
        items.addAll(this.items)

        for (Item item : this.items) {
            items.addAll(item.getAllItems())
        }

        items
    }

    boolean containsVerb(CustomVerb verb) {
        this.customVerbs.containsKey(verb.getId())
    }

    void addVerb(CustomVerb verb, CustomVerbInstance verbInstance) {
        this.customVerbs.put(verb.getId(), verbInstance)
    }

    String getVerbScript(CustomVerb verb) {
        this.customVerbs.get(verb.getId()).getScript()
    }

    Map<UUID, CustomVerbInstance> getCustomVerbs() {
        this.customVerbs
    }

    void setCustomVerbs(Map<UUID, CustomVerbInstance> customVerbs) {
        this.customVerbs.clear()
        this.customVerbs.putAll(customVerbs)
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
