package uk.co.rjsoftware.adventure.model

/**
  * Created by richardsimpson on 15/05/2017.
  */
class Adventure(private val introduction:String) {

    private var rooms:List[Room] = Nil
    private var startRoom:Room = null

    def addRoom(room:Room) : Unit = {
        this.rooms ::= room
    }

    def getStartRoom : Room = {
        this.startRoom
    }

    def setStartRoom(startRoom:Room) : Unit = {
        this.startRoom = startRoom
    }

    def getIntroduction : String = {
        this.introduction
    }

    def getRooms : List[Room] = {
        this.rooms
    }
}
