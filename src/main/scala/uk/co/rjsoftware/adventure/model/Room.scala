package uk.co.rjsoftware.adventure.model

import scala.collection.immutable.ListMap

/**
  * Created by richardsimpson on 15/05/2017.
  */
class Room(private val name:String, private var description:String,
           private var beforeEnterRoomScript:String = null, private var afterEnterRoomScript:String = null,
           private var afterLeaveRoomScript:String = null,
           private var beforeEnterRoomFirstTimeScript:String = null, private var afterEnterRoomFirstTimeScript:String = null)
        extends ItemContainer with VerbContainer {

    private var exits:ListMap[Direction, Room] = ListMap[Direction, Room]()
    private var customVerbs:Map[CustomVerb, String] = Map[CustomVerb, String]()
    private var items:Map[String, Item] = Map[String, Item]()

    def this(name:String) {
        this(name, "")
    }

    def getName : String = {
        this.name
    }

    def getDescription : String = {
        this.description
    }

    def setDescription(description:String): Unit = {
        this.description = description
    }

    def addExit(direction:Direction, room:Room) : Unit = {
        this.exits += (direction -> room)
    }

    def addItem(item:Item) : Unit = {
        this.items += (item.getId.toUpperCase -> item)
    }

    def getExits : Map[Direction, Room] = {
        this.exits
    }

    def getExit(direction:Direction) : Option[Room] = {
        this.exits.get(direction)
    }

    def getItem(itemId:String) : Item = {
        this.items.get(itemId.toUpperCase).orNull
    }

    def removeItem(item:Item) : Unit = {
        this.items -= item.getId.toUpperCase
    }

    def contains(item:Item) : Boolean = {
        this.items.contains(item.getId.toUpperCase)
    }

    def getItems : Map[String, Item] = {
        this.items
    }

    def getVerbs : Map[CustomVerb, String] = {
        this.customVerbs
    }

    def addVerb(verb:CustomVerb, script:String) : Unit = {
        this.customVerbs += (verb -> script)
    }

    def getBeforeEnterRoomScript : String = {
        this.beforeEnterRoomScript
    }

    def setBeforeEnterRoomScript(script:String) : Unit = {
        this.beforeEnterRoomScript = script
    }

    def getAfterEnterRoomScript : String = {
        this.afterEnterRoomScript
    }

    def setAfterEnterRoomScript(script:String) : Unit = {
        this.afterEnterRoomScript = script
    }

    def getAfterLeaveRoomScript : String = {
        this.afterLeaveRoomScript
    }

    def setAfterLeaveRoomScript(script:String) : Unit = {
        this.afterLeaveRoomScript = script
    }

    def getBeforeEnterRoomFirstTimeScript : String = {
        this.beforeEnterRoomFirstTimeScript
    }

    def setBeforeEnterRoomFirstTimeScript(script:String) : Unit = {
        this.beforeEnterRoomFirstTimeScript = script
    }

    def getAfterEnterRoomFirstTimeScript : String = {
        this.afterEnterRoomFirstTimeScript
    }

    def setAfterEnterRoomFirstTimeScript(script:String) : Unit = {
        this.afterEnterRoomFirstTimeScript = script
    }

}
