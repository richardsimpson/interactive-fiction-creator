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

    Map<String, Item> getItems() {
        final Map<String, Item> items = new HashMap<>()

        for (Room room : getRooms()) {
            for (Map.Entry<String, Item> itemEntry : room.getItems()) {
                items.put(itemEntry.key.toUpperCase(), itemEntry.value)
            }
        }

        items
    }

    Item getItem(String itemName) {
        getItems().get(itemName.toUpperCase())
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
