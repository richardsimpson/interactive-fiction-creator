package uk.co.rjsoftware.adventure.model

import groovy.transform.TypeChecked

@TypeChecked
class Adventure {

    private String introduction
    private String title = null
    private List<Room> rooms = new ArrayList()
    private Item player = null
    private List<CustomVerb> customVerbs = new ArrayList()

    private String waitText = null
    private String getText = null

    Adventure(String introduction) {
        this.introduction = introduction
    }

    Adventure copy() {
        final Adventure adventureCopy = new Adventure(this.introduction)

        adventureCopy.title = this.title
        for (Room room : this.rooms) {
            adventureCopy.rooms.add(room.copy())
        }
        adventureCopy.customVerbs.addAll(this.customVerbs)
        adventureCopy.waitText = this.waitText
        adventureCopy.getText = this.getText

        // now fixup the room references (exits)
        for (Room room : this.rooms) {
            final Room roomCopy = adventureCopy.getRoom(room.name)
            for (Map.Entry<Direction, Room> entry : room.exits) {
                roomCopy.addExit(entry.key, adventureCopy.getRoom(entry.value.name))
            }
        }

        // todo: now fixup the player reference
        if (this.player != null) {
            adventureCopy.player = adventureCopy.getItem(this.player.getName())
        }
        adventureCopy
    }

    void setTitle(String title) {
        this.title = title
    }

    String getTitle() {
        this.title
    }

    void addRoom(Room room) {
        this.rooms.add(room)
    }

    Item getPlayer() {
        if (this.player != null) {
            return this.player
        }

        getItem("player")
    }

    void setPlayer(Item player) {
        this.player = player
    }

    String getIntroduction() {
        this.introduction
    }

    void setIntroduction(String introduction) {
        this.introduction = introduction
    }

    List<Room> getRooms() {
        this.rooms
    }

    Room getRoom(String roomName) {
        this.rooms.find {room ->
            room.getName().equals(roomName)
        }
    }

    Map<String, Item> getAllItems() {
        final Map<String, Item> items = new HashMap<>()

        for (Room room : getRooms()) {
            items.putAll(room.getAllItems())
        }

        items
    }

    Item getItem(String itemName) {
        getAllItems().get(itemName.toUpperCase())
    }

    void addCustomVerb(CustomVerb customVerb) {
        this.customVerbs.add(customVerb)
    }

    List<CustomVerb> getCustomVerbs() {
        this.customVerbs
    }

    CustomVerb findCustomVerb(String verbId) {
        this.customVerbs.find { verb ->
            verb.getId().equals(verbId)
        }
    }

    String getWaitText() {
        this.waitText
    }

    void setWaitText(String waitText) {
        this.waitText = waitText
    }

    String getGetText() {
        this.getText
    }

    void setGetText(String getText) {
        this.getText = getText
    }

}
