package uk.co.rjsoftware.adventure.model

/**
  * Created by richardsimpson on 15/05/2017.
  */
class Adventure(private var introduction:String) {

    private var rooms:List[Room] = Nil
    private var startRoom:Room = null
    private var customVerbs : List[CustomVerb] = Nil

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

    def setIntroduction(introduction:String) : Unit = {
        this.introduction = introduction
    }

    def getRooms : List[Room] = {
        this.rooms
    }

    def findRoom(roomName : String) : Room = {
        val roomOptional : Option[Room] = this.rooms.find((room) => {
            room.getName.equals(roomName)
        })

        roomOptional.orNull
    }

    def addCustomVerb(customVerb:CustomVerb) : Unit = {
        this.customVerbs ::= customVerb
    }

    def getCustomVerbs : List[CustomVerb] = {
        this.customVerbs
    }

    def findCustomVerb(verbName : String) : CustomVerb = {
        val customVerbOptional : Option[CustomVerb] = this.customVerbs.find((verb) => {
            verb.getVerb.equals(verbName)
        })

        customVerbOptional.orNull
    }


}
