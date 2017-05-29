package uk.co.rjsoftware.adventure.model

import scala.collection.immutable.ListMap

/**
  * Created by richardsimpson on 15/05/2017.
  */
class Room(private val name:String, private val description:String,
           private val beforeEnterRoomScript:String = null, private val afterEnterRoomScript:String = null,
           private val afterLeaveRoomScript:String = null,
           private val beforeEnterRoomFirstTimeScript:String = null, private val afterEnterRoomFirstTimeScript:String = null) {

    private var exits:ListMap[Direction, Room] = ListMap[Direction, Room]()
    private var items:Map[String, Item] = Map[String, Item]()


    def getDescription : String = {
        this.description
    }

    def addExit(direction:Direction, room:Room) : Unit = {
        this.exits += (direction -> room)
    }

    def addItem(item:Item) : Unit = {
        this.items += (item.getName -> item)
    }

    def getExits : Map[Direction, Room] = {
        this.exits
    }

    def getExit(direction:Direction) : Option[Room] = {
        this.exits.get(direction)
    }

    def getItem(itemName:String) : Option[Item] = {
        this.items.get(itemName)
    }

    def removeItem(item:Item) : Unit = {
        this.items -= item.getName
    }

    def contains(item:Item) : Boolean = {
        this.items.contains(item.getName)
    }

    def getItems : Map[String, Item] = {
        this.items
    }

    def getBeforeEnterRoomScript : String = {
        this.beforeEnterRoomScript
    }

    def getAfterEnterRoomScript : String = {
        this.afterEnterRoomScript
    }

    def getAfterLeaveRoomScript : String = {
        this.afterLeaveRoomScript
    }

    def getBeforeEnterRoomFirstTimeScript : String = {
        this.beforeEnterRoomFirstTimeScript
    }

    def getAfterEnterRoomFirstTimeScript : String = {
        this.afterEnterRoomFirstTimeScript
    }

}
