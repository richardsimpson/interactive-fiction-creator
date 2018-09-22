package uk.co.rjsoftware.adventure.model

import groovy.transform.TypeChecked

@TypeChecked
class Adventure {

    private String introduction
    private String title = null
    private List<Room> rooms = new ArrayList()
    private Room startRoom = null
    private List<CustomVerb> customVerbs = new ArrayList()

    private String waitText = null

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

    Room getStartRoom() {
        this.startRoom
    }

    void setStartRoom(Room startRoom) {
        this.startRoom = startRoom
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

    Room findRoom(String roomName) {
        this.rooms.find {room ->
            room.getName().equals(roomName)
        }
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

}
