package uk.co.rjsoftware.adventure.model

/**
  * Created by richardsimpson on 03/06/2017.
  */
trait ItemContainer {

    def getItems : Map[String, Item]

    def addItem(item:Item) : Unit

    def getItem(itemName: String): Item

    def removeItem(item:Item) : Unit

    def contains(item:Item) : Boolean
}
