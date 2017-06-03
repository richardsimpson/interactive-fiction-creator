package uk.co.rjsoftware.adventure.model

/**
  * Created by richardsimpson on 03/06/2017.
  */
trait ItemContainer {

    def contains(item:Item) : Boolean
}
