package uk.co.rjsoftware.adventure.model

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

    Adventure createCopy() {
        final Adventure adventureCopy = new Adventure(introduction)
        adventureCopy.title = title
        adventureCopy.rooms = rooms.collect {it.createCopy()}
        adventureCopy.startRoom = adventureCopy.rooms.find{it.getName() == startRoom.getName()}
        adventureCopy.customVerbs = customVerbs.collect {it.createCopy()}
        adventureCopy.waitText = waitText

        // now fixup the exits in the rooms
        for (Room newRoom : adventureCopy.rooms) {
            newRoom.fixupExits(this, adventureCopy)
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

    def findCustomVerb(String verbName) {
        this.customVerbs.find { verb ->
            verb.getVerb().equals(verbName)
        }
    }

    String getWaitText() {
        this.waitText
    }

    void setWaitText(String waitText) {
        this.waitText = waitText
    }

}
