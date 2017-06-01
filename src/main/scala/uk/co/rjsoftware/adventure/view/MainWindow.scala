package uk.co.rjsoftware.adventure.view

/**
  * Created by richardsimpson on 20/05/2017.
  */
trait MainWindow {

    def say(outputText:String) : Unit

    def addCommandListener(listener: CommandEvent => Unit) : Unit

    def addLoadListener(listener: LoadEvent => Unit) : Unit

    def loadAdventure(title:String, introduction:String)
}
