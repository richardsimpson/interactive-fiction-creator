package uk.co.rjsoftware.adventure.model

import scala.collection.mutable.HashMap
import scala.collection.mutable.Map

/**
  * Created by richardsimpson on 16/05/2017.
  */
class Player {
    private val items:Map[String, Item] = new HashMap[String, Item]

    def getItems : Map[String, Item] = {
        this.items
    }

    def addItem(item:Item) : Unit = {
        this.items.put(item.getName, item)
    }

    def getItem(itemName: String): Option[Item] = {
        this.items.get(itemName)
    }

    def removeItem(item:Item) : Unit = {
        this.items.remove(item.getName)
    }

    def contains(item:Item) : Boolean = {
        this.items.contains(item.getName)
    }
}
