package uk.co.rjsoftware.adventure.model

/**
  * Created by richardsimpson on 16/05/2017.
  */
class Player extends ItemContainer {
    private var items:Map[String, Item] = Map[String, Item]()

    def getItems : Map[String, Item] = {
        this.items
    }

    def addItem(item:Item) : Unit = {
        this.items += (item.getId.toUpperCase -> item)
    }

    def getItem(itemId: String): Item = {
        this.items.get(itemId.toUpperCase).orNull
    }

    def removeItem(item:Item) : Unit = {
        this.items -= item.getId.toUpperCase
    }

    def contains(item:Item) : Boolean = {
        this.items.contains(item.getId.toUpperCase)
    }
}
